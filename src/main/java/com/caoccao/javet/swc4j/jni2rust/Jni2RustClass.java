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
 * Annotation for marking classes to be converted to Rust JNI bindings.
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Jni2RustClass {
    /**
     * Whether to use custom fromJava conversion.
     *
     * @return true if custom conversion is used
     */
    boolean customFromJava() default false;

    /**
     * Whether to use custom toJava conversion.
     *
     * @return true if custom conversion is used
     */
    boolean customToJava() default false;

    /**
     * The Rust file path for this class.
     *
     * @return the file path
     */
    Jni2RustFilePath filePath() default Jni2RustFilePath.None;

    /**
     * Whether to ignore this class during code generation.
     *
     * @return true if ignored
     */
    boolean ignore() default false;

    /**
     * Enum variant mappings for this class.
     *
     * @return the enum mappings
     */
    Jni2RustEnumMapping[] mappings() default {};

    /**
     * The name of this class in Rust.
     *
     * @return the Rust name
     */
    String name() default "";

    /**
     * Whether to include span information.
     *
     * @return true if span is included
     */
    boolean span() default true;
}
