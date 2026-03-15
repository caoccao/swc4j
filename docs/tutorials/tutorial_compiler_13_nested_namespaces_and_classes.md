# Tutorial Compiler 13: Nested Namespaces and Classes

In this tutorial, we are going to learn the following.

* Nest namespaces to produce multi-level Java package names.
* Use companion namespaces to group classes like Java static nested classes.
* Instantiate classes across namespaces using qualified names.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Nested Namespaces

Namespaces can be nested to produce dotted Java package names. `namespace com { namespace example { ... } }` produces classes in the `com.example` package.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare nested namespaces.
String code = """
        namespace com {
          namespace example {
            export class Hello {
              greet(): String { return "hello from com.example" }
            }
          }
          namespace util {
            export class Helper {
              help(): String { return "help from com.util" }
            }
          }
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Nested namespaces.");
System.out.println("*********************************************/");
System.out.println(runner.createInstanceRunner("com.example.Hello").invoke("greet"));
System.out.println(runner.createInstanceRunner("com.util.Helper").invoke("help"));
```

* The output is as follows. Each nested namespace becomes a level in the Java package name.

```java
/*********************************************
     Nested namespaces.
*********************************************/
hello from com.example
help from com.util
```

## Companion Namespaces (Static Nested Classes)

TypeScript doesn't have Java-style inner classes that capture an outer instance. However, a namespace with the same name as a class creates a "companion namespace" — its classes behave like Java static nested classes, scoped under the outer class name.

* Append the following code to that Java application.

```java
// Prepare a class with a companion namespace.
code = """
        namespace com {
          export class Outer {
            getValue(): int { return 1 }
          }
          namespace Outer {
            export class Inner {
              getValue(): int { return 2 }
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Companion namespaces.");
System.out.println("*********************************************/");
System.out.println("Outer.getValue() = " + (int) runner.createInstanceRunner("com.Outer").invoke("getValue"));
System.out.println("Inner.getValue() = " + (int) runner.createInstanceRunner("com.Outer.Inner").invoke("getValue"));
```

* The output is as follows. `Outer` lives at `com.Outer` and `Inner` lives at `com.Outer.Inner`.

```java
/*********************************************
     Companion namespaces.
*********************************************/
Outer.getValue() = 1
Inner.getValue() = 2
```

## Cross-Namespace Instantiation

A class in one namespace can create an instance of a class in another namespace using the fully qualified name in the `new` expression.

* Append the following code to that Java application.

```java
// Prepare classes that reference each other across namespaces.
code = """
        namespace com {
          namespace types {
            export class Point {
              x: int
              y: int
              constructor(x: int, y: int) { this.x = x; this.y = y }
              getX(): int { return this.x }
              getY(): int { return this.y }
            }
          }
          namespace app {
            export class Main {
              run(): int {
                const p = new com.types.Point(3, 4)
                return p.getX() + p.getY()
              }
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
int result = runner.createInstanceRunner("com.app.Main").invoke("run");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Cross-namespace instantiation.");
System.out.println("*********************************************/");
System.out.println("Main.run() = " + result);
```

* The output is as follows. `Main` in `com.app` creates a `Point` from `com.types` using `new com.types.Point(3, 4)`.

```java
/*********************************************
     Cross-namespace instantiation.
*********************************************/
Main.run() = 7
```

## Conclusion

In this tutorial we've learned how to nest namespaces to create multi-level Java packages, use companion namespaces to group classes like static nested classes, and instantiate classes across namespaces using qualified names. Note that TypeScript does not support Java-style inner classes that capture an outer instance — namespace nesting is purely a name-scoping mechanism.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler13NestedNamespacesAndClasses.java).
