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
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.plugins.jsfuck.Swc4jPluginHostJsFuckDecoder;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial07Decode {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException, JavetException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a JavaScript code snippet.
        String code = "[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]"; // 1+1
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Create a plugin host.
        ISwc4jPluginHost pluginHost = new Swc4jPluginHostJsFuckDecoder();
        Swc4jTransformOptions options = new Swc4jTransformOptions()
                .setSpecifier(specifier)
                .setPluginHost(pluginHost)
                .setMediaType(Swc4jMediaType.JavaScript)
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None);
        // Transform the code.
        Swc4jTransformOutput output = swc4j.transform(code, options);
        System.out.println("/*********************************************");
        System.out.println("       The transformed code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
        System.out.println("/*********************************************");
        System.out.println("       The evaluated result in V8.");
        System.out.println("*********************************************/");
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            System.out.println(v8Runtime.getExecutor(output.getCode()).executeString());
        }
    }
}
