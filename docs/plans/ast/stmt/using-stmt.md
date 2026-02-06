# Using Declaration Implementation Plan

## Overview

This document outlines the implementation plan for supporting `Swc4jAstUsingDecl` (using declarations) in TypeScript to JVM bytecode compilation. The `using` declaration provides deterministic resource management similar to Java's try-with-resources, ensuring that resources are properly disposed of when they go out of scope.

**Current Status:** IMPLEMENTED (All phases complete)

**Strategy:** TypeScript `using` declarations will be compiled to JVM try-finally bytecode structures, mirroring Java's try-with-resources pattern. Each `using` resource is wrapped in a try block whose finally clause calls `close()` on the resource. The resource must implement `java.lang.AutoCloseable`. The `await using` variant is not supported (async is out of scope).

**Syntax:**
```typescript
// Single resource
using resource = new SomeCloseable()

// Multiple resources in one declaration
using a = createA(), b = createB()

// Expression as resource (existing variable)
using resource = existingCloseable

// Nested using statements
using outer = createOuter()
{
  using inner = createInner()
  inner.doWork()
}
outer.doWork()

// Using in a block scope
{
  using conn = openConnection()
  conn.query("SELECT 1")
}
// conn.close() has been called here
```

**Implementation Files:**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/UsingDeclProcessor.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/memory/UsingResourceInfo.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/usingstmt/BaseTestCompileAstUsingStmt.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/usingstmt/TestCompileAstUsingStmtBasic.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/usingstmt/TestCompileAstUsingStmtControlFlow.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/usingstmt/TestCompileAstUsingStmtMultiple.java`

**AST Definition:** [Swc4jAstUsingDecl.java](../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstUsingDecl.java)

---

## AST Structure

### Swc4jAstUsingDecl

```java
public class Swc4jAstUsingDecl extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstVarDeclOrExpr, ISwc4jAstForHead {
    // The list of variable declarators (resources)
    protected final List<Swc4jAstVarDeclarator> decls;

    // Whether this is `await using` (async disposal)
    protected boolean _await;
}
```

### Swc4jAstVarDeclarator (Resource Entry)

Each declarator in the `decls` list represents one resource:

```java
public class Swc4jAstVarDeclarator extends Swc4jAst implements ISwc4jAstDecl {
    // The variable name pattern (e.g., BindingIdent "resource")
    protected ISwc4jAstPat name;

    // The initializer expression (e.g., new SomeCloseable())
    protected Optional<ISwc4jAstExpr> init;

    // TypeScript definite assignment assertion
    protected boolean definite;
}
```

### Interfaces Implemented

| Interface              | Purpose                                                          |
| ---------------------- | ---------------------------------------------------------------- |
| `ISwc4jAstDecl`       | Marks as a declaration (can appear as a statement)               |
| `ISwc4jAstVarDeclOrExpr` | Can be used where var declarations or expressions are expected |
| `ISwc4jAstForHead`    | Can appear in for-loop head (`for (using x = ...; ...; ...)`)   |

### Example AST

For `using conn = openConnection()`:

```
UsingDecl (await=false)
  └─ VarDeclarator
      ├─ name: BindingIdent
      │   └─ Ident (sym="conn")
      └─ init: CallExpr
          └─ callee: Ident (sym="openConnection")
```

For `using a = createA(), b = createB()`:

```
UsingDecl (await=false)
  ├─ VarDeclarator
  │   ├─ name: BindingIdent → Ident (sym="a")
  │   └─ init: CallExpr → Ident (sym="createA")
  └─ VarDeclarator
      ├─ name: BindingIdent → Ident (sym="b")
      └─ init: CallExpr → Ident (sym="createB")
```

---

## JVM Bytecode Strategy

### Mapping to try-finally

The `using` declaration maps to Java's try-with-resources, which the JVM lowers to try-finally. The key invariant is that `close()` is called on each resource when the enclosing scope exits, whether normally or via exception.

**Java try-with-resources desugaring pattern:**

```java
// Source: try (Resource r = expr) { body }
// Desugared:
Resource r = expr;
Throwable $primaryExc = null;
try {
    body
} catch (Throwable t) {
    $primaryExc = t;
    throw t;
} finally {
    if (r != null) {
        if ($primaryExc != null) {
            try { r.close(); } catch (Throwable t) { $primaryExc.addSuppressed(t); }
        } else {
            r.close();
        }
    }
}
```

### Simplified Strategy (Phase 1)

For the initial implementation, use a simplified try-finally pattern without suppressed exception handling:

```
// For: using resource = expr
//      <body statements until end of scope>

