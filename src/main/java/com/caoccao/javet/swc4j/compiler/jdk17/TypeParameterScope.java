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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParam;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a scope of type parameters for generic classes and methods.
 * Used for type erasure during bytecode compilation.
 * <p>
 * For example, in {@code class Box<T extends Comparable>}, this scope tracks
 * that {@code T} should be erased to {@code Comparable}.
 * <p>
 * For {@code class Box<T>} without constraints, {@code T} erases to {@code Object}.
 */
public final class TypeParameterScope {
    /**
     * Maps type parameter names to their constraint types (or null for Object).
     * Example: for {@code <T extends Number>}, maps "T" -> TsTypeRef("Number")
     */
    private final Map<String, ISwc4jAstTsType> typeParameterConstraints;

    public TypeParameterScope() {
        this.typeParameterConstraints = new HashMap<>();
    }

    /**
     * Create a type parameter scope from a type parameter declaration.
     *
     * @param typeParamDecl the type parameter declaration (e.g., {@code <T, U extends Number>})
     * @return the type parameter scope
     */
    public static TypeParameterScope fromDecl(Swc4jAstTsTypeParamDecl typeParamDecl) {
        TypeParameterScope scope = new TypeParameterScope();
        if (typeParamDecl != null) {
            for (Swc4jAstTsTypeParam param : typeParamDecl.getParams()) {
                String name = param.getName().getSym();
                // Constraint is the upper bound, or null for Object
                ISwc4jAstTsType constraint = param.getConstraint().orElse(null);
                scope.addTypeParameter(name, constraint);
            }
        }
        return scope;
    }

    /**
     * Add a type parameter to this scope.
     *
     * @param name       the type parameter name (e.g., "T")
     * @param constraint the constraint type, or null if no constraint (erases to Object)
     */
    public void addTypeParameter(String name, ISwc4jAstTsType constraint) {
        typeParameterConstraints.put(name, constraint);
    }

    /**
     * Get the constraint type for a type parameter.
     *
     * @param name the type parameter name
     * @return Optional containing the constraint type, or empty if not a type parameter in this scope
     */
    public Optional<ISwc4jAstTsType> getConstraint(String name) {
        if (typeParameterConstraints.containsKey(name)) {
            return Optional.ofNullable(typeParameterConstraints.get(name));
        }
        return Optional.empty();
    }

    /**
     * Check if this scope is empty (no type parameters).
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return typeParameterConstraints.isEmpty();
    }

    /**
     * Check if a name is a type parameter in this scope.
     *
     * @param name the name to check
     * @return true if it's a type parameter in this scope
     */
    public boolean isTypeParameter(String name) {
        return typeParameterConstraints.containsKey(name);
    }
}
