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

package com.caoccao.javet.swc4j.plugins.jsfuck;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jPluginHostJsFuckDecoder extends BaseTestSuite {
    @Test
    public void test() throws Swc4jCoreException {
        Map<String, String> testCaseMap = new LinkedHashMap<>();
        testCaseMap.put("(![]+[])[+!+[]]", "\"a\";");
        testCaseMap.put("+'.0000001'", "1e-7;");
        testCaseMap.put("+((11e20+[])[1]+[\"0000001\"])", "1e-7;");
        testCaseMap.put("+(\"1e309\")", "Infinity;");
        testCaseMap.put("020", "16;");
        testCaseMap.put("0x20", "32;");
        testCaseMap.put("+[![]]", "NaN;");
        testCaseMap.put("[][[]]+[]", "\"undefined\";");
        testCaseMap.put("''+[]['entries']()", "\"[object Array Iterator]\";");
        testCaseMap.put("([![]]+[][[]])[+!+[]+[+[]]]", "\"i\";");
        testCaseMap.put("true+[][\"flat\"]", "\"truefunction flat() { [native code] }\";");
        testCaseMap.put("[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]", "\"1+1\";");
        testCaseMap.put("(+((+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+[+[]+[+[]]+[+[]]+[+[]]+[+[]]+[+[]]+[+!+[]]])+[])[!+[]+!+[]]", "\"-\";");
        testCaseMap.put("[+!+[]]+(+((+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+[+[]+[+[]]+[+[]]+[+[]]+[+[]]+[+[]]+[+!+[]]])+[])[!+[]+!+[]]+[+!+[]]", "\"1-1\";");
        testCaseMap.put("(![]+[])[+!+[]]+(+((+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+[+[]+[+[]]+[+[]]+[+[]]+[+[]]+[+[]]+[+!+[]]])+[])[!+[]+!+[]]+([][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]()+[])[!+[]+!+[]]", "\"a-b\";");
        testCaseMap.put("(+[![]]+[][(!![]+[])[!+[]+!+[]+!+[]]+([][[]]+[])[+!+[]]+(!![]+[])[+[]]+(!![]+[])[+!+[]]+([![]]+[][[]])[+!+[]+[+[]]]+(!![]+[])[!+[]+!+[]+!+[]]+(![]+[])[!+[]+!+[]+!+[]]]())[+!+[]+[+!+[]]]+(!![]+[])[+!+[]]+(!![]+[])[+!+[]]+(![]+[])[+!+[]]+(+[![]]+[+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]]+[+[]])])[+!+[]+[+[]]]", "\"Array\";");
        ISwc4jPluginHost pluginHost = new Swc4jPluginHostJsFuckDecoder();
        jsScriptTransformOptions
                .setPluginHost(pluginHost)
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None);
        for (Map.Entry<String, String> entry : testCaseMap.entrySet()) {
            Swc4jTransformOutput output = swc4j.transform(entry.getKey(), jsScriptTransformOptions);
            assertEquals(entry.getValue(), output.getCode(), entry.getKey());
        }
    }
}
