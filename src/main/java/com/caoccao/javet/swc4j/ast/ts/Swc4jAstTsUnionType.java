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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsUnionOrIntersectionType;
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
public class Swc4jAstTsUnionType
        extends Swc4jAst
        implements ISwc4jAstTsUnionOrIntersectionType {
    @Jni2RustField(componentBox = true)
    protected final List<ISwc4jAstTsType> types;

    @Jni2RustMethod
    public Swc4jAstTsUnionType(
            List<ISwc4jAstTsType> types,
            Swc4jSpan span) {
        super(span);
        this.types = AssertionUtils.notNull(types, "Types");
        this.types.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsUnionType create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstTsUnionType create(List<ISwc4jAstTsType> types) {
        return new Swc4jAstTsUnionType(types, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(types);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsUnionType;
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsType> getTypes() {
        return types;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!types.isEmpty() && newNode instanceof ISwc4jAstTsType newType) {
            final int size = types.size();
            for (int i = 0; i < size; i++) {
                if (types.get(i) == oldNode) {
                    types.set(i, newType);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsUnionType(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