// Step 1: Evaluate and store resource
[evaluate expr]
astore <resource_var>           // Store resource in local variable

// Step 2: Begin try block
try_start:
[body statements]               // All statements until end of enclosing scope
[finally code - inline copy 1]  // Normal path: call close()
goto after_finally

// Step 3: Exception handler
try_end:
catch_all:
astore <exception_var>          // Store exception
[finally code - inline copy 2]  // Exception path: call close()
aload <exception_var>
athrow                          // Re-throw exception

after_finally:
```

**Finally Code (close call):**
```
aload <resource_var>            // Load resource
ifnull skip_close               // Skip if null
aload <resource_var>
invokeinterface java/lang/AutoCloseable.close:()V
skip_close:
```

### Multiple Resources

Multiple resources in a single `using` declaration are closed in reverse declaration order:

```typescript
using a = createA(), b = createB()
// body
```

Compiles to nested try-finally:

```
[evaluate createA()]
astore <a>
try_start_a:
  [evaluate createB()]
  astore <b>
  try_start_b:
    [body]
    // close b (inline finally 1)
    goto after_b
  try_end_b:
  catch_b:
    astore <exc>
    // close b (inline finally 2)
    aload <exc>
    athrow
  after_b:
  // close a (inline finally 1)
  goto after_a
try_end_a:
catch_a:
  astore <exc>
  // close a (inline finally 2)
  aload <exc>
  athrow
after_a:
```

### Full Strategy (Phase 3) - With Suppressed Exceptions

The full try-with-resources pattern adds suppressed exception handling:

```
[evaluate expr]
astore <resource>
aconst_null
astore <primaryExc>                  // Throwable $primaryExc = null

try_start:
[body]
goto close_resource                  // Normal exit

catch_body:                          // catch (Throwable t)
astore <primaryExc>                  // $primaryExc = t
aload <primaryExc>
athrow                               // throw t

catch_all:                           // finally equivalent
astore <tempExc>
// Close resource with suppressed exception handling
aload <resource>
ifnull skip_close
aload <primaryExc>
ifnull close_no_suppress
// Has primary exception - wrap close in try-catch for suppression
try_suppress_start:
aload <resource>
invokeinterface AutoCloseable.close:()V
goto skip_close
try_suppress_end:
catch_suppress:
astore <closeExc>
aload <primaryExc>
aload <closeExc>
invokevirtual Throwable.addSuppressed:(Ljava/lang/Throwable;)V
goto skip_close

close_no_suppress:
aload <resource>
invokeinterface AutoCloseable.close:()V
skip_close:
aload <tempExc>
athrow

close_resource:                      // Normal path close
aload <resource>
ifnull done
aload <resource>
invokeinterface AutoCloseable.close:()V
done:

Exception table:
  1. try_start..try_end -> catch_body (Throwable)
  2. try_start..catch_all_end -> catch_all (any)
  3. try_suppress_start..try_suppress_end -> catch_suppress (Throwable)
```

---

## AutoCloseable Type Checking

### Compile-Time Validation

The compiler must verify that the resource type implements `java.lang.AutoCloseable`:

1. **Resolve the type** of the initializer expression using `TypeResolver`
2. **Check if the resolved type** implements `AutoCloseable`
3. **Reject at compile time** if the type does not implement `AutoCloseable`

### Type Resolution Cases

| Resource Expression | Type Resolution Strategy |
| ------------------- | ----------------------- |
| `new SomeClass()`   | Look up `SomeClass` in class registry, check implements `AutoCloseable` |
| `methodCall()`      | Infer return type, check implements `AutoCloseable` |
| `existingVar`       | Look up variable type, check implements `AutoCloseable` |
| Untyped/unknown     | Reject with compile error |

### Error Message

```
Compile error at line X, column Y: Resource type 'SomeClass' does not implement java.lang.AutoCloseable.
The 'using' declaration requires resources that implement AutoCloseable.
```

---

## Implementation Phases

### Phase 1: Basic Using Declaration (Priority: HIGH)

Support single `using` declarations with simple try-finally pattern (no suppressed exceptions).

**Scope:**
- Single resource per `using` declaration
- Resource must implement `AutoCloseable`
- Resource initialized with `new` expression or method call
- Null-check before calling `close()`
- Compile-time type checking for `AutoCloseable`
- Proper exception table generation
- Stack map frame generation

**Example Bytecode:**
```
// using conn = new Connection()
// conn.query("SELECT 1")
// return conn.getResult()

