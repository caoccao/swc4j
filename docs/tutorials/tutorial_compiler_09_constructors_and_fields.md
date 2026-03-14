# Tutorial Compiler 09: Constructors and Fields

In this tutorial, we are going to learn the following.

* Define class fields and constructors.
* Use TypeScript parameter property syntax to auto-generate fields.
* Define static fields and methods.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Class Fields

Let's define a class with fields, a constructor that assigns them, and getter methods.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a class with fields and a constructor.
String code = """
        namespace com {
          export class Person {
            name: String
            age: int
            constructor(name: String, age: int) {
              this.name = name
              this.age = age
            }
            getName(): String { return this.name }
            getAge(): int { return this.age }
          }
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Person", "Alice", 30);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Class fields.");
System.out.println("*********************************************/");
System.out.println("name = " + (String) classRunner.invoke("getName"));
System.out.println("age  = " + (int) classRunner.invoke("getAge"));
```

* The output is as follows. The constructor receives the arguments and assigns them to the fields.

```java
/*********************************************
     Class fields.
*********************************************/
name = Alice
age  = 30
```

## Parameter Properties

TypeScript parameter property syntax (`constructor(public x: int)`) automatically generates a field and assigns the constructor argument to it. This avoids writing the field declaration and assignment separately. You still define getters yourself.

* Append the following code to that Java application.

```java
// Prepare a class with parameter properties.
code = """
        namespace com {
          export class Point {
            constructor(public x: int, public y: int) {
            }
            getX(): int { return this.x }
            getY(): int { return this.y }
            sum(): int { return this.x + this.y }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.Point", 3, 7);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Parameter properties.");
System.out.println("*********************************************/");
System.out.println("x = " + (int) classRunner.invoke("getX"));
System.out.println("y = " + (int) classRunner.invoke("getY"));
System.out.println("sum = " + (int) classRunner.invoke("sum"));
```

* The output is as follows. The `public x: int` parameter property creates a field `x` and assigns the constructor argument automatically — no separate field declaration or `this.x = x` needed.

```java
/*********************************************
     Parameter properties.
*********************************************/
x = 3
y = 7
sum = 10
```

## Static Fields and Methods

A class can have static fields and methods. Static fields are shared across all instances, and static methods are called on the class itself.

* Append the following code to that Java application.

```java
// Prepare a class with static fields and methods.
code = """
        namespace com {
          export class Counter {
            static count: int = 0
            static getCount(): int {
              return Counter.count
            }
            static increment(): void {
              Counter.count = Counter.count + 1
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
ByteCodeClassRunner staticRunner = runner.createStaticRunner("com.Counter");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Static fields and methods.");
System.out.println("*********************************************/");
System.out.println("count = " + (int) staticRunner.invoke("getCount"));
staticRunner.invoke("increment");
staticRunner.invoke("increment");
staticRunner.invoke("increment");
System.out.println("count after 3 increments = " + (int) staticRunner.invoke("getCount"));
```

* The output is as follows. The static field `count` is shared and persists across method calls.

```java
/*********************************************
     Static fields and methods.
*********************************************/
count = 0
count after 3 increments = 3
```

## Conclusion

In this tutorial we've learned how to define class fields and constructors, use parameter property syntax for auto-generated fields, and define static fields and methods. The compiler generates standard JVM field bytecode (`putfield`, `getfield`, `putstatic`, `getstatic`) and constructor bytecode (`invokespecial <init>`).

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler09ConstructorsAndFields.java).
