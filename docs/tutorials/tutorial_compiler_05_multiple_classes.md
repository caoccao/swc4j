# Tutorial Compiler 05: Multiple Classes

In this tutorial, we are going to learn the following.

* Define multiple classes in a namespace.
* Instantiate one class from another with `new`.
* Call methods across classes.
* Use `this` to call methods within the same class.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Two Classes

Let's define a `Calculator` class and an `App` class that creates a `Calculator` instance and calls its method.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare two classes in the same namespace.
String code = """
        namespace com {
          export class Calculator {
            add(a: int, b: int): int {
              return a + b
            }
          }
          export class App {
            test(): int {
              const calc = new Calculator()
              return calc.add(10, 20)
            }
          }
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.App");
int result = classRunner.invoke("test");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Two classes interacting.");
System.out.println("*********************************************/");
System.out.println("App.test() = " + result);
```

* The output is as follows. `App` creates a `Calculator` instance and calls `add(10, 20)`.

```java
/*********************************************
     Two classes interacting.
*********************************************/
App.test() = 30
```

## This Reference

A class can call its own methods using `this`. Let's define a class with a helper method.

* Append the following code to that Java application.

```java
// Prepare a class that calls its own method via this.
code = """
        namespace com {
          export class MathHelper {
            square(x: int): int {
              return x * x
            }
            sumOfSquares(a: int, b: int): int {
              return this.square(a) + this.square(b)
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.MathHelper");
result = classRunner.invoke("sumOfSquares", 3, 4);
// Print the result.
System.out.println("/*********************************************");
System.out.println("     This reference.");
System.out.println("*********************************************/");
System.out.println("sumOfSquares(3, 4) = " + result);
```

* The output is as follows. `3² + 4² = 9 + 16 = 25`.

```java
/*********************************************
     This reference.
*********************************************/
sumOfSquares(3, 4) = 25
```

## Conclusion

In this tutorial we've learned how to define multiple classes in a namespace, instantiate one class from another with `new`, call methods across classes, and use `this` for intra-class method calls.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler05MultipleClasses.java).
