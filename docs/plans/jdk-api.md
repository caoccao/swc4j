# JDK API Import and Usage Plan

## Goal
Enable TypeScript/JavaScript code to import and use Java standard library classes (JDK APIs) through ES6 import syntax. The test case `TestPrimeNumber` should compile and execute successfully:

```typescript
import { Math } from 'java.lang'
namespace com {
  export class A {
    public isPrime(number: number): boolean {
      const limit = Math.floor(number / 2)
      for (let i = 2; i <= limit; i++) {
        if (number % i === 0) {
          return false
        }
      }
      return true
    }
  }
}
```

This should generate bytecode that calls `java.lang.Math.floor()` as a static method.

## Architecture Overview

### Current State
The compiler already has infrastructure for type resolution:
- **ByteCodeCompilerMemory**: Central memory store with:
  - `ScopedTypeRegistry`: Manages type aliases with scope awareness
  - `TypeRegistry`: Stores fully qualified type information
  - `typeAliasMap`: Maps type aliases to descriptors
  - `CompilationContext`: Stores compilation state

- **Import AST Nodes** (already exist):
  - `Swc4jAstImportDecl`: Represents import declarations
  - `Swc4jAstImportNamedSpecifier`: Named imports like `{ Math }`
  - `Swc4jAstImportDefaultSpecifier`: Default imports
  - `Swc4jAstImportStarAsSpecifier`: Star imports like `import * as foo`

### Compilation Model
- **Single Compiler Instance**: One `ByteCodeCompiler` can compile multiple files
- **Per-File Compilation**: Each call to `compile(String code)` processes one source file
- **Shared Memory**: `ByteCodeCompilerMemory` persists across multiple `compile()` calls
- **Output**: `Map<String, byte[]>` where keys are fully qualified class names

**Critical Requirement**: Imports in one file must NOT be visible to another file.

### Missing Components
1. **Import Declaration Processor**: No handler to process `Swc4jAstImportDecl` nodes
2. **Java Package Resolution**: No mapping from module specifiers (e.g., `'java.lang'`) to Java packages
3. **Static Method Call Generator**: No specialized handling for Java static methods
4. **Type Inference for Java Classes**: TypeResolver doesn't know how to infer types of imported Java classes
5. **File-Level Import Isolation**: No mechanism to separate imports between files

## File-Level Import Isolation Strategy

### Problem
When compiling multiple files with the same compiler instance:
```java
// File A
compiler.compile("import { Math } from 'java.lang'\nclass A { ... }");

// File B (different file, should NOT see Math from File A)
compiler.compile("class B { x = Math.floor(3) }");  // ERROR: Math not imported!
```

Currently, if we store imports in `JavaClassRegistry` without isolation, imports from File A would leak into File B.

### Solution: Scoped Java Class Registry

**Design Philosophy**: Mirror the existing `ScopedTypeRegistry` pattern for consistency.

#### ScopedJavaClassRegistry Architecture

Create a **scope-based registry** similar to `ScopedTypeRegistry` that manages Java class imports with automatic scope isolation.

**Key Features**:
- Stack-based scope management
- Each scope contains Java class imports
- File-level scope for file isolation
- Can support nested scopes in the future (e.g., for modules within files)
- Automatic lookup from innermost to outermost scope

#### Data Structure

**Location**: `src/main/java/com/caoccao/javet/swc4j/compiler/memory/ScopedJavaClassRegistry.java`

