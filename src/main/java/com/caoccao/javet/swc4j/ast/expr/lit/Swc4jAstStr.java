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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleExportName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.Optional;

public class Swc4jAstStr
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstModuleExportName, ISwc4jAstPropName, ISwc4jAstTsModuleName {
    protected final Optional<String> raw;
    protected final String value;

    public Swc4jAstStr(
            String value,
            String raw,
            Swc4jAstSpan span) {
        super(span);
        this.value = AssertionUtils.notNull(value, "Value");
        this.raw = Optional.ofNullable(raw);
    }

    public Optional<String> getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Str;
    }

    public String getValue() {
        return value;
    }
}
