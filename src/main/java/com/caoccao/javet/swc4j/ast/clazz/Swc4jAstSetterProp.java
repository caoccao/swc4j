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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstSetterProp
        extends Swc4jAst
        implements ISwc4jAstProp {
    protected Optional<Swc4jAstBlockStmt> body;
    protected ISwc4jAstPropName key;
    @Jni2RustField(box = true)
    protected ISwc4jAstPat param;
    protected Optional<ISwc4jAstPat> thisParam;

    @Jni2RustMethod
    public Swc4jAstSetterProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) ISwc4jAstPat thisParam,
            ISwc4jAstPat param,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setKey(key);
        setParam(param);
        setThisParam(thisParam);
    }

    @Jni2RustMethod
    public Optional<Swc4jAstBlockStmt> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(key, param);
        body.ifPresent(childNodes::add);
        thisParam.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    @Jni2RustMethod
    public ISwc4jAstPat getParam() {
        return param;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstPat> getThisParam() {
        return thisParam;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SetterProp;
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
        if (param == oldNode && newNode instanceof ISwc4jAstPat) {
            setParam((ISwc4jAstPat) newNode);
            return true;
        }
        if (thisParam.isPresent() && thisParam.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstPat)) {
            setThisParam((ISwc4jAstPat) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstSetterProp setBody(Swc4jAstBlockStmt body) {
        this.body = Optional.ofNullable(body);
        this.body.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstSetterProp setKey(ISwc4jAstPropName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstSetterProp setParam(ISwc4jAstPat param) {
        this.param = AssertionUtils.notNull(param, "Param");
        this.param.setParent(this);
        return this;
    }

    public Swc4jAstSetterProp setThisParam(ISwc4jAstPat thisParam) {
        this.thisParam = Optional.ofNullable(thisParam);
        this.thisParam.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitSetterProp(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
