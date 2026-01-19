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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
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
public class Swc4jAstWithStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt body;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr obj;

    @Jni2RustMethod
    public Swc4jAstWithStmt(
            ISwc4jAstExpr obj,
            ISwc4jAstStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setObj(obj);
    }

    public static Swc4jAstWithStmt create(ISwc4jAstExpr obj) {
        return create(obj, ISwc4jAstStmt.createDefault());
    }

    public static Swc4jAstWithStmt create(ISwc4jAstExpr obj, ISwc4jAstStmt body) {
        return new Swc4jAstWithStmt(obj, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public ISwc4jAstStmt getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(obj, body);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getObj() {
        return obj;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.WithStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstStmt newBody) {
            setBody(newBody);
            return true;
        }
        if (obj == oldNode && newNode instanceof ISwc4jAstExpr newObj) {
            setObj(newObj);
            return true;
        }
        return false;
    }

    public Swc4jAstWithStmt setBody(ISwc4jAstStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    public Swc4jAstWithStmt setObj(ISwc4jAstExpr obj) {
        this.obj = AssertionUtils.notNull(obj, "Obj");
        this.obj.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitWithStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
