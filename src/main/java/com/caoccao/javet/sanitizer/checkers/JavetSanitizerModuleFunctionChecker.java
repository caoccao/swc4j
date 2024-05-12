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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Javet sanitizer module function checker.
 *
 * @since 0.7.0
 */
public class JavetSanitizerModuleFunctionChecker extends BaseJavetSanitizerModuleChecker {
    /**
     * The Function map.
     *
     * @since 0.7.0
     */
    protected final Map<String, Swc4jAstFnDecl> functionMap;

    /**
     * Instantiates a new Javet sanitizer module function checker.
     *
     * @since 0.7.0
     */
    public JavetSanitizerModuleFunctionChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Javet sanitizer module function checker.
     *
     * @param options the options
     * @since 0.7.0
     */
    public JavetSanitizerModuleFunctionChecker(JavetSanitizerOptions options) {
        super(options);
        functionMap = new LinkedHashMap<>();
    }

    @Override
    public void check(String codeString) throws JavetSanitizerException {
        super.check(codeString);
        validateNoShebang(Swc4jAstType.getName(Swc4jAstFnDecl.class));
        validateBodyNotEmpty();
        for (ISwc4jAst node : program.getBody()) {
            if (node instanceof ISwc4jAstModuleDecl) {
                validateExportNode(node.as(ISwc4jAstModuleDecl.class));
                validateImportNode(node.as(ISwc4jAstModuleDecl.class));
            } else {
                checkNode(node);
                Swc4jAstFnDecl fnDecl = node.as(Swc4jAstFnDecl.class);
                functionMap.put(fnDecl.getIdent().getSym(), fnDecl);
            }
        }
        validateReservedFunctions();
    }

    protected void checkNode(ISwc4jAst node) throws JavetSanitizerException {
        validateNode(node, Swc4jAstFnDecl.class);
    }

    /**
     * Gets function map.
     *
     * @return the function map
     * @since 0.7.0
     */
    public Map<String, Swc4jAstFnDecl> getFunctionMap() {
        return functionMap;
    }

    @Override
    public String getName() {
        return "Module Function";
    }

    @Override
    protected void reset() {
        super.reset();
        functionMap.clear();
    }

    /**
     * Validate reserved functions.
     *
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    protected void validateReservedFunctions() throws JavetSanitizerException {
        for (String functionIdentifier : options.getReservedFunctionIdentifierSet()) {
            if (!functionMap.containsKey(functionIdentifier)) {
                throw JavetSanitizerException.functionNotFound(functionIdentifier);
            }
        }
    }
}