```java
public final class ScopedJavaClassRegistry {
    // Stack of scopes: each scope maps class alias -> JavaTypeInfo
    private final Stack<Map<String, JavaTypeInfo>> scopeStack;

    public ScopedJavaClassRegistry() {
        scopeStack = new Stack<>();
        // Global scope is always present
        scopeStack.push(new HashMap<>());
    }

    /**
     * Enter a new scope (typically at the start of a file).
     * All Java class imports registered after this will be scoped to this level.
     */
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    /**
     * Exit the current scope (typically at the end of a file).
     * All Java class imports registered in this scope will be removed.
     * Cannot exit the global scope.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    /**
     * Get the current scope depth.
     * Global scope is depth 1, file scopes are 2, nested scopes are 3+.
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Register a Java class import in the current scope.
     * E.g., from `import { Math } from 'java.lang'`
     *
     * @param alias              the class alias (e.g., "Math")
     * @param javaClassInfo      the Java class metadata
     */
    public void registerClass(String alias, JavaTypeInfo javaClassInfo) {
        scopeStack.peek().put(alias, javaClassInfo);
    }

    /**
     * Resolve a class name to its JavaTypeInfo.
     * Searches from the innermost scope to the outermost scope.
     *
     * @param className the class name (alias)
     * @return the JavaTypeInfo, or null if not found
     */
    public JavaTypeInfo resolve(String className) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, JavaTypeInfo> scope = scopeStack.get(i);
            JavaTypeInfo info = scope.get(className);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    /**
     * Check if a class name is registered as an imported Java class.
     */
    public boolean isImportedJavaClass(String className) {
        return resolve(className) != null;
    }

    /**
     * Get JavaTypeInfo for a registered class.
     */
    public JavaTypeInfo getClassInfo(String className) {
        return resolve(className);
    }

    /**
     * Clear all scopes and reset to initial state.
     * Used for testing or explicit cleanup.
     */
    public void clear() {
        scopeStack.clear();
        scopeStack.push(new HashMap<>());
    }
}
```

#### JavaTypeInfo Structure

```java
public static class JavaTypeInfo {
    String simpleName;           // e.g., "Math"
    String fullyQualifiedName;   // e.g., "java.lang.Math"
    String internalName;         // e.g., "java/lang/Math"
    boolean isInterface;         // false for classes, true for interfaces

    // Cache of known static methods (populated on demand)
    Map<String, MethodInfo> staticMethods;

    public JavaTypeInfo(String simpleName, String fullyQualifiedName) {
        this.simpleName = simpleName;
        this.fullyQualifiedName = fullyQualifiedName;
        this.internalName = fullyQualifiedName.replace('.', '/');
        this.staticMethods = new HashMap<>();
    }
}

public static class MethodInfo {
    String name;                 // e.g., "floor"
    String descriptor;           // e.g., "(D)D"
    boolean isStatic;            // true for static methods
}
```

#### ByteCodeCompilerMemory Integration

```java
public final class ByteCodeCompilerMemory {
    private final ScopedJavaClassRegistry scopedJavaClassRegistry;  // NEW

    public ByteCodeCompilerMemory() {
        // ... existing initialization ...
        scopedJavaClassRegistry = new ScopedJavaClassRegistry();
    }

    public ScopedJavaClassRegistry getScopedJavaClassRegistry() {
        return scopedJavaClassRegistry;
    }

    public void reset() {
        compilationContext.reset();
        scopedTypeRegistry.clear();
        typeRegistry.clear();
        typeAliasMap.clear();
        scopedJavaClassRegistry.clear();  // NEW
    }
}
```

#### Compilation Flow with Scoped Registry

Update `ByteCodeCompiler17.compileProgram()`:

```java
@Override
Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException {
    Map<String, byte[]> byteCodeMap = new HashMap<>();

    // Enter file-level scope
    memory.getScopedJavaClassRegistry().enterScope();

    try {
        if (program instanceof Swc4jAstModule module) {
            // First pass: Process imports (registers in current scope)
            importDeclProcessor.processImports(module.getBody());

            // Collect type aliases and declarations
            typeAliasCollector.collectFromModuleItems(module.getBody());
            memory.getTypeRegistry().collectFromModuleItems(module.getBody(), options.packagePrefix());

            // Second pass: Generate bytecode
            astProcessor.processModuleItems(module.getBody(), options.packagePrefix(), byteCodeMap);
        } else if (program instanceof Swc4jAstScript script) {
            // Similar for Script
            importDeclProcessor.processImports(script.getBody());
            typeAliasCollector.collectFromStmts(script.getBody());
            memory.getTypeRegistry().collectFromStmts(script.getBody(), options.packagePrefix());
            astProcessor.processStmts(script.getBody(), options.packagePrefix(), byteCodeMap);
        }

        return byteCodeMap;
    } finally {
        // Exit file-level scope (automatic cleanup, even on exceptions)
        memory.getScopedJavaClassRegistry().exitScope();
    }
}
```

**Benefits of Scoped Approach**:
- ✅ **Consistency**: Mirrors existing `ScopedTypeRegistry` pattern
- ✅ **Automatic cleanup**: Scope exit removes all imports from that scope
- ✅ **Exception-safe**: `finally` block ensures cleanup even on errors
- ✅ **Extensible**: Can support nested scopes in the future
- ✅ **Simple API**: Just `enterScope()` / `exitScope()`
- ✅ **No state tracking**: No need for isPersistent flags or dual registries

