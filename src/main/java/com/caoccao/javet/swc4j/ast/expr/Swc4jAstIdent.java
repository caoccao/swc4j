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
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type swc4j ast ident.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstIdent
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstProp, ISwc4jAstTsModuleRef, ISwc4jAstModuleExportName, ISwc4jAstTsEntityName,
        ISwc4jAstTsModuleName, ISwc4jAstJsxObject, ISwc4jAstJsxElementName, ISwc4jAstTsThisTypeOrIdent,
        ISwc4jAstTsEnumMemberId {
    /**
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Optional.
     */
    protected boolean optional;
    /**
     * The Sym.
     */
    @Jni2RustField(atom = true)
    protected String sym;

    /**
     * Instantiates a new swc4j ast ident.
     *
     * @param ctxt     the ctxt
     * @param sym      the sym
     * @param optional the optional
     * @param span     the span
     */
    @Jni2RustMethod
    public Swc4jAstIdent(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            String sym,
            boolean optional,
            Swc4jSpan span) {
        super(span);
        setCtxt(ctxt);
        setOptional(optional);
        setSym(sym);
    }

    /**
     * Create swc4j ast ident.
     *
     * @param sym the sym
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent create(String sym) {
        return create(sym, false);
    }

    /**
     * Create swc4j ast ident.
     *
     * @param sym      the sym
     * @param optional the optional
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent create(String sym, boolean optional) {
        return create(0, sym, optional);
    }

    /**
     * Create swc4j ast ident.
     *
     * @param ctxt     the ctxt
     * @param sym      the sym
     * @param optional the optional
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent create(int ctxt, String sym, boolean optional) {
        return new Swc4jAstIdent(ctxt, sym, optional, Swc4jSpan.DUMMY);
    }

    /**
     * Create apply swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createApply() {
        return create(ISwc4jConstants.APPLY);
    }

    /**
     * Create array swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createArray() {
        return create(ISwc4jConstants.ARRAY);
    }

    /**
     * Create concat swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createConcat() {
        return create(ISwc4jConstants.CONCAT);
    }

    /**
     * Create dummy swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createDummy() {
        return create(ISwc4jConstants.DUMMY);
    }

    /**
     * Create function swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createFunction() {
        return create(ISwc4jConstants.FUNCTION);
    }

    /**
     * Create undefined swc4j ast ident.
     *
     * @return the swc4j ast ident
     */
    public static Swc4jAstIdent createUndefined() {
        return create(ISwc4jConstants.UNDEFINED);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
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
     * Gets sym.
     *
     * @return the sym
     */
    @Jni2RustMethod
    public String getSym() {
        return sym;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Ident;
    }

    @Override
    public boolean isInfinity() {
        return !optional && ISwc4jConstants.INFINITY.equals(sym);
    }

    @Override
    public boolean isNaN() {
        return !optional && ISwc4jConstants.NAN.equals(sym);
    }

    /**
     * Is optional boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean isUndefined() {
        return !optional && ISwc4jConstants.UNDEFINED.equals(sym);
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstIdent setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets optional.
     *
     * @param optional the optional
     * @return the optional
     */
    public Swc4jAstIdent setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * Sets sym.
     *
     * @param sym the sym
     * @return the sym
     */
    public Swc4jAstIdent setSym(String sym) {
        this.sym = AssertionUtils.notNull(sym, "Sym");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(sym);
        if (optional) {
            sb.append(ISwc4jConstants.QUESTION_MARK);
        }
        return sb.toString();
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitIdent(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
