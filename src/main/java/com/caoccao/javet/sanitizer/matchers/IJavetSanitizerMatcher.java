/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.matchers;

import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;

/**
 * The interface Javet sanitizer matcher.
 *
 * @since 0.7.0
 */
public interface IJavetSanitizerMatcher {
    /**
     * Matches ast node.
     *
     * @param options the options
     * @param node    the node
     * @return the matched node
     * @since 0.7.0
     */
    ISwc4jAst matches(JavetSanitizerOptions options, ISwc4jAst node);
}