**Why This Approach is Better**:
1. **Architectural consistency** - Uses the same pattern as `ScopedTypeRegistry`
2. **Less error-prone** - No beginFile/endFile methods to forget
3. **More flexible** - Can handle nested scopes if needed in the future
4. **Simpler code** - Single registry instead of global + file registries
5. **Cleaner semantics** - Scope management is a well-understood pattern

### Summary: File Isolation Architecture

**Key Design Decision**: Use `ScopedJavaClassRegistry` with scope-based isolation (mirroring `ScopedTypeRegistry`).

**Data Flow**:
```
1. User calls: compiler.compile(fileA_code)
   ↓
2. compileProgram() calls: scopedJavaClassRegistry.enterScope()
   - Pushes new scope onto stack
   - All imports will be registered in this scope
   ↓
3. importDeclProcessor.processImports(...)
   - Registers imports in current scope
   - scopedJavaClassRegistry.registerClass(alias, classInfo)
   ↓
4. astProcessor generates bytecode
   - Lookups use scopedJavaClassRegistry.resolve(className)
   - Searches from innermost scope outward
   - Only sees imports from current file scope + global scope
   ↓
5. compileProgram() calls: scopedJavaClassRegistry.exitScope() (in finally)
   - Pops current scope from stack
   - All imports in that scope are automatically removed
   ↓
6. User calls: compiler.compile(fileB_code)
   - enterScope() creates new clean scope
   - fileA imports are gone (popped off stack)
```

**Guarantees**:
- ✅ Imports in file A are invisible to file B (different scopes)
- ✅ Same compiler instance can compile multiple files safely
- ✅ Each file has clean import namespace (separate scope)
- ✅ Exceptions don't cause import leakage (finally block ensures exitScope)
- ✅ Consistent with existing `ScopedTypeRegistry` pattern

### Testing File Isolation

Add test cases to verify import isolation:

```java
@Test
public void testImportIsolationBetweenFiles() throws Exception {
    ByteCodeCompiler compiler = new ByteCodeCompiler17(options);

    // File 1: Import Math
    Map<String, byte[]> file1 = compiler.compile("""
        import { Math } from 'java.lang'
        namespace com {
          export class A {
            test(): int { return Math.floor(3.7) }
          }
        }
    """);

    // File 2: Should NOT see Math from File 1
    assertThrows(Swc4jByteCodeCompilerException.class, () -> {
        compiler.compile("""
            namespace com {
              export class B {
                test(): int { return Math.floor(5.2) }  // ERROR: Math not imported
              }
            }
        """);
    });

    // File 3: Can import Math independently
    Map<String, byte[]> file3 = compiler.compile("""
        import { Math } from 'java.lang'
        namespace com {
          export class C {
            test(): int { return Math.floor(7.9) }
          }
        }
    """);

    // All three classes should be compilable independently
    assertNotNull(file1.get("com.A"));
    assertNotNull(file3.get("com.C"));
}
```

## Implementation Plan

### Phase 0: File-Level Import Isolation (Foundation)

**Must be implemented FIRST** before Phase 1.

#### 0.1 Create ScopedJavaClassRegistry
**Location**: `src/main/java/com/caoccao/javet/swc4j/compiler/memory/ScopedJavaClassRegistry.java`

- Implement stack-based scope management (like `ScopedTypeRegistry`)
- Add `enterScope()` method to push new scope
- Add `exitScope()` method to pop current scope
- Add `registerClass(String alias, JavaTypeInfo info)` in current scope
- Add `resolve(String className)` to search scopes (innermost to outermost)
- Add `isImportedJavaClass(String className)` convenience method
- Add `getClassInfo(String className)` convenience method
- Add `getScopeDepth()` for debugging
- Add `clear()` to reset to initial state
- Add comprehensive javadoc explaining scope semantics

**Implementation Notes**:
- Use `Stack<Map<String, JavaTypeInfo>>` for scope storage
- Global scope (index 0) is always present and never popped
- File scope is typically depth 2 (global + file)
- Future: Could support nested scopes for modules within files

#### 0.2 Create JavaTypeInfo and MethodInfo Classes
**Location**: Same file as `ScopedJavaClassRegistry` (nested static classes)

