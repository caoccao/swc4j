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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstRegex;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.comments.Swc4jComment;
import com.caoccao.javet.swc4j.comments.Swc4jComments;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;

import java.net.MalformedURLException;
import java.net.URL;

public class Tutorial04Ast {
    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a TypeScript code snippet.
        String code = """
                import { Validator } from './Validator';
                
                class EmailValidator implements Validator {
                    isValid(s: string): boolean {
                        // This is a regex for email validation.
                        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
                        return emailRegex.test(s);
                    }
                }
                
                export { EmailValidator };""";
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Prepare an option with script name and media type.
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                // Set capture ast.
                .setCaptureAst(true)
                .setParseMode(Swc4jParseMode.Module);
        // Parse the code.
        Swc4jParseOutput output = swc4j.parse(code, options);
        // Print the ast.
        System.out.println("/*********************************************");
        System.out.println("         The ast is as follows.");
        System.out.println("*********************************************/");
        System.out.println(output.getProgram().toDebugString());
        // Visit the ast.
        System.out.println("/*********************************************");
        System.out.println("      The visitor output is as follows.");
        System.out.println("*********************************************/");
        output.getProgram().visit(new Visitor());
        // Turn on the comments.
        options.setCaptureComments(true);
        // Parse the code again.
        output = swc4j.parse(code, options);
        System.out.println("/*********************************************");
        System.out.println("      The visitor output is as follows.");
        System.out.println("*********************************************/");
        output.getProgram().visit(new Visitor(output.getComments()));
    }

    static class Visitor extends Swc4jAstVisitor {
        private final Swc4jComments comments;

        public Visitor() {
            this(null);
        }

        public Visitor(Swc4jComments comments) {
            this.comments = comments;
        }

        @Override
        public Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node) {
            System.out.println("Class name is " + node.getIdent());
            node.getClazz().getImplements().forEach(impl ->
                    System.out.println("  Implements " + impl.getExpr()));
            return super.visitClassDecl(node);
        }

        @Override
        public Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node) {
            System.out.println("Method name is " + node.getKey());
            return super.visitClassMethod(node);
        }

        @Override
        public Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node) {
            System.out.println("Regex is " + node);
            return super.visitRegex(node);
        }

        @Override
        public Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node) {
            if (comments != null) {
                Swc4jComment comment = comments.getLeading(node.getSpan()).get(0);
                System.out.println("Leading comment is a " + comment.getKind().name() + " comment.");
                System.out.println("  And the comment text is '" + comment.getText() + "'");
            }
            return super.visitVarDecl(node);
        }
    }
}
