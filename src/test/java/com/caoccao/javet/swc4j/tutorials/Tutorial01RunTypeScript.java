/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.tutorials;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial01RunTypeScript {
    public static void main(String[] args) throws Swc4jCoreException, JavetException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a simple TypeScript code snippet.
        String code = "function add(a:number, b:number) { return a+b; }";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Prepare an option with script name and media type.
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript);
        // Transpile the code.
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        // Print the transpiled code.
        System.out.println("/*********************************************");
        System.out.println("      The transpiled code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
        // Run the code in Javet.
        System.out.println("/*********************************************");
        System.out.println("   The transpiled code is executed in Javet.");
        System.out.println("*********************************************/");
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            v8Runtime.getExecutor(output.getCode()).executeVoid();
            System.out.println("1 + 2 = " +
                    v8Runtime.getGlobalObject().invokeInteger(
                            "add", 1, 2));
        }
        // Remove the inline source map.
        options.setSourceMap(Swc4jSourceMapOption.Separate);
        output = swc4j.transpile(code, options);
        // Print the transpiled code.
        System.out.println("/*********************************************");
        System.out.println("      The transpiled code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
        System.out.println("/*********************************************");
        System.out.println("   The transpiled source map is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getSourceMap());
    }
}
