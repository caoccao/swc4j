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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProp;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstAssignProp
        extends Swc4jAst
        implements ISwc4jAstProp {
    protected Swc4jAstIdent key;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr value;

    @Jni2RustMethod
    public Swc4jAstAssignProp(
            Swc4jAstIdent key,
            ISwc4jAstExpr value,
            Swc4jSpan span) {
        super(span);
        setKey(key);
        setValue(value);
    }

    public static Swc4jAstAssignProp create(Swc4jAstIdent key, ISwc4jAstExpr value) {
        return new Swc4jAstAssignProp(key, value, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(key, value);
    }

    @Jni2RustMethod
    public Swc4jAstIdent getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.AssignProp;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getValue() {
        return value;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (key == oldNode && newNode instanceof Swc4jAstIdent) {
            setKey((Swc4jAstIdent) newNode);
            return true;
        }
        if (value == oldNode && newNode instanceof ISwc4jAstExpr) {
            setValue((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstAssignProp setKey(Swc4jAstIdent key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstAssignProp setValue(ISwc4jAstExpr value) {
        this.value = AssertionUtils.notNull(value, "Value");
        this.value.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitAssignProp(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
