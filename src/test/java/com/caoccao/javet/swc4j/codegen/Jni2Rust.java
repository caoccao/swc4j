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

package com.caoccao.javet.swc4j.codegen;

import com.caoccao.javet.swc4j.utils.ClassUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jni2Rust {
    protected static final String PREFIX_NAME = "Java";
    protected final Class<?> clazz;

    public Jni2Rust(Class<?> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
    }

    protected List<Method> getMethods() {
        return Stream.of(clazz.getDeclaredMethods())
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
    }

    public String getCode() {
        String structName = PREFIX_NAME + clazz.getSimpleName();
        List<Method> methods = getMethods();
        List<String> lines = new ArrayList<>();
        // struct
        lines.add(String.format("struct %s {", structName));
        lines.add("  #[allow(dead_code)]");
        lines.add("  class: GlobalRef,");
        methods.forEach(method -> {
            String methodName = StringUtils.toSnakeCase(method.getName());
            if (Modifier.isStatic(method.getModifiers())) {
                lines.add(String.format("  method_%s: JStaticMethodID,", methodName));
            } else {
                lines.add(String.format("  method_%s: JMethodID,", methodName));
            }
        });
        lines.add("}");
        lines.add(String.format("unsafe impl Send for %s {}", structName));
        lines.add(String.format("unsafe impl Sync for %s {}", structName));
        // impl
        lines.add(StringUtils.EMPTY);
        lines.add(String.format("impl %s {", structName));
        // new
        lines.add("  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {");
        lines.add("    let class = env");
        lines.add(String.format("      .find_class(\"%s\")", clazz.getName().replace('.', '/')));
        lines.add(String.format("      .expect(\"Couldn't find class %s\");", clazz.getSimpleName()));
        lines.add("    let class = env");
        lines.add("      .new_global_ref(class)");
        lines.add(String.format("      .expect(\"Couldn't globalize class %s\");", clazz.getSimpleName()));
        methods.forEach(method -> {
            String snakeCasedName = StringUtils.toSnakeCase(method.getName());
            lines.add(String.format("    let method_%s = env", snakeCasedName));
            if (Modifier.isStatic(method.getModifiers())) {
                lines.add("      .get_static_method_id(");
            } else {
                lines.add("      .get_method_id(");
            }
            lines.add("        &class,");
            lines.add(String.format("        \"%s\",", method.getName()));
            StringBuilder sb = new StringBuilder("(");
            for (Class<?> parameterClass : method.getParameterTypes()) {
                sb.append(ClassUtils.toJniClassName(parameterClass.getName()));
            }
            sb.append(")");
            sb.append(ClassUtils.toJniClassName(method.getReturnType().getName()));
            lines.add(String.format("        \"%s\",", sb));
            lines.add("      )");
            lines.add(String.format("      .expect(\"Couldn't find method %s.%s\");", clazz.getSimpleName(), method.getName()));
        });
        lines.add(String.format("    %s {", structName));
        lines.add("      class,");
        methods.stream()
                .map(Method::getName)
                .map(StringUtils::toSnakeCase)
                .forEach(snakeCasedName -> lines.add(String.format("      method_%s,", snakeCasedName)));
        lines.add("    }");
        lines.add("  }");
        return StringUtils.join("\n", lines);
    }
}
