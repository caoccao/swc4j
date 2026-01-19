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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstIdent
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstProp, ISwc4jAstTsModuleRef, ISwc4jAstModuleExportName, ISwc4jAstTsEntityName,
        ISwc4jAstTsModuleName, ISwc4jAstJsxObject, ISwc4jAstJsxElementName, ISwc4jAstTsThisTypeOrIdent,
        ISwc4jAstTsEnumMemberId {
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    protected boolean optional;
    @Jni2RustField(atom = true)
    protected String sym;

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

    public static Swc4jAstIdent create(String sym) {
        return create(sym, false);
    }

    public static Swc4jAstIdent create(String sym, boolean optional) {
        return create(0, sym, optional);
    }

    public static Swc4jAstIdent create(int ctxt, String sym, boolean optional) {
        return new Swc4jAstIdent(ctxt, sym, optional, Swc4jSpan.DUMMY);
    }

    public static Swc4jAstIdent createApply() {
        return create(ISwc4jConstants.APPLY);
    }

    public static Swc4jAstIdent createArray() {
        return create(ISwc4jConstants.ARRAY);
    }

    public static Swc4jAstIdent createConcat() {
        return create(ISwc4jConstants.CONCAT);
    }

    public static Swc4jAstIdent createDummy() {
        return create(ISwc4jConstants.DUMMY);
    }

    public static Swc4jAstIdent createFunction() {
        return create(ISwc4jConstants.FUNCTION);
    }

    public static Swc4jAstIdent createUndefined() {
        return create(ISwc4jConstants.UNDEFINED);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

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

    public Swc4jAstIdent setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    public Swc4jAstIdent setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

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
