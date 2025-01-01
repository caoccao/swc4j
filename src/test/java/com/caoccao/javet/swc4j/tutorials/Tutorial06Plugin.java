/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginHost;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginVisitors;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial06Plugin {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a JavaScript code snippet.
        String codeForAssignExpr = "a = b; c = d;";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Create a plugin visitors and add an assign expression visitor.
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(new Swc4jAstVisitor() {
            @Override
            public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
                Swc4jAstBindingIdent leftBindingIdent = node.getLeft().as(Swc4jAstBindingIdent.class);
                Swc4jAstIdent leftIdent = leftBindingIdent.getId().as(Swc4jAstIdent.class);
                Swc4jAstIdent rightIdent = node.getRight().as(Swc4jAstIdent.class);
                leftBindingIdent.setId(rightIdent);
                node.setRight(leftIdent);
                return super.visitAssignExpr(node);
            }
        });
        // Create a plugin host and add the plugin visitors.
        Swc4jPluginHost pluginHost = new Swc4jPluginHost().add(pluginVisitors);
        transpileAndTransform(specifier, pluginHost, swc4j, codeForAssignExpr);
        // Add an if statement visitor to the plugin visitors.
        pluginVisitors.add(new Swc4jAstVisitor() {
            @Override
            public Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node) {
                ISwc4jAstStmt cons = node.getCons().as(ISwc4jAstStmt.class);
                ISwc4jAstStmt alt = node.getAlt().get().as(ISwc4jAstStmt.class);
                node.setCons(alt);
                node.setAlt(cons);
                return super.visitIfStmt(node);
            }
        });
        // Prepare a JavaScript code snippet.
        String codeForIfStmt = "if (a) { b; } else { c; }";
        transpileAndTransform(specifier, pluginHost, swc4j, codeForIfStmt);
    }

    private static void transpileAndTransform(
            URL specifier,
            Swc4jPluginHost pluginHost,
            Swc4j swc4j,
            String code) throws Swc4jCoreException {
        {
            // Prepare an option with script name and media type.
            Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                    .setSpecifier(specifier)
                    .setMediaType(Swc4jMediaType.JavaScript)
                    .setInlineSources(false)
                    // Add the plugin host
                    .setPluginHost(pluginHost)
                    .setSourceMap(Swc4jSourceMapOption.None);
            // Transpile the code.
            Swc4jTranspileOutput output = swc4j.transpile(code, options);
            // Print the transpiled code.
            System.out.println("/*********************************************");
            System.out.println("       The transpiled code is as follows.");
            System.out.println("*********************************************/");
            System.out.println(output.getCode());
        }
        {
            // Prepare an option with script name and media type.
            // Minify is turned on by default.
            Swc4jTransformOptions options = new Swc4jTransformOptions()
                    .setSpecifier(specifier)
                    .setMediaType(Swc4jMediaType.JavaScript)
                    .setInlineSources(false)
                    // Add the plugin host
                    .setPluginHost(pluginHost)
                    .setSourceMap(Swc4jSourceMapOption.None);
            // Transform the code.
            Swc4jTransformOutput output = swc4j.transform(code, options);
            // Print the minified code.
            System.out.println("/*********************************************");
            System.out.println("       The minified code is as follows.");
            System.out.println("*********************************************/");
            System.out.println(output.getCode());
        }
    }
}
