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

public class TutorialCompiler13NestedNamespacesAndClasses {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare nested namespaces.
        String code = """
                namespace com {
                  namespace example {
                    export class Hello {
                      greet(): String { return "hello from com.example" }
                    }
                  }
                  namespace util {
                    export class Helper {
                      help(): String { return "help from com.util" }
                    }
                  }
                }""";
        // Compile and run.
        ByteCodeRunner runner = compiler.compile(code);
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Nested namespaces.");
        System.out.println("*********************************************/");
        System.out.println((String) runner.createInstanceRunner("com.example.Hello").invoke("greet"));
        System.out.println((String) runner.createInstanceRunner("com.util.Helper").invoke("help"));
        // Prepare a class with a companion namespace.
        code = """
                namespace com {
                  export class Outer {
                    getValue(): int { return 1 }
                  }
                  namespace Outer {
                    export class Inner {
                      getValue(): int { return 2 }
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Companion namespaces.");
        System.out.println("*********************************************/");
        System.out.println("Outer.getValue() = " + (int) runner.createInstanceRunner("com.Outer").invoke("getValue"));
        System.out.println("Inner.getValue() = " + (int) runner.createInstanceRunner("com.Outer.Inner").invoke("getValue"));
        // Prepare classes that reference each other across namespaces.
        code = """
                namespace com {
                  namespace types {
                    export class Point {
                      x: int
                      y: int
                      constructor(x: int, y: int) { this.x = x; this.y = y }
                      getX(): int { return this.x }
                      getY(): int { return this.y }
                    }
                  }
                  namespace app {
                    export class Main {
                      run(): int {
                        const p = new com.types.Point(3, 4)
                        return p.getX() + p.getY()
                      }
                    }
                  }
                }""";
        // Compile and run.
        runner = compiler.compile(code);
        int result = runner.createInstanceRunner("com.app.Main").invoke("run");
        // Print the result.
        System.out.println("/*********************************************");
        System.out.println("     Cross-namespace instantiation.");
        System.out.println("*********************************************/");
        System.out.println("Main.run() = " + result);
    }
}
