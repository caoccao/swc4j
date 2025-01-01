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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxElement;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxExprContainer;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxFragment;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxSpreadChild;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstJsxText;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        name = "JSXElementChild",
        mappings = {
                @Jni2RustEnumMapping(name = "JSXElement", type = Swc4jAstJsxElement.class, box = true),
                @Jni2RustEnumMapping(name = "JSXExprContainer", type = Swc4jAstJsxExprContainer.class),
                @Jni2RustEnumMapping(name = "JSXFragment", type = Swc4jAstJsxFragment.class),
                @Jni2RustEnumMapping(name = "JSXSpreadChild", type = Swc4jAstJsxSpreadChild.class),
                @Jni2RustEnumMapping(name = "JSXText", type = Swc4jAstJsxText.class),
        }
)
public interface ISwc4jAstJsxElementChild extends ISwc4jAst {
}
