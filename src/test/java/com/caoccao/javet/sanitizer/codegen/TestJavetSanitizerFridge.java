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

package com.caoccao.javet.sanitizer.codegen;

import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJavetSanitizerFridge {
    @Test
    public void testGenerate() {
        JavetSanitizerOptions option = JavetSanitizerOptions.Default.toClone();
        option.getToBeDeletedIdentifierList().clear();
        option.getToBeDeletedIdentifierList().add("eval");
        option.getToBeDeletedIdentifierList().add("Function");
        option.getToBeFrozenIdentifierList().clear();
        option.getToBeFrozenIdentifierList().add("Object");
        option.getToBeFrozenIdentifierList().add("Array");
        option.seal();
        String codeString = JavetSanitizerFridge.generate(option);
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
    }
}
