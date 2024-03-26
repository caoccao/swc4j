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

import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class Jni2Rust<T> {
    protected static final String PREFIX_NAME = "Java";
    protected final Class<T> clazz;
    protected final List<Method> methods;
    protected final Jni2RustOptions options;
    protected boolean completelyAuto;
    protected Constructor<T> constructor;
    protected String filePath;
    protected String structName;

    public Jni2Rust(Class<T> clazz) {
        this(clazz, new Jni2RustOptions());
    }

    public Jni2Rust(Class<T> clazz, Jni2RustOptions options) {
        this.clazz = Objects.requireNonNull(clazz);
        methods = new ArrayList<>();
        this.options = Objects.requireNonNull(options);
        init();
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getCode() {
        List<String> lines = new ArrayList<>();
        getCodeStruct(lines);
        getCodeImpl(lines);
        return StringUtils.join("\n", lines);
    }

    protected void getCodeImpl(List<String> lines) {
        lines.add(StringUtils.EMPTY);
        lines.add(String.format("impl %s {", structName));
        getCodeNew(lines);
        getCodeMethods(lines);
        if (completelyAuto) {
            lines.add("}");
        }
    }

    protected void getCodeMethods(List<String> lines) {
        methods.stream()
                .filter(method -> !method.isAnnotationPresent(Jni2RustMethod.class)
                        || (method.getAnnotation(Jni2RustMethod.class).mode() == Jni2RustMethodMode.Auto))
                .forEach(method -> {
                    boolean isPrimitive = method.getReturnType().isPrimitive();
                    boolean isVoid = method.getReturnType() == Void.class;
                    String methodName = getMethodName(method);
                    List<String> parameterNames = Stream.of(method.getParameters())
                            .map(this::getParameterName)
                            .collect(Collectors.toList());
                    lines.add(StringUtils.EMPTY);
                    if (isVoid || isPrimitive) {
                        lines.add(String.format("  pub fn %s<'local>(", methodName));
                    } else {
                        lines.add(String.format("  pub fn %s<'local, 'a>(", methodName));
                    }
                    lines.add("    &self,");
                    lines.add("    env: &mut JNIEnv<'local>,");
                    Set<String> rustTypeSet = new HashSet<>();
                    for (Parameter parameter : method.getParameters()) {
                        Class<?> parameterType = parameter.getType();
                        boolean isOptional = false;
                        boolean isCustomRustType = false;
                        String rustType = null;
                        if (parameter.isAnnotationPresent(Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = parameter.getAnnotation(Jni2RustParam.class);
                            isOptional = jni2RustParam.optional();
                            if (StringUtils.isNotEmpty(jni2RustParam.rustType())) {
                                isCustomRustType = true;
                                rustType = jni2RustParam.rustType();
                            }
                        }
                        if (isCustomRustType) {
                            if (rustTypeSet.contains(rustType)) {
                                rustType = null;
                            } else {
                                rustTypeSet.add(rustType);
                            }
                        } else {
                            String name = getParameterName(parameter);
                            StringBuilder sb = new StringBuilder(name).append(": &");
                            if (isOptional) {
                                if (parameterType.isPrimitive()) {
                                    // TODO
                                } else if (parameterType == String.class) {
                                    sb.append("Option<String>");
                                } else {
                                    sb.append("Option<JObject>");
                                }
                            } else {
                                if (parameterType.isPrimitive()) {
                                    // TODO
                                } else if (parameterType == String.class) {
                                    sb.append("str");
                                } else {
                                    sb.append("JObject<'_>");
                                }
                            }
                            rustType = sb.toString();
                        }
                        if (rustType != null) {
                            lines.add(String.format("    %s,", rustType));
                        }
                    }
                    if (isVoid) {
                        lines.add("  )");
                    } else if (isPrimitive) {
                        // TODO
                    } else {
                        lines.add("  ) -> JObject<'a>");
                        lines.add("  where");
                        lines.add("    'local: 'a,");
                    }
                    lines.add("  {");
                    // pre-call
                    for (Parameter parameter : method.getParameters()) {
                        String name = getParameterName(parameter);
                        Class<?> parameterType = parameter.getType();
                        boolean isOptional = false;
                        String preCall = null;
                        if (parameter.isAnnotationPresent(Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = parameter.getAnnotation(Jni2RustParam.class);
                            isOptional = jni2RustParam.optional();
                            if (StringUtils.isNotEmpty(jni2RustParam.preCall())) {
                                preCall = jni2RustParam.preCall();
                            }
                        }
                        if (preCall == null) {
                            if (parameterType.isPrimitive()) {
                                // TODO
                            } else if (parameterType == String.class) {
                                if (isOptional) {
                                    lines.add(String.format("    let java_%s = match &%s {", name, name));
                                    lines.add(String.format("      Some(%s) => converter::string_to_jstring(env, &%s),", name, name));
                                    lines.add("      None => Default::default(),");
                                    lines.add("    };");
                                    lines.add(String.format("    let %s = jvalue {", name));
                                    lines.add(String.format("      l: java_%s.as_raw(),", name));
                                    lines.add("    };");
                                } else {
                                    // TODO
                                }
                            } else {
                                lines.add(String.format("    let %s = jvalue { l: %s.as_raw() };", name, name));
                            }
                        } else {
                            lines.add(String.format("    let %s = %s;", name, preCall));
                        }
                    }
                    // call
                    lines.add("    let return_value = unsafe {");
                    lines.add("      env");
                    if (Modifier.isStatic(method.getModifiers())) {
                        lines.add("        .call_static_method_unchecked(");
                        lines.add("          &self.class,");
                    } else {
                        lines.add("        .call_method_unchecked(");
                        // TODO
                    }
                    lines.add(String.format("          self.method_%s,", methodName));
                    lines.add("          ReturnType::Object,");
                    lines.add(String.format("          &[%s],", StringUtils.join(", ", parameterNames)));
                    lines.add("        )");
                    if (isVoid) {
                        // TODO
                    } else if (isPrimitive) {
                        // TODO
                    } else {
                        lines.add(String.format("        .expect(\"Couldn't create %s\")", method.getReturnType().getSimpleName()));
                        lines.add("        .l()");
                        lines.add(String.format("        .expect(\"Couldn't convert %s\")", method.getReturnType().getSimpleName()));
                    }
                    lines.add("    };");
                    // post-call
                    for (Parameter parameter : method.getParameters()) {
                        String name = getParameterName(parameter);
                        Class<?> parameterType = parameter.getType();
                        boolean isOptional = false;
                        String postCall = null;
                        if (parameter.isAnnotationPresent(Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = parameter.getAnnotation(Jni2RustParam.class);
                            isOptional = jni2RustParam.optional();
                            if (StringUtils.isNotEmpty(jni2RustParam.postCall())) {
                                postCall = jni2RustParam.postCall();
                            }
                        }
                        if (postCall == null) {
                            if (parameterType == String.class) {
                                if (isOptional) {
                                    lines.add("    env");
                                    lines.add(String.format("      .delete_local_ref(java_%s)", name));
                                    lines.add(String.format("      .expect(\"Couldn't delete local %s\");", name));
                                }
                            }
                        } else {
                            lines.add(String.format("    %s;", postCall));
                        }
                    }
                    // return
                    lines.add("    return_value");
                });
    }

    protected void getCodeNew(List<String> lines) {
        lines.add("  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {");
        lines.add("    let class = env");
        lines.add(String.format("      .find_class(\"%s\")", clazz.getName().replace('.', '/')));
        lines.add(String.format("      .expect(\"Couldn't find class %s\");", clazz.getSimpleName()));
        lines.add("    let class = env");
        lines.add("      .new_global_ref(class)");
        lines.add(String.format("      .expect(\"Couldn't globalize class %s\");", clazz.getSimpleName()));
        methods.forEach(method -> {
            String methodName = getMethodName(method);
            lines.add(String.format("    let method_%s = env", methodName));
            if (Modifier.isStatic(method.getModifiers())) {
                lines.add("      .get_static_method_id(");
            } else {
                lines.add("      .get_method_id(");
            }
            lines.add("        &class,");
            lines.add(String.format("        \"%s\",", method.getName()));
            StringBuilder sb = new StringBuilder("(");
            for (Class<?> parameterClass : method.getParameterTypes()) {
                sb.append(getSignature(parameterClass.getName()));
            }
            sb.append(")");
            sb.append(getSignature(method.getReturnType().getName()));
            lines.add(String.format("        \"%s\",", sb));
            lines.add("      )");
            lines.add(String.format("      .expect(\"Couldn't find method %s.%s\");", clazz.getSimpleName(), method.getName()));
        });
        lines.add(String.format("    %s {", structName));
        lines.add("      class,");
        methods.stream()
                .map(this::getMethodName)
                .forEach(methodName -> lines.add(String.format("      method_%s,", methodName)));
        lines.add("    }");
        lines.add("  }");
    }

    protected void getCodeStruct(List<String> lines) {
        lines.add(String.format("struct %s {", structName));
        lines.add("  #[allow(dead_code)]");
        lines.add("  class: GlobalRef,");
        methods.forEach(method -> {
            String methodName = getMethodName(method);
            if (Modifier.isStatic(method.getModifiers())) {
                lines.add(String.format("  method_%s: JStaticMethodID,", methodName));
            } else {
                lines.add(String.format("  method_%s: JMethodID,", methodName));
            }
        });
        lines.add("}");
        lines.add(String.format("unsafe impl Send for %s {}", structName));
        lines.add(String.format("unsafe impl Sync for %s {}", structName));
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public String getFilePath() {
        return filePath;
    }

    protected String getMethodName(Method method) {
        String methodName = null;
        if (method.isAnnotationPresent(Jni2RustMethod.class)) {
            methodName = method.getAnnotation(Jni2RustMethod.class).name();
        }
        if (StringUtils.isEmpty(methodName)) {
            methodName = StringUtils.toSnakeCase(method.getName());
        }
        return methodName;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Jni2RustOptions getOptions() {
        return options;
    }

    protected String getParameterName(Parameter parameter) {
        String parameterName = null;
        if (parameter.isAnnotationPresent(Jni2RustParam.class)) {
            parameterName = parameter.getAnnotation(Jni2RustParam.class).name();
        }
        if (StringUtils.isEmpty(parameterName)) {
            parameterName = StringUtils.toSnakeCase(parameter.getName());
        }
        return parameterName;
    }

    protected String getSignature(String javaType) {
        String jniType = options.getJavaTypeToJniTypeMap().get(Objects.requireNonNull(javaType));
        if (jniType == null) {
            if (javaType.endsWith("[]")) {
                String baseJavaType = javaType.substring(0, javaType.length() - 2);
                String jniBaseType = getSignature(baseJavaType);
                if (jniBaseType != null) {
                    jniType = "[" + jniBaseType;
                }
            } else {
                jniType = "L" + javaType.replace('.', '/') + ";";
            }
        }
        return jniType;
    }

    public String getStructName() {
        return structName;
    }

    protected void init() {
        Jni2RustClass jni2RustClass = clazz.getAnnotation(Jni2RustClass.class);
        // filePath and structName
        setFilePath(null);
        setStructName(PREFIX_NAME + clazz.getSimpleName());
        if (jni2RustClass != null) {
            if (StringUtils.isNotEmpty(jni2RustClass.filePath())) {
                setFilePath(jni2RustClass.filePath());
            }
            if (StringUtils.isNotEmpty(jni2RustClass.name())) {
                setStructName(jni2RustClass.name());
            }
        }
        // constructor
        constructor = (Constructor<T>) Stream.of(clazz.getConstructors())
                .filter(method -> method.isAnnotationPresent(Jni2RustMethod.class))
                .findFirst()
                .orElse(null);
        // methods
        methods.clear();
        Stream.of(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Jni2RustMethod.class))
                .sorted(Comparator.comparing(Method::getName))
                .forEach(methods::add);
        boolean manualMethodFound = Optional.ofNullable(constructor)
                .map(method -> method.getAnnotation(Jni2RustMethod.class))
                .map(annotation -> annotation.mode() == Jni2RustMethodMode.Manual)
                .orElse(false);
        manualMethodFound = !manualMethodFound && methods.stream()
                .map(method -> method.getAnnotation(Jni2RustMethod.class))
                .map(annotation -> annotation.mode() == Jni2RustMethodMode.Manual)
                .findFirst()
                .orElse(false);
        completelyAuto = !manualMethodFound;
    }

    public boolean isCompletelyAuto() {
        return completelyAuto;
    }

    public void setCompletelyAuto(boolean completelyAuto) {
        this.completelyAuto = completelyAuto;
    }

    public void setConstructor(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public boolean updateFile() throws IOException {
        String startSign = String.format("\n/* %s Begin */\n", structName);
        String endSign = String.format("\n/* %s End */\n", structName);
        File file = new File(OSUtils.WORKING_DIRECTORY, filePath);
        AssertionUtils.notTrue(file.exists(), file.getAbsolutePath() + " is not found");
        AssertionUtils.notTrue(file.canRead(), file.getAbsolutePath() + " is not readable");
        AssertionUtils.notTrue(file.canWrite(), file.getAbsolutePath() + " is not writable");
        byte[] originalBuffer = Files.readAllBytes(file.toPath());
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        AssertionUtils.notNull(fileContent, "File content");
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.lastIndexOf(endSign);
        AssertionUtils.notTrue(startPosition > 0, "Start position is invalid");
        AssertionUtils.notTrue(endPosition >= startPosition, "End position is invalid");
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        sb.append(getCode());
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(file.toPath(), newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        }
        return false;
    }
}