```java
public static class JavaTypeInfo {
    String simpleName;
    String fullyQualifiedName;
    String internalName;
    boolean isInterface;
    Map<String, MethodInfo> staticMethods;

    public JavaTypeInfo(String simpleName, String fullyQualifiedName) { ... }
}

public static class MethodInfo {
    String name;
    String descriptor;
    boolean isStatic;
}
```

#### 0.3 Update ByteCodeCompilerMemory
- Add `scopedJavaClassRegistry` field (new instance in constructor)
- Add `getScopedJavaClassRegistry()` getter
- Update `reset()` method to call `scopedJavaClassRegistry.clear()`
- Add javadoc explaining the scoped Java class registry

#### 0.4 Update ByteCodeCompiler17.compileProgram()
**Key Changes**:
```java
@Override
Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) {
    // Enter file-level scope
    memory.getScopedJavaClassRegistry().enterScope();

    try {
        // Process imports (registers in current scope)
        // ... compilation logic ...
        return byteCodeMap;
    } finally {
        // Exit file-level scope (automatic cleanup)
        memory.getScopedJavaClassRegistry().exitScope();
    }
}
```

**Important**: The try/finally ensures scope cleanup even on exceptions.

#### 0.5 Update ByteCodeCompiler Constructor
Add initialization for `ScopedJavaClassRegistry`:
```java
ByteCodeCompiler(ByteCodeCompilerOptions options) {
    // ... existing initialization ...
    // ScopedJavaClassRegistry is already created in ByteCodeCompilerMemory
    // No additional setup needed here
}
```

#### 0.6 Add Unit Tests for ScopedJavaClassRegistry
**Location**: `src/test/java/com/caoccao/javet/swc4j/compiler/memory/TestScopedJavaClassRegistry.java`

Test cases:
- `testEnterExitScope()` - Verify scope depth changes
- `testRegisterAndResolve()` - Register in scope, resolve correctly
- `testScopeIsolation()` - Register in scope 1, exit, should not be visible
- `testNestedScopes()` - Test multiple nested scopes
- `testResolveSearchesOutward()` - Inner scope can see outer scope registrations
- `testCannotExitGlobalScope()` - Verify global scope is always present
- `testClear()` - Verify clear resets to initial state

#### 0.7 Add Integration Tests for File Isolation
**Location**: `src/test/java/com/caoccao/javet/swc4j/compiler/TestJavaImportIsolation.java`

Test cases:
- Test that imports don't leak between files
- Test that same compiler can compile multiple files with different imports
- Test that errors in one file don't affect subsequent compilations
- Test that global scope (if any) is accessible from all files

### Phase 1: Import Declaration Processing

#### 1.1 Create ImportDeclProcessor
**Location**: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ImportDeclProcessor.java`

**Responsibilities**:
- Process `Swc4jAstImportDecl` nodes from the AST
- Extract import specifiers and source module
- Determine if source is a Java package (e.g., `'java.lang'`, `'java.util'`)
- Register imported Java types in **file-scoped** compiler memory (NOT global)

**Key Methods**:
```java
public class ImportDeclProcessor extends BaseAstProcessor<Swc4jAstImportDecl> {
    /**
     * Process an import declaration
     * Example: import { Math, String } from 'java.lang'
     */
    public void processImportDecl(Swc4jAstImportDecl importDecl)
        throws Swc4jByteCodeCompilerException;

    /**
     * Check if a module source is a Java package
     * Examples: 'java.lang', 'java.util', 'java.io'
     */
    private boolean isJavaPackage(String source);

    /**
     * Register a Java type import in compiler memory
     * Maps alias -> fully qualified class name
     */
    private void registerJavaTypeImport(String alias, String packageName, String className);
}
```

**Algorithm**:
```
1. Parse importDecl to get source (e.g., 'java.lang')
2. Check if source is a Java package:
   - Starts with known Java prefixes: 'java.', 'javax.', 'com.', etc.
   - OR matches a registered Java package pattern
3. For each named specifier { Class1, Class2 }:
   a. Extract the class name (local name in the specifier)
   b. Build fully qualified name: source + '.' + className
      Example: 'java.lang' + '.' + 'Math' = 'java.lang.Math'
   c. Register in ScopedTypeRegistry (file-scoped):
      scopedTypeRegistry.registerAlias(className, fullyQualifiedName)
   d. Register in FILE JavaClassRegistry (NOT global):
      memory.getJavaClassRegistry().registerClass(className, fullyQualifiedName)
      // This uses the file-scoped registry created by beginFile()

