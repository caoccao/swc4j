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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNull;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstOptCall;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;

import java.util.List;
import java.util.Optional;

/**
 * The type Swc4j plugin visitor ES2015 transform spread.
 *
 * @since 0.8.0
 */
public class Swc4jPluginVisitorEs2015TransformSpread extends Swc4jAstVisitor {
    protected ISwc4jAstExpr convertArguments(ISwc4jAstExpr expr) {
        ISwc4jAstExpr innerExpr = expr.unParenExpr();
        if (innerExpr instanceof Swc4jAstIdent &&
                ISwc4jConstants.ARGUMENTS.equals(innerExpr.as(Swc4jAstIdent.class).getSym())) {
            Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(
                    Swc4jAstIdent.createArray(),
                    Swc4jAstIdent.createApply());
            Swc4jAstCallExpr callExpr = Swc4jAstCallExpr.create(memberExpr);
            callExpr.getArgs().add(Swc4jAstExprOrSpread.create(Swc4jAstNull.create()));
            callExpr.getArgs().add(Swc4jAstExprOrSpread.create(innerExpr));
            return callExpr;
        }
        return expr;
    }

    protected Swc4jAstExprOrSpread getConcatNode(List<Swc4jAstExprOrSpread> args) {
        final int length = args.size();
        Swc4jAstExprOrSpread arg;
        if (length == 1) {
            arg = Swc4jAstExprOrSpread.create(convertArguments(args.get(0).getExpr()));
        } else {
            // Prepare obj
            ISwc4jAstExpr obj;
            arg = args.get(0);
            int startIndex;
            if (arg.getSpread().isPresent()) {
                startIndex = 1;
                obj = convertArguments(arg.getExpr());
            } else {
                startIndex = 0;
                Swc4jAstArrayLit objArrayLit = Swc4jAstArrayLit.create();
                for (int i = 0; i < length; i++) {
                    arg = args.get(i);
                    if (arg.getSpread().isPresent()) {
                        break;
                    } else {
                        Swc4jAstExprOrSpread elem = Swc4jAstExprOrSpread.create(arg.getExpr());
                        elem.setParent(objArrayLit);
                        objArrayLit.getElems().add(Optional.of(elem));
                        startIndex = i + 1;
                    }
                }
                obj = objArrayLit;
            }
            // Prepare concat()
            Swc4jAstMemberExpr childMemberExpr = Swc4jAstMemberExpr.create(obj, Swc4jAstIdent.createConcat());
            Swc4jAstCallExpr childCallExpr = Swc4jAstCallExpr.create(childMemberExpr);
            Swc4jAstArrayLit objArrayLit = null;
            // Prepare args
            for (int i = startIndex; i < length; i++) {
                arg = args.get(i);
                if (arg.getSpread().isPresent()) {
                    if (objArrayLit != null) {
                        Swc4jAstExprOrSpread childArg = Swc4jAstExprOrSpread.create(objArrayLit);
                        childArg.setParent(childCallExpr);
                        childCallExpr.getArgs().add(childArg);
                        objArrayLit = null;
                    }
                    Swc4jAstExprOrSpread childElem = Swc4jAstExprOrSpread.create(convertArguments(arg.getExpr()));
                    childElem.setParent(childCallExpr);
                    childCallExpr.getArgs().add(childElem);
                } else {
                    if (objArrayLit == null) {
                        objArrayLit = Swc4jAstArrayLit.create();
                    }
                    Swc4jAstExprOrSpread elem = Swc4jAstExprOrSpread.create(arg.getExpr());
                    elem.setParent(objArrayLit);
                    objArrayLit.getElems().add(Optional.of(elem));
                }
            }
            if (objArrayLit != null) {
                Swc4jAstExprOrSpread childArg = Swc4jAstExprOrSpread.create(objArrayLit);
                childArg.setParent(childCallExpr);
                childCallExpr.getArgs().add(childArg);
            }
            arg = Swc4jAstExprOrSpread.create(childCallExpr);
        }
        return arg;
    }

