/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.utils.AnnotationUtils;

import java.lang.reflect.Executable;
import java.util.Optional;

public class Jni2RustMethodUtils {
    protected final Executable executable;
    protected final Optional<Jni2RustMethod> optionalJni2RustMethod;

    public Jni2RustMethodUtils(Executable executable) {
        this.executable = executable;
        optionalJni2RustMethod = Optional.ofNullable(AnnotationUtils.getAnnotation(executable, Jni2RustMethod.class));
    }

    public boolean isOptional() {
        return optionalJni2RustMethod.map(Jni2RustMethod::optional).orElse(false);
    }
}
