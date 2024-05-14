/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.blog;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.sanitizer.checkers.JavetSanitizerModuleFunctionChecker;
import com.caoccao.javet.sanitizer.checkers.JavetSanitizerSingleStatementChecker;
import com.caoccao.javet.sanitizer.checkers.JavetSanitizerStatementListChecker;
import com.caoccao.javet.sanitizer.codegen.JavetSanitizerFridge;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * https://sjtucaocao.medium.com/how-to-compromise-v8-on-jvm-ceb385572461
 */
public class TestCompromiseV8 {
    protected V8Runtime getV8Runtime(FridgeStatus fridgeStatus) throws JavetException {
        V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime();
        if (fridgeStatus == FridgeStatus.Enabled) {
            v8Runtime.getExecutor(JavetSanitizerFridge.generate(JavetSanitizerOptions.Default)).executeVoid();
        }
        return v8Runtime;
    }

    @Test
    public void testCallApplyBindCall() {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier apply is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("a.apply(b);"),
                        "apply should not pass the check.")
                        .getMessage());
        assertEquals(
                "Identifier bind is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("a.bind(b);"),
                        "bind should not pass the check.")
                        .getMessage());
        assertEquals(
                "Identifier call is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("a.call(b);"),
                        "call should not pass the check.")
                        .getMessage());
    }

    @Test
    public void testCallEval() throws JavetException {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier eval is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("eval('1');"),
                        "eval should not pass the check.")
                        .getMessage());
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Enabled)) {
            assertEquals(
                    "ReferenceError: eval is not defined",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor("eval('1');").executeVoid(),
                            "eval should be inaccessible in V8.")
                            .getMessage());
        }
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Disabled)) {
            assertEquals(
                    1,
                    v8Runtime.getExecutor("eval('1');").executeInteger(),
                    "eval should be executed successfully in V8.");
            v8Runtime.allowEval(false);
            assertEquals(
                    "EvalError: Code generation from strings disallowed for this context",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor("eval('1');").executeVoid(),
                            "eval should be disallowed.")
                            .getMessage());
        }
    }

    @Test
    public void testJSFxxk() throws JavetException {
        // Object = 1; Object;
        String codeString = "[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]][([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]]((!![]+[])[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+([][[]]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+!+[]]+(+[![]]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+!+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(+(!+[]+!+[]+!+[]+[+!+[]]))[(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([]+[])[([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]][([][[]]+[])[+!+[]]+(![]+[])[+!+[]]+((+[])[([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]]+[])[+!+[]+[+!+[]]]+(!![]+[])[!+[]+!+[]+!+[]]]](!+[]+!+[]+!+[]+[!+[]+!+[]])+(![]+[])[+!+[]]+(![]+[])[!+[]+!+[]])()((+[]+[][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()[([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]])[+!+[]+[+[]]]+([][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()+[])[!+[]+!+[]]+([][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()+[])[!+[]+!+[]+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(+[![]]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+!+[]]]+([]+[])[(![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(![]+[])[!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]]()[+!+[]+[+!+[]]]+(+[![]]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+!+[]]]+[+!+[]]+([]+[])[(![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(![]+[])[!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]](+[![]]+([]+[])[(![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(![]+[])[!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]]()[+!+[]+[!+[]+!+[]]])[!+[]+!+[]+[+!+[]]]+(+[![]]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+!+[]]]+(+[]+[][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()[([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(![]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([][[]]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]])[+!+[]+[+[]]]+([][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()+[])[!+[]+!+[]]+([][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()+[])[!+[]+!+[]+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[])[+[]]+([]+[])[(![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(![]+[])[!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]](+[![]]+([]+[])[(![]+[])[+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+([][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]]+[])[!+[]+!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(![]+[])[!+[]+!+[]]+(!![]+[][(![]+[])[+[]]+(![]+[])[!+[]+!+[]]+(![]+[])[+!+[]]+(!![]+[])[+[]]])[+!+[]+[+[]]]+(!![]+[])[+!+[]]]()[+!+[]+[!+[]+!+[]]])[!+[]+!+[]+[+!+[]]])";
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Enabled)) {
            assertEquals(
                    "ReferenceError: eval is not defined",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor(codeString).executeVoid(),
                            "eval should be disallowed.")
                            .getMessage());
        }
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Disabled)) {
            assertEquals(
                    1,
                    v8Runtime.getExecutor(codeString).executeInteger(),
                    "The obfuscated code should pass V8.");
            v8Runtime.allowEval(false);
            assertEquals(
                    "EvalError: Code generation from strings disallowed for this context",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor(codeString).executeVoid(),
                            "eval should be disallowed.")
                            .getMessage());
        }
    }

    @Test
    public void testKeywordAsyncAwait() {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier Promise is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("new Promise((resolve, reject) => {});"),
                        "Promise should not pass the check.")
                        .getMessage());
        assertEquals(
                "Keyword async is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("async function a() {}"),
                        "async should not pass the check.")
                        .getMessage());
        assertEquals(
                "Keyword await is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("await f();"),
                        "await should not pass the check.")
                        .getMessage());
    }

    @Test
    public void testKeywordVarLetConst() throws JavetSanitizerException {
        JavetSanitizerStatementListChecker checker = new JavetSanitizerStatementListChecker();
        assertEquals(
                "Keyword var is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("var a = 1;"),
                        "var should not pass the check.")
                        .getMessage());
        checker.check("let a = 1;");
        checker.check("const a = 1;");
    }

    @Test
    public void testModules() throws JavetSanitizerException {
        {
            JavetSanitizerModuleFunctionChecker checker = new JavetSanitizerModuleFunctionChecker();
            assertEquals(
                    "Var Declaration is unexpected. Expecting Function Declaration in Module Function.",
                    assertThrows(
                            JavetSanitizerException.class,
                            () -> checker.check("const a = 1;"),
                            "Variable statement should not pass the check.")
                            .getMessage());
            assertEquals(
                    "Function main is not found.",
                    assertThrows(
                            JavetSanitizerException.class,
                            () -> checker.check("function a() {}"),
                            "main() should be reported missing.")
                            .getMessage());
            checker.check("function main() {} function a() {}");
            assertEquals(2, checker.getFunctionMap().size(), "There should be 2 functions.");
            assertTrue(checker.getFunctionMap().containsKey("main"), "main() should be found.");
            assertTrue(checker.getFunctionMap().containsKey("a"), "a() should be found.");
            assertEquals(
                    "Keyword import is not allowed.",
                    assertThrows(
                            JavetSanitizerException.class,
                            () -> checker.check("import { a } from 'a.js'; function main() {}"),
                            "import should not pass the check.")
                            .getMessage());
        }
        {
            JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                    .setKeywordImportEnabled(true)
                    .seal();
            JavetSanitizerModuleFunctionChecker checker = new JavetSanitizerModuleFunctionChecker(options);
            checker.check("import { x } from 'x.mjs'; function main() {}");
            assertEquals(1, checker.getFunctionMap().size(), "There should be 1 functions.");
            assertTrue(checker.getFunctionMap().containsKey("main"), "main() should be found.");
        }
    }

    @Test
    public void testUpdateBuiltInObjects() throws JavetException {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier Object is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("Object = 1;"),
                        "Object should not pass the check.")
                        .getMessage());
        assertEquals(
                "Identifier Object is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("Object.a = 1;"),
                        "Object.a should not pass the check.")
                        .getMessage());
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Enabled)) {
            assertEquals(
                    "TypeError: Assignment to constant variable.",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor("Object = 1;").executeVoid(),
                            "Object should be immutable.")
                            .getMessage());
        }
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Disabled)) {
            assertEquals(
                    1,
                    v8Runtime.getExecutor("Object = 1; Object").executeInteger(),
                    "Object should be updated.");
        }
    }

    @Test
    public void testUpdateGlobal() {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier globalThis is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("globalThis.a = 1;"),
                        "globalThis should be inaccessible.")
                        .getMessage());
        assertEquals(
                "Identifier global is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("global.a = 1;"),
                        "global should be inaccessible.")
                        .getMessage());
    }

    @Test
    public void testUpdatePrototype() throws JavetException {
        JavetSanitizerSingleStatementChecker checker = new JavetSanitizerSingleStatementChecker();
        assertEquals(
                "Identifier prototype is not allowed.",
                assertThrows(
                        JavetSanitizerException.class,
                        () -> checker.check("A.prototype.x = () => {};"),
                        "prototype should not pass the check.")
                        .getMessage());
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Enabled)) {
            assertEquals(
                    "TypeError: Cannot add property size, object is not extensible",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor(
                                            "Array.prototype.size = function() { return this.length; }")
                                    .executeVoid(),
                            "Array.prototype should be immutable.")
                            .getMessage());
            assertEquals(
                    "TypeError: Cannot add property size, object is not extensible",
                    assertThrows(
                            JavetExecutionException.class,
                            () -> v8Runtime.getExecutor(
                                            "Array['prototype'].size = function() { return this.length; }")
                                    .executeVoid(),
                            "Array['prototype'] should be immutable.")
                            .getMessage());
        }
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Disabled)) {
            assertEquals(
                    2,
                    v8Runtime.getExecutor(
                                    "Array.prototype.size = function() { return this.length; };" +
                                            "[1, 2].size();")
                            .executeInteger(),
                    "Array.prototype should be updated.");
        }
        try (V8Runtime v8Runtime = getV8Runtime(FridgeStatus.Disabled)) {
            assertEquals(
                    2,
                    v8Runtime.getExecutor(
                                    "Array['prototype'].size = function() { return this.length; };" +
                                            "[1, 2].size();")
                            .executeInteger(),
                    "Array['prototype'] should be updated.");
        }
    }

    public enum FridgeStatus {
        Disabled,
        Enabled,
    }
}