IMPORTANT: Always use memory.getJavaClassRegistry() which returns the file-scoped
registry during compilation. Never directly access globalJavaClassRegistry for imports.
```

**Integration Point**:
- Call from `AstProcessor` or `ClassGenerator` when processing module-level statements
- Process all imports at the beginning of module compilation
- Must run before any class or method processing

#### 1.2 Use ScopedJavaClassRegistry (Already Created in Phase 0)
**No new file needed** - `ScopedJavaClassRegistry` was created in Phase 0.

The registry is already integrated into `ByteCodeCompilerMemory` and provides:
- `registerClass(String alias, JavaTypeInfo info)` - Register in current scope
- `resolve(String className)` - Find class info by searching scopes
- `isImportedJavaClass(String name)` - Check if class is imported
- `getClassInfo(String className)` - Get class metadata

**Usage in ImportDeclProcessor**:
```java
// Register a Java class import
JavaTypeInfo classInfo = new JavaTypeInfo(className, fullyQualifiedName);
compiler.getMemory()
    .getScopedJavaClassRegistry()
    .registerClass(className, classInfo);
```

#### 1.3 ByteCodeCompilerMemory Already Updated
In Phase 0, we already added `ScopedJavaClassRegistry` to `ByteCodeCompilerMemory`:
```java
public final class ByteCodeCompilerMemory {
    private final ScopedJavaClassRegistry scopedJavaClassRegistry;

    public ScopedJavaClassRegistry getScopedJavaClassRegistry() {
        return scopedJavaClassRegistry;
    }
}
```

No additional changes needed in this phase.

#### 1.4 Call ImportDeclProcessor During Compilation
Update `ByteCodeCompiler17.compileProgram()` to process imports in the first pass:

```java
@Override
Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException {
    Map<String, byte[]> byteCodeMap = new HashMap<>();

    // NEW: Begin file-scoped compilation context
    memory.beginFile();

    try {
        if (program instanceof Swc4jAstModule module) {
            // First pass: Process imports, collect type aliases and declarations
            importDeclProcessor.processImports(module.getBody());  // NEW: Process imports FIRST
            typeAliasCollector.collectFromModuleItems(module.getBody());
            memory.getTypeRegistry().collectFromModuleItems(module.getBody(), options.packagePrefix());

            // Second pass: Generate bytecode
            astProcessor.processModuleItems(module.getBody(), options.packagePrefix(), byteCodeMap);
        } else if (program instanceof Swc4jAstScript script) {
            // Similar for Script
            importDeclProcessor.processImports(script.getBody());  // NEW
            typeAliasCollector.collectFromStmts(script.getBody());
            memory.getTypeRegistry().collectFromStmts(script.getBody(), options.packagePrefix());
            astProcessor.processStmts(script.getBody(), options.packagePrefix(), byteCodeMap);
        }

        return byteCodeMap;
    } finally {
        // NEW: Clean up file-scoped imports (runs even on exception)
        memory.endFile();
    }
}
```

**Key Points**:
- `beginFile()` creates a fresh file-scoped import context
- Process imports BEFORE type collection (imports may affect type resolution)
- `endFile()` in `finally` ensures cleanup even on errors
- Each `compile()` call is fully isolated from previous calls

### Phase 2: Member Expression Type Inference

#### 2.1 Enhance TypeResolver
**Location**: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/TypeResolver.java`

Update `inferTypeFromExpr` to recognize imported Java classes:

```java
public String inferTypeFromExpr(ISwc4jAstExpr expr) throws Swc4jByteCodeCompilerException {
    // ... existing logic ...

    // NEW: Handle identifiers that refer to imported Java classes
    if (expr instanceof Swc4jAstIdent ident) {
        String name = ident.getSym();

        // Check if this is an imported Java class
        if (compiler.getMemory().getJavaClassRegistry().isImportedJavaClass(name)) {
            JavaTypeInfo classInfo = compiler.getMemory()
                .getJavaClassRegistry().getClassInfo(name);
            // Return the class type (not instance type)
            return "Ljava/lang/Class;";  // Or a custom marker type
        }

        // ... existing identifier resolution ...
    }

    // ... rest of existing logic ...
}
```

#### 2.2 Infer Return Types for Java Static Methods
When `Math.floor(x)` is encountered, we need to infer that it returns `double`:

