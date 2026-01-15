# Object Literal Implementation Plan

## Overview

This document outlines the implementation plan for supporting JavaScript/TypeScript object literals (`Swc4jAstObjectLit`) and compiling them to JVM bytecode using `LinkedHashMap<Object, Object>` as the underlying data structure.

**Current Status:** ❌ Not Implemented

**Implementation File:** To be created at `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/ObjectLiteralGenerator.java`

**Test File:** To be created at `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/TestCompileObjectLit.java`

**AST Definition:** [Swc4jAstObjectLit.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstObjectLit.java)

---

## JavaScript Object Literal Syntax

### Basic Object Literals
```javascript
const empty = {}                              // Empty object
const simple = {a: 1, b: "hello", c: true}   // Simple key-value pairs
const nested = {outer: {inner: 42}}          // Nested objects
```

### Advanced Features
```javascript
const computed = {["key" + 1]: "value"}      // Computed property names
const shorthand = {x, y}                      // Property shorthand
const spread = {a: 1, ...other}               // Spread operator
const method = {greet() { return "hi" }}      // Method properties
const getSet = {                              // Getter/setter
  get name() { return this._name },
  set name(v) { this._name = v }
}
```

---

## Java LinkedHashMap Representation

### Type Mapping Strategy

**TypeScript Record Type → Java LinkedHashMap:**

| TypeScript Type | Java Type | Descriptor |
|----------------|-----------|------------|
| `Record<string, number>` | `LinkedHashMap<String, Integer>` | `Ljava/util/LinkedHashMap;` |
| `Record<string, string>` | `LinkedHashMap<String, String>` | `Ljava/util/LinkedHashMap;` |
| `Record<string, Object>` | `LinkedHashMap<String, Object>` | `Ljava/util/LinkedHashMap;` |
| `Record<number, string>` | `LinkedHashMap<Integer, String>` | `Ljava/util/LinkedHashMap;` |
| No annotation | `LinkedHashMap<String, Object>` | `Ljava/util/LinkedHashMap;` |

**Default Behavior:**
```typescript
// No type annotation - defaults to LinkedHashMap<String, Object>
const obj = {a: 1, b: "hello", c: true}
// Type: LinkedHashMap<String, Object>

// With Record type - strict typing
const obj: Record<string, number> = {a: 1, b: 2, c: 3}
// Type: LinkedHashMap<String, Integer>
```

### Why LinkedHashMap?

- ✅ Maintains insertion order (like JavaScript objects in modern engines)
- ✅ Supports generic type parameters for type safety
- ✅ O(1) access time for most operations
- ✅ Supports null values (but null keys should be validated)
- ✅ Standard Java collections API

### Type Constraints

**Key Type:**
- **Default:** `String` (JavaScript object keys are always strings or symbols)
- **Alternative:** `Integer` for numeric keys (converted to Integer, not String)
- **Validation:** Compiler must verify all keys match the declared key type

**Value Type:**
- **Default:** `Object` (allows any value type)
- **Specific:** Declared type in `Record<K, V>`
- **Validation:** Compiler must verify all values are assignable to declared value type
- **Boxing:** Primitive values must be boxed to wrapper types

---

## JavaScript Object API → Java Map API Mapping

### Property Access Operations

| JavaScript Operation | Java Map Equivalent | Notes |
|---------------------|---------------------|-------|
| `obj.prop` | `map.get("prop")` | Dot notation → Map.get() |
| `obj["prop"]` | `map.get("prop")` | Bracket notation → Map.get() |
| `obj[expr]` | `map.get(expr)` | Computed key → Map.get() |
| `obj.prop = value` | `map.put("prop", value)` | Assignment → Map.put() |
| `obj["prop"] = value` | `map.put("prop", value)` | Bracket assignment → Map.put() |

### Property Operations

| JavaScript Operation | Java Map Equivalent | Notes |
|---------------------|---------------------|-------|
| `"prop" in obj` | `map.containsKey("prop")` | Existence check |
| `delete obj.prop` | `map.remove("prop")` | Property deletion |
| `Object.keys(obj)` | `map.keySet()` | Get all keys |
| `Object.values(obj)` | `map.values()` | Get all values |
| `Object.entries(obj)` | `map.entrySet()` | Get key-value pairs |

### Iteration Operations

| JavaScript Operation | Java Map Equivalent | Notes |
|---------------------|---------------------|-------|
| `for (let k in obj)` | `for (Object k : map.keySet())` | Key iteration |
| `for (let [k,v] of Object.entries(obj))` | `for (Map.Entry e : map.entrySet())` | Entry iteration |

### Utility Operations

| JavaScript Operation | Java Map Equivalent | Notes |
|---------------------|---------------------|-------|
| `Object.assign(t, s)` | `target.putAll(source)` | Shallow merge |
| `{...obj}` | `new LinkedHashMap<>(obj)` | Shallow clone |
| `Object.freeze(obj)` | `Collections.unmodifiableMap(map)` | Make immutable |
| `Object.seal(obj)` | Not directly supported | Prevents add/delete |
| `obj.hasOwnProperty(k)` | `map.containsKey(k)` | Own property check |

---

## AST Structure

### Swc4jAstObjectLit Components

```java
public class Swc4jAstObjectLit {
    protected final List<ISwc4jAstPropOrSpread> props;

    public List<ISwc4jAstPropOrSpread> getProps() { return props; }
}
```

### Property Types (ISwc4jAstPropOrSpread)

1. **Swc4jAstKeyValueProp** - Regular key-value pairs
   ```javascript
   {key: value}
   ```

2. **Swc4jAstAssignProp** - Shorthand properties
   ```javascript
   {x}  // equivalent to {x: x}
   ```

3. **Swc4jAstMethodProp** - Method properties
   ```javascript
   {method() { return 42; }}
   ```

4. **Swc4jAstGetterProp** - Getter properties
   ```javascript
   {get name() { return this._name; }}
   ```

5. **Swc4jAstSetterProp** - Setter properties
   ```javascript
   {set name(v) { this._name = v; }}
   ```

