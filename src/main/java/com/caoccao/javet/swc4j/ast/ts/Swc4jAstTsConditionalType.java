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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
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
public class Swc4jAstTsConditionalType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType checkType;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType extendsType;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType falseType;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType trueType;

    @Jni2RustMethod
    public Swc4jAstTsConditionalType(
            ISwc4jAstTsType checkType,
            ISwc4jAstTsType extendsType,
            ISwc4jAstTsType trueType,
            ISwc4jAstTsType falseType,
            Swc4jSpan span) {
        super(span);
        setCheckType(checkType);
        setExtendsType(extendsType);
        setFalseType(falseType);
        setTrueType(trueType);
    }

    public static Swc4jAstTsConditionalType create(
            ISwc4jAstTsType checkType,
            ISwc4jAstTsType extendsType,
            ISwc4jAstTsType trueType,
            ISwc4jAstTsType falseType) {
        return new Swc4jAstTsConditionalType(checkType, extendsType, trueType, falseType, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getCheckType() {
        return checkType;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(checkType, extendsType, trueType, falseType);
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getExtendsType() {
        return extendsType;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getFalseType() {
        return falseType;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getTrueType() {
        return trueType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsConditionalType;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (checkType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setCheckType((ISwc4jAstTsType) newNode);
            return true;
        }
        if (extendsType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setExtendsType((ISwc4jAstTsType) newNode);
            return true;
        }
        if (falseType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setFalseType((ISwc4jAstTsType) newNode);
            return true;
        }
        if (trueType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setTrueType((ISwc4jAstTsType) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsConditionalType setCheckType(ISwc4jAstTsType checkType) {
        this.checkType = AssertionUtils.notNull(checkType, "Check type");
        this.checkType.setParent(this);
        return this;
    }

    public Swc4jAstTsConditionalType setExtendsType(ISwc4jAstTsType extendsType) {
        this.extendsType = AssertionUtils.notNull(extendsType, "Extends type");
        this.extendsType.setParent(this);
        return this;
    }

    public Swc4jAstTsConditionalType setFalseType(ISwc4jAstTsType falseType) {
        this.falseType = AssertionUtils.notNull(falseType, "False type");
        this.falseType.setParent(this);
        return this;
    }

    public Swc4jAstTsConditionalType setTrueType(ISwc4jAstTsType trueType) {
        this.trueType = AssertionUtils.notNull(trueType, "True type");
        this.trueType.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsConditionalType(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
