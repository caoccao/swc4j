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

package com.caoccao.javet.swc4j.jni2rust;

import com.caoccao.javet.swc4j.utils.AnnotationUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.Optional;

public class Jni2RustClassUtils<T> {
    protected final Class<T> clazz;
    protected final Optional<Jni2RustClass> optionalJni2RustClass;

    public Jni2RustClassUtils(Class<T> clazz) {
        this.clazz = clazz;
        optionalJni2RustClass = Optional.ofNullable(AnnotationUtils.getAnnotation(clazz, Jni2RustClass.class));
    }

    public Jni2RustFilePath getFilePath() {
        return optionalJni2RustClass
                .map(Jni2RustClass::filePath)
                .orElse(Jni2RustFilePath.None);
    }

    public Jni2RustEnumMapping[] getMappings() {
        return optionalJni2RustClass.map(Jni2RustClass::mappings).orElse(new Jni2RustEnumMapping[0]);
    }

    public String getName() {
        return getName(getRawName());
    }

    public String getName(String defaultName) {
        return optionalJni2RustClass
                .map(Jni2RustClass::name)
                .filter(StringUtils::isNotEmpty)
                .orElse(defaultName);
    }

    public String getRawName() {
        return clazz.getSimpleName().substring(clazz.isInterface() ? 9 : 8);
    }

    public boolean isCustomCreation() {
        return optionalJni2RustClass.map(Jni2RustClass::customCreation).orElse(false);
    }

    public boolean isIgnore() {
        return optionalJni2RustClass.map(Jni2RustClass::ignore).orElse(false);
    }

    public boolean isSpan() {
        return optionalJni2RustClass.map(Jni2RustClass::span).orElse(true);
    }
}
