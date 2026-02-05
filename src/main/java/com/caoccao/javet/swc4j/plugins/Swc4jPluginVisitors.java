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

package com.caoccao.javet.swc4j.plugins;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Collections;
import java.util.List;

/**
 * The type swc4j plugin visitors.
 */
public class Swc4jPluginVisitors implements ISwc4jPlugin {
    /**
     * The Visitors.
     */
    protected final List<ISwc4jAstVisitor> visitors;

    /**
     * Instantiates a new swc4j plugin visitors.
     */
    public Swc4jPluginVisitors() {
        this(SimpleList.of());
    }

    /**
     * Instantiates a new swc4j plugin visitors.
     *
     * @param visitors the visitors
     */
    public Swc4jPluginVisitors(List<ISwc4jAstVisitor> visitors) {
        this.visitors = AssertionUtils.notNull(visitors, "Visitors");
    }

    /**
     * Add swc4j plugin visitors.
     *
     * @param visitors the visitors
     * @return the swc4j plugin visitors
     */
    public Swc4jPluginVisitors add(ISwc4jAstVisitor... visitors) {
        Collections.addAll(this.visitors, visitors);
        return this;
    }

    /**
     * Gets visitors.
     *
     * @return the visitors
     */
    public List<ISwc4jAstVisitor> getVisitors() {
        return visitors;
    }

    @Override
    public Swc4jPluginResponse process(ISwc4jAstProgram<?> program) {
        for (ISwc4jAstVisitor visitor : visitors) {
            switch (program.visit(visitor)) {
                case Error:
                    return Swc4jPluginResponse.Error;
                case OkAndBreak:
                    return Swc4jPluginResponse.OkAndBreak;
                default:
                    break;
            }
        }
        return Swc4jPluginResponse.OkAndContinue;
    }

    /**
     * Remove swc4j plugin visitors.
     *
     * @param visitors the visitors
     * @return the swc4j plugin visitors
     */
    public Swc4jPluginVisitors remove(ISwc4jAstVisitor... visitors) {
        this.visitors.removeAll(SimpleList.of(visitors));
        return this;
    }
}
