# Tutorial Compiler 02: Variables and Arithmetic

In this tutorial, we are going to learn the following.

* Declare typed local variables with `const` and `let`.
* Perform integer arithmetic with `+`, `-`, `*`, `/`.
* Use floating-point types (`double`).
* Combine multiple operators in a single expression.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## Integer Arithmetic

Let's declare two `int` variables and add them together.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a function that adds two integers.
String code = "export function add(): int {\n"
        + "  const a: int = 5\n"
        + "  const b: int = 10\n"
        + "  return a + b\n"
        + "}";
// Compile and run.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createStaticRunner("$");
int result = classRunner.invoke("add");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Integer arithmetic.");
System.out.println("*********************************************/");
System.out.println("5 + 10 = " + result);
```

* The output is as follows.

```
/*********************************************
     Integer arithmetic.
*********************************************/
5 + 10 = 15
```

## Floating Point

The compiler supports `double` and `float` types. Let's multiply two doubles.

* Append the following code to that Java application.

```java
// Prepare a function that multiplies two doubles.
code = "export function multiply(): double {\n"
        + "  const x: double = 3.14\n"
        + "  const y: double = 2.0\n"
        + "  return x * y\n"
        + "}";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
double doubleResult = classRunner.invoke("multiply");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Floating point arithmetic.");
System.out.println("*********************************************/");
System.out.println("3.14 * 2.0 = " + doubleResult);
```

* The output is as follows.

```
/*********************************************
     Floating point arithmetic.
*********************************************/
3.14 * 2.0 = 6.28
```

## Mixed Expressions

Multiple operators can be combined in a single expression. The SWC parser handles operator precedence correctly (`*` and `/` before `+` and `-`).

* Append the following code to that Java application.

```java
// Prepare a function with a mixed expression.
code = "export function compute(): int {\n"
        + "  const a: int = 10\n"
        + "  const b: int = 3\n"
        + "  const c: int = 4\n"
        + "  const d: int = 20\n"
        + "  const e: int = 5\n"
        + "  return a + b * c - d / e\n"
        + "}";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
result = classRunner.invoke("compute");
// Print the result.
System.out.println("/*********************************************");
System.out.println("     Mixed expression.");
System.out.println("*********************************************/");
System.out.println("10 + 3 * 4 - 20 / 5 = " + result);
```

* The output is as follows. `3 * 4 = 12`, `20 / 5 = 4`, so `10 + 12 - 4 = 18`.

```
/*********************************************
     Mixed expression.
*********************************************/
10 + 3 * 4 - 20 / 5 = 18
```

## Conclusion

In this tutorial we've learned how to declare typed variables, perform integer and floating-point arithmetic, and combine multiple operators in a single expression. The compiler generates standard JVM bytecode instructions (`iadd`, `imul`, `dmul`, etc.) for each operation.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler02VariablesAndArithmetic.java).
