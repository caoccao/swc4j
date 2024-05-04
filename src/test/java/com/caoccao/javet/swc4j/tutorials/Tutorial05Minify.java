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
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial05Minify {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a TypeScript code snippet.
        String code = "import { Validator } from './Validator';\n" +
                "\n" +
                "class EmailValidator implements Validator {\n" +
                "    isValid(s: string): boolean {\n" +
                "        // This is a regex for email validation.\n" +
                "        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n" +
                "        return emailRegex.test(s);\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "export { EmailValidator };";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Prepare an option with script name and media type.
        // Minify is turned on by default.
        Swc4jTransformOptions options = new Swc4jTransformOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Module)
                .setSourceMap(Swc4jSourceMapOption.Separate);
        // Parse the code.
        Swc4jTransformOutput output = swc4j.transform(code, options);
        // Print the minified code.
        System.out.println("/*********************************************");
        System.out.println("       The minified code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
        System.out.println("/*********************************************");
        System.out.println("       The source map is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getSourceMap());
        // Turn on keep comments.
        options.setKeepComments(true);
        // Parse the code again.
        output = swc4j.transform(code, options);
        // Print the minified code.
        System.out.println("/*********************************************");
        System.out.println("       The minified code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
    }
}
