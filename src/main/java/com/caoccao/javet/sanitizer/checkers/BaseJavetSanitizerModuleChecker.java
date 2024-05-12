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


import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.utils.SimpleSet;

import java.util.Set;

/**
 * The type Base javet sanitizer module checker.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetSanitizerModuleChecker extends BaseJavetSanitizerChecker {
    /**
     * The constant EXPORT_CLASSES.
     *
     * @since 0.7.0
     */
    protected static final Set<Class<? extends ISwc4jAst>> EXPORT_CLASSES = SimpleSet.immutableOf(
            Swc4jAstExportAll.class,
            Swc4jAstExportDecl.class,
            Swc4jAstExportDefaultDecl.class,
            Swc4jAstExportDefaultExpr.class,
            Swc4jAstNamedExport.class,
            Swc4jAstTsExportAssignment.class,
            Swc4jAstTsNamespaceExportDecl.class);
    /**
     * The constant IMPORT_CLASSES.
     *
     * @since 0.7.0
     */
    protected static final Set<Class<? extends ISwc4jAst>> IMPORT_CLASSES = SimpleSet.immutableOf(
            Swc4jAstImportDecl.class,
            Swc4jAstTsImportEqualsDecl.class);

    /**
     * Instantiates a new Base javet sanitizer module checker.
     *
     * @since 0.7.0
     */
    public BaseJavetSanitizerModuleChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Base javet sanitizer module checker.
     *
     * @param options the options
     * @since 0.7.0
     */
    public BaseJavetSanitizerModuleChecker(JavetSanitizerOptions options) {
        super(options);
    }
}
