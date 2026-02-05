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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeQueryExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast ts import type.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsImportType
        extends Swc4jAst
        implements ISwc4jAstTsType, ISwc4jAstTsTypeQueryExpr {
    /**
     * The Arg.
     */
    protected Swc4jAstStr arg;
    /**
     * The Attributes.
     */
    protected Optional<Swc4jAstTsImportCallOptions> attributes;
    /**
     * The Qualifier.
     */
    protected Optional<ISwc4jAstTsEntityName> qualifier;
    /**
     * The Type args.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    /**
     * Instantiates a new swc4j ast ts import type.
     *
     * @param arg        the arg
     * @param qualifier  the qualifier
     * @param typeArgs   the type args
     * @param attributes the attributes
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstTsImportType(
            Swc4jAstStr arg,
            @Jni2RustParam(optional = true) ISwc4jAstTsEntityName qualifier,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam(optional = true) Swc4jAstTsImportCallOptions attributes,
            Swc4jSpan span) {
        super(span);
        setArg(arg);
        setAttributes(attributes);
        setQualifier(qualifier);
        setTypeArgs(typeArgs);
    }

    /**
     * Create swc4j ast ts import type.
     *
     * @param arg the arg
     * @return the swc4j ast ts import type
     */
    public static Swc4jAstTsImportType create(Swc4jAstStr arg) {
        return create(arg, null, null);
    }

    /**
     * Create swc4j ast ts import type.
     *
     * @param arg       the arg
     * @param qualifier the qualifier
     * @return the swc4j ast ts import type
     */
    public static Swc4jAstTsImportType create(Swc4jAstStr arg, ISwc4jAstTsEntityName qualifier) {
        return create(arg, qualifier, null);
    }

    /**
     * Create swc4j ast ts import type.
     *
     * @param arg      the arg
     * @param typeArgs the type args
     * @return the swc4j ast ts import type
     */
    public static Swc4jAstTsImportType create(Swc4jAstStr arg, Swc4jAstTsTypeParamInstantiation typeArgs) {
        return create(arg, null, typeArgs);
    }

    /**
     * Create swc4j ast ts import type.
     *
     * @param arg       the arg
     * @param qualifier the qualifier
     * @param typeArgs  the type args
     * @return the swc4j ast ts import type
     */
    public static Swc4jAstTsImportType create(
            Swc4jAstStr arg,
            ISwc4jAstTsEntityName qualifier,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return create(arg, qualifier, typeArgs, null);
    }

    /**
     * Create swc4j ast ts import type.
     *
     * @param arg        the arg
     * @param qualifier  the qualifier
     * @param typeArgs   the type args
     * @param attributes the attributes
     * @return the swc4j ast ts import type
     */
    public static Swc4jAstTsImportType create(
            Swc4jAstStr arg,
            ISwc4jAstTsEntityName qualifier,
            Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jAstTsImportCallOptions attributes) {
        return new Swc4jAstTsImportType(arg, qualifier, typeArgs, attributes, Swc4jSpan.DUMMY);
    }

    /**
     * Gets arg.
     *
     * @return the arg
     */
    @Jni2RustMethod
    public Swc4jAstStr getArg() {
        return arg;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsImportCallOptions> getAttributes() {
        return attributes;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(arg);
        attributes.ifPresent(childNodes::add);
        qualifier.ifPresent(childNodes::add);
        typeArgs.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets qualifier.
     *
     * @return the qualifier
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstTsEntityName> getQualifier() {
        return qualifier;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsImportType;
    }

    /**
     * Gets type args.
     *
     * @return the type args
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (arg == oldNode && newNode instanceof Swc4jAstStr newArg) {
            setArg(newArg);
            return true;
        }
        if (attributes.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsImportCallOptions)) {
            setAttributes((Swc4jAstTsImportCallOptions) newNode);
            return true;
        }
        if (qualifier.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstTsEntityName)) {
            setQualifier((ISwc4jAstTsEntityName) newNode);
            return true;
        }
        if (typeArgs.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeArgs((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets arg.
     *
     * @param arg the arg
     * @return the arg
     */
    public Swc4jAstTsImportType setArg(Swc4jAstStr arg) {
        this.arg = AssertionUtils.notNull(arg, "Arg");
        this.arg.setParent(this);
        return this;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    public Swc4jAstTsImportType setAttributes(Swc4jAstTsImportCallOptions attributes) {
        this.attributes = Optional.ofNullable(attributes);
        this.attributes.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets qualifier.
     *
     * @param qualifier the qualifier
     * @return the qualifier
     */
    public Swc4jAstTsImportType setQualifier(ISwc4jAstTsEntityName qualifier) {
        this.qualifier = Optional.ofNullable(qualifier);
        this.qualifier.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets type args.
     *
     * @param typeArgs the type args
     * @return the type args
     */
    public Swc4jAstTsImportType setTypeArgs(Swc4jAstTsTypeParamInstantiation typeArgs) {
        this.typeArgs = Optional.ofNullable(typeArgs);
        this.typeArgs.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsImportType(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
