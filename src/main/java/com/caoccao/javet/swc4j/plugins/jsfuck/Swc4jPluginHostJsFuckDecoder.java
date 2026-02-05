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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;

/**
 * The type swc4j plugin host js fuck decoder.
 */
public class Swc4jPluginHostJsFuckDecoder implements ISwc4jPluginHost {
    /**
     * The Max iteration.
     */
    protected int maxIteration;

    /**
     * Instantiates a new swc4j plugin host js fuck decoder.
     */
    public Swc4jPluginHostJsFuckDecoder() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Instantiates a new swc4j plugin host js fuck decoder.
     *
     * @param maxIteration the max iteration
     */
    public Swc4jPluginHostJsFuckDecoder(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    /**
     * Gets max iteration.
     *
     * @return the max iteration
     */
    public int getMaxIteration() {
        return maxIteration;
    }

    @Override
    public boolean process(ISwc4jAstProgram<?> program) {
        Swc4jPluginVisitorJsFuckDecoder jsFuckDecoder = new Swc4jPluginVisitorJsFuckDecoder();
        for (int i = 0; i < maxIteration; i++) {
            jsFuckDecoder.reset();
            program.visit(jsFuckDecoder);
            if (jsFuckDecoder.getCount() == 0) {
                break;
            }
        }
        return true;
    }

    /**
     * Sets max iteration.
     *
     * @param maxIteration the max iteration
     */
    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }
}