```java
// In inferTypeFromExpr, handle member expressions on Java classes
if (expr instanceof Swc4jAstMemberExpr memberExpr) {
    // Check if object is an imported Java class
    if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent) {
        String objName = objIdent.getSym();

        if (compiler.getMemory().getJavaClassRegistry().isImportedJavaClass(objName)) {
            JavaTypeInfo classInfo = compiler.getMemory()
                .getJavaClassRegistry().getClassInfo(objName);

            // Get method name from member expression
            String methodName = getMemberName(memberExpr.getProp());

            // Use reflection to get method info
            MethodInfo methodInfo = getMethodInfo(classInfo, methodName);
            if (methodInfo != null) {
                return parseReturnType(methodInfo.descriptor);
            }
        }
    }
}
```

**Helper**: Use Java Reflection to get method signatures:
```java
private MethodInfo getMethodInfo(JavaTypeInfo classInfo, String methodName) {
    try {
        Class<?> clazz = Class.forName(classInfo.fullyQualifiedName);

        // Find all methods with this name (may be overloaded)
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) &&
                Modifier.isStatic(method.getModifiers())) {
                // Found a static method with this name
                // Convert to method descriptor
                String descriptor = getMethodDescriptor(method);
                return new MethodInfo(methodName, descriptor, true);
            }
        }
    } catch (ClassNotFoundException e) {
        // Handle error
    }
    return null;
}

private String getMethodDescriptor(Method method) {
    // Convert Method to JVM descriptor format
    // Example: double floor(double) -> "(D)D"
    StringBuilder desc = new StringBuilder("(");
    for (Class<?> paramType : method.getParameterTypes()) {
        desc.append(getTypeDescriptor(paramType));
    }
    desc.append(")");
    desc.append(getTypeDescriptor(method.getReturnType()));
    return desc.toString();
}
```

### Phase 3: Static Method Call Generation

