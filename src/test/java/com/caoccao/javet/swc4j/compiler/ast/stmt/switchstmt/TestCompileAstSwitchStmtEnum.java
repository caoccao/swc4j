/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.stmt.switchstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for enum switch statements
 * Tests switch on enum values using ordinal() conversion with enum member expressions in case labels
 */
public class TestCompileAstSwitchStmtEnum extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumAllValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Day {
                    MON,
                    TUE,
                    WED,
                    THU,
                    FRI,
                    SAT,
                    SUN
                  }
                
                  export class A {
                    test(d: Day): int {
                      switch (d) {
                        case Day.MON: return 1
                        case Day.TUE: return 2
                        case Day.WED: return 3
                        case Day.THU: return 4
                        case Day.FRI: return 5
                        case Day.SAT: return 6
                        case Day.SUN: return 7
                      }
                      return 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumDay = runner.getClass("com.Day");
        Object[] enumConstants = enumDay.getEnumConstants();

        assertEquals(1, (int) instanceRunner.invoke("test", enumConstants[0])); // MON
        assertEquals(2, (int) instanceRunner.invoke("test", enumConstants[1])); // TUE
        assertEquals(3, (int) instanceRunner.invoke("test", enumConstants[2])); // WED
        assertEquals(4, (int) instanceRunner.invoke("test", enumConstants[3])); // THU
        assertEquals(5, (int) instanceRunner.invoke("test", enumConstants[4])); // FRI
        assertEquals(6, (int) instanceRunner.invoke("test", enumConstants[5])); // SAT
        assertEquals(7, (int) instanceRunner.invoke("test", enumConstants[6])); // SUN
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Color {
                    RED,
                    GREEN,
                    BLUE
                  }
                
                  export class A {
                    test(c: Color): int {
                      let result: int = 0
                      switch (c) {
                        case Color.RED:
                          result = 1
                          break
                        case Color.GREEN:
                          result = 2
                          break
                        case Color.BLUE:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumColor = runner.getClass("com.Color");
        Object[] enumConstants = enumColor.getEnumConstants();

        assertEquals(1, (int) instanceRunner.invoke("test", enumConstants[0])); // RED
        assertEquals(2, (int) instanceRunner.invoke("test", enumConstants[1])); // GREEN
        assertEquals(3, (int) instanceRunner.invoke("test", enumConstants[2])); // BLUE
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumExplicitValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum HttpStatus {
                    OK = 200,
                    NOT_FOUND = 404,
                    INTERNAL_ERROR = 500
                  }
                
                  export class A {
                    test(status: HttpStatus): string {
                      switch (status) {
                        case HttpStatus.OK:
                          return "ok"
                        case HttpStatus.NOT_FOUND:
                          return "not found"
                        case HttpStatus.INTERNAL_ERROR:
                          return "error"
                      }
                      return "unknown"
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumHttpStatus = runner.getClass("com.HttpStatus");
        Object[] enumConstants = enumHttpStatus.getEnumConstants();

        assertEquals("ok", instanceRunner.invoke("test", enumConstants[0])); // OK
        assertEquals("not found", instanceRunner.invoke("test", enumConstants[1])); // NOT_FOUND
        assertEquals("error", instanceRunner.invoke("test", enumConstants[2])); // INTERNAL_ERROR
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumFallThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Level {
                    LOW,
                    MEDIUM,
                    HIGH,
                    CRITICAL
                  }
                
                  export class A {
                    test(l: Level): int {
                      let result: int = 0
                      switch (l) {
                        case Level.LOW:
                          result += 1
                        case Level.MEDIUM:
                          result += 10
                          break
                        case Level.HIGH:
                          result += 100
                        case Level.CRITICAL:
                          result += 1000
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumLevel = runner.getClass("com.Level");
        Object[] enumConstants = enumLevel.getEnumConstants();

        assertEquals(11, (int) instanceRunner.invoke("test", enumConstants[0])); // LOW: 1 + 10
        assertEquals(10, (int) instanceRunner.invoke("test", enumConstants[1])); // MEDIUM: 10
        assertEquals(1100, (int) instanceRunner.invoke("test", enumConstants[2])); // HIGH: 100 + 1000
        assertEquals(1000, (int) instanceRunner.invoke("test", enumConstants[3])); // CRITICAL: 1000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumMultipleEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    LOWEST,
                    LOW,
                    NORMAL,
                    HIGH,
                    HIGHEST
                  }
                
                  export class A {
                    test(p: Priority): int {
                      let result: int = 0
                      switch (p) {
                        case Priority.LOWEST:
                        case Priority.LOW:
                          result = 1
                          break
                        case Priority.NORMAL:
                          result = 2
                          break
                        case Priority.HIGH:
                        case Priority.HIGHEST:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumPriority = runner.getClass("com.Priority");
        Object[] enumConstants = enumPriority.getEnumConstants();

        assertEquals(1, (int) instanceRunner.invoke("test", enumConstants[0])); // LOWEST
        assertEquals(1, (int) instanceRunner.invoke("test", enumConstants[1])); // LOW
        assertEquals(2, (int) instanceRunner.invoke("test", enumConstants[2])); // NORMAL
        assertEquals(3, (int) instanceRunner.invoke("test", enumConstants[3])); // HIGH
        assertEquals(3, (int) instanceRunner.invoke("test", enumConstants[4])); // HIGHEST
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    BACK = -1,
                    STAY = 0,
                    FORWARD = 1
                  }
                
                  export class A {
                    test(dir: Direction): int {
                      switch (dir) {
                        case Direction.BACK: return -10
                        case Direction.STAY: return 0
                        case Direction.FORWARD: return 10
                      }
                      return 99
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumDirection = runner.getClass("com.Direction");
        Object[] enumConstants = enumDirection.getEnumConstants();

        assertEquals(-10, (int) instanceRunner.invoke("test", enumConstants[0])); // BACK
        assertEquals(0, (int) instanceRunner.invoke("test", enumConstants[1])); // STAY
        assertEquals(10, (int) instanceRunner.invoke("test", enumConstants[2])); // FORWARD
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumReturnsFromCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum ErrorLevel {
                    INFO,
                    WARNING,
                    ERROR,
                    FATAL
                  }
                
                  export class A {
                    test(e: ErrorLevel): string {
                      switch (e) {
                        case ErrorLevel.INFO:
                          return "info"
                        case ErrorLevel.WARNING:
                          return "warning"
                        case ErrorLevel.ERROR:
                          return "error"
                        case ErrorLevel.FATAL:
                          return "fatal"
                        default:
                          return "unknown"
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumErrorLevel = runner.getClass("com.ErrorLevel");
        Object[] enumConstants = enumErrorLevel.getEnumConstants();

        assertEquals("info", instanceRunner.invoke("test", enumConstants[0])); // INFO
        assertEquals("warning", instanceRunner.invoke("test", enumConstants[1])); // WARNING
        assertEquals("error", instanceRunner.invoke("test", enumConstants[2])); // ERROR
        assertEquals("fatal", instanceRunner.invoke("test", enumConstants[3])); // FATAL
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEnumWithDefault(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    PENDING,
                    ACTIVE,
                    COMPLETED,
                    CANCELLED
                  }
                
                  export class A {
                    test(s: Status): int {
                      let result: int = 0
                      switch (s) {
                        case Status.PENDING:
                          result = 1
                          break
                        case Status.ACTIVE:
                          result = 2
                          break
                        default:
                          result = 99
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Class<?> enumStatus = runner.getClass("com.Status");
        Object[] enumConstants = enumStatus.getEnumConstants();

        assertEquals(1, (int) instanceRunner.invoke("test", enumConstants[0])); // PENDING
        assertEquals(2, (int) instanceRunner.invoke("test", enumConstants[1])); // ACTIVE
        assertEquals(99, (int) instanceRunner.invoke("test", enumConstants[2])); // COMPLETED (default)
        assertEquals(99, (int) instanceRunner.invoke("test", enumConstants[3])); // CANCELLED (default)
    }
}
