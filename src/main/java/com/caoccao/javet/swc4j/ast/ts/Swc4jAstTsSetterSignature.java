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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
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
public class Swc4jAstTsSetterSignature
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected boolean computed;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr key;
    protected boolean optional;
    protected ISwc4jAstTsFnParam param;
    protected boolean readonly;

    @Jni2RustMethod
    public Swc4jAstTsSetterSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            ISwc4jAstTsFnParam param,
            Swc4jSpan span) {
        super(span);
        setComputed(computed);
        setKey(key);
        setOptional(optional);
        setParam(param);
        setReadonly(readonly);
    }

    public static Swc4jAstTsSetterSignature create(ISwc4jAstExpr key, ISwc4jAstTsFnParam param) {
        return create(false, key, param);
    }

    public static Swc4jAstTsSetterSignature create(
            boolean readonly,
            ISwc4jAstExpr key,
            ISwc4jAstTsFnParam param) {
        return create(readonly, key, false, param);
    }

    public static Swc4jAstTsSetterSignature create(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            ISwc4jAstTsFnParam param) {
        return create(readonly, key, computed, false, param);
    }

    public static Swc4jAstTsSetterSignature create(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            ISwc4jAstTsFnParam param) {
        return new Swc4jAstTsSetterSignature(readonly, key, computed, optional, param, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(key, param);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getKey() {
        return key;
    }

    @Jni2RustMethod
    public ISwc4jAstTsFnParam getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsSetterSignature;
    }

    @Jni2RustMethod
    public boolean isComputed() {
        return computed;
    }

    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (key == oldNode && newNode instanceof ISwc4jAstExpr) {
            setKey((ISwc4jAstExpr) newNode);
            return true;
        }
        if (param == oldNode && newNode instanceof ISwc4jAstTsFnParam) {
            setParam((ISwc4jAstTsFnParam) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsSetterSignature setComputed(boolean computed) {
        this.computed = computed;
        return this;
    }

    public Swc4jAstTsSetterSignature setKey(ISwc4jAstExpr key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstTsSetterSignature setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Swc4jAstTsSetterSignature setParam(ISwc4jAstTsFnParam param) {
        this.param = AssertionUtils.notNull(param, "Param");
        this.param.setParent(this);
        return this;
    }

    public Swc4jAstTsSetterSignature setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsSetterSignature(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
