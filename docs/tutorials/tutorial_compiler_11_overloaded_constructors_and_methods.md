# Tutorial Compiler 11: Overloaded Constructors and Methods

In this tutorial, we are going to learn the following.

* Define overloaded methods with the same name but different parameter types or arity.
* Define overloaded constructors.
* Chain overloaded constructors with `this(...)`.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Overloaded Methods

Unlike standard TypeScript, the bytecode compiler allows multiple method declarations with the same name as long as the parameter types or arity differ — just like Java.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a class with overloaded methods.
String code = """
        namespace com {
          export class Calculator {
            add(a: int, b: int): int { return a + b }
            add(a: double, b: double): double { return a + b }
            add(a: String, b: String): String { return a + b }
          }
        }""";
// Compile.
ByteCodeRunner runner = compiler.compile(code);
Class<?> clazz = runner.getClass("com.Calculator");
Object calc = clazz.getConstructor().newInstance();
// Invoke each overload via reflection.
int intResult = (int) clazz.getMethod("add", int.class, int.class).invoke(calc, 1, 2);
double doubleResult = (double) clazz.getMethod("add", double.class, double.class).invoke(calc, 1.5, 2.5);
String strResult = (String) clazz.getMethod("add", String.class, String.class).invoke(calc, "Hello", "World");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Overloaded methods.");
System.out.println("*********************************************/");
System.out.println("add(1, 2)             = " + intResult);
System.out.println("add(1.5, 2.5)         = " + doubleResult);
System.out.println("add(\"Hello\", \"World\") = " + strResult);
```

* The output is as follows. The compiler generates three separate JVM methods, each with a distinct descriptor.

```java
/*********************************************
     Overloaded methods.
*********************************************/
add(1, 2)             = 3
add(1.5, 2.5)         = 4.0
add("Hello", "World") = HelloWorld
```

## Overloaded Constructors

A class can have multiple constructors with different parameter lists.

* Append the following code to that Java application.

```java
// Prepare a class with overloaded constructors.
code = """
        namespace com {
          export class Point {
            x: int = 0
            y: int = 0
            constructor() { }
            constructor(x: int) { this.x = x }
            constructor(x: int, y: int) { this.x = x; this.y = y }
            getX(): int { return this.x }
            getY(): int { return this.y }
          }
        }""";
// Compile.
runner = compiler.compile(code);
clazz = runner.getClass("com.Point");
// Invoke each constructor overload.
Object p0 = clazz.getConstructor().newInstance();
Object p1 = clazz.getConstructor(int.class).newInstance(10);
Object p2 = clazz.getConstructor(int.class, int.class).newInstance(3, 7);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Overloaded constructors.");
System.out.println("*********************************************/");
System.out.println("Point()      -> (" + clazz.getMethod("getX").invoke(p0) + ", " + clazz.getMethod("getY").invoke(p0) + ")");
System.out.println("Point(10)    -> (" + clazz.getMethod("getX").invoke(p1) + ", " + clazz.getMethod("getY").invoke(p1) + ")");
System.out.println("Point(3, 7)  -> (" + clazz.getMethod("getX").invoke(p2) + ", " + clazz.getMethod("getY").invoke(p2) + ")");
```

* The output is as follows.

```java
/*********************************************
     Overloaded constructors.
*********************************************/
Point()      -> (0, 0)
Point(10)    -> (10, 0)
Point(3, 7)  -> (3, 7)
```

## Constructor Chaining with `this(...)`

Overloaded constructors can delegate to each other using `this(...)`, avoiding code duplication.

* Append the following code to that Java application.

```java
// Prepare a class with constructor chaining.
code = """
        namespace com {
          export class Rect {
            x: int
            y: int
            w: int
            h: int
            constructor(x: int, y: int, w: int, h: int) {
              this.x = x; this.y = y; this.w = w; this.h = h
            }
            constructor(w: int, h: int) {
              this(0, 0, w, h)
            }
            constructor(size: int) {
              this(size, size)
            }
            area(): int { return this.w * this.h }
            getX(): int { return this.x }
            getY(): int { return this.y }
          }
        }""";
// Compile.
runner = compiler.compile(code);
clazz = runner.getClass("com.Rect");
// Invoke each constructor.
Object full = clazz.getConstructor(int.class, int.class, int.class, int.class).newInstance(1, 2, 10, 20);
Object wh = clazz.getConstructor(int.class, int.class).newInstance(10, 20);
Object square = clazz.getConstructor(int.class).newInstance(5);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Constructor chaining.");
System.out.println("*********************************************/");
System.out.println("Rect(1,2,10,20) -> area=" + clazz.getMethod("area").invoke(full) + " x=" + clazz.getMethod("getX").invoke(full));
System.out.println("Rect(10,20)     -> area=" + clazz.getMethod("area").invoke(wh) + " x=" + clazz.getMethod("getX").invoke(wh));
System.out.println("Rect(5)         -> area=" + clazz.getMethod("area").invoke(square) + " x=" + clazz.getMethod("getX").invoke(square));
```

* The output is as follows. `Rect(5)` chains to `Rect(5, 5)` which chains to `Rect(0, 0, 5, 5)`.

```java
/*********************************************
     Constructor chaining.
*********************************************/
Rect(1,2,10,20) -> area=200 x=1
Rect(10,20)     -> area=200 x=0
Rect(5)         -> area=25 x=0
```

## Conclusion

In this tutorial we've learned how to define overloaded methods and constructors, and chain constructors with `this(...)`. The compiler generates separate JVM methods with distinct descriptors for each overload, matching Java's overloading semantics.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler11OverloadedConstructorsAndMethods.java).
