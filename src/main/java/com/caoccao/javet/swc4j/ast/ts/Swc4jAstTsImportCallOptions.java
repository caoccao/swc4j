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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
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
 * The type swc4j ast ts import call options.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsImportCallOptions
        extends Swc4jAst
        implements ISwc4jAst {
    /**
     * The With.
     */
    @Jni2RustField(box = true)
    protected Swc4jAstObjectLit with;

    /**
     * Instantiates a new swc4j ast ts import call options.
     *
     * @param with the with
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstTsImportCallOptions(
            Swc4jAstObjectLit with,
            Swc4jSpan span) {
        super(span);
        setWith(with);
    }

    /**
     * Create swc4j ast ts import call options.
     *
     * @param with the with
     * @return the swc4j ast ts import call options
     */
    public static Swc4jAstTsImportCallOptions create(Swc4jAstObjectLit with) {
        return new Swc4jAstTsImportCallOptions(with, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(with);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsImportCallOptions;
    }

    /**
     * Gets with.
     *
     * @return the with
     */
    @Jni2RustMethod
    public Swc4jAstObjectLit getWith() {
        return with;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (with == oldNode && newNode instanceof Swc4jAstObjectLit newWith) {
            setWith(newWith);
            return true;
        }
        return false;
    }

    /**
     * Sets with.
     *
     * @param with the with
     * @return the with
     */
    public Swc4jAstTsImportCallOptions setWith(Swc4jAstObjectLit with) {
        this.with = AssertionUtils.notNull(with, "With");
        this.with.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsImportCallOptions(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
