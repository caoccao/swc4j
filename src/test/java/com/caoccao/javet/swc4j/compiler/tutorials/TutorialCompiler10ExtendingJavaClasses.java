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

import java.util.Map;

public class TutorialCompiler10ExtendingJavaClasses {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare a class that extends ArrayList.
        String code = """
                namespace com {
                  export class MyList extends java.util.ArrayList<Object> {
                    label: String = "my-list"
                    getLabel(): String {
                      return this.label
                    }
                  }
                }""";
        // Compile.
        ByteCodeRunner runner = compiler.compile(code);
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.MyList");
        // Use the custom method.
        String label = classRunner.invoke("getLabel");
        // Cast to ArrayList and use inherited methods.
        @SuppressWarnings("unchecked")
        java.util.ArrayList<Object> list = (java.util.ArrayList<Object>) classRunner.getInstance();
        list.add("Hello");
        list.add("World");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Extend ArrayList.");
        System.out.println("*********************************************/");
        System.out.println("label = " + label);
        System.out.println("size  = " + list.size());
        System.out.println("items = " + list);
        // Create a compiler with type aliases.
        compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .typeAliasMap(Map.of(
                                "int", "int",
                                "void", "void",
                                "String", "java.lang.String",
                                "Object", "java.lang.Object",
                                "ArrayList", "java.util.ArrayList"))
                        .build());
        // Now the TypeScript code can use the short name ArrayList.
        code = """
                namespace com {
                  export class NameList extends ArrayList<Object> {
                    addName(name: String): void {
                      this.add(name)
                    }
                    getCount(): int {
                      return this.size()
                    }
                  }
                }""";
        // Compile.
        runner = compiler.compile(code);
        classRunner = runner.createInstanceRunner("com.NameList");
        classRunner.invoke("addName", "Alice");
        classRunner.invoke("addName", "Bob");
        int count = classRunner.invoke("getCount");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Type aliases.");
        System.out.println("*********************************************/");
        System.out.println("count = " + count);
    }
}