new Connection
dup
invokespecial Connection.<init>:()V
astore_1                              // Store resource

try_start:
aload_1                               // conn
ldc "SELECT 1"
invokevirtual Connection.query:(Ljava/lang/String;)V
aload_1                               // conn
invokevirtual Connection.getResult:()Ljava/lang/Object;

// Inline finally (normal path)
astore_2                              // Save return value
aload_1                               // Load resource
ifnull skip_close_1
aload_1
invokeinterface java/lang/AutoCloseable.close:()V
skip_close_1:
aload_2                               // Restore return value
areturn

try_end:
catch_all:
astore_3                              // Store exception
aload_1                               // Load resource
ifnull skip_close_2
aload_1
invokeinterface java/lang/AutoCloseable.close:()V
skip_close_2:
aload_3                               // Re-throw
athrow

Exception table:
  try_start..try_end -> catch_all (catch_type=0)
```

**Test Coverage:**
1. Basic using with AutoCloseable resource - verify `close()` is called
2. Using with resource that returns a value before scope exit
3. Using with void resource usage
4. Using with null resource (should not throw NullPointerException on close)
5. Using where body throws exception - verify `close()` is still called
6. Using where body throws and close() also throws
7. Using with resource created by method call
8. Using with resource created by `new` expression
9. Compile-time error when resource type does not implement AutoCloseable
10. Using with resource assigned from existing variable

---

### Phase 2: Multiple Resources (Priority: HIGH)

Support multiple resources in a single `using` declaration and nested `using` statements.

**Scope:**
- Multiple declarators in a single `using` (e.g., `using a = createA(), b = createB()`)
- Close in reverse declaration order
- Nested `using` statements (using inside using scope)
- Each resource has its own try-finally wrapper
- Exception in one close does not prevent others from closing

**Example Bytecode:**
```
// using a = createA(), b = createB()
// doWork(a, b)

[evaluate createA()]
astore_1                              // a

try_start_a:
  [evaluate createB()]
  astore_2                            // b

  try_start_b:
    aload_1                           // a
    aload_2                           // b
    invokestatic doWork:(LA;LB;)V

    // Inline finally for b (normal path)
    aload_2
    ifnull skip_close_b_1
    aload_2
    invokeinterface AutoCloseable.close:()V
    skip_close_b_1:
    goto after_b

  try_end_b:
  catch_b:
    astore_3
    aload_2
    ifnull skip_close_b_2
    aload_2
    invokeinterface AutoCloseable.close:()V
    skip_close_b_2:
    aload_3
    athrow

  after_b:
  // Inline finally for a (normal path)
  aload_1
  ifnull skip_close_a_1
  aload_1
  invokeinterface AutoCloseable.close:()V
  skip_close_a_1:
  goto after_a

try_end_a:
catch_a:
  astore_3
  aload_1
  ifnull skip_close_a_2
  aload_1
  invokeinterface AutoCloseable.close:()V
  skip_close_a_2:
  aload_3
  athrow

after_a:
```

**Test Coverage:**
1. Two resources - both `close()` called in reverse order
2. Three resources - all closed in reverse order
3. First resource close throws - second resource still closed
4. Second resource init throws - first resource still closed
5. Body throws - all resources closed
6. Nested using statements (using inside using block)
7. Nested using with exception in inner body
8. Nested using with exception in outer body
9. Mixed: using inside try-catch
10. Mixed: try-catch inside using body

---

### Phase 3: Suppressed Exception Support (Priority: MEDIUM)

Implement the full try-with-resources semantics with `addSuppressed()`.

**Scope:**
- Track primary exception thrown from body
- Catch exceptions from `close()` and add as suppressed to primary
- If no primary exception and `close()` throws, propagate close exception
- Multiple resource close exceptions properly chained

**Example Bytecode:**
```
// Full try-with-resources with suppressed exceptions
// using resource = expr
// body

[evaluate expr]
astore <resource>
aconst_null
astore <primaryExc>

