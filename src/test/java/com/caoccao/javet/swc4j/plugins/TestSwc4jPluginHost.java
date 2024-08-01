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

package com.caoccao.javet.swc4j.plugins;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jPluginHost extends BaseTestSuite {
    @Test
    public void testParseModuleCount() {
        String code = "import a from 'a';";
        SimpleList.of(jsProgramParseOptions, jsModuleParseOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jParseOutput output = swc4j.parse(code, options
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(1, pluginCounter.getModuleCount());
                assertEquals(0, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Module, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testParseScriptCount() {
        String code = "1 + 1";
        SimpleList.of(jsProgramParseOptions, jsScriptParseOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jParseOutput output = swc4j.parse(code, options
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(0, pluginCounter.getModuleCount());
                assertEquals(1, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Script, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testParseWithException() {
        String code = "1 + 1";
        try {
            Swc4jAstVisitor visitor = new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitScript(Swc4jAstScript node) {
                    throw new RuntimeException("Test");
                }
            };
            swc4j.parse(code, jsScriptParseOptions
                    .setPluginHost(new Swc4jPluginHost().add(new Swc4jPluginVisitors(SimpleList.of(visitor)))));
            fail("Failed to throw exception.");
        } catch (Swc4jCoreException e) {
            assertEquals("Couldn't call boolean process() because Java exception was thrown", e.getMessage());
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertEquals("Test", e.getCause().getMessage());
        }
    }

    @Test
    public void testTransformModuleCount() {
        String code = "import a from 'a'; a + 1;";
        String expectedCode = "import a from\"a\";a+1;";
        SimpleList.of(jsProgramTransformOptions, jsModuleTransformOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jTransformOutput output = swc4j.transform(code, options
                        .setInlineSources(false)
                        .setSourceMap(Swc4jSourceMapOption.None)
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(expectedCode, output.getCode());
                assertEquals(1, pluginCounter.getModuleCount());
                assertEquals(0, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Module, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testTransformScriptCount() {
        String code = "a + 1;";
        String expectedCode = "a+1;";
        SimpleList.of(jsProgramTransformOptions, jsScriptTransformOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jTransformOutput output = swc4j.transform(code, options
                        .setInlineSources(false)
                        .setSourceMap(Swc4jSourceMapOption.None)
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(expectedCode, output.getCode());
                assertEquals(0, pluginCounter.getModuleCount());
                assertEquals(1, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Script, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testTransformWithException() {
        String code = "1 + 1";
        try {
            Swc4jAstVisitor visitor = new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitScript(Swc4jAstScript node) {
                    throw new RuntimeException("Test");
                }
            };
            swc4j.transform(code, jsScriptTransformOptions
                    .setPluginHost(new Swc4jPluginHost().add(new Swc4jPluginVisitors(SimpleList.of(visitor)))));
            fail("Failed to throw exception.");
        } catch (Swc4jCoreException e) {
            assertEquals("Couldn't call boolean process() because Java exception was thrown", e.getMessage());
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertEquals("Test", e.getCause().getMessage());
        }
    }

    @Test
    public void testTranspileModuleCount() {
        String code = "import a from 'a'; a + 1;";
        String expectedCode = "import a from 'a';\na + 1;\n";
        SimpleList.of(jsProgramTranspileOptions, jsModuleTranspileOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jTranspileOutput output = swc4j.transpile(code, options
                        .setInlineSources(false)
                        .setSourceMap(Swc4jSourceMapOption.None)
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(expectedCode, output.getCode());
                assertEquals(1, pluginCounter.getModuleCount());
                assertEquals(0, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Module, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testTranspileScriptCount() {
        String code = "a + 1;";
        String expectedCode = "a + 1;\n";
        SimpleList.of(jsProgramTranspileOptions, jsScriptTranspileOptions).forEach(options -> {
            try {
                Swc4jPluginCounter pluginCounter = new Swc4jPluginCounter();
                Swc4jTranspileOutput output = swc4j.transpile(code, options
                        .setInlineSources(false)
                        .setSourceMap(Swc4jSourceMapOption.None)
                        .setPluginHost(new Swc4jPluginHost().add(pluginCounter)));
                assertNotNull(output);
                assertEquals(expectedCode, output.getCode());
                assertEquals(0, pluginCounter.getModuleCount());
                assertEquals(1, pluginCounter.getScriptCount());
                assertEquals(Swc4jParseMode.Script, output.getParseMode());
            } catch (Swc4jCoreException e) {
                fail(e);
            }
        });
    }

    @Test
    public void testTranspileWithException() {
        String code = "1 + 1";
        try {
            Swc4jAstVisitor visitor = new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitScript(Swc4jAstScript node) {
                    throw new RuntimeException("Test");
                }
            };
            swc4j.transpile(code, jsScriptTranspileOptions
                    .setPluginHost(new Swc4jPluginHost().add(new Swc4jPluginVisitors(SimpleList.of(visitor)))));
            fail("Failed to throw exception.");
        } catch (Swc4jCoreException e) {
            assertEquals("Couldn't call boolean process() because Java exception was thrown", e.getMessage());
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertEquals("Test", e.getCause().getMessage());
        }
    }
}
