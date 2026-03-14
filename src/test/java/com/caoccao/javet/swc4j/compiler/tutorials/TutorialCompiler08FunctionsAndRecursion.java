/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.compiler.tutorials;

import com.caoccao.javet.swc4j.compiler.*;

import java.util.Map;

public class TutorialCompiler08FunctionsAndRecursion {
    public static void main(String[] args) throws Exception {
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
    }
}