try_start:
  [body]
  goto normal_close

catch_body:
  astore <caughtExc>
  aload <caughtExc>
  astore <primaryExc>
  aload <caughtExc>
  athrow

catch_all:
  astore <tempExc>
  // Close with suppression
  aload <resource>
  ifnull rethrow
  aload <primaryExc>
  ifnull close_simple

  // Has primary - suppress close exceptions
  try_close_start:
    aload <resource>
    invokeinterface AutoCloseable.close:()V
    goto rethrow
  try_close_end:
  catch_close:
    astore <closeExc>
    aload <primaryExc>
    aload <closeExc>
    invokevirtual Throwable.addSuppressed:(Ljava/lang/Throwable;)V
    goto rethrow

  close_simple:
    aload <resource>
    invokeinterface AutoCloseable.close:()V

  rethrow:
    aload <tempExc>
    athrow

normal_close:
  aload <resource>
  ifnull done
  aload <resource>
  invokeinterface AutoCloseable.close:()V
done:

Exception table:
  1. try_start..try_end -> catch_body (Throwable)
  2. try_start..catch_all_end -> catch_all (0 = any)
  3. try_close_start..try_close_end -> catch_close (Throwable)
```

**Test Coverage:**
1. Body throws, close succeeds - body exception propagated
2. Body succeeds, close throws - close exception propagated
3. Body throws, close throws - body exception has close as suppressed
4. Multiple resources: body throws, first close throws, second close succeeds - both suppressed
5. Access suppressed exceptions from caught exception
6. Verify `getSuppressed()` array contents
7. Null resource with body exception - no NullPointerException from close
8. Compare behavior with Java try-with-resources

---

### Phase 4: Return, Break, Continue in Using Scope (Priority: MEDIUM)

Handle control flow statements that exit the using scope early.

**Scope:**
- `return` inside using body must close resources before returning
- `break` inside using body in a loop must close resources before breaking
- `continue` inside using body in a loop must close resources before continuing
- `throw` inside using body must close resources (already handled by exception path)
- Interactions with finally blocks from enclosing try-catch-finally

**Example Bytecode:**
```
// for (let i = 0; i < 10; i++) {
//   using resource = create(i)
//   if (resource.isDone()) return resource.getValue()
// }

// Each loop iteration:
[evaluate create(i)]
astore <resource>

try_start:
  aload <resource>
  invokevirtual isDone:()Z
  ifeq continue_loop

  // Early return: must close resource first
  aload <resource>
  invokevirtual getValue:()I  // Get return value
  istore <temp_return>        // Save return value

  // Close resource
  aload <resource>
  ifnull skip_close_return
  aload <resource>
  invokeinterface AutoCloseable.close:()V
  skip_close_return:

  iload <temp_return>
  ireturn                     // Return after close

continue_loop:
  // Normal path: close and continue loop
  aload <resource>
  ifnull skip_close_normal
  aload <resource>
  invokeinterface AutoCloseable.close:()V
  skip_close_normal:
  goto loop_update

try_end:
catch_all:
  astore <exc>
  aload <resource>
  ifnull skip_close_exc
  aload <resource>
  invokeinterface AutoCloseable.close:()V
  skip_close_exc:
  aload <exc>
  athrow
