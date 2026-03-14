# Tutorial Compiler 08: Functions and Recursion

In this tutorial, we are going to learn the following.

* Define methods with typed parameters.
* Implement recursive methods.
* Use default parameter values.
* Use type alias maps to register Java types.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Method Parameters

Let's define a class with a method that takes typed parameters.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a class with a parameterized method.
String code = """
        namespace com {
          export class Calculator {
            add(a: int, b: int): int {
              return a + b
            }
            multiply(a: double, b: double): double {
              return a * b
            }
          }
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Calculator");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Method parameters.");
System.out.println("*********************************************/");
System.out.println("add(3, 4) = " + (int) classRunner.invoke("add", 3, 4));
System.out.println("multiply(2.5, 4.0) = " + (double) classRunner.invoke("multiply", 2.5, 4.0));
```

* The output is as follows.

```java
/*********************************************
     Method parameters.
*********************************************/
add(3, 4) = 7
multiply(2.5, 4.0) = 10.0
```

## Recursion

A method can call itself recursively. The compiler generates `invokevirtual` instructions that target the same method.

* Append the following code to that Java application.

```java
// Prepare a class with a recursive method.
code = """
        namespace com {
          export class MathUtils {
            factorial(n: int): int {
              if (n <= 1) return 1
              return n * this.factorial(n - 1)
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.MathUtils");
int result = classRunner.invoke("factorial", 6);
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Recursion.");
System.out.println("*********************************************/");
System.out.println("factorial(6) = " + result);
```

* The output is as follows. 6! = 720.

```java
/*********************************************
     Recursion.
*********************************************/
factorial(6) = 720
```

## Default Parameters

A method parameter can have a default value. The compiler generates an overloaded method that callers without the argument can use.

* Append the following code to that Java application.

```java
// Prepare a class with a default parameter.
code = """
        namespace com {
          export class Formatter {
            format(value: int, uppercase: boolean = false): String {
              if (uppercase) {
                return "VALUE: " + value
              }
              return "value: " + value
            }
          }
        }""";
// Compile and run.
runner = compiler.compile(code);
Class<?> formatterClass = runner.getClass("com.Formatter");
Object formatter = formatterClass.getConstructor().newInstance();
// Call with both arguments.
String withArg = (String) formatterClass
        .getMethod("format", int.class, boolean.class)
        .invoke(formatter, 42, true);
// Call without the default argument.
String withDefault = (String) formatterClass
        .getMethod("format", int.class)
        .invoke(formatter, 42);
// Print the results.
System.out.println("/*********************************************");
System.out.println("     Default parameters.");
System.out.println("*********************************************/");
System.out.println("format(42, true) = " + withArg);
System.out.println("format(42)       = " + withDefault);
```

* The output is as follows. When `uppercase` is omitted, the default value `false` is used.

```java
/*********************************************
     Default parameters.
*********************************************/
format(42, true) = VALUE: 42
format(42)       = value: 42
```

## Type Alias Map

In [Tutorial 07](tutorial_compiler_07_interfaces_and_abstract_classes.md) we used `import { Runnable } from 'java.lang'` in TypeScript to reference a Java type. An alternative is to register type aliases via `typeAliasMap` in the compiler options. This lets the TypeScript code use short names without import statements.

Note: calling `typeAliasMap()` on the builder **replaces** the default aliases (which include primitives, `String`, `Object`, etc.), so include all types the code needs.

* Append the following code to that Java application.

```java
// Create a compiler with a custom type alias map.
Map<String, String> typeAliases = Map.of(
        "int", "int",
        "void", "void",
        "String", "java.lang.String",
        "Runnable", "java.lang.Runnable");
compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .typeAliasMap(typeAliases)
                .build());
// Prepare a class that uses Runnable without an import statement.
code = """
        namespace com {
          export class Worker implements Runnable {
            run(): void { }
            name(): String { return "Worker" }
          }
        }""";
// Compile and verify.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.Worker");
System.out.println("/*********************************************");
System.out.println("     Type alias map.");
System.out.println("*********************************************/");
System.out.println("Worker.name() = " + (String) classRunner.invoke("name"));
System.out.println("Worker implements Runnable: "
        + Runnable.class.isAssignableFrom(runner.getClass("com.Worker")));
```

* The output is as follows. The `Runnable` type was resolved via the type alias map without any `import` in the TypeScript code.

```java
/*********************************************
     Type alias map.
*********************************************/
Worker.name() = Worker
Worker implements Runnable: true
```

## Conclusion

In this tutorial we've learned how to define methods with typed parameters, implement recursion, use default parameter values, and register Java types via `typeAliasMap`. The compiler handles JVM local variable slots, recursive `invokevirtual` calls, and generates method overloads for default parameters.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler08FunctionsAndRecursion.java).
