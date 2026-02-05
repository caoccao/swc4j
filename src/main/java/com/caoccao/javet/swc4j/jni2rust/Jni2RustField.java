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

import java.lang.annotation.*;

/**
 * Annotation for marking fields to be converted to Rust JNI bindings.
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Jni2RustField {
    /**
     * Whether this field is an atom.
     *
     * @return true if atom
     */
    boolean atom() default false;

    /**
     * Whether this field should be boxed.
     *
     * @return true if boxed
     */
    boolean box() default false;

    /**
     * Whether the component is an atom.
     *
     * @return true if component is atom
     */
    boolean componentAtom() default false;

    /**
     * Whether the component should be boxed.
     *
     * @return true if component is boxed
     */
    boolean componentBox() default false;

    /**
     * Whether to ignore this field during code generation.
     *
     * @return true if ignored
     */
    boolean ignore() default false;

    /**
     * The name of this field in Rust.
     *
     * @return the Rust name
     */
    String name() default "";

    /**
     * Whether this field has syntax context.
     *
     * @return true if has syntax context
     */
    boolean syntaxContext() default false;
}
