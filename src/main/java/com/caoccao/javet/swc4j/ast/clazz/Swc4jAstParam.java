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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstParam
        extends Swc4jAst
        implements ISwc4jAstParamOrTsParamProp {
    protected final List<Swc4jAstDecorator> decorators;
    protected ISwc4jAstPat pat;

    @Jni2RustMethod
    public Swc4jAstParam(
            List<Swc4jAstDecorator> decorators,
            ISwc4jAstPat pat,
            Swc4jSpan span) {
        super(span);
        setPat(pat);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.decorators.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstParam create(ISwc4jAstPat pat) {
        return create(SimpleList.of(), pat);
    }

    public static Swc4jAstParam create(List<Swc4jAstDecorator> decorators, ISwc4jAstPat pat) {
        return new Swc4jAstParam(decorators, pat, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(decorators);
        childNodes.add(pat);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    @Jni2RustMethod
    public ISwc4jAstPat getPat() {
        return pat;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Param;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (pat == oldNode && newNode instanceof ISwc4jAstPat) {
            setPat((ISwc4jAstPat) newNode);
            return true;
        }
        if (!decorators.isEmpty() && newNode instanceof Swc4jAstDecorator) {
            final int size = decorators.size();
            for (int i = 0; i < size; i++) {
                if (decorators.get(i) == oldNode) {
                    decorators.set(i, (Swc4jAstDecorator) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    public Swc4jAstParam setPat(ISwc4jAstPat pat) {
        this.pat = AssertionUtils.notNull(pat, "Pat");
        this.pat.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitParam(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
