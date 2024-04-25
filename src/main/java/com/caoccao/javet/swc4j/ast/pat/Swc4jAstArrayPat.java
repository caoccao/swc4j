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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstAssignTargetPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSimpleAssignTarget;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstArrayPat
        extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstAssignTargetPat, ISwc4jAstTsFnParam, ISwc4jAstSimpleAssignTarget {
    protected final List<Optional<ISwc4jAstPat>> elems;
    protected final boolean optional;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstArrayPat(
            List<ISwc4jAstPat> elems,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            Swc4jSpan span) {
        super(span);
        this.elems = SimpleList.immutable(AssertionUtils.notNull(elems, "Elems").stream()
                .map(Optional::ofNullable)
                .collect(Collectors.toList()));
        this.optional = optional;
        this.typeAnn = Optional.ofNullable(typeAnn);
        childNodes = SimpleList.copyOf(elems);
        childNodes.add(typeAnn);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public List<Optional<ISwc4jAstPat>> getElems() {
        return elems;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ArrayPat;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitArrayPat(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
