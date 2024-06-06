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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginHost;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginVisitors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tutorial07MimicBabel {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a JavaScript code snippet.
        String code = "const add = (a, b) => a + b; const multiply = (a, b) => { return a * b; }";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Create a plugin visitors and add an arrow expression visitor.
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(new Swc4jAstVisitor() {
            @Override
            public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
                // Transform the params.
                List<Swc4jAstParam> params = node.getParams().stream()
                        .map(Swc4jAstParam::create)
                        .collect(Collectors.toList());
                // Transform the block statement.
                Swc4jAstBlockStmt blockStmt;
                ISwc4jAstBlockStmtOrExpr body = node.getBody();
                if (body instanceof Swc4jAstBlockStmt) {
                    // If the body is a block statement, convert the body directly.
                    blockStmt = body.as(Swc4jAstBlockStmt.class);
                } else {
                    // If the body is an expression, put that expression in a return statement
                    // and add that return statement to the block statement.
                    List<ISwc4jAstStmt> stmts = new ArrayList<>();
                    stmts.add(Swc4jAstReturnStmt.create(body.as(ISwc4jAstExpr.class)));
                    blockStmt = Swc4jAstBlockStmt.create(stmts);
                }
                // Create the function.
                Swc4jAstFunction fn = Swc4jAstFunction.create(
                        params,
                        new ArrayList<>(),
                        blockStmt,
                        node.isGenerator(),
                        node.isAsync(),
                        node.getTypeParams().orElse(null),
                        node.getReturnType().orElse(null));
                // Create the function expression.
                Swc4jAstFnExpr fnExpr = Swc4jAstFnExpr.create(fn);
                // Replace the arrow expression with the function expression.
                node.getParent().replaceNode(node, fnExpr);
                return super.visitArrowExpr(node);
            }
        });
        // Create a plugin host and add the plugin visitors.
        Swc4jPluginHost pluginHost = new Swc4jPluginHost().add(pluginVisitors);
        // Prepare an option with script name and media type.
        Swc4jTransformOptions options = new Swc4jTransformOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.JavaScript)
                .setMinify(false)
                .setInlineSources(false)
                .setPluginHost(pluginHost)
                .setSourceMap(Swc4jSourceMapOption.None);
        // Transform the code.
        Swc4jTransformOutput output = swc4j.transform(code, options);
        // Print the transformed code.
        System.out.println("/*********************************************");
        System.out.println("       The transformed code is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getCode());
    }
}
