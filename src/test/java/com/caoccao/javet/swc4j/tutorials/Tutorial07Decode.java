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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial07Decode {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a JavaScript code snippet.
        String code = "[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Create a plugin host.
        ISwc4jPluginHost pluginHost = new DecoderPluginHost();
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
    }

    static class DecoderPluginHost implements ISwc4jPluginHost {
        @Override
        public boolean process(ISwc4jAstProgram<?> program) {
            JsFuckVisitor jsFuckVisitor = new JsFuckVisitor();
            do {
                jsFuckVisitor.reset();
                program.visit(jsFuckVisitor);
            } while (jsFuckVisitor.getCount() > 0);
            return true;
        }
    }

    static class JsFuckVisitor extends Swc4jAstVisitor {
        protected int count;

        public JsFuckVisitor() {
            count = 0;
        }

        public int getCount() {
            return count;
        }

        public void reset() {
            count = 0;
        }

        @Override
        public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
            ISwc4jAst newNode = null;
            ISwc4jAstExpr left = node.getLeft();
            ISwc4jAstExpr right = node.getRight();
            switch (node.getOp()) {
                case Add:
                    if (left.getType() == Swc4jAstType.Bool && right.getType() == Swc4jAstType.Bool) {
                        int value = ((Swc4jAstBool) left).asInt() + ((Swc4jAstBool) right).asInt();
                        newNode = Swc4jAstNumber.create(value);
                    } else if (left.getType() == Swc4jAstType.Bool && right.getType() == Swc4jAstType.Number) {
                        int value = ((Swc4jAstBool) left).asInt() + ((Swc4jAstNumber) right).asInt();
                        newNode = Swc4jAstNumber.create(value);
                    } else if (left.getType() == Swc4jAstType.Number && right.getType() == Swc4jAstType.Bool) {
                        int value = ((Swc4jAstNumber) left).asInt() + ((Swc4jAstBool) right).asInt();
                        newNode = Swc4jAstNumber.create(value);
                    }
                    break;
                default:
                    break;
            }
            if (newNode != null) {
                ++count;
                node.getParent().replaceNode(node, newNode);
            }
            return super.visitBinExpr(node);
        }

        @Override
        public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
            ISwc4jAst newNode = null;
            ISwc4jAstExpr arg = node.getArg();
            switch (node.getOp()) {
                case Bang:
                    switch (arg.getType()) {
                        case ArrayLit:
                            newNode = Swc4jAstBool.create(false);
                            break;
                        case Number:
                            newNode = Swc4jAstBool.create(!((Swc4jAstNumber) arg).asBoolean());
                            break;
                        default:
                            break;
                    }
                    break;
                case Plus:
                    switch (arg.getType()) {
                        case ArrayLit:
                            Swc4jAstArrayLit arrayLit = (Swc4jAstArrayLit) arg;
                            if (arrayLit.getElems().isEmpty()) {
                                newNode = Swc4jAstNumber.create(0);
                            }
                            break;
                        case Number: {
                            int value = ((Swc4jAstNumber) arg).asInt();
                            newNode = Swc4jAstNumber.create(value);
                        }
                        break;
                        case Bool: {
                            int value = ((Swc4jAstBool) arg).asInt();
                            newNode = Swc4jAstNumber.create(value);
                        }
                        break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (newNode != null) {
                ++count;
                node.getParent().replaceNode(node, newNode);
            }
            return super.visitUnaryExpr(node);
        }
    }
}