#### 3.1 Create JavaStaticMethodCallGenerator
**Location**: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/JavaStaticMethodCallGenerator.java`

**Responsibilities**:
- Detect calls to Java static methods (e.g., `Math.floor(x)`)
- Generate `invokestatic` bytecode for these calls
- Handle type conversions between JavaScript and Java types

**Key Method**:
```java
public boolean tryGenerateJavaStaticCall(
    CodeBuilder code,
    ClassWriter.ConstantPool cp,
    Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {

    // Check if callee is a member expression on imported Java class
    if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
        if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent) {
            String className = objIdent.getSym();

            if (compiler.getMemory().getJavaClassRegistry()
                    .isImportedJavaClass(className)) {

                // This is a Java static method call!
                JavaTypeInfo classInfo = compiler.getMemory()
                    .getJavaClassRegistry().getClassInfo(className);
                String methodName = getMemberName(memberExpr.getProp());

                // Get method info via reflection
                MethodInfo methodInfo = getMethodInfo(classInfo, methodName);

                // Generate arguments
                for (ExprOrSpread arg : callExpr.getArgs()) {
                    compiler.getExpressionGenerator()
                        .generate(code, cp, arg.getExpr(), null);

                    // TODO: Type conversion if needed
                    // JavaScript number -> Java int/long/float/double
                }

                // Generate invokestatic
                int methodRef = cp.addMethodRef(
                    classInfo.internalName,
                    methodName,
                    methodInfo.descriptor
                );
                code.invokestatic(methodRef);

                return true;  // Successfully generated
            }
        }
    }

    return false;  // Not a Java static call
}
```

#### 3.2 Integrate into CallExpressionGenerator
Update `CallExpressionGenerator.generate()`:

```java
@Override
public void generate(...) throws Swc4jByteCodeCompilerException {
    // NEW: Try to generate Java static method call first
    if (javaStaticMethodCallGenerator.tryGenerateJavaStaticCall(code, cp, callExpr)) {
        return;  // Successfully generated
    }

    // ... existing logic for regular method calls ...
}
```

### Phase 4: Type Conversion

#### 4.1 JavaScript ↔ Java Type Mapping
When calling Java methods from TypeScript, handle type conversions:

**Primitive Types**:
- JavaScript `number` → Java `int`, `long`, `float`, or `double` (context-dependent)
- JavaScript `boolean` → Java `boolean`
- JavaScript `string` → Java `String`

**Conversion Strategy**:
```java
private void convertJsToJavaType(
    CodeBuilder code,
    ClassWriter.ConstantPool cp,
    String jsType,
    String javaType) {

    // JavaScript numbers are always double in bytecode
    if (jsType.equals("D")) {  // double
        if (javaType.equals("I")) {
            // double -> int
            code.d2i();
        } else if (javaType.equals("J")) {
            // double -> long
            code.d2l();
        } else if (javaType.equals("F")) {
            // double -> float
            code.d2f();
        }
        // else: already double, no conversion needed
    }
}
```

#### 4.2 Method Overload Resolution
Java methods can be overloaded. When calling `Math.floor(x)`:
- `Math.floor(double)` exists
- We need to choose the right overload based on argument types

**Strategy**:
1. Use reflection to get all methods with the given name
2. For each overload, check if argument types match
3. Select the best match (exact match > widening conversion > error)

### Phase 5: Testing and Edge Cases

#### 5.1 Test Cases to Add

**Basic Static Method Call**:
```typescript
import { Math } from 'java.lang'
const result = Math.floor(3.7)  // Should be 3
```

**Multiple Imports**:
```typescript
import { Math, String } from 'java.lang'
const s = String.valueOf(42)
const n = Math.abs(-5)
```

**Chained Calls**:
```typescript
import { Math } from 'java.lang'
const result = Math.floor(Math.sqrt(16))  // Should be 4
```

**Star Imports** (future):
```typescript
import * as Lang from 'java.lang'
const result = Lang.Math.floor(3.7)
```

#### 5.2 Error Handling

**Import Validation**:
- Error if imported class doesn't exist: `import { Foo } from 'java.lang'`
- Error if method doesn't exist: `Math.nonexistent()`
- Error if method signature doesn't match arguments

**Runtime Considerations**:
- Ensure Java classes are available on classpath
- Handle `NoClassDefFoundError` gracefully

### Phase 6: Documentation and Examples

#### 6.1 Update User Documentation
Add section on Java interop:
- How to import Java classes
- Supported packages
- Type conversion rules
- Limitations (no instance creation yet, only static methods)

#### 6.2 Add More Test Examples
- Different Java packages: `java.util`, `java.io`
- Different method types: void methods, methods with multiple parameters
- Error cases: missing imports, wrong method names

## Implementation Order

**CRITICAL**: Phase 0 must be implemented first to prevent import leakage between files.

1. **Phase 0** (File Isolation): File-scoped import context with beginFile/endFile lifecycle
2. **Phase 1** (Foundation): ImportDeclProcessor + JavaClassRegistry
3. **Phase 2** (Type System): TypeResolver enhancements
4. **Phase 3** (Code Gen): JavaStaticMethodCallGenerator
5. **Phase 4** (Conversions): Type conversion utilities
6. **Phase 5** (Testing): Comprehensive test suite including multi-file isolation tests
7. **Phase 6** (Documentation): User guides and examples

## Success Criteria

✅ `TestPrimeNumber` passes with correct behavior
✅ Can import and call Java static methods from standard library
✅ Type inference works correctly for Java method return types
✅ Proper bytecode generated (`invokestatic` with correct descriptors)
✅ Error messages are clear when imports or methods don't exist
✅ **File isolation works**: Imports in one file don't leak to another file
✅ **Multiple compilation safety**: Same compiler can compile multiple files without interference

## Future Enhancements

- **Instance Creation**: `new String("hello")` via constructor calls
- **Instance Methods**: Creating Java objects and calling instance methods
- **Field Access**: Reading static fields like `Math.PI`
- **Inheritance**: TypeScript classes extending Java classes
- **Generics**: Handling Java generic types
- **Package Wildcards**: `import * from 'java.lang'` to import all classes

## Technical Notes

### Reflection vs. Static Analysis
- Use Java reflection at compile time to discover method signatures
- Cache method info in `JavaClassRegistry` to avoid repeated reflection
- Consider pre-loading commonly used classes (java.lang.*)

### Scope Management
- Java imports are module-scoped (top-level)
- Use `ScopedTypeRegistry.enterScope()` / `exitScope()` for proper cleanup
- Global scope should contain all top-level imports

### Bytecode Generation
- Static method calls: `invokestatic <class> <method> <descriptor>`
- Class references: Use internal names with slashes (java/lang/Math)
- Method descriptors: Follow JVM spec (e.g., "(D)D" for double→double)

### Type Safety
- TypeScript type annotations guide Java type selection
- `number` type may map to int, long, float, or double based on context
- Consider adding explicit type hints: `Math.floor(x as double)`
