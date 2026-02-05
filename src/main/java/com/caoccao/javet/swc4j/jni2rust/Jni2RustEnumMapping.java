/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.jni2rust;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;

import java.lang.annotation.*;

/**
 * Annotation for mapping enum variants to Java types in JNI code generation.
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Jni2RustEnumMapping {
    /**
     * Whether the enum variant should be boxed.
     *
     * @return true if boxed
     */
    boolean box() default false;

    /**
     * The name of the enum variant in Rust.
     *
     * @return the variant name
     */
    String name();

    /**
     * The Java type this variant maps to.
     *
     * @return the Java type
     */
    Class<? extends ISwc4jAst> type();
}