6. **Swc4jAstSpreadElement** - Spread syntax
   ```javascript
   {...other}
   ```

### Property Name Types (ISwc4jAstPropName)

1. **Swc4jAstIdentName** - Identifier keys
   ```javascript
   {name: value}
   ```

2. **Swc4jAstStr** - String literal keys
   ```javascript
   {"string-key": value}
   ```

3. **Swc4jAstNumber** - Numeric keys
   ```javascript
   {42: value}
   ```

4. **Swc4jAstComputedPropName** - Computed keys
   ```javascript
   {[expr]: value}
   ```

---

## Type Inference and Validation

### Type Inference Rules

**Scenario 1: No Type Annotation**
```typescript
const obj = {a: 1, b: "hello", c: true}
```
**Inferred Type:** `LinkedHashMap<String, Object>`
- **Key Type:** String (default)
- **Value Type:** Object (allows mixed types)
- **Validation:** None required (permissive)

**Scenario 2: Record Type Annotation**
```typescript
const obj: Record<string, number> = {a: 1, b: 2, c: 3}
```
**Inferred Type:** `LinkedHashMap<String, Integer>`
- **Key Type:** String
- **Value Type:** Integer (all values must be numbers)
- **Validation:** REQUIRED - verify all values are numbers

**Scenario 3: Nested Record Types**
```typescript
const obj: Record<string, Record<string, number>> = {
  outer1: {inner: 42},
  outer2: {value: 99}
}
```
**Inferred Type:** `LinkedHashMap<String, LinkedHashMap<String, Integer>>`
- **Validation:** REQUIRED - verify nested structure matches

**Scenario 4: Numeric Keys**
```typescript
const obj: Record<number, string> = {1: "one", 2: "two", 3: "three"}
```
**Inferred Type:** `LinkedHashMap<Integer, String>`
- **Key Type:** Integer (numeric keys boxed)
- **Value Type:** String
- **Validation:** REQUIRED - verify all keys are numbers, all values are strings

### Type Validation Logic

**Validation Steps:**

1. **Extract Type Information from ReturnTypeInfo**
   ```java
   // Parse generic type parameters from Record<K, V>
   GenericTypeInfo genericInfo = parseGenericType(returnTypeInfo);
   String keyType = genericInfo.getKeyType();     // e.g., "Ljava/lang/String;"
   String valueType = genericInfo.getValueType(); // e.g., "Ljava/lang/Integer;"
   ```

2. **Validate Each Key**
   ```java
   for (ISwc4jAstPropOrSpread prop : objectLit.getProps()) {
       if (prop instanceof Swc4jAstKeyValueProp kvProp) {
           ISwc4jAstPropName key = kvProp.getKey();

           // Infer key type from AST
           String actualKeyType = inferKeyType(key);

           // Validate against expected key type
           if (!isAssignable(actualKeyType, keyType)) {
               throw new Swc4jByteCodeCompilerException(
                   "Key type mismatch: expected " + keyType +
                   ", got " + actualKeyType);
           }
       }
   }
   ```

3. **Validate Each Value**
   ```java
   ISwc4jAstExpr valueExpr = kvProp.getValue();
   String actualValueType = TypeResolver.inferTypeFromExpr(valueExpr, context, options);

   // Check assignability (with boxing/unboxing)
   if (!isAssignable(actualValueType, valueType)) {
       throw new Swc4jByteCodeCompilerException(
           "Value type mismatch for key '" + keyName + "': " +
           "expected " + valueType + ", got " + actualValueType);
   }
   ```

4. **Handle Primitive to Wrapper Conversion**
   ```java
   // If value type is primitive but declared type is wrapper, allow it
   // Example: value is int (I), declared is Integer (Ljava/lang/Integer;)
   if (isPrimitive(actualValueType) && isWrapperOf(valueType, actualValueType)) {
       // Box the primitive value during generation
       return true;
   }
   ```

5. **Handle Nested Object Validation**
   ```java
   if (valueExpr instanceof Swc4jAstObjectLit nestedObj) {
       // Recursively validate nested object against nested Record type
       validateNestedObject(nestedObj, valueType, context, options);
   }
   ```

### Type Validation Error Messages

**Clear, actionable error messages:**

```
❌ "Object literal has property 'age' with type String,
    but Record<string, number> requires all values to be number"

❌ "Object literal has numeric key 42,
    but Record<string, any> requires string keys"

❌ "Object literal has nested object with property 'x' of type string,
    but Record<string, Record<string, number>> requires number values in nested objects"

❌ "Cannot assign null to property 'name' in Record<string, string>
    (nullable types not supported)"
```

### Type Assignability Rules

**Primitive to Wrapper:**
- `I` (int) → `Ljava/lang/Integer;` ✅
- `J` (long) → `Ljava/lang/Long;` ✅
- `D` (double) → `Ljava/lang/Double;` ✅
- etc.

**Widening Conversions:**
- `I` (int) → `J` (long) ✅
- `I` (int) → `D` (double) ✅
- `F` (float) → `D` (double) ✅

**Object Hierarchy:**
- Any type → `Ljava/lang/Object;` ✅
- `Ljava/lang/String;` → `Ljava/lang/CharSequence;` ✅

**Disallowed:**
- `Ljava/lang/String;` → `I` (int) ❌
- `I` (int) → `Ljava/lang/String;` ❌
- `Ljava/lang/Integer;` → `I` (int) without unboxing ❌

---

## Implementation Strategy

### Phase 0: Type Validation Infrastructure (Priority: CRITICAL)

**Goal:** Build type validation system before object literal generation.

**Components:**

1. **Generic Type Parser**
   ```java
   class GenericTypeInfo {
       String keyType;      // e.g., "Ljava/lang/String;"
       String valueType;    // e.g., "Ljava/lang/Integer;"
       boolean isNested;    // true if valueType is also LinkedHashMap
       GenericTypeInfo nestedTypeInfo;  // for nested Records
   }

   GenericTypeInfo parseRecordType(ReturnTypeInfo returnTypeInfo) {
       // Parse type annotation like "Record<string, number>"
       // Extract key and value types
       // Handle nested Record types
   }
   ```

