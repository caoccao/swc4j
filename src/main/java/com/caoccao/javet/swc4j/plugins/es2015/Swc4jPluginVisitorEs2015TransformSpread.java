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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
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
    public Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node) {
        if (node.isSpreadPresent()) {
            final int length = node.getElems().size();
            if (length == 1) {
                node.getParent().replaceNode(node, node.getElems().get(0).get().getExpr());
            } else {
                // Prepare obj
                ISwc4jAstExpr obj;
                Optional<Swc4jAstExprOrSpread> optionalExprOrSpread = node.getElems().get(0);
                int startIndex;
                if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                    startIndex = 1;
                    obj = optionalExprOrSpread.get().getExpr();
                } else {
                    startIndex = 0;
                    Swc4jAstArrayLit objArrayLit = Swc4jAstArrayLit.create();
                    for (int i = 0; i < length; i++) {
                        optionalExprOrSpread = node.getElems().get(i);
                        if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                            break;
                        } else {
                            Swc4jAstExprOrSpread elem = Swc4jAstExprOrSpread.create(optionalExprOrSpread.get().getExpr());
                            elem.setParent(objArrayLit);
                            objArrayLit.getElems().add(Optional.of(elem));
                            startIndex = i + 1;
                        }
                    }
                    obj = objArrayLit;
                }
                // Prepare concat()
                Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(
                        obj,
                        Swc4jAstIdent.create(Swc4jAstArrayLit.CONCAT));
                Swc4jAstCallExpr callExpr = Swc4jAstCallExpr.create(memberExpr);
                Swc4jAstArrayLit objArrayLit = null;
                // Prepare args
                for (int i = startIndex; i < length; i++) {
                    optionalExprOrSpread = node.getElems().get(i);
                    if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                        if (objArrayLit != null) {
                            Swc4jAstExprOrSpread arg = Swc4jAstExprOrSpread.create(objArrayLit);
                            arg.setParent(callExpr);
                            callExpr.getArgs().add(arg);
                            objArrayLit = null;
                        }
                        Swc4jAstExprOrSpread elem = Swc4jAstExprOrSpread.create(optionalExprOrSpread.get().getExpr());
                        elem.setParent(callExpr);
                        callExpr.getArgs().add(elem);
                    } else {
                        if (objArrayLit == null) {
                            objArrayLit = Swc4jAstArrayLit.create();
                        }
                        Swc4jAstExprOrSpread elem = null;
                        if (optionalExprOrSpread.isPresent()) {
                            elem = Swc4jAstExprOrSpread.create(optionalExprOrSpread.get().getExpr());
                            elem.setParent(objArrayLit);
                        }
                        objArrayLit.getElems().add(Optional.ofNullable(elem));
                    }
                }
                if (objArrayLit != null) {
                    Swc4jAstExprOrSpread arg = Swc4jAstExprOrSpread.create(objArrayLit);
                    arg.setParent(callExpr);
                    callExpr.getArgs().add(arg);
                }
                node.getParent().replaceNode(node, callExpr);
            }
        }
        return super.visitArrayLit(node);
    }
}
