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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type swc4j ast export decl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstExportDecl
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    /**
     * The Decl.
     */
    protected ISwc4jAstDecl decl;

    /**
     * Instantiates a new swc4j ast export decl.
     *
     * @param decl the decl
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstExportDecl(
            ISwc4jAstDecl decl,
            Swc4jSpan span) {
        super(span);
        setDecl(decl);
    }

    /**
     * Create swc4j ast export decl.
     *
     * @param decl the decl
     * @return the swc4j ast export decl
     */
    public static Swc4jAstExportDecl create(ISwc4jAstDecl decl) {
        return new Swc4jAstExportDecl(decl, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(decl);
    }

    /**
     * Gets decl.
     *
     * @return the decl
     */
    @Jni2RustMethod
    public ISwc4jAstDecl getDecl() {
        return decl;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExportDecl;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (decl == oldNode && newNode instanceof ISwc4jAstDecl newDecl) {
            setDecl(newDecl);
            return true;
        }
        return false;
    }

    /**
     * Sets decl.
     *
     * @param decl the decl
     * @return the decl
     */
    public Swc4jAstExportDecl setDecl(ISwc4jAstDecl decl) {
        this.decl = AssertionUtils.notNull(decl, "Decl");
        this.decl.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitExportDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
