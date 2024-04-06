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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsGetterSignature
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected final boolean computed;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr key;
    protected final boolean optional;
    protected final boolean readonly;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;

    public Swc4jAstTsGetterSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.computed = computed;
        this.key = key;
        this.optional = optional;
        this.readonly = readonly;
        this.typeAnn = Optional.ofNullable(typeAnn);
        childNodes = SimpleList.immutableOf(typeAnn);
        updateParent();
    }

    public ISwc4jAstExpr getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsGetterSignature;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public boolean isComputed() {
        return computed;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
