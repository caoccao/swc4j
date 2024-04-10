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

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

public class Tutorial02TranspileJsx {
    public static void main(String[] args) throws Swc4jCoreException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a simple React Hello World Jsx code snippet.
        String code = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "    return (\n" +
                "        <h1> Hello World! </h1>\n" +
                "    );\n" +
                "}\n" +
                "export default App;";
        // Prepare a script name.
        String specifier = "file:///abc.ts";
        // Prepare an option with script name and media type.
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.Jsx)
                // React Jsx must be parsed in module mode.
                .setParseMode(Swc4jParseMode.Module);
        // Transpile the code.
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        // Print the transpiled code.
        System.out.println("/*********************************************");
        System.out.println("      The transpiled code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
        // Remove the inline source map.
        options.setInlineSourceMap(false).setSourceMap(true);
        // Customize the Jsx factory.
        options.setJsxFactory("CustomJsxFactory.createElement");
        output = swc4j.transpile(code, options);
        // Print the transpiled code.
        System.out.println("/*********************************************");
        System.out.println("      The transpiled code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
    }
}
