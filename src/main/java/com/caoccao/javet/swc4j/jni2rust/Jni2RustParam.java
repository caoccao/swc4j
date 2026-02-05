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
 * The interface Jni 2 rust param.
 */
@Documented
@Inherited
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Jni2RustParam {
    /**
     * Name string.
     *
     * @return the string
     */
    String name() default "";

    /**
     * Optional boolean.
     *
     * @return the boolean
     */
    boolean optional() default false;

    /**
     * Post calls string [ ].
     *
     * @return the string [ ]
     */
    String[] postCalls() default {};

    /**
     * Pre calls string [ ].
     *
     * @return the string [ ]
     */
    String[] preCalls() default {};

    /**
     * Rust type string.
     *
     * @return the string
     */
    String rustType() default "";

    /**
     * Syntax context boolean.
     *
     * @return the boolean
     */
    boolean syntaxContext() default false;
}
