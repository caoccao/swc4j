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
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstIdentName
        extends Swc4jAst
        implements ISwc4jAstSuperProp, ISwc4jAstPropName, ISwc4jAstMemberProp, ISwc4jAstJsxAttrName {
    @Jni2RustField(atom = true)
    protected String sym;

    @Jni2RustMethod
    public Swc4jAstIdentName(
            String sym,
            Swc4jSpan span) {
        super(span);
        setSym(sym);
    }

    public static Swc4jAstIdentName create(String sym) {
        return new Swc4jAstIdentName(sym, Swc4jSpan.DUMMY);
    }

    public static Swc4jAstIdentName createApply() {
        return create(ISwc4jConstants.APPLY);
    }

    public static Swc4jAstIdentName createArray() {
        return create(ISwc4jConstants.ARRAY);
    }

    public static Swc4jAstIdentName createConcat() {
        return create(ISwc4jConstants.CONCAT);
    }

    public static Swc4jAstIdentName createDummy() {
        return create(ISwc4jConstants.DUMMY);
    }

    public static Swc4jAstIdentName createFunction() {
        return create(ISwc4jConstants.FUNCTION);
    }

    public static Swc4jAstIdentName createUndefined() {
        return create(ISwc4jConstants.UNDEFINED);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    @Jni2RustMethod
    public String getSym() {
        return sym;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.IdentName;
    }

    @Override
    public boolean isInfinity() {
        return ISwc4jConstants.INFINITY.equals(sym);
    }

    @Override
    public boolean isNaN() {
        return ISwc4jConstants.NAN.equals(sym);
    }

    @Override
    public boolean isUndefined() {
        return ISwc4jConstants.UNDEFINED.equals(sym);
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    public Swc4jAstIdentName setSym(String sym) {
        this.sym = AssertionUtils.notNull(sym, "Sym");
        return this;
    }

    @Override
    public String toString() {
        return sym;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitIdentName(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
