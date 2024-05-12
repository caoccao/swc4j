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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.utils.SimpleSet;

import java.util.ArrayList;
import java.util.List;
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
     * The Export nodes.
     *
     * @since 0.7.0
     */
    protected final List<ISwc4jAstModuleDecl> exportNodes;
    /**
     * The Import nodes.
     *
     * @since 0.7.0
     */
    protected final List<ISwc4jAstModuleDecl> importNodes;

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
        exportNodes = new ArrayList<>();
        importNodes = new ArrayList<>();
    }

    /**
     * Check node.
     *
     * @param node the node
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    protected abstract void checkNode(ISwc4jAst node) throws JavetSanitizerException;

    /**
     * Gets export nodes.
     *
     * @return the export nodes
     * @since 0.7.0
     */
    public List<ISwc4jAstModuleDecl> getExportNodes() {
        return exportNodes;
    }

    /**
     * Gets import nodes.
     *
     * @return the import nodes
     * @since 0.7.0
     */
    public List<ISwc4jAstModuleDecl> getImportNodes() {
        return importNodes;
    }

    @Override
    protected void reset() {
        super.reset();
        exportNodes.clear();
        importNodes.clear();
    }

    /**
     * Validate export node.
     *
     * @param node the node
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    protected void validateExportNode(ISwc4jAstModuleDecl node) throws JavetSanitizerException {
        if (EXPORT_CLASSES.contains(node.getClass())) {
            if (options.isKeywordExportEnabled()) {
                exportNodes.add(node);
            } else {
                checkNode(node);
            }
        }
    }

    /**
     * Validate import node.
     *
     * @param node the node
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    protected void validateImportNode(ISwc4jAstModuleDecl node) throws JavetSanitizerException {
        if (IMPORT_CLASSES.contains(node.getClass())) {
            if (options.isKeywordImportEnabled()) {
                importNodes.add(node);
            } else {
                checkNode(node);
            }
        }
    }
}
