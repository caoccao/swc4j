# Tutorial Compiler 07: Interfaces and Abstract Classes

In this tutorial, we are going to learn the following.

* Implement a Java interface with `implements`.
* Define an abstract class with abstract methods.
* Combine `extends` and `implements` in a single class.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Implement an Interface

A compiled class can implement standard Java interfaces. To use a Java type like `Runnable` in TypeScript, import it with `import { Runnable } from 'java.lang'`.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a class that implements Runnable.
String code = """
        import { Runnable } from 'java.lang'
        namespace com {
          export class Task implements Runnable {
            run(): void { }
          }
        }""";
// Compile and verify.
ByteCodeRunner runner = compiler.compile(code);
Class<?> taskClass = runner.getClass("com.Task");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Implement an interface.");
System.out.println("*********************************************/");
System.out.println("Task implements Runnable: " + Runnable.class.isAssignableFrom(taskClass));
// Instantiate and invoke.
Runnable task = (Runnable) taskClass.getConstructor().newInstance();
task.run();
System.out.println("task.run() executed successfully.");
```

* The output is as follows. The compiled class is a real JVM class that implements `Runnable` and can be cast to it.

```java
/*********************************************
     Implement an interface.
*********************************************/
Task implements Runnable: true
task.run() executed successfully.
```

## Abstract Class

An abstract class defines methods without a body. Concrete subclasses must implement them.

* Append the following code to that Java application.

```java
// Prepare an abstract class and a concrete subclass.
code = """
        namespace com {
          export abstract class Base {
            abstract compute(): int
            helper(): int { return 100 }
          }
          export class Derived extends Base {
            compute(): int {
              return this.helper() + 1
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Derived");
int result = classRunner.invoke("compute");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Abstract class.");
System.out.println("*********************************************/");
System.out.println("Derived.compute() = " + result);
```

* The output is as follows. `Derived.compute()` calls the inherited `helper()` (100) and adds 1.

```java
/*********************************************
     Abstract class.
*********************************************/
Derived.compute() = 101
```

## Extends and Implements

A class can extend a base class and implement an interface at the same time.

* Append the following code to that Java application.

```java
// Prepare a class that extends and implements.
code = """
        import { Runnable } from 'java.lang'
        namespace com {
          export class Animal {
            speak(): String { return "..." }
          }
          export class ServiceDog extends Animal implements Runnable {
            speak(): String { return "Woof" }
            run(): void { }
          }
        }""";
// Compile and verify.
runner = compiler.compile(code);
Class<?> dogClass = runner.getClass("com.ServiceDog");
Class<?> animalClass = runner.getClass("com.Animal");
ByteCodeClassRunner dogRunner = runner.createInstanceRunner("com.ServiceDog");
String speak = dogRunner.invoke("speak");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Extends and implements.");
System.out.println("*********************************************/");
System.out.println("ServiceDog extends Animal:      " + animalClass.isAssignableFrom(dogClass));
System.out.println("ServiceDog implements Runnable:  " + Runnable.class.isAssignableFrom(dogClass));
System.out.println("ServiceDog.speak() = " + speak);
```

* The output is as follows. `ServiceDog` is both an `Animal` and a `Runnable`.

```java
/*********************************************
     Extends and implements.
*********************************************/
ServiceDog extends Animal:      true
ServiceDog implements Runnable:  true
ServiceDog.speak() = Woof
```

## Conclusion

In this tutorial we've learned how to implement Java interfaces, define abstract classes, and combine `extends` with `implements`. The compiled classes fully participate in the Java type system — they can be cast to interfaces and checked with `instanceof`.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler07InterfacesAndAbstractClasses.java).
