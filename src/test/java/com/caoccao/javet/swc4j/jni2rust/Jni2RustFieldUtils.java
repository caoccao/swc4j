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

import com.caoccao.javet.swc4j.utils.AnnotationUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Optional;

public class Jni2RustFieldUtils {
    protected final Field field;
    protected final Optional<Jni2RustField> optionalJni2RustField;

    public Jni2RustFieldUtils(Field field) {
        this.field = field;
        optionalJni2RustField = Optional.ofNullable(AnnotationUtils.getAnnotation(field, Jni2RustField.class));
    }

    public String getName() {
        return optionalJni2RustField
                .map(Jni2RustField::name)
                .filter(StringUtils::isNotEmpty)
                .orElse(field.getName());
    }

    public boolean isAtom() {
        return optionalJni2RustField.map(Jni2RustField::atom).orElse(false);
    }

    public boolean isBox() {
        return optionalJni2RustField.map(Jni2RustField::box).orElse(false);
    }

    public boolean isComponentAtom() {
        return optionalJni2RustField.map(Jni2RustField::componentAtom).orElse(false);
    }

    public boolean isComponentBox() {
        return optionalJni2RustField.map(Jni2RustField::componentBox).orElse(false);
    }

    public boolean isIgnore() {
        return optionalJni2RustField.map(Jni2RustField::ignore).orElse(false);
    }

    public boolean isSyntaxContext() {
        return optionalJni2RustField.map(Jni2RustField::syntaxContext).orElse(false);
    }
}
