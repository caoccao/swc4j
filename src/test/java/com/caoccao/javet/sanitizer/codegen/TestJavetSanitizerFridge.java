/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.codegen;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetSanitizerFridge {
    @Test
    public void testGenerate() throws JavetException {
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
        options.getToBeDeletedIdentifierList().clear();
        options.getToBeDeletedIdentifierList().add("eval");
        options.getToBeDeletedIdentifierList().add("Function");
        options.getToBeFrozenIdentifierList().clear();
        options.getToBeFrozenIdentifierList().add("Object");
        options.getToBeFrozenIdentifierList().add("Array");
        options.seal();
        String codeString = JavetSanitizerFridge.generate(options);
        assertEquals(
                "/***** Delete 2 object(s). *****/\n" +
                        "\n" +
                        "delete globalThis.eval;\n" +
                        "delete globalThis.Function;\n" +
                        "\n" +
                        "/***** Freeze 2 object(s). *****/\n" +
                        "\n" +
                        "// Object\n" +
                        "const Object = (() => {\n" +
                        "  const _Object = globalThis.Object;\n" +
                        "  delete globalThis.Object;\n" +
                        "  return _Object;\n" +
                        "})();\n" +
                        "Object.freeze(Object);\n" +
                        "Object.freeze(Object.prototype);\n" +
                        "\n" +
                        "// Array\n" +
                        "const Array = (() => {\n" +
                        "  const _Array = globalThis.Array;\n" +
                        "  delete globalThis.Array;\n" +
                        "  return _Array;\n" +
                        "})();\n" +
                        "Object.freeze(Array);\n" +
                        "Object.freeze(Array.prototype);\n\n",
                codeString);
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            try (V8ValueBuiltInObject v8ValueBuiltInObject = v8Runtime.getGlobalObject().getBuiltInObject()) {
                assertFalse(v8ValueBuiltInObject.isFrozen());
                v8Runtime.getExecutor(codeString).executeVoid();
                assertTrue(v8ValueBuiltInObject.isFrozen());
            }
        }
    }
}
