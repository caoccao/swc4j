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

package com.caoccao.javet.swc4j.plugins.es2015;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;

import java.util.Optional;

/**
 * The type Swc4j plugin visitor ES2015 transform spread.
 *
 * @since 0.8.0
 */
public class Swc4jPluginVisitorEs2015TransformSpread extends Swc4jAstVisitor {
    @Override
    public Swc4jAstVisitorResponse visitExprOrSpread(Swc4jAstExprOrSpread node) {
        if (node.getSpread().isPresent()) {
            switch (node.getParent().getType()) {
                case ArrayLit:
                    Swc4jAstArrayLit arrayLit = node.getParent().as(Swc4jAstArrayLit.class);
                    final int length = arrayLit.getElems().size();
                    if (length == 1) {
                        arrayLit.getParent().replaceNode(arrayLit, node.getExpr());
                    } else {
                        int index = arrayLit.indexOf(node);
                        if (index == 0) {
                            Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(
                                    node.getExpr(),
                                    Swc4jAstIdent.create(Swc4jAstArrayLit.CONCAT));
                            Swc4jAstCallExpr callExpr = Swc4jAstCallExpr.create(memberExpr);
                            ISwc4jAst newNode = callExpr;
                            for (int i = 1; i < length; i++) {
                                Optional<Swc4jAstExprOrSpread> elem = arrayLit.getElems().get(i);
                                if (elem.isPresent()) {
                                    if (elem.get().getSpread().isPresent()) {
                                        callExpr.getArgs().add(Swc4jAstExprOrSpread.create(elem.get().getExpr()));
                                        newNode = callExpr;
                                        memberExpr = Swc4jAstMemberExpr.create(
                                                callExpr,
                                                Swc4jAstIdent.create(Swc4jAstArrayLit.CONCAT));
                                        callExpr = Swc4jAstCallExpr.create(memberExpr);
                                    } else {
                                        // TODO
                                    }
                                } else {
                                    // TODO
                                }
                            }
                            arrayLit.getParent().replaceNode(arrayLit, newNode);
                        } else if (index == length - 1) {
                            // TODO
                        } else {
                            // TODO
                        }
                    }
                    break;
                case CallExpr:
                    break;
                case NewExpr:
                    break;
                case OptCall:
                    break;
                default:
                    break;
            }
        }
        return super.visitExprOrSpread(node);
    }
}
