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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstMethodProp
        extends Swc4jAst
        implements ISwc4jAstProp {
    protected final Swc4jAstFunction function;
    protected final ISwc4jAstPropName key;

    public Swc4jAstMethodProp(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstSpan span) {
        super(span);
        this.function = AssertionUtils.notNull(function, "Function");
        this.key = AssertionUtils.notNull(key, "Key");
        children = SimpleList.immutableOf(key, function);
        updateParent();
    }

    public Swc4jAstFunction getFunction() {
        return function;
    }

    public ISwc4jAstPropName getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.MethodProp;
    }
}
