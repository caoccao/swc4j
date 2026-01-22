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

package com.caoccao.javet.swc4j.compiler.performance;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPrimeNumber extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void test(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public isPrime(number: int): boolean {
                      const limit = Math.floor(number / 2)
                      for (let i = 2; i <= limit; i++) {
                        if (number % i === 0) {
                          return false
                        }
                      }
                      return true
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var method = classA.getMethod("isPrime", int.class);
        assertEquals(true, method.invoke(instance, 7));  // 7 is prime
        assertEquals(false, method.invoke(instance, 8)); // 8 is not prime
    }
}
