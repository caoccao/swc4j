# Tutorial Compiler 04: Control Flow

In this tutorial, we are going to learn the following.

* Use if/else for conditional branching.
* Use a while loop for iterative computation.
* Use a for loop with a counter variable.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.

## If-Else

Let's write a function that classifies a number as positive, negative, or zero using if/else chains.

* Create a simple Java application with the code as follows.

```java
// Create a compiler targeting JDK 17.
ByteCodeCompiler compiler = ByteCodeCompiler.of(
        ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .build());
// Prepare a function with if/else branching.
String code = """
        export function classify(n: int): String {
          if (n > 0) {
            return "positive"
          } else if (n < 0) {
            return "negative"
          } else {
            return "zero"
          }
        }""";
// Compile and run with different arguments.
ByteCodeRunner runner = compiler.compile(code);
ByteCodeClassRunner classRunner = runner.createStaticRunner("$");
// Print the results.
System.out.println("/*********************************************");
System.out.println("     If-Else branching.");
System.out.println("*********************************************/");
System.out.println("classify(42)  = " + classRunner.invoke("classify", 42));
System.out.println("classify(-7)  = " + classRunner.invoke("classify", -7));
System.out.println("classify(0)   = " + classRunner.invoke("classify", 0));
```

* The output is as follows.

```java
/*********************************************
     If-Else branching.
*********************************************/
classify(42)  = positive
classify(-7)  = negative
classify(0)   = zero
```

## While Loop

Let's compute the sum of integers from 1 to N using a while loop.

* Append the following code to that Java application.

```java
// Prepare a function with a while loop.
code = """
        export function sumUpTo(n: int): int {
          let sum: int = 0
          let i: int = 1
          while (i <= n) {
            sum = sum + i
            i = i + 1
          }
          return sum
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
int sum = classRunner.invoke("sumUpTo", 10);
// Print the result.
System.out.println("/*********************************************");
System.out.println("     While loop.");
System.out.println("*********************************************/");
System.out.println("sumUpTo(10) = " + sum);
```

* The output is as follows. The sum of 1 through 10 is 55.

```java
/*********************************************
     While loop.
*********************************************/
sumUpTo(10) = 55
```

## For Loop

Let's compute the factorial of a number using a for loop.

* Append the following code to that Java application.

```java
// Prepare a function with a for loop.
code = """
        export function factorial(n: int): int {
          let result: int = 1
          for (let i: int = 2; i <= n; i = i + 1) {
            result = result * i
          }
          return result
        }""";
// Compile and run.
runner = compiler.compile(code);
classRunner = runner.createStaticRunner("$");
int fact = classRunner.invoke("factorial", 5);
// Print the result.
System.out.println("/*********************************************");
System.out.println("     For loop.");
System.out.println("*********************************************/");
System.out.println("factorial(5) = " + fact);
```

* The output is as follows. 5! = 120.

```java
/*********************************************
     For loop.
*********************************************/
factorial(5) = 120
```

## Conclusion

In this tutorial we've learned how to use if/else branching, while loops, and for loops in compiled code. The compiler generates proper JVM control flow bytecode (`goto`, `if_icmp*`, etc.) for each construct.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/compiler/tutorials/TutorialCompiler04ControlFlow.java).
