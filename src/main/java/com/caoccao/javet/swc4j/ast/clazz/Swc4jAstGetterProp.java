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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstGetterProp
        extends Swc4jAst
        implements ISwc4jAstProp {
    protected Optional<Swc4jAstBlockStmt> body;
    protected ISwc4jAstPropName key;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstGetterProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setKey(key);
        setTypeAnn(typeAnn);
    }

    @Jni2RustMethod
    public Optional<Swc4jAstBlockStmt> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(key);
        body.ifPresent(childNodes::add);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.GetterProp;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body.isPresent() && body.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstBlockStmt)) {
            setBody((Swc4jAstBlockStmt) newNode);
            return true;
        }
        if (key == oldNode && newNode instanceof ISwc4jAstPropName) {
            setKey((ISwc4jAstPropName) newNode);
            return true;
        }
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstGetterProp setBody(Swc4jAstBlockStmt body) {
        this.body = Optional.ofNullable(body);
        this.body.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstGetterProp setKey(ISwc4jAstPropName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstGetterProp setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitGetterProp(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
