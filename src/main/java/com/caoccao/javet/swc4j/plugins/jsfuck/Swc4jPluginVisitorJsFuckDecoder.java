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

package com.caoccao.javet.swc4j.plugins.jsfuck;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Swc4j plugin visitor jsfuck decoder.
 * It partially implements the features.
 *
 * @since 0.8.0
 */
public class Swc4jPluginVisitorJsFuckDecoder extends Swc4jAstVisitor {
    protected AtomicInteger counter;

    public Swc4jPluginVisitorJsFuckDecoder() {
        counter = new AtomicInteger();
    }

    public int getCount() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }

    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitBinExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitCallExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitMemberExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitUnaryExpr(node);
    }
}
