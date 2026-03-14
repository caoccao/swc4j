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

public class TutorialCompiler06ClassInheritance {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare classes with inheritance.
        String code = """
                namespace com {
                  export class Animal {
                    speak(): String {
                      return "..."
                    }
                  }
                  export class Dog extends Animal {
                    speak(): String {
                      return "Woof"
                    }
                  }
                  export class Cat extends Animal {
                    speak(): String {
                      return "Meow"
                    }
                  }
                }""";
        // Compile and run.
        ByteCodeRunner runner = compiler.compile(code);
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Simple inheritance.");
        System.out.println("*********************************************/");
        System.out.println("Animal: " + runner.createInstanceRunner("com.Animal").invoke("speak"));
        System.out.println("Dog:    " + runner.createInstanceRunner("com.Dog").invoke("speak"));
        System.out.println("Cat:    " + runner.createInstanceRunner("com.Cat").invoke("speak"));
        // Prepare classes with super calls.
        code = """
                namespace com {
                  export class Base {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class Derived extends Base {
                    getValue(): int {
                      return super.getValue() + 50
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.Derived");
        int result = classRunner.invoke("getValue");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Super calls.");
        System.out.println("*********************************************/");
        System.out.println("Derived.getValue() = " + result);
        // Prepare multi-level inheritance.
        code = """
                namespace com {
                  export class A {
                    getA(): int { return 1 }
                  }
                  export class B extends A {
                    getB(): int { return 2 }
                  }
                  export class C extends B {
                    getC(): int { return 3 }
                    getSum(): int {
                      return this.getA() + this.getB() + this.getC()
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        classRunner = runner.createInstanceRunner("com.C");
        result = classRunner.invoke("getSum");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Multi-level inheritance.");
        System.out.println("*********************************************/");
        System.out.println("C.getSum() = " + result);
    }
}
