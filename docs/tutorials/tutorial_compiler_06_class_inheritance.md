# Tutorial Compiler 06: Class Inheritance

In this tutorial, we are going to learn the following.

* Extend a class with `extends`.
* Override methods in a subclass.
* Call parent methods with `super`.
* Use multi-level inheritance.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Simple Inheritance

Let's define an `Animal` base class and override its `speak()` method in `Dog` and `Cat` subclasses.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare classes with inheritance.
String code = """
        namespace com {
          export class Animal {
            speak(): String {
              return "..."
            }
          }
          export class Dog extends Animal {
            speak(): String {
              return "Woof"
            }
          }
          export class Cat extends Animal {
            speak(): String {
              return "Meow"
            }
          }
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Simple inheritance.");
System.out.println("*********************************************/");
System.out.println("Animal: " + runner.createInstanceRunner("com.Animal").invoke("speak"));
System.out.println("Dog:    " + runner.createInstanceRunner("com.Dog").invoke("speak"));
System.out.println("Cat:    " + runner.createInstanceRunner("com.Cat").invoke("speak"));
```

* The output is as follows. Each subclass overrides the `speak()` method.

```java
/*********************************************
     Simple inheritance.
*********************************************/
Animal: ...
Dog:    Woof
Cat:    Meow
```

## Super Calls

A subclass can call its parent's method using `super`.

* Append the following code to that Java application.

```java
// Prepare classes with super calls.
code = """
        namespace com {
          export class Base {
            getValue(): int {
              return 100
            }
          }
          export class Derived extends Base {
            getValue(): int {
              return super.getValue() + 50
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Derived");
int result = classRunner.invoke("getValue");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Super calls.");
System.out.println("*********************************************/");
System.out.println("Derived.getValue() = " + result);
```

* The output is as follows. `Derived.getValue()` calls `super.getValue()` (100) and adds 50.

```java
/*********************************************
     Super calls.
*********************************************/
Derived.getValue() = 150
```

## Multi-Level Inheritance

Classes can extend other subclasses, forming a chain.

* Append the following code to that Java application.

```java
// Prepare multi-level inheritance.
code = """
        namespace com {
          export class A {
            getA(): int { return 1 }
          }
          export class B extends A {
            getB(): int { return 2 }
          }
          export class C extends B {
            getC(): int { return 3 }
            getSum(): int {
              return this.getA() + this.getB() + this.getC()
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.C");
result = classRunner.invoke("getSum");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Multi-level inheritance.");
System.out.println("*********************************************/");
System.out.println("C.getSum() = " + result);
```

* The output is as follows. `C` inherits `getA()` from `A` and `getB()` from `B`, so `1 + 2 + 3 = 6`.

```java
/*********************************************
     Multi-level inheritance.
*********************************************/
C.getSum() = 6
```

## Conclusion

In this tutorial we've learned how to extend classes, override methods, call parent methods with `super`, and use multi-level inheritance. The compiler generates standard JVM class hierarchy bytecode with `invokespecial` for `super` calls.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler06ClassInheritance.java).
