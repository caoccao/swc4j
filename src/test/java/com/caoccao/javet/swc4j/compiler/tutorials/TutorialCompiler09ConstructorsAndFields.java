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

public class TutorialCompiler09ConstructorsAndFields {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare a class with fields and a constructor.
        String code = """
                namespace com {
                  export class Person {
                    name: String
                    age: int
                    constructor(name: String, age: int) {
                      this.name = name
                      this.age = age
                    }
                    getName(): String { return this.name }
                    getAge(): int { return this.age }
                  }
                }""";
        // Compile and run.
        ByteCodeRunner runner = compiler.compile(code);
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Person", "Alice", 30);
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Class fields.");
        System.out.println("*********************************************/");
        System.out.println("name = " + (String) classRunner.invoke("getName"));
        System.out.println("age  = " + (int) classRunner.invoke("getAge"));
        // Prepare a class with parameter properties.
        code = """
                namespace com {
                  export class Point {
                    constructor(public x: int, public y: int) {
                    }
                    getX(): int { return this.x }
                    getY(): int { return this.y }
                    sum(): int { return this.x + this.y }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        classRunner = runner.createInstanceRunner("com.Point", 3, 7);
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Parameter properties.");
        System.out.println("*********************************************/");
        System.out.println("x = " + (int) classRunner.invoke("getX"));
        System.out.println("y = " + (int) classRunner.invoke("getY"));
        System.out.println("sum = " + (int) classRunner.invoke("sum"));
        // Prepare a class with static fields and methods.
        code = """
                namespace com {
                  export class Counter {
                    static count: int = 0
                    static getCount(): int {
                      return Counter.count
                    }
                    static increment(): void {
                      Counter.count = Counter.count + 1
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        ByteCodeClassRunner staticRunner = runner.createStaticRunner("com.Counter");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Static fields and methods.");
        System.out.println("*********************************************/");
        System.out.println("count = " + (int) staticRunner.invoke("getCount"));
        staticRunner.invoke("increment");
        staticRunner.invoke("increment");
        staticRunner.invoke("increment");
        System.out.println("count after 3 increments = " + (int) staticRunner.invoke("getCount"));
    }
}
