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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstMemberExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstOptChainBase, ISwc4jAstSimpleAssignTarget {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr obj;
    protected ISwc4jAstMemberProp prop;

    @Jni2RustMethod
    public Swc4jAstMemberExpr(
            ISwc4jAstExpr obj,
            ISwc4jAstMemberProp prop,
            Swc4jSpan span) {
        super(span);
        setObj(obj);
        setProp(prop);
    }

    public static Swc4jAstMemberExpr create(ISwc4jAstExpr obj, ISwc4jAstMemberProp prop) {
        return new Swc4jAstMemberExpr(obj, prop, Swc4jSpan.DUMMY);
    }

    @Override
    public Optional<ISwc4jAst> eval() {
        ISwc4jAstExpr obj = this.obj.unParenExpr();
        switch (obj.getType()) {
            case ArrayLit:
                Swc4jAstArrayLit arrayLit = obj.as(Swc4jAstArrayLit.class);
                if (prop instanceof Swc4jAstComputedPropName) {
                    Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                    switch (expr.getType()) {
                        case BinExpr:
                        case Str:
                            return super.eval();
                    }
                } else if (prop instanceof Swc4jAstIdent) {
                    return super.eval();
                }
                if (arrayLit.getElems().isEmpty()) {
                    return Optional.of(Swc4jAstIdent.createUndefined());
                }
                break;
            case MemberExpr: {
                Optional<String> call = evalAsCall();
                if (call.isPresent()) {
                    if (ISwc4jConstants.CONSTRUCTOR.equals(call.get())) {
                        Swc4jAstMemberExpr childMemberExpr = obj.as(Swc4jAstMemberExpr.class);
                        if (childMemberExpr.getObj() instanceof Swc4jAstArrayLit) {
                            return childMemberExpr.evalAsCall()
                                    .filter(Swc4jAstArrayLit.ARRAY_FUNCTION_SET::contains)
                                    .map(c -> Swc4jAstIdent.createFunction());
                        }
                    } else if (ISwc4jConstants.NAME.equals(call.get())) {
                        Swc4jAstMemberExpr childMemberExpr = obj.as(Swc4jAstMemberExpr.class);
                        Optional<String> childCall = childMemberExpr.evalAsCall();
                        if (childCall.isPresent()) {
                            if (ISwc4jConstants.CONSTRUCTOR.equals(childCall.get())) {
                                ISwc4jAstExpr childExpr = childMemberExpr.getObj().unParenExpr();
                                if (childExpr instanceof Swc4jAstStr) {
                                    return Optional.of(Swc4jAstStr.create(ISwc4jConstants.STRING));
                                }
                            }
                        }
                    }
                }
                break;
            }
            case Str:
                if (prop instanceof Swc4jAstComputedPropName) {
                    Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                    String value = obj.as(Swc4jAstStr.class).getValue();
                    switch (expr.getType()) {
                        case Number: {
                            // 'string'[0] => 's'
                            int index = expr.as(Swc4jAstNumber.class).asInt();
                            if (index >= 0 && index < value.length()) {
                                value = value.substring(index, index + 1);
                                return Optional.of(Swc4jAstStr.create(value));
                            }
                            break;
                        }
                        case Str: {
                            // 'string'['0'] => 's'
                            Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                            if (StringUtils.isDigit(str.getValue())) {
                                int index = str.asInt();
                                if (index >= 0 && index < value.length()) {
                                    value = value.substring(index, index + 1);
                                    return Optional.of(Swc4jAstStr.create(value));
                                }
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return super.eval();
    }

    public Optional<String> evalAsCall() {
        if (prop instanceof Swc4jAstComputedPropName) {
            Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
            ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
            if (expr instanceof Swc4jAstStr) {
                return Optional.of(expr.as(Swc4jAstStr.class).getValue());
            }
        } else if (prop instanceof Swc4jAstIdentName) {
            return Optional.of(prop.as(Swc4jAstIdentName.class).getSym());
        }
        return Optional.empty();
    }

    public Optional<String> evalAsString() {
        ISwc4jAstExpr obj = this.obj.unParenExpr();
        Optional<String> call = evalAsCall();
        if (call.isPresent()) {
            switch (obj.getType()) {
                case ArrayLit:
                    if (Swc4jAstArrayLit.ARRAY_FUNCTION_SET.contains(call.get())) {
                        return Optional.of("function " + call.get() + "() { [native code] }");
                    }
                    if (ISwc4jConstants.CONSTRUCTOR.equals(call.get())) {
                        return Optional.of("function " + ISwc4jConstants.ARRAY + "() { [native code] }");
                    }
                    return Optional.of(ISwc4jConstants.UNDEFINED);
                case Number:
                    if (ISwc4jConstants.CONSTRUCTOR.equals(call.get())) {
                        return Optional.of("function " + ISwc4jConstants.NUMBER + "() { [native code] }");
                    }
                    return Optional.of(ISwc4jConstants.UNDEFINED);
                default:
                    break;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(obj, prop);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getObj() {
        return obj;
    }

    @Jni2RustMethod
    public ISwc4jAstMemberProp getProp() {
        return prop;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.MemberExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (obj == oldNode && newNode instanceof ISwc4jAstExpr) {
            setObj((ISwc4jAstExpr) newNode);
            return true;
        }
        if (prop == oldNode && newNode instanceof ISwc4jAstMemberProp) {
            setProp((ISwc4jAstMemberProp) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstMemberExpr setObj(ISwc4jAstExpr obj) {
        this.obj = AssertionUtils.notNull(obj, "Obj");
        this.obj.setParent(this);
        return this;
    }

    public Swc4jAstMemberExpr setProp(ISwc4jAstMemberProp prop) {
        this.prop = AssertionUtils.notNull(prop, "Prop");
        this.prop.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitMemberExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
