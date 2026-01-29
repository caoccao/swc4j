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
import static org.assertj.core.api.Assertions.assertThat;


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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(1); // MON
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(2); // TUE
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(3); // WED
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[3])).isEqualTo(4); // THU
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[4])).isEqualTo(5); // FRI
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[5])).isEqualTo(6); // SAT
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[6])).isEqualTo(7); // SUN
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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(1); // RED
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(2); // GREEN
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(3); // BLUE
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

        assertThat(instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo("ok"); // OK
        assertThat(instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo("not found"); // NOT_FOUND
        assertThat(instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo("error"); // INTERNAL_ERROR
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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(11); // LOW: 1 + 10
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(10); // MEDIUM: 10
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(1100); // HIGH: 100 + 1000
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[3])).isEqualTo(1000); // CRITICAL: 1000
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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(1); // LOWEST
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(1); // LOW
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(2); // NORMAL
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[3])).isEqualTo(3); // HIGH
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[4])).isEqualTo(3); // HIGHEST
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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(-10); // BACK
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(0); // STAY
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(10); // FORWARD
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

        assertThat(instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo("info"); // INFO
        assertThat(instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo("warning"); // WARNING
        assertThat(instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo("error"); // ERROR
        assertThat(instanceRunner.<Object>invoke("test", enumConstants[3])).isEqualTo("fatal"); // FATAL
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

        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[0])).isEqualTo(1); // PENDING
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[1])).isEqualTo(2); // ACTIVE
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[2])).isEqualTo(99); //COMPLETED (default)
        assertThat((int) instanceRunner.<Object>invoke("test", enumConstants[3])).isEqualTo(99); //CANCELLED (default)
    }
}