2. **Type Compatibility Checker**
   ```java
   boolean isAssignable(String fromType, String toType) {
       // Check if fromType can be assigned to toType
       // Handle primitive-to-wrapper boxing
       // Handle widening conversions
       // Handle object hierarchy
   }
   ```

3. **Key Type Inferrer**
   ```java
   String inferKeyType(ISwc4jAstPropName key) {
       if (key instanceof Swc4jAstIdentName || key instanceof Swc4jAstStr) {
           return "Ljava/lang/String;";
       } else if (key instanceof Swc4jAstNumber) {
           return "Ljava/lang/Integer;";  // or determine int vs long
       } else if (key instanceof Swc4jAstComputedPropName computed) {
           return TypeResolver.inferTypeFromExpr(computed.getExpr(), context, options);
       }
   }
   ```

4. **Validation Error Formatter**
   ```java
   String formatTypeMismatchError(
       String propertyName,
       String expectedType,
       String actualType,
       boolean isKey) {
       // Format clear, actionable error message
   }
   ```

**Test Cases:**
```typescript
// Should compile successfully
const obj1: Record<string, number> = {a: 1, b: 2, c: 3}

// Should fail - value type mismatch
const obj2: Record<string, number> = {a: 1, b: "hello"}
// Error: Property 'b' has type String, but Record requires number

// Should fail - key type mismatch
const obj3: Record<number, string> = {"a": "hello"}
// Error: Key 'a' has type String, but Record requires number

// Should compile - primitive to wrapper
const obj4: Record<string, Integer> = {a: 1, b: 2}

// Should compile - nested objects
const obj5: Record<string, Record<string, number>> = {
  outer: {inner: 42}
}

// Should fail - nested type mismatch
const obj6: Record<string, Record<string, number>> = {
  outer: {inner: "hello"}
}
// Error: Nested property 'outer.inner' has type String, but Record requires number
```

---

### Phase 1: Basic Key-Value Pairs (Priority: CRITICAL)

**Goal:** Support simple object literals with string keys and primitive/object values.

**Bytecode Generation:**
```java
public static void generate(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstObjectLit objectLit,
        ReturnTypeInfo returnTypeInfo,
        CompilationContext context,
        ByteCodeCompilerOptions options,
        ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

    // Create new LinkedHashMap instance
    int hashMapClass = cp.addClass("java/util/LinkedHashMap");
    int hashMapInit = cp.addMethodRef("java/util/LinkedHashMap", "<init>", "()V");
    int hashMapPut = cp.addMethodRef("java/util/LinkedHashMap", "put",
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    code.newInstance(hashMapClass);  // Stack: [map]
    code.dup();                      // Stack: [map, map]
    code.invokespecial(hashMapInit); // Stack: [map]

    // Add each property to the map
    for (ISwc4jAstPropOrSpread prop : objectLit.getProps()) {
        if (prop instanceof Swc4jAstKeyValueProp kvProp) {
            code.dup(); // Duplicate map reference - Stack: [map, map]

            // Generate key
            generateKey(code, cp, kvProp.getKey(), context, options, callback);
            // Stack: [map, map, key]

            // Generate value
            ISwc4jAstExpr valueExpr = kvProp.getValue();
            String valueType = TypeResolver.inferTypeFromExpr(valueExpr, context, options);
            if (valueType == null) valueType = "Ljava/lang/Object;";

            callback.generateExpr(code, cp, valueExpr, null, context, options);
            // Stack: [map, map, key, value]

            // Box primitive values
            if (isPrimitive(valueType)) {
                TypeConversionHelper.boxPrimitiveType(code, cp, valueType,
                    TypeConversionHelper.getWrapperType(valueType));
            }
            // Stack: [map, map, key, boxedValue]

            // Call map.put(key, value)
            code.invokevirtual(hashMapPut); // Stack: [map, oldValue]
            code.pop(); // Discard old value - Stack: [map]
        }
    }
    // Stack: [map]
}
```

**Key Generation:**
```java
private static void generateKey(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        ISwc4jAstPropName key,
        CompilationContext context,
        ByteCodeCompilerOptions options,
        ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

    if (key instanceof Swc4jAstIdentName identName) {
        // String literal key
        int keyIndex = cp.addString(identName.getSym());
        code.ldc(keyIndex);
    } else if (key instanceof Swc4jAstStr str) {
        // String literal key
        int keyIndex = cp.addString(str.getValue());
        code.ldc(keyIndex);
    } else if (key instanceof Swc4jAstNumber num) {
        // Numeric key - convert to string for Map key
        String keyStr = String.valueOf((int)num.getValue());
        int keyIndex = cp.addString(keyStr);
        code.ldc(keyIndex);
    } else if (key instanceof Swc4jAstComputedPropName computed) {
        // Computed property name - generate expression
        callback.generateExpr(code, cp, computed.getExpr(), null, context, options);
        // Convert to String if needed
        // TODO: Handle non-string computed keys
    }
}
```

**Test Cases:**
```typescript
// Empty object
const obj1 = {}

// Simple properties
const obj2 = {a: 1, b: "hello", c: true}

// Different value types
const obj3 = {
  int: 42,
  long: 1000000000000,
  double: 3.14,
  bool: false,
  str: "value",
  nil: null
}

// Nested objects
const obj4 = {
  outer: {
    inner: {
      deep: "value"
    }
  }
}
```

---

### Phase 2: Computed Property Names (Priority: HIGH)

**Goal:** Support computed property names using bracket notation.

**JavaScript:**
```javascript
const key = "dynamic"
const obj = {[key]: "value", ["key" + 1]: "value2"}
```

**Implementation:**
- Evaluate the computed expression at runtime
- Convert result to String for Map key
- Support any expression type as computed key

**Edge Cases:**
- ❗ Computed expressions with side effects
- ❗ Non-string computed keys (numbers, symbols)
- ❗ Computed keys that evaluate to null/undefined

