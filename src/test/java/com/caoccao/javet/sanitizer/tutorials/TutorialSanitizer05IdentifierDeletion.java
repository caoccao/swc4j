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

package com.caoccao.javet.sanitizer.tutorials;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.sanitizer.codegen.JavetSanitizerFridge;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;

public class TutorialSanitizer05IdentifierDeletion {
    public static void main(String[] args) throws JavetException {
        String codeString = JavetSanitizerFridge.generate(JavetSanitizerOptions.Default);
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Initialize V8 with the default option.
            v8Runtime.getExecutor(codeString).executeVoid();
            codeString = "const a = WebAssembly;";
            v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
        } catch (JavetExecutionException e) {
            System.out.println("/******************************************************/");
            System.out.println(codeString + " // Invalid");
            System.out.println("/******************************************************/");
            System.out.println(e.getScriptingError());
        }

        // Create a new option with WebAssembly allowed.
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
        options.getToBeDeletedIdentifierList().remove("WebAssembly");
        options.seal();
        codeString = JavetSanitizerFridge.generate(options);
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Initialize V8 with the new option.
            v8Runtime.getExecutor(codeString).executeVoid();
            codeString = "const a = WebAssembly;";
            v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
            System.out.println("/******************************************************/");
            System.out.println(codeString + " // Valid");
            System.out.println("/******************************************************/");
        }
    }
}
