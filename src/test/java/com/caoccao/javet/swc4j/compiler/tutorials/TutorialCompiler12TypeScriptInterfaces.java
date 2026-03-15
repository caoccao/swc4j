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

public class TutorialCompiler12TypeScriptInterfaces {
    public static void main(String[] args) throws Exception {
        // Create a compiler targeting JDK 17.
        ByteCodeCompiler compiler = ByteCodeCompiler.of(
                ByteCodeCompilerOptions.builder()
                        .jdkVersion(JdkVersion.JDK_17)
                        .build());
        // Prepare an interface and an implementing class.
        String code = """
                namespace com {
                  export interface Greeter {
                    name: String
                    greet(): String
                  }
                  export class SimpleGreeter implements Greeter {
                    name: String = ""
                    constructor(name: String) {
                      this.name = name
                    }
                    getName(): String { return this.name }
                    setName(name: String): void { this.name = name }
                    greet(): String { return "Hello, " + this.name }
                  }
                }""";
        // Compile.
        ByteCodeRunner runner = compiler.compile(code);
        Class<?> greeterInterface = runner.getClass("com.Greeter");
        Class<?> implClass = runner.getClass("com.SimpleGreeter");
        // Verify the interface.
        ByteCodeClassRunner classRunner = runner.createInstanceRunner("com.SimpleGreeter", "Alice");
        String greeting = classRunner.invoke("greet");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Define and implement an interface.");
        System.out.println("*********************************************/");
        System.out.println("Greeter is interface:              " + greeterInterface.isInterface());
        System.out.println("SimpleGreeter implements Greeter:  " + greeterInterface.isAssignableFrom(implClass));
        System.out.println("greeting = " + greeting);
        // Prepare an interface with two implementations.
        code = """
                namespace com {
                  export interface Shape {
                    area(): double
                  }
                  export class Circle implements Shape {
                    radius: double
                    constructor(r: double) {
                      this.radius = r
                    }
                    area(): double { return 3.14159 * this.radius * this.radius }
                  }
                  export class Square implements Shape {
                    side: double
                    constructor(s: double) {
                      this.side = s
                    }
                    area(): double { return this.side * this.side }
                  }
                }""";
        // Compile.
        runner = compiler.compile(code);
        Class<?> shapeInterface = runner.getClass("com.Shape");
        double circleArea = runner.createInstanceRunner("com.Circle", 5.0).invoke("area");
        double squareArea = runner.createInstanceRunner("com.Square", 4.0).invoke("area");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Multiple implementations.");
        System.out.println("*********************************************/");
        System.out.println("Shape is interface:  " + shapeInterface.isInterface());
        System.out.println("Circle area(5.0)  = " + circleArea);
        System.out.println("Square area(4.0)  = " + squareArea);
        // Prepare interfaces with extends.
        code = """
                namespace com {
                  export interface Named {
                    name: String
                  }
                  export interface Aged {
                    age: int
                  }
                  export interface Person extends Named, Aged {
                    greet(): String
                  }
                  export class Student implements Person {
                    name: String = ""
                    age: int = 0
                    constructor(name: String, age: int) {
                      this.name = name
                      this.age = age
                    }
                    getName(): String { return this.name }
                    setName(name: String): void { this.name = name }
                    getAge(): int { return this.age }
                    setAge(age: int): void { this.age = age }
                    greet(): String { return this.name + " (" + this.age + ")" }
                  }
                }""";
        // Compile.
        runner = compiler.compile(code);
        Class<?> namedInterface = runner.getClass("com.Named");
        Class<?> agedInterface = runner.getClass("com.Aged");
        Class<?> personInterface = runner.getClass("com.Person");
        Class<?> studentClass = runner.getClass("com.Student");
        String greet = runner.createInstanceRunner("com.Student", "Bob", 20).invoke("greet");
        // Print the results.
        System.out.println("/*********************************************");
        System.out.println("     Interface extends.");
        System.out.println("*********************************************/");
        System.out.println("Person extends Named:      " + namedInterface.isAssignableFrom(personInterface));
        System.out.println("Person extends Aged:       " + agedInterface.isAssignableFrom(personInterface));
        System.out.println("Student implements Person: " + personInterface.isAssignableFrom(studentClass));
        System.out.println("Student implements Named:  " + namedInterface.isAssignableFrom(studentClass));
        System.out.println("greet = " + greet);
    }
}
