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
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial03Tokens {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a TypeScript code snippet.
        String code = "function quickSort(arr: number[]): number[] {\n" +
                "  if (arr.length <= 1) {\n" +
                "    return arr;\n" +
                "  }\n" +
                "\n" +
                "  const pivot = arr[arr.length - 1];\n" +
                "  const leftArr = [];\n" +
                "  const rightArr = [];\n" +
                "\n" +
                "  for (let i = 0; i < arr.length - 1; i++) {\n" +
                "    if (arr[i] < pivot) {\n" +
                "      leftArr.push(arr[i]);\n" +
                "    } else {\n" +
                "      rightArr.push(arr[i]);\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  return [...quickSort(leftArr), pivot, ...quickSort(rightArr)];\n" +
                "}";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Prepare an option with script name and media type.
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                // Set capture tokens.
                .setCaptureTokens(true)
                .setParseMode(Swc4jParseMode.Script);
        // Parse the code.
        Swc4jParseOutput output = swc4j.parse(code, options);
        // Print the tokens.
        System.out.println("/*********************************************");
        System.out.println("         The tokens are as follows.");
        System.out.println("*********************************************/");
        output.getTokens().forEach(System.out::println);
    }
}
