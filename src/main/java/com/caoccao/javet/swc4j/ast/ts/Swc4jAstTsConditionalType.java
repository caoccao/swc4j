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

/**
 * The type swc4j ast ts conditional type.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsConditionalType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    /**
     * The Check type.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType checkType;
    /**
     * The Extends type.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType extendsType;
    /**
     * The False type.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType falseType;
    /**
     * The True type.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType trueType;

    /**
     * Instantiates a new swc4j ast ts conditional type.
     *
     * @param checkType   the check type
     * @param extendsType the extends type
     * @param trueType    the true type
     * @param falseType   the false type
     * @param span        the span
     */
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

    /**
     * Create swc4j ast ts conditional type.
     *
     * @param checkType   the check type
     * @param extendsType the extends type
     * @param trueType    the true type
     * @param falseType   the false type
     * @return the swc4j ast ts conditional type
     */
    public static Swc4jAstTsConditionalType create(
            ISwc4jAstTsType checkType,
            ISwc4jAstTsType extendsType,
            ISwc4jAstTsType trueType,
            ISwc4jAstTsType falseType) {
        return new Swc4jAstTsConditionalType(checkType, extendsType, trueType, falseType, Swc4jSpan.DUMMY);
    }

    /**
     * Gets check type.
     *
     * @return the check type
     */
    @Jni2RustMethod
    public ISwc4jAstTsType getCheckType() {
        return checkType;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(checkType, extendsType, trueType, falseType);
    }

    /**
     * Gets extends type.
     *
     * @return the extends type
     */
    @Jni2RustMethod
    public ISwc4jAstTsType getExtendsType() {
        return extendsType;
    }

    /**
     * Gets false type.
     *
     * @return the false type
     */
    @Jni2RustMethod
    public ISwc4jAstTsType getFalseType() {
        return falseType;
    }

    /**
     * Gets true type.
     *
     * @return the true type
     */
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
        if (checkType == oldNode && newNode instanceof ISwc4jAstTsType newCheckType) {
            setCheckType(newCheckType);
            return true;
        }
        if (extendsType == oldNode && newNode instanceof ISwc4jAstTsType newExtendsType) {
            setExtendsType(newExtendsType);
            return true;
        }
        if (falseType == oldNode && newNode instanceof ISwc4jAstTsType newFalseType) {
            setFalseType(newFalseType);
            return true;
        }
        if (trueType == oldNode && newNode instanceof ISwc4jAstTsType newTrueType) {
            setTrueType(newTrueType);
            return true;
        }
        return false;
    }

    /**
     * Sets check type.
     *
     * @param checkType the check type
     * @return the check type
     */
    public Swc4jAstTsConditionalType setCheckType(ISwc4jAstTsType checkType) {
        this.checkType = AssertionUtils.notNull(checkType, "Check type");
        this.checkType.setParent(this);
        return this;
    }

    /**
     * Sets extends type.
     *
     * @param extendsType the extends type
     * @return the extends type
     */
    public Swc4jAstTsConditionalType setExtendsType(ISwc4jAstTsType extendsType) {
        this.extendsType = AssertionUtils.notNull(extendsType, "Extends type");
        this.extendsType.setParent(this);
        return this;
    }

    /**
     * Sets false type.
     *
     * @param falseType the false type
     * @return the false type
     */
    public Swc4jAstTsConditionalType setFalseType(ISwc4jAstTsType falseType) {
        this.falseType = AssertionUtils.notNull(falseType, "False type");
        this.falseType.setParent(this);
        return this;
    }

    /**
     * Sets true type.
     *
     * @param trueType the true type
     * @return the true type
     */
    public Swc4jAstTsConditionalType setTrueType(ISwc4jAstTsType trueType) {
        this.trueType = AssertionUtils.notNull(trueType, "True type");
        this.trueType.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsConditionalType(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
