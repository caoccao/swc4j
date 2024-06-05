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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
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
public class Swc4jAstTsTplLitType
        extends Swc4jAst
        implements ISwc4jAstTsLit {
    protected final List<Swc4jAstTplElement> quasis;
    @Jni2RustField(componentBox = true)
    protected final List<ISwc4jAstTsType> types;

    @Jni2RustMethod
    public Swc4jAstTsTplLitType(
            List<ISwc4jAstTsType> types,
            List<Swc4jAstTplElement> quasis,
            Swc4jSpan span) {
        super(span);
        this.quasis = AssertionUtils.notNull(quasis, "Quasis");
        this.quasis.forEach(node -> node.setParent(this));
        this.types = AssertionUtils.notNull(types, "Types");
        this.types.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsTplLitType create(List<ISwc4jAstTsType> types) {
        return create(types, SimpleList.of());
    }

    public static Swc4jAstTsTplLitType create(List<ISwc4jAstTsType> types, List<Swc4jAstTplElement> quasis) {
        return new Swc4jAstTsTplLitType(types, quasis, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(types);
        childNodes.addAll(quasis);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Swc4jAstTplElement> getQuasis() {
        return quasis;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTplLitType;
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsType> getTypes() {
        return types;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!quasis.isEmpty() && newNode instanceof Swc4jAstTplElement) {
            final int size = quasis.size();
            for (int i = 0; i < size; i++) {
                if (quasis.get(i) == oldNode) {
                    quasis.set(i, (Swc4jAstTplElement) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (!types.isEmpty() && newNode instanceof ISwc4jAstTsType) {
            final int size = types.size();
            for (int i = 0; i < size; i++) {
                if (types.get(i) == oldNode) {
                    types.set(i, (ISwc4jAstTsType) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTplLitType(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