    @Override
    public Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node) {
        if (node.isSpreadPresent()) {
            List<Optional<Swc4jAstExprOrSpread>> elems = node.getElems();
            final int length = elems.size();
            if (length == 1) {
                node.getParent().replaceNode(node, convertArguments(elems.get(0).get().getExpr()));
            } else {
                // Prepare obj
                ISwc4jAstExpr obj;
                Optional<Swc4jAstExprOrSpread> optionalExprOrSpread = elems.get(0);
                int startIndex;
                if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                    startIndex = 1;
                    obj = convertArguments(optionalExprOrSpread.get().getExpr());
                } else {
                    startIndex = 0;
                    Swc4jAstArrayLit objArrayLit = Swc4jAstArrayLit.create();
                    for (int i = 0; i < length; i++) {
                        optionalExprOrSpread = elems.get(i);
                        if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                            break;
                        } else {
                            Optional<Swc4jAstExprOrSpread> elem = optionalExprOrSpread
                                    .map(e -> Swc4jAstExprOrSpread.create(e.getExpr()));
                            elem.ifPresent(e -> e.setParent(objArrayLit));
                            objArrayLit.getElems().add(elem);
                            startIndex = i + 1;
                        }
                    }
                    obj = objArrayLit;
                }
                // Prepare concat()
                Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(obj, Swc4jAstIdent.createConcat());
                Swc4jAstCallExpr callExpr = Swc4jAstCallExpr.create(memberExpr);
                Swc4jAstArrayLit objArrayLit = null;
                // Prepare args
                for (int i = startIndex; i < length; i++) {
                    optionalExprOrSpread = elems.get(i);
                    if (optionalExprOrSpread.map(e -> e.getSpread().isPresent()).orElse(false)) {
                        if (objArrayLit != null) {
                            Swc4jAstExprOrSpread arg = Swc4jAstExprOrSpread.create(objArrayLit);
                            arg.setParent(callExpr);
                            callExpr.getArgs().add(arg);
                            objArrayLit = null;
                        }
                        Swc4jAstExprOrSpread elem = Swc4jAstExprOrSpread.create(
                                convertArguments(optionalExprOrSpread.get().getExpr()));
                        elem.setParent(callExpr);
                        callExpr.getArgs().add(elem);
                    } else {
                        if (objArrayLit == null) {
                            objArrayLit = Swc4jAstArrayLit.create();
                        }
                        Optional<Swc4jAstExprOrSpread> elem = optionalExprOrSpread
                                .map(e -> Swc4jAstExprOrSpread.create(e.getExpr()));
                        if (elem.isPresent()) {
                            elem.get().setParent(objArrayLit);
                        }
                        objArrayLit.getElems().add(elem);
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

    @Override
    public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
        if (node.isSpreadPresent() && node.getCallee() instanceof ISwc4jAstExpr) {
            ISwc4jAstExpr callee = node.getCallee().as(ISwc4jAstExpr.class);
            List<Swc4jAstExprOrSpread> args = node.getArgs();
            Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(callee, Swc4jAstIdent.createApply());
            node.setCallee(memberExpr);
            Swc4jAstExprOrSpread thisArg;
            if (callee instanceof Swc4jAstMemberExpr) {
                Swc4jAstMemberExpr childMemberExpr = callee.as(Swc4jAstMemberExpr.class);
                ISwc4jAstStmt stmt = node.getParent(ISwc4jAstStmt.class);
                Swc4jAstBlockStmt blockStmt = Swc4jAstBlockStmt.create();
                Swc4jAstVarDecl varDecl = Swc4jAstVarDecl.create(Swc4jAstVarDeclKind.Var);
                Swc4jAstVarDeclarator varDeclarator = Swc4jAstVarDeclarator.create(
                        Swc4jAstIdent.createDummy(),
                        childMemberExpr.getObj());
                varDecl.getDecls().add(varDeclarator);
                varDecl.updateParent();
                blockStmt.getStmts().add(varDecl);
                blockStmt.getStmts().add(Swc4jAstExprStmt.create(node));
                blockStmt.updateParent();
                stmt.getParent().replaceNode(stmt, blockStmt);
                childMemberExpr.setObj(Swc4jAstIdent.createDummy());
                thisArg = Swc4jAstExprOrSpread.create(Swc4jAstIdent.createDummy());
            } else {
                thisArg = Swc4jAstExprOrSpread.create(Swc4jAstNull.create());
            }
            Swc4jAstExprOrSpread arg = getConcatNode(args);
            args.clear();
            args.add(thisArg);
            args.add(arg);
        }
        return super.visitCallExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNewExpr(Swc4jAstNewExpr node) {
        if (node.isSpreadPresent()) {
            ISwc4jAstExpr callee = node.getCallee();
            List<Swc4jAstExprOrSpread> args = node.getArgs().get();
            Swc4jAstExprOrSpread arg = getConcatNode(args);
            Swc4jAstMemberExpr memberExpr = Swc4jAstMemberExpr.create(callee, Swc4jAstIdent.createApply());
            Swc4jAstCallExpr callExpr = Swc4jAstCallExpr.create(memberExpr);
            callExpr.getArgs().add(Swc4jAstExprOrSpread.create(Swc4jAstNull.create()));
            callExpr.getArgs().add(arg);
            node.getParent().replaceNode(node, callExpr);
        }
        return super.visitNewExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitOptCall(Swc4jAstOptCall node) {
        // TODO
        return super.visitOptCall(node);
    }
}