**Test Cases:**
```typescript
// String concatenation
const obj1 = {["key" + 1]: "value"}

// Variable references
const k = "dynamic"
const obj2 = {[k]: "value"}

// Expression evaluation
const obj3 = {[getKey()]: getValue()}

// Numeric computed keys
const obj4 = {[42]: "value", [1 + 1]: "two"}
```

---

### Phase 3: Property Shorthand (Priority: MEDIUM)

**Goal:** Support ES6 shorthand property syntax.

**JavaScript:**
```javascript
const x = 1, y = 2
const obj = {x, y}  // equivalent to {x: x, y: y}
```

**Implementation:**
```java
if (prop instanceof Swc4jAstAssignProp assignProp) {
    code.dup(); // Duplicate map reference

    // Key is the identifier name
    String keyName = assignProp.getKey().getSym();
    int keyIndex = cp.addString(keyName);
    code.ldc(keyIndex);

    // Value is the identifier value from context
    Swc4jAstIdent valueIdent = /* get from assignProp */;
    callback.generateExpr(code, cp, valueIdent, null, context, options);

    // Box if primitive
    // ...

    code.invokevirtual(hashMapPut);
    code.pop();
}
```

**Test Cases:**
```typescript
const a = 1, b = 2, c = 3
const obj = {a, b, c}
assertEquals(1, obj.get("a"))
assertEquals(2, obj.get("b"))
```

---

### Phase 4: Spread Operator (Priority: MEDIUM)

**Goal:** Support object spread syntax for shallow merging.

**JavaScript:**
```javascript
const obj1 = {a: 1}
const obj2 = {b: 2}
const merged = {a: 0, ...obj1, ...obj2, c: 3}
```

**Implementation:**
```java
if (prop instanceof Swc4jAstSpreadElement spread) {
    // Generate the spread expression (should evaluate to a Map)
    callback.generateExpr(code, cp, spread.getExpr(), null, context, options);
    // Stack: [map, spreadMap]

    // Call map.putAll(spreadMap)
    int putAllMethod = cp.addMethodRef("java/util/LinkedHashMap",
        "putAll", "(Ljava/util/Map;)V");
    code.invokevirtual(putAllMethod);
    // Stack: [map]
}
```

**Edge Cases:**
- ❗ Spread order matters (later spreads override earlier values)
- ❗ Spread of non-object values (should throw error)
- ❗ Spread with null/undefined (should skip)
- ❗ Multiple spreads in same object

**Test Cases:**
```typescript
const base: LinkedHashMap<Object> = new LinkedHashMap<Object>()
base.put("a", 1)
base.put("b", 2)

const obj = {a: 0, ...base, c: 3}
// obj should be {a: 1, b: 2, c: 3}
assertEquals(1, obj.get("a"))  // base.a overrides initial a
```

---

### Phase 5: Method Properties (Priority: LOW)

**Goal:** Support method syntax in object literals.

**JavaScript:**
```javascript
const obj = {
  method() { return 42; },
  async asyncMethod() { return await Promise.resolve(1); }
}
```

**Implementation Strategy:**
- Methods in objects should be stored as Function objects
- Requires implementing function/lambda support first
- Map value would be a functional interface or method handle

**Challenges:**
- ❌ Requires function compilation support
- ❌ `this` binding for methods
- ❌ Method references and closures

**Status:** Defer until function compilation is implemented

---

### Phase 6: Getter/Setter Properties (Priority: VERY LOW)

**Goal:** Support getter and setter syntax.

**JavaScript:**
```javascript
const obj = {
  _value: 0,
  get value() { return this._value; },
  set value(v) { this._value = v; }
}
```

**Implementation Strategy:**
- Java Maps don't natively support getter/setter semantics
- Would require creating a custom Map wrapper or proxy
- Or generate a proper Java class with getter/setter methods

**Challenges:**
- ❌ Maps are simple data structures, not objects with behavior
- ❌ Would need property descriptor concept
- ❌ Very complex to implement properly

**Status:** Not feasible with LinkedHashMap - requires class generation

---

## Edge Cases and Special Scenarios

### 1. Empty Objects
```javascript
const empty = {}
```
**Handling:** Create empty LinkedHashMap
**Bytecode:** Just new LinkedHashMap() with no put() calls

### 2. Duplicate Keys
```javascript
const obj = {a: 1, a: 2}  // Later value wins
```
**Handling:** Map.put() naturally handles this (overwrites)
**Expected:** obj.get("a") == 2

### 3. Null and Undefined Values
```javascript
const obj = {a: null, b: undefined}
```
**Handling:**
- null → null (Java null)
- undefined → Not supported in typed context, throw error
**Test:** Verify null values are stored correctly

### 4. Numeric Keys

**Scenario A: No Type Annotation (Default to String keys)**
```typescript
const obj = {0: "zero", 1: "one", 42: "forty-two"}
```
**Handling:** Convert numbers to String for Map keys
**Java:** `map.put("0", "zero")`
**Type:** `LinkedHashMap<String, Object>`
**Rationale:** JavaScript coerces numeric keys to strings

**Scenario B: Record<number, string> (Numeric key type)**
```typescript
const obj: Record<number, string> = {0: "zero", 1: "one", 42: "forty-two"}
```
**Handling:** Keep as Integer keys (boxed)
**Java:** `map.put(Integer.valueOf(0), "zero")`
**Type:** `LinkedHashMap<Integer, String>`
**Validation:** All keys must be numeric, all values must be strings

### 5. Symbol Keys
```javascript
const sym = Symbol("key")
const obj = {[sym]: "value"}
```
**Handling:** Symbols not supported in Java - throw error
**Error:** "Symbol keys not supported in Map representation"

### 6. Non-String Primitive Keys
```javascript
const obj = {true: "bool", null: "null"}
```
**Handling:** Convert to string representation
- true → "true"
- false → "false"
- null → "null"

