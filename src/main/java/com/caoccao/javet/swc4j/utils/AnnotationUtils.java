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

package com.caoccao.javet.swc4j.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class AnnotationUtils {
    private static final Map<AnnotatedElement, Annotation> ANNOTATION_MAP = new HashMap<>();

    private AnnotationUtils() {
    }

    public static <A extends Annotation> A getAnnotation(
            AnnotatedElement annotatedElement,
            Class<A> annotationClass) {
        if (ANNOTATION_MAP.containsKey(annotatedElement)) {
            return (A) ANNOTATION_MAP.get(annotatedElement);
        }
        A annotation = annotatedElement.getAnnotation(annotationClass);
        if (annotation != null) {
            ANNOTATION_MAP.put(annotatedElement, annotation);
            return annotation;
        }
        if (annotatedElement.getClass() != Class.class) {
            for (Annotation declaredAnnotation : annotatedElement.getAnnotations()) {
                annotation = getAnnotation(declaredAnnotation.annotationType(), annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return null;
    }

    public static <A extends Annotation> boolean isAnnotationPresent(
            AnnotatedElement annotatedElement,
            Class<A> annotationClass) {
        return getAnnotation(annotatedElement, annotationClass) != null;
    }
}
