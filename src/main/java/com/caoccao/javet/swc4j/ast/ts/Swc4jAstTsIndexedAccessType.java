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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsIndexedAccessType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType indexType;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType objType;
    protected boolean readonly;

    @Jni2RustMethod
    public Swc4jAstTsIndexedAccessType(
            boolean readonly,
            ISwc4jAstTsType objType,
            ISwc4jAstTsType indexType,
            Swc4jSpan span) {
        super(span);
        setIndexType(indexType);
        setObjType(objType);
        setReadonly(readonly);
    }

    public static Swc4jAstTsIndexedAccessType create(ISwc4jAstTsType objType, ISwc4jAstTsType indexType) {
        return create(false, objType, indexType);
    }

    public static Swc4jAstTsIndexedAccessType create(
            boolean readonly,
            ISwc4jAstTsType objType,
            ISwc4jAstTsType indexType) {
        return new Swc4jAstTsIndexedAccessType(readonly, objType, indexType, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(objType, indexType);
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getIndexType() {
        return indexType;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getObjType() {
        return objType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsIndexedAccessType;
    }

    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (indexType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setIndexType((ISwc4jAstTsType) newNode);
            return true;
        }
        if (objType == oldNode && newNode instanceof ISwc4jAstTsType) {
            setObjType((ISwc4jAstTsType) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsIndexedAccessType setIndexType(ISwc4jAstTsType indexType) {
        this.indexType = AssertionUtils.notNull(indexType, "IndexType");
        this.indexType.setParent(this);
        return this;
    }

    public Swc4jAstTsIndexedAccessType setObjType(ISwc4jAstTsType objType) {
        this.objType = AssertionUtils.notNull(objType, "ObjType");
        this.objType.setParent(this);
        return this;
    }

    public Swc4jAstTsIndexedAccessType setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsIndexedAccessType(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