### 7. Nested Objects
```javascript
const nested = {
  level1: {
    level2: {
      level3: "deep"
    }
  }
}
```
**Handling:** Recursive generation - inner objects are also LinkedHashMap
**Test:** Verify nested map access: `((Map)map.get("level1")).get("level2")`

### 8. Arrays as Values
```javascript
const obj = {arr: [1, 2, 3]}
```
**Handling:** Array literal generates ArrayList, stored as Map value
**Test:** Verify ArrayList retrieval and access

### 9. Circular References
```javascript
const obj = {}
obj.self = obj  // Circular reference
```
**Handling:** Runtime circular reference - no compile-time issue
**Note:** LinkedHashMap supports this, but serialization would fail

### 10. Reserved Keywords as Keys
```javascript
const obj = {class: "value", for: "loop", if: "condition"}
```
**Handling:** No issue - Map keys are just strings
**Java:** `map.put("class", "value")` - perfectly valid

### 11. Whitespace in Keys
```javascript
const obj = {"key with spaces": "value", "  trim  ": "test"}
```
**Handling:** Preserve exact string including whitespace
**Test:** `map.get("key with spaces")` and `map.get("  trim  ")`

### 12. Unicode Keys
```javascript
const obj = {"你好": "hello", "🔥": "fire", "café": "coffee"}
```
**Handling:** Java strings support full Unicode
**Test:** Verify UTF-8 encoding preservation

### 13. Very Large Objects
```javascript
const large = {prop1: 1, prop2: 2, /* ... */, prop10000: 10000}
```
**Handling:** LinkedHashMap handles arbitrary size
**Performance:** O(1) put operations, but compilation time may be slow
**Note:** May hit JVM method size limits for inline initialization

### 14. Object as Value Type Annotation
```typescript
const obj: Object = {a: 1}
```
**Handling:** Still generate LinkedHashMap, but type is Object
**Note:** Return type would be Ljava/lang/Object;

### 15. Mixed Key Types
```javascript
const obj = {"str": 1, 42: 2, [computed]: 3}
```
**Handling:** All keys converted to String or Object
**Strategy:** Use Object as key type for maximum flexibility

### 16. Trailing Commas
```javascript
const obj = {a: 1, b: 2,}  // Trailing comma
```
**Handling:** AST already handles this - no special handling needed

### 17. Computed Keys with Side Effects
```javascript
let i = 0
const obj = {[i++]: "a", [i++]: "b"}
```
**Handling:** Execute side effects in order during key evaluation
**Test:** Verify execution order matches JavaScript

### 18. Property Name Collisions After Coercion
```javascript
const obj = {1: "num", "1": "str"}
```
**Handling:** Both coerce to "1" - later one wins
**Expected:** obj.get("1") == "str"

### 19. Expression Values
```javascript
const obj = {a: 1 + 2, b: fn(), c: x ? y : z}
```
**Handling:** Generate expression bytecode, evaluate at runtime
**Test:** Verify complex expressions are evaluated correctly

### 20. Object in Return Type Context
```typescript
function getObject(): LinkedHashMap<String, Object> {
  return {a: 1, b: 2}
}
```
**Handling:** Return type determines Map type
**Test:** Verify return type matches LinkedHashMap

---

## Additional Edge Cases for Type Validation

### 21. Type Mismatch in Record Values
```typescript
const obj: Record<string, number> = {a: 1, b: "hello"}
```
**Handling:** Compilation error
**Error:** "Property 'b' has type String, but Record<string, number> requires number values"
**Status:** Must validate and reject

### 22. Type Mismatch in Record Keys
```typescript
const obj: Record<number, string> = {"a": "hello", "b": "world"}
```
**Handling:** Compilation error
**Error:** "Keys must be numeric for Record<number, V>, got string key 'a'"
**Status:** Must validate and reject

### 23. Mixed Numeric and String Keys with Record<number, V>
```typescript
const obj: Record<number, string> = {1: "one", "2": "two"}
```
**Handling:**
- If key "2" can be parsed as number, allow it
- Or reject string keys entirely for strict type safety
**Recommendation:** Reject for type safety

