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

package com.caoccao.javet.sanitizer.matchers;

import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropOrSpread;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;

import java.util.Optional;

public final class JavetSanitizerBuiltInObjectMatcher implements IJavetSanitizerMatcher {

    private static final JavetSanitizerBuiltInObjectMatcher INSTANCE = new JavetSanitizerBuiltInObjectMatcher();

    private JavetSanitizerBuiltInObjectMatcher() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 0.7.0
     */
    public static JavetSanitizerBuiltInObjectMatcher getInstance() {
        return INSTANCE;
    }

    @Override
    public ISwc4jAst matches(JavetSanitizerOptions options, ISwc4jAst node) {
        switch (node.getType()) {
            case AssignExpr:
                return matches(options, node.as(Swc4jAstAssignExpr.class).getLeft());
            case ArrayLit:
                for (Optional<Swc4jAstExprOrSpread> optionalExprOrSpread : node.as(Swc4jAstArrayLit.class).getElems()) {
                    if (optionalExprOrSpread.isPresent()) {
                        ISwc4jAst matchedNode = matches(options, optionalExprOrSpread.get());
                        if (matchedNode != null) {
                            return matchedNode;
                        }
                    }
                }
                break;
            case BindingIdent:
                return matches(options, node.as(Swc4jAstBindingIdent.class).getId());
            case ExprOrSpread:
                return matches(options, node.as(Swc4jAstExprOrSpread.class).getExpr());
            case Ident:
                if (options.getBuiltInObjectSet().contains(node.as(Swc4jAstIdent.class).getSym())) {
                    return node;
                }
                break;
            case MemberExpr:
                return matches(options, node.as(Swc4jAstMemberExpr.class).getObj());
            case ObjectLit:
                for (ISwc4jAstPropOrSpread propOrSpread : node.as(Swc4jAstObjectLit.class).getProps()) {
                    ISwc4jAst matchedNode = matches(options, propOrSpread);
                    if (matchedNode != null) {
                        return matchedNode;
                    }
                }
                break;
            // TODO
            default:
                break;
        }
        return null;
    }
}
