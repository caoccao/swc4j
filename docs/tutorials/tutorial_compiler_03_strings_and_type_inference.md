# Tutorial Compiler 03: Strings and Type Inference

In this tutorial, we are going to learn the following.

* Concatenate strings with the `+` operator.
* Rely on the compiler's type inference to omit type annotations.
* Concatenate a string with a number.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## String Concatenation

Let's declare two `String` variables and concatenate them.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a function that concatenates two strings.
String code = """
        export function greet(): String {
          const first: String = "Hello, "
          const second: String = "World!"
          return first + second
        }""";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createStaticRunner("$");
String result = classRunner.invoke("greet");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     String concatenation.");
System.out.println("*********************************************/");
System.out.println(result);
```

* The output is as follows. The compiler uses `StringBuilder` under the hood to concatenate strings, just like `javac` does.

```java
/*********************************************
     String concatenation.
*********************************************/
Hello, World!
```

## Type Inference

Type annotations can be omitted when the compiler can infer the type from the literal value. For example, `5` is inferred as `int` and `"hello"` is inferred as `String`.

* Append the following code to that Java application.

```java
// Prepare a function that relies on type inference.
code = """
        export function compute(): String {
          const x = 5
          const y = 10
          const label = "sum"
          return label + ": " + (x + y)
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
result = classRunner.invoke("compute");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Type inference.");
System.out.println("*********************************************/");
System.out.println(result);
```

* The output is as follows. The compiler inferred `x` and `y` as `int` and `label` as `String` without any explicit type annotations.

```java
/*********************************************
     Type inference.
*********************************************/
sum: 15
```

## String and Number Concatenation

When a `String` is concatenated with a number using `+`, the number is automatically converted to a string, just like in Java.

* Append the following code to that Java application.

```java
// Prepare a function that concatenates a string with a number.
code = """
        export function format(): String {
          const value: int = 42
          return "value: " + value
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
result = classRunner.invoke("format");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     String and number concatenation.");
System.out.println("*********************************************/");
System.out.println(result);
```

* The output is as follows.

```java
/*********************************************
     String and number concatenation.
*********************************************/
value: 42
```

## Conclusion

In this tutorial we've learned how to concatenate strings, rely on the compiler's type inference to omit type annotations, and mix strings with numbers in concatenation expressions.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler03StringsAndTypeInference.java).
