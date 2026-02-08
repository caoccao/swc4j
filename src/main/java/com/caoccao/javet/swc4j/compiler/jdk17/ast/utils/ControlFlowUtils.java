/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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


package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.*;

/**
 * Utility class for control flow analysis helpers.
 */
public final class ControlFlowUtils {
    private ControlFlowUtils() {
    }

    /**
     * Check if a statement can fall through (complete normally).
     * A statement cannot fall through if it always ends with break, return, or throw.
     *
     * @param stmt the statement to check
     * @return true if the statement can fall through
     */
    public static boolean canFallThrough(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstBreakStmt) {
            return false;
        }
        if (stmt instanceof Swc4jAstReturnStmt) {
            return false;
        }
        if (stmt instanceof Swc4jAstContinueStmt) {
            return false;
        }
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            var stmts = blockStmt.getStmts();
            if (stmts.isEmpty()) {
                return true;
            }
            for (ISwc4jAstStmt s : stmts) {
                if (!canFallThrough(s)) {
                    return false;
                }
            }
            return true;
        }
        if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            if (ifStmt.getAlt().isEmpty()) {
                return true;
            }
            boolean thenFallsThrough = canFallThrough(ifStmt.getCons());
            boolean elseFallsThrough = canFallThrough(ifStmt.getAlt().get());
            return thenFallsThrough || elseFallsThrough;
        }
        return true;
    }
}
