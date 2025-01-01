/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.checkers;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerError;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.swc4j.utils.SimpleMap;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseTestSuiteCheckers {
    protected static final Map<String, String> invalidIdentifierCodeStringMap = SimpleMap.of(
            "const a = Object.__proto__;", "__proto__",
            "const a = new AsyncFunction();", "AsyncFunction",
            "const a = new AsyncGenerator();", "AsyncGenerator",
            "const a = new AsyncGeneratorFunction();", "AsyncGeneratorFunction",
            "clearInterval()", "clearInterval",
            "clearTimeout()", "clearTimeout",
            "defineProperty()", "defineProperty",
            "defineProperties()", "defineProperties",
            "eval()", "eval",
            "const a = Function('abc')", "Function",
            "global()", "global",
            "globalThis()", "globalThis",
            "getPrototypeOf()", "getPrototypeOf",
            "const a = new Generator();", "Generator",
            "const a = new GeneratorFunction();", "GeneratorFunction",
            "const a = Intl.a;", "Intl",
            "const a = Object.prototype;", "prototype",
            "const a = new Proxy();", "Proxy",
            "const a = new Promise();", "Promise",
            "const a = require('abc');", "require",
            "const a = Reflect;", "Reflect",
            "setImmediate()", "setImmediate",
            "setInterval()", "setInterval",
            "setTimeout()", "setTimeout",
            "const a = Object.setPrototypeOf;", "setPrototypeOf",
            "const a = Symbol.toPrimitive;", "Symbol",
            "uneval()", "uneval",
            "const a = new XMLHttpRequest();", "XMLHttpRequest",
            "const a = new WebAssembly();", "WebAssembly",
            "const a = window;", "window",
            "let Object = 1", "Object",
            "Object.a = 1", "Object",
            "Object?.a = 1", "Object",
            "let [ Object ] = [ 1 ]", "Object",
            // SWC bug
            // "{ Object } = { Object: 1 }", "Object",
            "let { Object } = { Object: 1 }", "Object",
            "class Object {}", "Object",
            "function Object() {}", "Object",
            "class $abc {}", "$abc",
            "function $abc() {}", "$abc");

    protected IJavetSanitizerChecker checker;

    protected JavetSanitizerException assertException(
            String code,
            JavetSanitizerError expectedError,
            String expectedErrorMessage) {
        return assertException(code, expectedError, expectedErrorMessage, true);
    }

    protected JavetSanitizerException assertException(
            String code,
            JavetSanitizerError expectedError,
            String expectedErrorMessage,
            boolean detailed) {
        JavetSanitizerException exception = assertThrows(
                JavetSanitizerException.class,
                () -> checker.check(code),
                "Failed to throw exception for [" + code + "]");
        assertEquals(
                expectedError.getCode(),
                exception.getError().getCode(),
                "Error code mismatches for " + code);
        if (detailed) {
            assertEquals(
                    expectedErrorMessage,
                    exception.getDetailedMessage(),
                    "Detailed error message mismatches for " + code);
        } else {
            assertEquals(
                    expectedErrorMessage,
                    exception.getMessage(),
                    "Error message mismatches for " + code);
        }
        return exception;
    }
}
