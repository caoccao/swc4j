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
public class Swc4jAstTsTypeAnn
        extends Swc4jAst {
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType typeAnn;

    @Jni2RustMethod
    public Swc4jAstTsTypeAnn(
            ISwc4jAstTsType typeAnn,
            Swc4jSpan span) {
        super(span);
        setTypeAnn(typeAnn);
    }

    public static Swc4jAstTsTypeAnn create(ISwc4jAstTsType typeAnn) {
        return new Swc4jAstTsTypeAnn(typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(typeAnn);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeAnn;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (typeAnn == oldNode && newNode instanceof ISwc4jAstTsType newTypeAnn) {
            setTypeAnn(newTypeAnn);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTypeAnn setTypeAnn(ISwc4jAstTsType typeAnn) {
        this.typeAnn = AssertionUtils.notNull(typeAnn, "Type ann");
        this.typeAnn.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsTypeAnn(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
