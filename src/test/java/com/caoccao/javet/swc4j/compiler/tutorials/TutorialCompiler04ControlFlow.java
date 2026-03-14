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

public class TutorialCompiler04ControlFlow {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare a function with if/else branching.
        String code = "export function classify(n: int): String {\n"
                + "  if (n > 0) {\n"
                + "    return \"positive\"\n"
                + "  } else if (n < 0) {\n"
                + "    return \"negative\"\n"
                + "  } else {\n"
                + "    return \"zero\"\n"
                + "  }\n"
                + "}";
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
        // Prepare a function with a while loop.
        code = "export function sumUpTo(n: int): int {\n"
                + "  let sum: int = 0\n"
                + "  let i: int = 1\n"
                + "  while (i <= n) {\n"
                + "    sum = sum + i\n"
                + "    i = i + 1\n"
                + "  }\n"
                + "  return sum\n"
                + "}";
        // Compile and run.
        runner = compiler.compile(code);
        classRunner = runner.createStaticRunner("$");
        int sum = classRunner.invoke("sumUpTo", 10);
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     While loop.");
        System.out.println("*********************************************/");
        System.out.println("sumUpTo(10) = " + sum);
        // Prepare a function with a for loop.
        code = "export function factorial(n: int): int {\n"
                + "  let result: int = 1\n"
                + "  for (let i: int = 2; i <= n; i = i + 1) {\n"
                + "    result = result * i\n"
                + "  }\n"
                + "  return result\n"
                + "}";
        // Compile and run.
        runner = compiler.compile(code);
        classRunner = runner.createStaticRunner("$");
        int fact = classRunner.invoke("factorial", 5);
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     For loop.");
        System.out.println("*********************************************/");
        System.out.println("factorial(5) = " + fact);
    }
}
