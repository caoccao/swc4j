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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast tagged tpl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTaggedTpl
        extends Swc4jAst
        implements ISwc4jAstExpr {
    /**
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Tag.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr tag;
    /**
     * The Tpl.
     */
    @Jni2RustField(box = true)
    protected Swc4jAstTpl tpl;
    /**
     * The Type params.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeParams;

    /**
     * Instantiates a new swc4j ast tagged tpl.
     *
     * @param ctxt       the ctxt
     * @param tag        the tag
     * @param typeParams the type params
     * @param tpl        the tpl
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstTaggedTpl(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            ISwc4jAstExpr tag,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl,
            Swc4jSpan span) {
        super(span);
        setCtxt(ctxt);
        setTag(tag);
        setTpl(tpl);
        setTypeParams(typeParams);
    }

    /**
     * Create swc4j ast tagged tpl.
     *
     * @param tag the tag
     * @param tpl the tpl
     * @return the swc4j ast tagged tpl
     */
    public static Swc4jAstTaggedTpl create(ISwc4jAstExpr tag, Swc4jAstTpl tpl) {
        return create(tag, null, tpl);
    }

    /**
     * Create swc4j ast tagged tpl.
     *
     * @param tag        the tag
     * @param typeParams the type params
     * @param tpl        the tpl
     * @return the swc4j ast tagged tpl
     */
    public static Swc4jAstTaggedTpl create(
            ISwc4jAstExpr tag,
            Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl) {
        return create(0, tag, typeParams, tpl);
    }

    /**
     * Create swc4j ast tagged tpl.
     *
     * @param ctxt       the ctxt
     * @param tag        the tag
     * @param typeParams the type params
     * @param tpl        the tpl
     * @return the swc4j ast tagged tpl
     */
    public static Swc4jAstTaggedTpl create(
            int ctxt,
            ISwc4jAstExpr tag,
            Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl) {
        return new Swc4jAstTaggedTpl(ctxt, tag, typeParams, tpl, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(tag, tpl);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets ctxt.
     *
     * @return the ctxt
     */
    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    /**
     * Gets tag.
     *
     * @return the tag
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getTag() {
        return tag;
    }

    /**
     * Gets tpl.
     *
     * @return the tpl
     */
    @Jni2RustMethod
    public Swc4jAstTpl getTpl() {
        return tpl;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TaggedTpl;
    }

    /**
     * Gets type params.
     *
     * @return the type params
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeParams() {
        return typeParams;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (tag == oldNode && newNode instanceof ISwc4jAstExpr newTag) {
            setTag(newTag);
            return true;
        }
        if (tpl == oldNode && newNode instanceof Swc4jAstTpl newTpl) {
            setTpl(newTpl);
            return true;
        }
        if (typeParams.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeParams((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstTaggedTpl setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets tag.
     *
     * @param tag the tag
     * @return the tag
     */
    public Swc4jAstTaggedTpl setTag(ISwc4jAstExpr tag) {
        this.tag = AssertionUtils.notNull(tag, "Tag");
        this.tag.setParent(this);
        return this;
    }

    /**
     * Sets tpl.
     *
     * @param tpl the tpl
     * @return the tpl
     */
    public Swc4jAstTaggedTpl setTpl(Swc4jAstTpl tpl) {
        this.tpl = AssertionUtils.notNull(tpl, "Tag");
        this.tpl.setParent(this);
        return this;
    }

    /**
     * Sets type params.
     *
     * @param typeParams the type params
     * @return the type params
     */
    public Swc4jAstTaggedTpl setTypeParams(Swc4jAstTsTypeParamInstantiation typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTaggedTpl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
