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

public class TutorialCompiler05MultipleClasses {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare two classes in the same namespace.
        String code = """
                namespace com {
                  export class Calculator {
                    add(a: int, b: int): int {
                      return a + b
                    }
                  }
                  export class App {
                    test(): int {
                      const calc = new Calculator()
                      return calc.add(10, 20)
                    }
                  }
                }""";
        // Compile and run.
        ByteCodeRunner runner = compiler.compile(code);
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.App");
        int result = classRunner.invoke("test");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Two classes interacting.");
        System.out.println("*********************************************/");
        System.out.println("App.test() = " + result);
        // Prepare a class that calls its own method via this.
        code = """
                namespace com {
                  export class MathHelper {
                    square(x: int): int {
                      return x * x
                    }
                    sumOfSquares(a: int, b: int): int {
                      return this.square(a) + this.square(b)
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        classRunner = runner.createInstanceRunner("com.MathHelper");
        result = classRunner.invoke("sumOfSquares", 3, 4);
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     This reference.");
        System.out.println("*********************************************/");
        System.out.println("sumOfSquares(3, 4) = " + result);
    }
}
