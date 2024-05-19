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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstInvalid;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "Array", type = Swc4jAstArrayLit.class),
                @Jni2RustEnumMapping(name = "Arrow", type = Swc4jAstArrowExpr.class),
                @Jni2RustEnumMapping(name = "Assign", type = Swc4jAstAssignExpr.class),
                @Jni2RustEnumMapping(name = "Await", type = Swc4jAstAwaitExpr.class),
                @Jni2RustEnumMapping(name = "Bin", type = Swc4jAstBinExpr.class),
                @Jni2RustEnumMapping(name = "Call", type = Swc4jAstCallExpr.class),
                @Jni2RustEnumMapping(name = "Class", type = Swc4jAstClassExpr.class),
                @Jni2RustEnumMapping(name = "Cond", type = Swc4jAstCondExpr.class),
                @Jni2RustEnumMapping(name = "Fn", type = Swc4jAstFnExpr.class),
                @Jni2RustEnumMapping(name = "Ident", type = Swc4jAstIdent.class),
                @Jni2RustEnumMapping(name = "Invalid", type = Swc4jAstInvalid.class),
                @Jni2RustEnumMapping(name = "JSXElement", type = Swc4jAstJsxElement.class, box = true),
                @Jni2RustEnumMapping(name = "JSXEmpty", type = Swc4jAstJsxEmptyExpr.class),
                @Jni2RustEnumMapping(name = "JSXFragment", type = Swc4jAstJsxFragment.class),
                @Jni2RustEnumMapping(name = "JSXMember", type = Swc4jAstJsxMemberExpr.class),
                @Jni2RustEnumMapping(name = "JSXNamespacedName", type = Swc4jAstJsxNamespacedName.class),
                @Jni2RustEnumMapping(name = "Lit", type = ISwc4jAstLit.class),
                @Jni2RustEnumMapping(name = "Member", type = Swc4jAstMemberExpr.class),
                @Jni2RustEnumMapping(name = "MetaProp", type = Swc4jAstMetaPropExpr.class),
                @Jni2RustEnumMapping(name = "New", type = Swc4jAstNewExpr.class),
                @Jni2RustEnumMapping(name = "Object", type = Swc4jAstObjectLit.class),
                @Jni2RustEnumMapping(name = "OptChain", type = Swc4jAstOptChainExpr.class),
                @Jni2RustEnumMapping(name = "Paren", type = Swc4jAstParenExpr.class),
                @Jni2RustEnumMapping(name = "PrivateName", type = Swc4jAstPrivateName.class),
                @Jni2RustEnumMapping(name = "Seq", type = Swc4jAstSeqExpr.class),
                @Jni2RustEnumMapping(name = "SuperProp", type = Swc4jAstSuperPropExpr.class),
                @Jni2RustEnumMapping(name = "TaggedTpl", type = Swc4jAstTaggedTpl.class),
                @Jni2RustEnumMapping(name = "This", type = Swc4jAstThisExpr.class),
                @Jni2RustEnumMapping(name = "Tpl", type = Swc4jAstTpl.class),
                @Jni2RustEnumMapping(name = "TsAs", type = Swc4jAstTsAsExpr.class),
                @Jni2RustEnumMapping(name = "TsConstAssertion", type = Swc4jAstTsConstAssertion.class),
                @Jni2RustEnumMapping(name = "TsInstantiation", type = Swc4jAstTsInstantiation.class),
                @Jni2RustEnumMapping(name = "TsNonNull", type = Swc4jAstTsNonNullExpr.class),
                @Jni2RustEnumMapping(name = "TsSatisfies", type = Swc4jAstTsSatisfiesExpr.class),
                @Jni2RustEnumMapping(name = "TsTypeAssertion", type = Swc4jAstTsTypeAssertion.class),
                @Jni2RustEnumMapping(name = "Unary", type = Swc4jAstUnaryExpr.class),
                @Jni2RustEnumMapping(name = "Update", type = Swc4jAstUpdateExpr.class),
                @Jni2RustEnumMapping(name = "Yield", type = Swc4jAstYieldExpr.class),
        }
)
public interface ISwc4jAstExpr
        extends ISwc4jAstVarDeclOrExpr, ISwc4jAstPat, ISwc4jAstJsxExpr, ISwc4jAstCallee, ISwc4jAstBlockStmtOrExpr,
        ISwc4jAstAssignTarget {
    default ISwc4jAstExpr unParenExpr() {
        ISwc4jAstExpr expr = this;
        while (true) {
            if (expr instanceof Swc4jAstParenExpr) {
                expr = expr.as(Swc4jAstParenExpr.class).getExpr();
            } else if (expr instanceof Swc4jAstSeqExpr) {
                Swc4jAstSeqExpr seqExpr = expr.as(Swc4jAstSeqExpr.class);
                if (seqExpr.getExprs().isEmpty()) {
                    break;
                } else {
                    expr = seqExpr.getExprs().get(seqExpr.getExprs().size() - 1);
                }
            } else {
                break;
            }
        }
        return expr;
    }
}
