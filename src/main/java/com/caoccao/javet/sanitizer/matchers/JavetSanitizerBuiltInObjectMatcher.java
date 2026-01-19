/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropOrSpread;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;

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
            case AssignExpr: // Entry
                return matches(options, node.as(Swc4jAstAssignExpr.class).getLeft());
            case AssignPatProp:
                return matches(options, node.as(Swc4jAstAssignPatProp.class).getKey());
            case ArrayPat:
                for (Optional<ISwc4jAstPat> elem : node.as(Swc4jAstArrayPat.class).getElems()) {
                    if (elem.isPresent()) {
                        return matches(options, elem.get().as(ISwc4jAstPat.class));
                    }
                }
                break;
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
            case ClassDecl: // Entry
                return matches(options, node.as(Swc4jAstClassDecl.class).getIdent());
            case ExprOrSpread:
                return matches(options, node.as(Swc4jAstExprOrSpread.class).getExpr());
            case FnDecl: // Entry
                return matches(options, node.as(Swc4jAstFnDecl.class).getIdent());
            case Ident:
                String identifier = node.as(Swc4jAstIdent.class).getSym();
                if (options.getReservedIdentifierMatcher().apply(identifier)) {
                    if (node.getParent() instanceof Swc4jAstFnDecl) {
                        if (!options.getReservedFunctionIdentifierSet().contains(identifier)) {
                            return node;
                        }
                    } else if (!options.getReservedIdentifierSet().contains(identifier)) {
                        return node;
                    } else if (!options.getReservedMutableIdentifierSet().contains(identifier)) {
                        return node;
                    }
                } else if (options.getBuiltInObjectSet().contains(identifier)) {
                    return node;
                }
                break;
            case KeyValuePatProp:
                return matches(options, node.as(Swc4jAstKeyValuePatProp.class).getKey());
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
            case ObjectPat:
                for (ISwc4jAstObjectPatProp objectPatProp : node.as(Swc4jAstObjectPat.class).getProps()) {
                    ISwc4jAst matchedNode = matches(options, objectPatProp);
                    if (matchedNode != null) {
                        return matchedNode;
                    }
                }
                break;
            case OptChainExpr:
                return matches(options, node.as(Swc4jAstOptChainExpr.class).getBase());
            case VarDeclarator: // Entry
                return matches(options, node.as(Swc4jAstVarDeclarator.class).getName());
            default:
                break;
        }
        return null;
    }
}
