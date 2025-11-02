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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstImportSpecifier;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstImportStarAsSpecifier
        extends Swc4jAst
        implements ISwc4jAstImportSpecifier {
    protected Swc4jAstIdent local;

    @Jni2RustMethod
    public Swc4jAstImportStarAsSpecifier(
            Swc4jAstIdent local,
            Swc4jSpan span) {
        super(span);
        setLocal(local);
    }

    public static Swc4jAstImportStarAsSpecifier create(Swc4jAstIdent local) {
        return new Swc4jAstImportStarAsSpecifier(local, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(local);
    }

    @Jni2RustMethod
    public Swc4jAstIdent getLocal() {
        return local;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ImportStarAsSpecifier;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (local == oldNode && newNode instanceof Swc4jAstIdent newLocal) {
            setLocal(newLocal);
            return true;
        }
        return false;
    }

    public Swc4jAstImportStarAsSpecifier setLocal(Swc4jAstIdent local) {
        this.local = AssertionUtils.notNull(local, "Local");
        this.local.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitImportStarAsSpecifier(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
