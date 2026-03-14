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

public class TutorialCompiler01HelloBytecode {
    public static void main(String[] args) throws Exception {
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
    }
}
