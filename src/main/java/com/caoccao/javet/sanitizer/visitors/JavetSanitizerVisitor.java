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

package com.caoccao.javet.sanitizer.visitors;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.matchers.JavetSanitizerBuiltInObjectMatcher;
import com.caoccao.javet.sanitizer.matchers.JavetSanitizerIdentifierMatcher;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstYieldExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

public class JavetSanitizerVisitor extends Swc4jAstVisitor implements IJavetSanitizerVisitor {
    protected static final String ASYNC = "async";
    protected static final String AWAIT = "await";
    protected static final String DEBUGGER = "debugger";
    protected static final String EXPORT = "export";
    protected static final String IMPORT = "import";
    protected static final String VAR = "var";
    protected static final String WITH = "with";
    protected static final String YIELD = "yield";
    protected JavetSanitizerException exception;
    protected JavetSanitizerOptions options;

    public JavetSanitizerVisitor(JavetSanitizerOptions options) {
        this.options = AssertionUtils.notNull(options, "options");
    }

    public JavetSanitizerException getException() {
        return exception;
    }

    public JavetSanitizerOptions getOptions() {
        return options;
    }

    protected void raiseError(JavetSanitizerException exception, ISwc4jAst node) {
        this.exception = AssertionUtils.notNull(exception, "Exception").setNode(node);
        throw new RuntimeException("Sanity check error.", exception);
    }

    protected void validateBuiltInObject(JavetSanitizerOptions options, ISwc4jAst node) {
        ISwc4jAst matchedNode = JavetSanitizerBuiltInObjectMatcher.getInstance().matches(options, node);
        if (matchedNode != null) {
            raiseError(JavetSanitizerException.identifierNotAllowed(matchedNode.as(Swc4jAstIdent.class).getSym()), matchedNode);
        }
    }

    protected void validateIdentifier(ISwc4jAst node) {
        ISwc4jAst matchedNode = JavetSanitizerIdentifierMatcher.getInstance().matches(options, node);
        if (matchedNode != null) {
            raiseError(JavetSanitizerException.identifierNotAllowed(node.as(Swc4jAstIdent.class).getSym()), matchedNode);
        }
    }

    @Override
    public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
        if (!options.isKeywordAsyncEnabled() && node.isAsync()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(ASYNC), node);
        }
        return super.visitArrowExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
        validateBuiltInObject(options, node);
        return super.visitAssignExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitDebuggerStmt(Swc4jAstDebuggerStmt node) {
        if (!options.isKeywordDebuggerEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(DEBUGGER), node);
        }
        return super.visitDebuggerStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportAll(Swc4jAstExportAll node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportAll(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDecl(Swc4jAstExportDecl node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultDecl(Swc4jAstExportDefaultDecl node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportDefaultDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultExpr(Swc4jAstExportDefaultExpr node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportDefaultExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultSpecifier(Swc4jAstExportDefaultSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportDefaultSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamedSpecifier(Swc4jAstExportNamedSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportNamedSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamespaceSpecifier(Swc4jAstExportNamespaceSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitExportNamespaceSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitForOfStmt(Swc4jAstForOfStmt node) {
        if (!options.isKeywordAwaitEnabled() && node.isAwait()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(AWAIT), node);
        }
        return super.visitForOfStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitFunction(Swc4jAstFunction node) {
        if (!options.isKeywordAsyncEnabled() && node.isAsync()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(ASYNC), node);
        }
        return super.visitFunction(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitIdent(Swc4jAstIdent node) {
        validateIdentifier(node);
        return super.visitIdent(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImport(Swc4jAstImport node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(IMPORT), node);
        }
        return super.visitImport(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDecl(Swc4jAstImportDecl node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(IMPORT), node);
        }
        return super.visitImportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDefaultSpecifier(Swc4jAstImportDefaultSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(IMPORT), node);
        }
        return super.visitImportDefaultSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportNamedSpecifier(Swc4jAstImportNamedSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(IMPORT), node);
        }
        return super.visitImportNamedSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportStarAsSpecifier(Swc4jAstImportStarAsSpecifier node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(IMPORT), node);
        }
        return super.visitImportStarAsSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNamedExport(Swc4jAstNamedExport node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitNamedExport(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExportAssignment(Swc4jAstTsExportAssignment node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitTsExportAssignment(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNamespaceExportDecl(Swc4jAstTsNamespaceExportDecl node) {
        if (!options.isKeywordExportEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(EXPORT), node);
        }
        return super.visitTsNamespaceExportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUsingDecl(Swc4jAstUsingDecl node) {
        if (!options.isKeywordAwaitEnabled() && node.isAwait()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(AWAIT), node);
        }
        return super.visitUsingDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node) {
        if (!options.isKeywordVarEnabled() && node.getKind() == Swc4jAstVarDeclKind.Var) {
            raiseError(JavetSanitizerException.keywordNotAllowed(VAR), node);
        }
        return super.visitVarDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitWithStmt(Swc4jAstWithStmt node) {
        if (!options.isKeywordWithEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(WITH), node);
        }
        return super.visitWithStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitYieldExpr(Swc4jAstYieldExpr node) {
        if (!options.isKeywordYieldEnabled()) {
            raiseError(JavetSanitizerException.keywordNotAllowed(YIELD), node);
        }
        return super.visitYieldExpr(node);
    }
}
