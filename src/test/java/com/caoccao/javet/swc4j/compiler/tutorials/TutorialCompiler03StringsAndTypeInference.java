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

import com.caoccao.javet.swc4j.compiler.ByteCodeClassRunner;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.ByteCodeRunner;
import com.caoccao.javet.swc4j.compiler.JdkVersion;

public class TutorialCompiler03StringsAndTypeInference {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare a function that concatenates two strings.
        String code = "export function greet(): String {\n"
                + "  const first: String = \"Hello, \"\n"
                + "  const second: String = \"World!\"\n"
                + "  return first + second\n"
                + "}";
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
        code = "export function compute(): String {\n"
                + "  const x = 5\n"
                + "  const y = 10\n"
                + "  const label = \"sum\"\n"
                + "  return label + \": \" + (x + y)\n"
                + "}";
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
        code = "export function format(): String {\n"
                + "  const value: int = 42\n"
                + "  return \"value: \" + value\n"
                + "}";
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
