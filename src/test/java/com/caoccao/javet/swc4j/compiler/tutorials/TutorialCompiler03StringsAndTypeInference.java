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

public class TutorialCompiler03StringsAndTypeInference {
    public static void main(String[] args) throws Exception {
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
    }
}
