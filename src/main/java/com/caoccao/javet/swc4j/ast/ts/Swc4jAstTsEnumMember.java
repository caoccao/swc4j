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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEnumMemberId;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast ts enum member.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsEnumMember
        extends Swc4jAst {
    /**
     * The Id.
     */
    protected ISwc4jAstTsEnumMemberId id;
    /**
     * The Init.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> init;

    /**
     * Instantiates a new swc4j ast ts enum member.
     *
     * @param id   the id
     * @param init the init
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstTsEnumMember(
            ISwc4jAstTsEnumMemberId id,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            Swc4jSpan span) {
        super(span);
        setId(id);
        setInit(init);
    }

    /**
     * Create swc4j ast ts enum member.
     *
     * @param id the id
     * @return the swc4j ast ts enum member
     */
    public static Swc4jAstTsEnumMember create(ISwc4jAstTsEnumMemberId id) {
        return create(id, null);
    }

    /**
     * Create swc4j ast ts enum member.
     *
     * @param id   the id
     * @param init the init
     * @return the swc4j ast ts enum member
     */
    public static Swc4jAstTsEnumMember create(ISwc4jAstTsEnumMemberId id, ISwc4jAstExpr init) {
        return new Swc4jAstTsEnumMember(id, init, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(id);
        init.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Jni2RustMethod
    public ISwc4jAstTsEnumMemberId getId() {
        return id;
    }

    /**
     * Gets init.
     *
     * @return the init
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getInit() {
        return init;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsEnumMember;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (id == oldNode && newNode instanceof ISwc4jAstTsEnumMemberId newId) {
            setId(newId);
            return true;
        }
        if (init.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setInit((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public Swc4jAstTsEnumMember setId(ISwc4jAstTsEnumMemberId id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    /**
     * Sets init.
     *
     * @param init the init
     * @return the init
     */
    public Swc4jAstTsEnumMember setInit(ISwc4jAstExpr init) {
        this.init = Optional.ofNullable(init);
        this.init.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsEnumMember(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
