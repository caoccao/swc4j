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

public class TutorialCompiler02VariablesAndArithmetic {
    public static void main(String[] args) throws Exception {
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
    }
}
