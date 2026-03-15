# Tutorial Compiler 12: TypeScript Interfaces

In this tutorial, we are going to learn the following.

* Define a TypeScript interface and compile it to a Java interface.
* Implement a TypeScript interface in a class.
* Extend interfaces from other interfaces.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Define and Implement an Interface

A TypeScript `interface` with properties compiles to a Java interface with abstract getter and setter methods. A class that `implements` the interface provides the concrete implementations.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare an interface and an implementing class.
String code = """
        namespace com {
          export interface Greeter {
            name: String
            greet(): String
          }
          export class SimpleGreeter implements Greeter {
            name: String = ""
            constructor(name: String) {
              this.name = name
            }
            getName(): String { return this.name }
            setName(name: String): void { this.name = name }
            greet(): String { return "Hello, " + this.name }
          }
        }""";
// Compile.
ByteCodeRunner runner = compiler.compile(code);
Class<?> greeterInterface = runner.getClass("com.Greeter");
Class<?> implClass = runner.getClass("com.SimpleGreeter");
// Verify the interface.
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.SimpleGreeter", "Alice");
String greeting = classRunner.invoke("greet");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Define and implement an interface.");
System.out.println("*********************************************/");
System.out.println("Greeter is interface:              " + greeterInterface.isInterface());
System.out.println("SimpleGreeter implements Greeter:  " + greeterInterface.isAssignableFrom(implClass));
System.out.println("greeting = " + greeting);
```

* The output is as follows. The TS `interface` compiles to a real Java interface, and the class implements it.

```java
/*********************************************
     Define and implement an interface.
*********************************************/
Greeter is interface:              true
SimpleGreeter implements Greeter:  true
greeting = Hello, Alice
```

## Multiple Implementations

Multiple classes can implement the same interface.

* Append the following code to that Java application.

```java
// Prepare an interface with two implementations.
code = """
        namespace com {
          export interface Shape {
            area(): double
          }
          export class Circle implements Shape {
            radius: double
            constructor(r: double) {
              this.radius = r
            }
            area(): double { return 3.14159 * this.radius * this.radius }
          }
          export class Square implements Shape {
            side: double
            constructor(s: double) {
              this.side = s
            }
            area(): double { return this.side * this.side }
          }
        }""";
// Compile.
runner = compiler.compile(code);
Class<?> shapeInterface = runner.getClass("com.Shape");
double circleArea = runner.createInstanceRunner("com.Circle", 5.0).invoke("area");
double squareArea = runner.createInstanceRunner("com.Square", 4.0).invoke("area");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Multiple implementations.");
System.out.println("*********************************************/");
System.out.println("Shape is interface:  " + shapeInterface.isInterface());
System.out.println("Circle area(5.0)  = " + circleArea);
System.out.println("Square area(4.0)  = " + squareArea);
```

* The output is as follows.

```java
/*********************************************
     Multiple implementations.
*********************************************/
Shape is interface:  true
Circle area(5.0)  = 78.53975
Square area(4.0)  = 16.0
```

## Interface Extends

TypeScript interfaces can extend other interfaces. This compiles to Java interface inheritance.

* Append the following code to that Java application.

```java
// Prepare interfaces with extends.
code = """
        namespace com {
          export interface Named {
            name: String
          }
          export interface Aged {
            age: int
          }
          export interface Person extends Named, Aged {
            greet(): String
          }
          export class Student implements Person {
            name: String = ""
            age: int = 0
            constructor(name: String, age: int) {
              this.name = name
              this.age = age
            }
            getName(): String { return this.name }
            setName(name: String): void { this.name = name }
            getAge(): int { return this.age }
            setAge(age: int): void { this.age = age }
            greet(): String { return this.name + " (" + this.age + ")" }
          }
        }""";
// Compile.
runner = compiler.compile(code);
Class<?> namedInterface = runner.getClass("com.Named");
Class<?> agedInterface = runner.getClass("com.Aged");
Class<?> personInterface = runner.getClass("com.Person");
Class<?> studentClass = runner.getClass("com.Student");
String greet = runner.createInstanceRunner("com.Student", "Bob", 20).invoke("greet");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Interface extends.");
System.out.println("*********************************************/");
System.out.println("Person extends Named:      " + namedInterface.isAssignableFrom(personInterface));
System.out.println("Person extends Aged:       " + agedInterface.isAssignableFrom(personInterface));
System.out.println("Student implements Person: " + personInterface.isAssignableFrom(studentClass));
System.out.println("Student implements Named:  " + namedInterface.isAssignableFrom(studentClass));
System.out.println("greet = " + greet);
```

* The output is as follows. `Person` extends both `Named` and `Aged`, and `Student` implements all three.

```java
/*********************************************
     Interface extends.
*********************************************/
Person extends Named:      true
Person extends Aged:       true
Student implements Person: true
Student implements Named:  true
greet = Bob (20)
```

## Conclusion

In this tutorial we've learned how to define TypeScript interfaces that compile to Java interfaces, implement them in classes, and extend interfaces from other interfaces. Interface properties automatically generate abstract getter and setter methods in the compiled Java interface.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler12TypeScriptInterfaces.java).
