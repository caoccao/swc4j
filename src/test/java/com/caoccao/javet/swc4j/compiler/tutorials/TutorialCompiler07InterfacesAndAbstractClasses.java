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

public class TutorialCompiler07InterfacesAndAbstractClasses {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare a class that implements Runnable.
        String code = """
                import { Runnable } from 'java.lang'
                namespace com {
                  export class Task implements Runnable {
                    run(): void { }
                  }
                }""";
        // Compile and verify.
        ByteCodeRunner runner = compiler.compile(code);
        Class<?> taskClass = runner.getClass("com.Task");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Implement an interface.");
        System.out.println("*********************************************/");
        System.out.println("Task implements Runnable: " + Runnable.class.isAssignableFrom(taskClass));
        // Instantiate and invoke.
        Runnable task = (Runnable) taskClass.getConstructor().newInstance();
        task.run();
        System.out.println("task.run() executed successfully.");
        // Prepare an abstract class and a concrete subclass.
        code = """
                namespace com {
                  export abstract class Base {
                    abstract compute(): int
                    helper(): int { return 100 }
                  }
                  export class Derived extends Base {
                    compute(): int {
                      return this.helper() + 1
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Derived");
        int result = classRunner.invoke("compute");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Abstract class.");
        System.out.println("*********************************************/");
        System.out.println("Derived.compute() = " + result);
        // Prepare a class that extends and implements.
        code = """
                import { Runnable } from 'java.lang'
                namespace com {
                  export class Animal {
                    speak(): String { return "..." }
                  }
                  export class ServiceDog extends Animal implements Runnable {
                    speak(): String { return "Woof" }
                    run(): void { }
                  }
                }""";
        // Compile and verify.
        runner = compiler.compile(code);
        Class<?> dogClass = runner.getClass("com.ServiceDog");
        Class<?> animalClass = runner.getClass("com.Animal");
        ByteCodeClassRunner dogRunner = runner.createInstanceRunner("com.ServiceDog");
        String speak = dogRunner.invoke("speak");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Extends and implements.");
        System.out.println("*********************************************/");
        System.out.println("ServiceDog extends Animal:      " + animalClass.isAssignableFrom(dogClass));
        System.out.println("ServiceDog implements Runnable:  " + Runnable.class.isAssignableFrom(dogClass));
        System.out.println("ServiceDog.speak() = " + speak);
    }
}
