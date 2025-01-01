/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.Optional;

/**
 * The type Javet sanitizer identifier matcher.
 *
 * @since 0.7.0
 */
public final class JavetSanitizerIdentifierMatcher implements IJavetSanitizerMatcher {

    private static final JavetSanitizerIdentifierMatcher INSTANCE = new JavetSanitizerIdentifierMatcher();

    private JavetSanitizerIdentifierMatcher() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 0.7.0
     */
    public static JavetSanitizerIdentifierMatcher getInstance() {
        return INSTANCE;
    }

    @Override
    public ISwc4jAst matches(JavetSanitizerOptions options, ISwc4jAst node) {
        return Optional.of(node)
                .filter(n -> {
                    if (n instanceof Swc4jAstIdent) {
                        return matches(options, n.as(Swc4jAstIdent.class).getSym());
                    }
                    if (n instanceof Swc4jAstIdentName) {
                        return matches(options, n.as(Swc4jAstIdentName.class).getSym());
                    }
                    return false;
                })
                .orElse(null);
    }

    boolean matches(JavetSanitizerOptions option, String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            return false;
        }
        if (option.getReservedIdentifierMatcher().apply(identifier)) {
            return !option.getReservedIdentifierSet().contains(identifier);
        }
        return option.getDisallowedIdentifierSet().contains(identifier);
    }
}
