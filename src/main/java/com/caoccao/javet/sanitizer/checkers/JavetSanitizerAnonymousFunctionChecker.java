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

package com.caoccao.javet.sanitizer.checkers;


import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;

/**
 * The type Javet sanitizer anonymous function checker.
 *
 * @since 0.7.0
 */
public class JavetSanitizerAnonymousFunctionChecker extends BaseJavetSanitizerChecker {
    /**
     * Instantiates a new Javet sanitizer anonymous function checker.
     *
     * @since 0.7.0
     */
    public JavetSanitizerAnonymousFunctionChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Javet sanitizer anonymous function checker.
     *
     * @param options the options
     * @since 0.7.0
     */
    public JavetSanitizerAnonymousFunctionChecker(JavetSanitizerOptions options) {
        super(options);
    }

    @Override
    public void check(String codeString) throws JavetSanitizerException {
        super.check(codeString);
        // TODO
    }
}