```

**Test Coverage:**
1. Return from using body - resource closed before return
2. Return with value from using body
3. Break from using body inside for loop
4. Continue from using body inside for loop
5. Break from using body inside while loop
6. Return from nested using - both resources closed
7. Using inside try-catch - return in using body
8. Using inside try-finally - return executes both close and finally
9. Labeled break from nested loop with using
10. Labeled continue from nested loop with using

---

### Phase 5: `await using` Rejection (Priority: LOW)

Reject `await using` declarations with a clear compile-time error.

**Scope:**
- Detect `isAwait() == true` on `Swc4jAstUsingDecl`
- Throw `Swc4jByteCodeCompilerException` with clear message
- Include source location in error

**Error Message:**
```
Compile error at line X, column Y: 'await using' is not supported.
Async resource disposal requires async/await support which is not implemented.
Use 'using' (synchronous) instead.
```

**Test Coverage:**
1. `await using` triggers compile error with location
2. Error message includes helpful suggestion

---

## Edge Cases and Special Scenarios

### Resource Initialization Edge Cases

1. **Null Resource**
   ```typescript
   using resource: SomeCloseable = null
   // Should not throw NullPointerException on scope exit
   ```

2. **Resource from Method Call Returning Null**
   ```typescript
   using resource = mayReturnNull()
   // Null check before close()
   ```

3. **Resource Initialization Throws**
   ```typescript
   using resource = throwingFactory()
   // Resource is never assigned, no close() needed
   ```

4. **Multiple Resources - Second Init Throws**
   ```typescript
   using a = createA(), b = throwingFactory()
   // a must still be closed
   ```

5. **Resource with Type Annotation**
   ```typescript
   using resource: Connection = new Connection()
   // Type annotation used for AutoCloseable check
   ```

6. **Resource Without Type Annotation**
   ```typescript
   using resource = new Connection()
   // Type inferred from init expression
   ```

### Scope and Lifetime Edge Cases

7. **Using at Method Top Level**
   ```typescript
   test(): void {
     using conn = openConnection()
     conn.query("SELECT 1")
   }
   // conn.close() called when method returns
   ```

8. **Using in Block Scope**
   ```typescript
   {
     using conn = openConnection()
     conn.query("SELECT 1")
   }
   // conn.close() called at block exit
   ```

9. **Using in If Branch**
   ```typescript
   if (condition) {
     using conn = openConnection()
     conn.query("SELECT 1")
   }
   // conn.close() called at if-block exit
   ```

10. **Using in Loop Body**
    ```typescript
    for (let i = 0; i < 3; i++) {
      using conn = openConnection()
      conn.query("SELECT " + i)
    }
    // conn.close() called each iteration
    ```

11. **Using in While Loop Body**
    ```typescript
    while (hasMore()) {
      using conn = openConnection()
      conn.process()
    }
    // conn.close() called each iteration
    ```

### Control Flow Edge Cases

12. **Return Before Using Resource**
    ```typescript
    using resource = create()
    if (earlyExit) return -1
    return resource.getValue()
    // resource.close() called on both paths
    ```

13. **Throw Inside Using Body**
    ```typescript
    using resource = create()
    throw new Error("fail")
    // resource.close() called despite throw
    ```

14. **Break from Loop with Using**
    ```typescript
    for (let i = 0; i < 10; i++) {
      using resource = create(i)
      if (i == 5) break
      resource.process()
    }
    // resource.close() called before break
    ```

15. **Continue in Loop with Using**
    ```typescript
    for (let i = 0; i < 10; i++) {
      using resource = create(i)
      if (i % 2 == 0) continue
      resource.process()
    }
    // resource.close() called before continue
    ```

16. **Nested Returns**
    ```typescript
    using outer = createOuter()
    {
      using inner = createInner()
      return inner.getValue()
    }
    // Both inner.close() and outer.close() called
    ```

### Exception Handling Edge Cases

17. **Using Inside Try-Catch**
    ```typescript
    try {
      using resource = create()
      resource.riskyOp()
    } catch (e) {
      // resource already closed before catch executes
    }
    ```

18. **Using Inside Try-Finally**
    ```typescript
    try {
      using resource = create()
      resource.work()
    } finally {
      // resource already closed before finally executes
      cleanup()
    }
    ```

19. **Try-Catch Inside Using Body**
    ```typescript
    using resource = create()
    try {
      resource.riskyOp()
    } catch (e) {
      handleError(e)
    }
    // resource.close() called after try-catch completes
    ```

20. **Close Throws Exception**
    ```typescript
    using resource = createBadCloser()
    resource.work()
    // close() throws - exception propagated to caller
    ```

21. **Body and Close Both Throw**
    ```typescript
    using resource = createBadCloser()
    throw new Error("body error")
    // Both body and close throw - body exception has close exception as suppressed
    ```

### Multiple Resources Edge Cases

22. **Two Resources - Normal Flow**
    ```typescript
    using a = createA(), b = createB()
    a.work()
    b.work()
    // b.close() called first, then a.close()
    ```

23. **Two Resources - First Close Throws**
    ```typescript
    using a = createA(), b = createBadCloser()
    a.work()
    b.work()
    // b.close() throws, a.close() still called
    ```

24. **Two Resources - Second Init Throws**
    ```typescript
    using a = createA(), b = throwingInit()
    // b init fails, a.close() still called
    ```

25. **Three Resources in Sequence**
    ```typescript
    using a = createA()
    using b = createB()
    using c = createC()
    work(a, b, c)
    // c.close(), b.close(), a.close() - inner to outer
    ```

### Nested Using Edge Cases

26. **Nested Using Statements**
    ```typescript
    using outer = createOuter()
    {
      using inner = createInner()
      inner.process(outer)
    }
    outer.finish()
    // inner.close() at inner block exit, outer.close() at method exit
    ```

27. **Deeply Nested Using (3 Levels)**
    ```typescript
    using a = create()
    {
      using b = create()
      {
        using c = create()
        work(a, b, c)
      }
    }
    // c.close(), then b.close(), then a.close()
    ```

28. **Using in Both If Branches**
    ```typescript
    if (condition) {
      using a = createA()
      a.work()
    } else {
      using b = createB()
      b.work()
    }
    // Only one resource created and closed depending on condition
    ```

### Type Checking Edge Cases

29. **Non-AutoCloseable Resource**
    ```typescript
    using resource = new NonCloseable()
    // Compile error: NonCloseable does not implement AutoCloseable
    ```

30. **Interface Type That Extends AutoCloseable**
    ```typescript
    using resource: Closeable = createCloseable()
    // Valid: Closeable extends AutoCloseable
    ```

31. **Resource with close() But Not AutoCloseable**
    ```typescript
    using resource = createWithClose()
    // Compile error: having close() method is not sufficient, must implement AutoCloseable
    ```

### Variable Access Edge Cases

32. **Access Resource After Using Scope**
    ```typescript
    {
      using resource = create()
      resource.work()
    }
    // resource is out of scope here - compile error if accessed
    ```

33. **Shadowing Resource Variable**
    ```typescript
    using resource = createA()
    {
      using resource = createB()
      resource.work()  // Refers to inner resource
    }
    // Inner resource closed, outer resource still open until method exit
    ```

34. **Resource Used in Expression**
    ```typescript
    using resource = create()
    const value = resource.getValue() + resource.getBonus()
    return value
    // resource.close() called before method returns
    ```

### Integration Edge Cases

35. **Using with For Loop**
    ```typescript
    for (let i = 0; i < 3; i++) {
      using conn = createConnection(i)
      conn.execute()
    }
    // Each iteration: create, execute, close
    ```

36. **Using with While Loop and Break**
    ```typescript
    while (true) {
      using resource = getNext()
      if (resource.isDone()) break
      resource.process()
    }
    // resource.close() called before break
    ```

37. **Using with Switch Statement**
    ```typescript
    using resource = create(type)
    switch (type) {
      case 1: return resource.handleA()
      case 2: return resource.handleB()
      default: return resource.handleDefault()
    }
    // resource.close() called before each return
    ```

38. **Using Inside Catch Block**
    ```typescript
    try {
      riskyOp()
    } catch (e) {
      using resource = createErrorHandler()
      resource.handle(e)
    }
    // resource.close() at catch block exit
    ```

39. **Using Inside Finally Block**
    ```typescript
    try {
      riskyOp()
    } finally {
      using resource = createCleaner()
      resource.cleanup()
    }
    // resource.close() at finally block exit
    ```

40. **Using with Labeled Break**
    ```typescript
    outer: for (let i = 0; i < 10; i++) {
      using resource = create(i)
      for (let j = 0; j < 10; j++) {
        if (i * j > 50) break outer
      }
    }
    // resource.close() called before labeled break exits outer loop
    ```

---

## Bytecode Instruction Reference

### Key Instructions

| Instruction         | Opcode | Description                              |
| ------------------- | ------ | ---------------------------------------- |
| `aload`             | 0x19   | Load reference from local variable       |
| `astore`            | 0x3a   | Store reference into local variable      |
| `aconst_null`       | 0x01   | Push null reference                      |
| `ifnull`            | 0xc6   | Branch if reference is null              |
| `ifnonnull`         | 0xc7   | Branch if reference is not null          |
| `invokeinterface`   | 0xb9   | Invoke interface method (AutoCloseable.close) |
| `invokevirtual`     | 0xb6   | Invoke virtual method (addSuppressed)    |
| `athrow`            | 0xbf   | Throw exception                          |
| `goto`              | 0xa7   | Unconditional branch                     |
| `dup`               | 0x59   | Duplicate top stack value                |

### Exception Table Structure

```
exception_table_entry {
    u2 start_pc;     // Start of try block (inclusive)
    u2 end_pc;       // End of try block (exclusive)
    u2 handler_pc;   // Start of exception handler
    u2 catch_type;   // 0 = catch all (for finally), or Throwable class index
}
```

### Stack Map Frames

Stack map frames are required at:
- Exception handler entry points (`catch_all`, `catch_body`)
- Branch targets (`skip_close`, `after_finally`, `done`)
- After `goto` instructions

---

## Success Criteria

### Phase 1
- [x] Basic `using` declaration with single AutoCloseable resource
- [x] `close()` called on normal scope exit
- [x] `close()` called on exception
- [x] Null-safe close (no NPE when resource is null)
- [ ] Compile-time error for non-AutoCloseable types
- [x] Proper exception table generation
- [x] Proper stack map frame generation
- [x] All Phase 1 tests passing

### Phase 2
- [x] Multiple resources in single `using` declaration
- [x] Resources closed in reverse declaration order
- [x] Nested `using` statements work correctly
- [x] Second resource init failure still closes first resource
- [x] All Phase 2 tests passing

### Phase 3
- [x] Suppressed exception support via `addSuppressed()`
- [x] Body exception preserved as primary, close exception suppressed
- [x] Close exception propagated when no body exception
- [x] Multiple suppressed exceptions chained correctly
- [x] All Phase 3 tests passing

### Phase 4
- [x] `return` in using body closes resources before returning
- [x] `break` in using body inside loop closes resources before breaking
- [x] `continue` in using body inside loop closes resources before continuing
- [x] Interaction with enclosing try-finally works correctly
- [x] All Phase 4 tests passing

### Phase 5
- [x] `await using` rejected with clear compile-time error
- [x] Error message includes source location and suggestion

### Overall
- [x] All javadoc builds with no warnings
- [x] No regressions in existing test suite
- [x] Integration with StatementProcessor dispatch

---

## Known Limitations

1. **`await using` not supported** - Async disposal requires async/await infrastructure which is not implemented.
2. **`Symbol.dispose` not supported** - The TC39 `using` declaration relies on `Symbol.dispose` protocol. This implementation maps directly to Java's `AutoCloseable.close()` instead.
3. **`Symbol.asyncDispose` not supported** - Same reason as `await using`.
4. **Duck typing not supported** - Unlike JavaScript where any object with `[Symbol.dispose]()` works, the JVM requires the type to actually implement the `AutoCloseable` interface.
5. **Suppressed exceptions in Phase 1** - Phase 1 uses simplified try-finally without suppressed exception tracking. Full semantics arrive in Phase 3.
6. **Using in for-loop head** - `for (using x = ...; ...; ...)` is syntactically valid but semantically unusual. Support deferred to future work.

---

## References

- **TC39 Proposal:** [Explicit Resource Management](https://github.com/tc39/proposal-explicit-resource-management)
- **TypeScript 5.2:** Using Declarations and Explicit Resource Management
- **Java Language Specification:** Section 14.20.3 - try-with-resources
- **JVM Specification:** Chapter 3.12 - Throwing and Handling Exceptions
- **JVM Specification:** Chapter 4.7.4 - The StackMapTable Attribute
- **Existing Implementation:** TryStatementProcessor.java (for try-finally bytecode patterns)
- **Existing Implementation:** StatementProcessor.java (for statement dispatch)
- **Test Reference:** TestCompileAstTryStmt*.java (for test structure patterns)
- **AST Reference:** Swc4jAstUsingDecl.java, Swc4jAstVarDeclarator.java

---

## Notes

- The `using` declaration is essentially syntactic sugar for try-with-resources in this JVM compilation context.
- Resources **must** implement `java.lang.AutoCloseable` - there is no duck-typing fallback.
- Multiple resources are closed in **reverse declaration order** to mirror Java's try-with-resources semantics.
- The `finally` blocks generated for using follow the same patterns as `TryStatementProcessor`.
- Stack must be **empty** at exception handler entry points for JVM verifier compliance.
- The `using` node implements `ISwc4jAstForHead`, meaning it can appear in for-loop head declarations, but this is a low-priority edge case.
- Consider reusing `TryStatementProcessor`'s finally inlining logic to avoid code duplication.
- The `UsingDeclProcessor` should be registered in `ByteCodeCompiler` and dispatched from `StatementProcessor`.