### 24. Null Values in Non-Nullable Record
```typescript
const obj: Record<string, string> = {a: "hello", b: null}
```
**Handling:** Compilation error (Java doesn't have nullable annotations by default)
**Error:** "Cannot assign null to property 'b' in Record<string, string>"
**Alternative:** Allow null if explicitly declared: `Record<string, string | null>`

### 25. Nested Record Type Mismatch
```typescript
const obj: Record<string, Record<string, number>> = {
  outer: {inner: "hello"}  // Should be number
}
```
**Handling:** Compilation error
**Error:** "Nested property 'outer.inner' has type String, but Record<string, Record<string, number>> requires number"
**Status:** Recursive validation required

### 26. Primitive to Wrapper Auto-Boxing
```typescript
const obj: Record<string, Integer> = {a: 1, b: 2, c: 3}
```
**Handling:** Allow - auto-box primitive int to Integer
**Java:** `map.put("a", Integer.valueOf(1))`
**Status:** Should work seamlessly

### 27. Widening Conversion in Record Values
```typescript
const obj: Record<string, long> = {a: 1, b: 2147483648}
```
**Handling:** Allow - int can widen to long
**Java:** Convert int to long before boxing
**Status:** Should work

### 28. Incompatible Widening (Narrowing Prohibited)
```typescript
const obj: Record<string, int> = {a: 1, b: 1000000000000}
```
**Handling:** Compilation error - long cannot narrow to int without explicit cast
**Error:** "Value 1000000000000 exceeds int range, cannot assign to Record<string, int>"
**Status:** Must reject

### 29. Object Type in Record (Permissive)
```typescript
const obj: Record<string, Object> = {
  a: 1,
  b: "hello",
  c: true,
  d: {nested: "object"},
  e: [1, 2, 3]
}
```
**Handling:** Allow - any value assignable to Object
**Java:** All values boxed to appropriate wrapper/object types
**Status:** Should work (permissive typing)

### 30. Union Types in Record (Not Supported)
```typescript
const obj: Record<string, number | string> = {a: 1, b: "hello"}
```
**Handling:** Not supported - Java doesn't have union types
**Error:** "Union types not supported in Record. Use Record<string, Object> instead"
**Status:** Must reject with helpful message

### 31. Array Values in Record
```typescript
const obj: Record<string, Array<number>> = {
  a: [1, 2, 3],
  b: [4, 5, 6]
}
```
**Handling:** Allow - ArrayList<Integer> assignable to expected type
**Type:** `LinkedHashMap<String, ArrayList<Integer>>`
**Status:** Should work with array literal support

### 32. Empty Object with Strict Record Type
```typescript
const obj: Record<string, number> = {}
```
**Handling:** Allow - empty map is valid
**Java:** `new LinkedHashMap<String, Integer>()`
**Status:** Should work

### 33. Computed Property with Type Validation
```typescript
const key: string = "dynamic"
const obj: Record<string, number> = {[key]: 42}
```
**Handling:** Allow - computed key evaluated at runtime, type checked
**Validation:** Key expression type must match Record key type
**Status:** Should work

### 34. Computed Property with Wrong Key Type
```typescript
const key: number = 123
const obj: Record<string, number> = {[key]: 42}
```
**Handling:** Compilation error
**Error:** "Computed property key has type number, but Record<string, V> requires string keys"
**Status:** Must validate and reject

### 35. Method Properties in Record (Not Supported)
```typescript
const obj: Record<string, Function> = {
  method() { return 42; }
}
```
**Handling:** Deferred - requires function compilation
**Status:** ⏸️ Not supported yet

### 36. Getter/Setter in Record (Not Supported)
```typescript
const obj: Record<string, number> = {
  get value() { return 42; }
}
```
**Handling:** Not feasible with Map
**Error:** "Getter/setter properties not supported in Record type"
**Status:** Must reject

### 37. Spread with Type Validation
```typescript
const base: Record<string, number> = {a: 1, b: 2}
const obj: Record<string, number> = {c: 3, ...base}
```
**Handling:** Allow - spread type must be compatible
**Validation:** Spread source type must match or be assignable to target Record type
**Status:** Should work

### 38. Spread Type Mismatch
```typescript
const base: Record<string, string> = {a: "hello"}
const obj: Record<string, number> = {b: 2, ...base}
```
**Handling:** Compilation error
**Error:** "Cannot spread Record<string, string> into Record<string, number> - value types incompatible"
**Status:** Must validate and reject

### 39. Shorthand Property with Type Validation
```typescript
const a: number = 42
const obj: Record<string, number> = {a}
```
**Handling:** Allow - infer value type from variable
**Validation:** Variable type must be assignable to Record value type
**Status:** Should work

### 40. Shorthand Property Type Mismatch
```typescript
const a: string = "hello"
const obj: Record<string, number> = {a}
```
**Handling:** Compilation error
**Error:** "Property 'a' has type string, but Record<string, number> requires number values"
**Status:** Must validate and reject

---

## Type Coercion and Conversion

### Key Type Handling

**Default Strategy (No Annotation):** Use `String` as key type

```java
LinkedHashMap<String, Object> map = new LinkedHashMap<>();
```

**Key Coercion Rules (Default):**
1. **Identifier keys** → String: `"identifier"`
2. **String literal keys** → String: `"value"`
3. **Numeric keys** → String: `"42"` (JavaScript coercion behavior)
4. **Computed keys** → Evaluate expression, convert to String
5. **Boolean keys** → String: `"true"` or `"false"`

**Key Type with Record<number, V>:**
```java
LinkedHashMap<Integer, V> map = new LinkedHashMap<>();
```
- Numeric keys stored as `Integer.valueOf(n)`
- No string coercion
- Validation: All keys must be numeric

**Key Type with Record<K, V> (Custom):**
- Keys must match declared type `K`
- Validation required for each key
- Type mismatch → compilation error

### Value Type Handling

**Default Strategy (No Annotation):** Use `Object` as value type (allows any value)

```java
// Mixed types allowed
map.put("count", Integer.valueOf(42));
map.put("flag", Boolean.valueOf(true));
map.put("name", "John");
map.put("nested", nestedMap);
```

**Typed Strategy (Record<K, V>):** Use declared type `V`

```typescript
const obj: Record<string, number> = {a: 1, b: 2}
```
```java
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("a", Integer.valueOf(1));
map.put("b", Integer.valueOf(2));
```

**Value Boxing Rules:**
- `I` → `Ljava/lang/Integer;`
- `J` → `Ljava/lang/Long;`
- `F` → `Ljava/lang/Float;`
- `D` → `Ljava/lang/Double;`
- `Z` → `Ljava/lang/Boolean;`
- `B` → `Ljava/lang/Byte;`
- `S` → `Ljava/lang/Short;`
- `C` → `Ljava/lang/Character;`

**Value Type Validation:**
- All values must be assignable to declared value type
- Primitive → wrapper boxing allowed
- Widening conversions allowed (int → long, int → double)
- Incompatible types → compilation error

---

## Return Type Inference

### Scenarios

1. **Explicit Type Annotation:**
   ```typescript
   const obj: LinkedHashMap<Object> = {a: 1}
   ```
   **Inference:** Use annotated type

2. **No Type Annotation:**
   ```typescript
   const obj = {a: 1}
   ```
   **Inference:** Default to `Ljava/util/LinkedHashMap;`

3. **Generic Type Context:**
   ```typescript
   function process(map: LinkedHashMap<Object>) {
     // ...
   }
   process({a: 1})
   ```
   **Inference:** Match function parameter type

4. **Object Type Annotation:**
   ```typescript
   const obj: Object = {a: 1}
   ```
   **Inference:** Generate LinkedHashMap, but return as Object

---

## JVM Bytecode Instructions

### Core Instructions Used

1. **Object Creation:**
   - `new` - Create new LinkedHashMap instance
   - `dup` - Duplicate map reference for chaining
   - `invokespecial` - Call constructor

2. **Method Invocation:**
   - `invokevirtual` - Call Map.put(), Map.putAll()
   - `ldc` - Load string constants (keys)

3. **Stack Management:**
   - `dup` - Duplicate references
   - `pop` - Discard return values
   - `swap` - Reorder stack for arguments

4. **Type Conversion:**
   - `invokestatic` - Call Integer.valueOf(), etc. for boxing

---

## Implementation Checklist

### Phase 0: Type Validation Infrastructure (CRITICAL)
- [ ] Create GenericTypeInfo class for parsing Record<K, V> types
- [ ] Implement parseRecordType() to extract key/value types from type annotations
- [ ] Create type assignability checker (isAssignable)
- [ ] Implement primitive-to-wrapper boxing checks
- [ ] Implement widening conversion checks
- [ ] Create key type inferrer (inferKeyType)
- [ ] Create validation error formatter with clear messages
- [ ] Test type validation infrastructure with unit tests

### Phase 1: Basic Implementation (No Type Annotation)
- [ ] Create ObjectLiteralGenerator.java
- [ ] Implement empty object generation `{}` → `LinkedHashMap<String, Object>`
- [ ] Implement simple key-value pairs `{a: 1, b: "x"}`
- [ ] Support IdentName keys → String keys
- [ ] Support Str literal keys → String keys
- [ ] Support Number literal keys → String keys (coerced)
- [ ] Box primitive values correctly to wrapper types
- [ ] Test nested objects (recursive generation)
- [ ] Test different value types (primitives, strings, booleans, null)
- [ ] Default type: `LinkedHashMap<String, Object>`

### Phase 2: Typed Objects with Record<K, V>
- [ ] Parse Record<string, number> type annotations
- [ ] Validate all keys are strings
- [ ] Validate all values match declared value type
- [ ] Generate `LinkedHashMap<String, Integer>` for Record<string, number>
- [ ] Test type validation errors (clear error messages)
- [ ] Support Record<number, V> with Integer keys
- [ ] Support Record<string, Record<string, V>> (nested)
- [ ] Validate nested object types recursively
- [ ] Test primitive-to-wrapper conversions
- [ ] Test widening conversions (int → long, int → double)
- [ ] Reject narrowing conversions (long → int)
- [ ] Reject incompatible types (string → number)

### Phase 3: Advanced Keys
- [ ] Implement computed property names `{[expr]: value}`
- [ ] Support expressions in computed keys
- [ ] Validate computed key type against Record<K, V>
- [ ] Handle numeric key coercion to string (default behavior)
- [ ] Handle numeric keys as Integer (Record<number, V> behavior)
- [ ] Test computed keys with side effects
- [ ] Test computed key type mismatches

### Phase 4: Spread Support with Type Validation
- [ ] Implement spread operator `{...other}`
- [ ] Validate spread source type is compatible with target Record type
- [ ] Reject spread type mismatches (Record<K1, V1> → Record<K2, V2>)
- [ ] Support multiple spreads
- [ ] Test spread order (later overrides earlier)
- [ ] Handle null/undefined spread sources
- [ ] Test spread with nested Record types

### Phase 5: Shorthand with Type Validation
- [ ] Implement property shorthand `{x, y}`
- [ ] Resolve identifier values from context
- [ ] Infer value type from variable type
- [ ] Validate value type against Record<K, V>
- [ ] Reject type mismatches in shorthand
- [ ] Test shorthand with various types

### Phase 6: Integration
- [ ] Integrate with ExpressionGenerator
- [ ] Add to type inference system (TypeResolver)
- [ ] Support in member access (obj.prop → map.get())
- [ ] Support in assignment (obj.prop = x → map.put())
- [ ] Support in return type context
- [ ] Update documentation

### Phase 7: Comprehensive Testing
- [ ] Test all 40+ edge cases listed above
- [ ] Test type validation errors (21-40)
- [ ] Test Record<string, number> with all scenarios
- [ ] Test Record<number, string> with numeric keys
- [ ] Test nested Record types (3 levels deep)
- [ ] Test mixed scenarios (spread + shorthand + computed)
- [ ] Performance testing with large objects (1000+ properties)
- [ ] Test interaction with arrays (Record<string, Array<number>>)
- [ ] Test null handling in typed vs untyped contexts
- [ ] Test compiler error messages are clear and actionable

---

## Known Limitations

1. **Getter/Setter Properties:**
   - ❌ Not feasible with Map representation
   - Would require generating Java classes instead

2. **Method Properties:**
   - ⏸️ Deferred until function compilation support
   - Methods would need to be wrapped in functional interfaces

3. **Symbol Keys:**
   - ❌ Not supported (Java has no Symbol primitive)
   - Will throw compilation error

4. **Property Descriptors:**
   - ❌ No equivalent to Object.defineProperty()
   - Maps don't support configurable/enumerable/writable attributes

5. **Prototype Chain:**
   - ❌ No prototype inheritance
   - Maps are simple data structures without prototypes

6. **Property Order Guarantees:**
   - ✅ LinkedHashMap maintains insertion order
   - Compatible with modern JavaScript behavior

---

## Performance Considerations

### Memory Overhead
- **LinkedHashMap:** Higher memory overhead than plain arrays
  - Entry objects for each key-value pair
  - Hash buckets and linked list nodes
  - ~32 bytes per entry overhead (estimated)

### Access Performance
- **Get/Put:** O(1) average case
- **Iteration:** O(n) where n is number of entries

### Compilation Performance
- Large object literals may generate many bytecode instructions
- Consider limits: JVM method size limit is 65535 bytes
- For very large objects (1000+ properties), may need to split initialization

---

## Testing Strategy

### Unit Tests

1. **Basic Functionality:**
   - Empty objects
   - Simple key-value pairs
   - All primitive value types
   - Null values

2. **Advanced Features:**
   - Nested objects (2-3 levels deep)
   - Computed property names
   - Property shorthand
   - Spread operator

3. **Edge Cases:**
   - Duplicate keys
   - Numeric keys
   - Unicode keys
   - Reserved word keys
   - Very long string keys
   - Circular references

4. **Integration Tests:**
   - Objects as function arguments
   - Objects as return values
   - Objects in arrays
   - Arrays in objects
   - Object property access
   - Object property assignment

### Performance Tests

1. **Small objects:** <10 properties
2. **Medium objects:** 10-100 properties
3. **Large objects:** 100-1000 properties
4. **Deeply nested:** 5+ levels

---

## Example Test Cases

```typescript
namespace com {
  export class ObjectTests {
    // Test 1: Empty object
    testEmpty() {
      const obj = {}
      return obj
    }

    // Test 2: Simple properties
    testSimple() {
      const obj = {a: 1, b: "hello", c: true}
      return obj.get("a")  // Should return 1
    }

    // Test 3: Nested objects
    testNested() {
      const obj = {
        outer: {
          inner: {
            value: 42
          }
        }
      }
      const outer = obj.get("outer") as LinkedHashMap<Object>
      const inner = outer.get("inner") as LinkedHashMap<Object>
      return inner.get("value")  // Should return 42
    }

    // Test 4: Computed keys
    testComputed() {
      const key = "dynamic"
      const obj = {[key]: "value", ["key" + 1]: "value2"}
      return obj.get("dynamic")  // Should return "value"
    }

    // Test 5: Spread operator
    testSpread() {
      const base: LinkedHashMap<Object> = new LinkedHashMap<Object>()
      base.put("a", 1)
      base.put("b", 2)
      const obj = {a: 0, ...base, c: 3}
      return obj.get("a")  // Should return 1 (spread overwrites)
    }

    // Test 6: All value types
    testValueTypes() {
      const obj = {
        int: 42,
        long: 1000000000000,
        double: 3.14,
        float: 2.5,
        bool: true,
        str: "text",
        nil: null,
        nested: {x: 1}
      }
      return obj
    }

    // Test 7: Duplicate keys
    testDuplicateKeys() {
      const obj = {a: 1, b: 2, a: 3}
      return obj.get("a")  // Should return 3 (last wins)
    }

    // Test 8: Numeric keys
    testNumericKeys() {
      const obj = {0: "zero", 1: "one", 42: "answer"}
      return obj.get("42")  // Should return "answer"
    }
  }
}
```

---

## References

- [JavaScript Object Literals (MDN)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Object_initializer)
- [Java LinkedHashMap Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)
- [Java Map Interface](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)
- [ES6 Object Literal Extensions](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Object_initializer#new_notations_in_ecmascript_2015)
- [ArrayLiteralGenerator Implementation](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/ArrayLiteralGenerator.java)

---

## Priority Summary

| Phase | Feature | Priority | Complexity | Impact |
|-------|---------|----------|------------|--------|
| 0 | Type validation infrastructure | CRITICAL | Medium | Very High |
| 1 | Basic key-value pairs (untyped) | CRITICAL | Low | High |
| 2 | Record<K, V> type validation | CRITICAL | High | Very High |
| 3 | Computed property names | HIGH | Medium | Medium |
| 4 | Spread operator with validation | MEDIUM | Medium | Medium |
| 5 | Property shorthand with validation | MEDIUM | Low | Medium |
| 6 | Method properties | LOW | Very High | Low |
| 7 | Getter/Setter | VERY LOW | Not Feasible | Low |

**Recommended Implementation Order:** Phase 0 → Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5

**Phases 6 and 7 should be deferred** until function compilation infrastructure is built or determined to be out of scope for Map representation.

---

## Summary

### Type System Requirements

**Core Principle:**
- **Default (no annotation):** `LinkedHashMap<String, Object>` - permissive, allows any value type
- **Record<string, V>:** `LinkedHashMap<String, V>` - strict type checking on values
- **Record<number, V>:** `LinkedHashMap<Integer, V>` - numeric keys as Integer
- **Record<K, Record<K2, V>>:** Nested maps with recursive validation

### Key Implementation Requirements

1. **Type Validation Infrastructure (Phase 0)**
   - Parse Record<K, V> type annotations
   - Extract generic type parameters (key type, value type)
   - Validate key types match declared type
   - Validate value types match declared type (with boxing/widening)
   - Provide clear, actionable error messages

2. **Untyped Objects (Phase 1)**
   - Generate `LinkedHashMap<String, Object>` by default
   - All keys coerced to String (JavaScript behavior)
   - All primitive values boxed to wrapper types
   - No validation required (permissive)

3. **Typed Objects (Phase 2)**
   - Generate `LinkedHashMap<K, V>` matching Record type
   - Validate every key-value pair during compilation
   - Reject type mismatches with clear errors
   - Support primitive-to-wrapper conversions
   - Support widening conversions (int → long, int → double)
   - Reject narrowing conversions
   - Recursive validation for nested Record types

### Critical Features

✅ **Must Have:**
- Type validation infrastructure
- Basic object literals (empty, simple properties)
- Record<string, number> support
- Record<number, string> support (numeric keys)
- Nested Record types
- Clear error messages for type mismatches
- Computed property names
- Spread operator with type checking

⏸️ **Deferred:**
- Method properties (requires function support)
- Getter/setter (not feasible with Map)

❌ **Not Supported:**
- Symbol keys
- Union types (use Object instead)
- Property descriptors
- Prototype chain

### Edge Cases Coverage

**40+ edge cases documented**, including:
- Type mismatches (values, keys, nested)
- Null handling
- Numeric vs string keys
- Primitive to wrapper conversions
- Widening/narrowing conversions
- Spread type compatibility
- Shorthand type validation
- Computed property type validation
- Empty objects with strict types
- And more...

### Success Criteria

**Implementation is complete when:**
1. ✅ All Phase 0-2 tasks completed
2. ✅ Type validation working for Record<K, V>
3. ✅ Clear error messages for all type mismatches
4. ✅ 100+ tests passing covering:
   - Untyped objects (default)
   - Typed objects (Record<K, V>)
   - Nested typed objects
   - Type validation errors
   - All 40+ edge cases
5. ✅ Integration with ExpressionGenerator
6. ✅ Documentation updated
