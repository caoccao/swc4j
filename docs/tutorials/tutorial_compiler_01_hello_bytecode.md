# Tutorial Compiler 01: Hello Bytecode

In this tutorial, we are going to learn the following.

* Create a bytecode compiler with options targeting JDK 17.
* Compile a TypeScript function to JVM bytecode.
* Run the compiled bytecode on the JVM without V8 or Javet.
* Compile and run a class that returns a string.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Create a Compiler and Run a Function

The bytecode compiler compiles TypeScript-like syntax directly into JVM bytecode. The compiled code can be loaded and executed via reflection, just like any Java class. No JavaScript runtime is required.

A top-level `export function` is the simplest thing we can compile. The compiler places it as a static method in a default class named `$`.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a simple TypeScript function.
String code = "export function getAnswer(): int {\n"
        + "  return 42\n"
        + "}";
// Compile the code to JVM bytecode.
ByteCodeRunner runner = compiler.compile(code);
// Invoke the compiled function as a static method.
ByteCodeClassRunner classRunner = runner.createStaticRunner("$");
int result = classRunner.invoke("getAnswer");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Compile and run a function.");
System.out.println("*********************************************/");
System.out.println("The answer is: " + result);
```

* The output is as follows. As we can see, the TypeScript function is compiled to JVM bytecode and returns the integer 42 directly on the JVM.

```
/*********************************************
     Compile and run a function.
*********************************************/
The answer is: 42
```

## Return a String

The compiler supports string literals. Let's compile a class that returns a greeting string.

* Append the following code to that Java application.

```java
// Prepare a TypeScript code snippet that returns a string.
code = "namespace com {\n"
        + "  export class Greeter {\n"
        + "    greet(): String {\n"
        + "      return \"Hello, JVM!\"\n"
        + "    }\n"
        + "  }\n"
        + "}";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createInstanceRunner("com.Greeter");
String greeting = classRunner.invoke("greet");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Compile and run a string return.");
System.out.println("*********************************************/");
System.out.println(greeting);
```

* The output is as follows. The compiled method returns a standard Java `String` object.

```
/*********************************************
     Compile and run a string return.
*********************************************/
Hello, JVM!
```

## Conclusion

In this tutorial we've learned how to create a bytecode compiler, compile TypeScript classes to JVM bytecode, and run the compiled code directly on the JVM. No V8 engine or JavaScript runtime was needed — the TypeScript code was compiled to standard Java 17 bytecode and executed via reflection.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler01HelloBytecode.java).
