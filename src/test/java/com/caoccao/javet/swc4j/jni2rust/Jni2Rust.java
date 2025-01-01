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

import com.caoccao.javet.swc4j.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
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

    protected void getCodeImpl(List<String> lines) {
        lines.add(StringUtils.EMPTY);
        lines.add("#[allow(dead_code)]");
        lines.add(String.format("impl %s {", structName));
        getCodeNew(lines);
        getCodeMethods(lines);
        if (completelyAuto) {
            lines.add("}");
        }
    }

    protected void getCodeMethods(List<String> lines) {
        List<Executable> executables = new ArrayList<>();
        if (constructor != null) {
            executables.add(constructor);
        }
        executables.addAll(methods);
        executables.stream()
                .filter(executable -> !AnnotationUtils.isAnnotationPresent(executable, Jni2RustMethod.class)
                        || (Objects.requireNonNull(AnnotationUtils.getAnnotation(executable, Jni2RustMethod.class)).mode()
                        == Jni2RustMethodMode.Auto))
                .forEach(executable -> {
                    Jni2RustMethodUtils jni2RustMethodUtils = new Jni2RustMethodUtils(executable);
                    boolean isStatic = Modifier.isStatic(executable.getModifiers());
                    boolean isConstructor = executable instanceof Constructor;
                    boolean isPrimitive = !isConstructor && ((Method) executable).getReturnType().isPrimitive();
                    boolean isString = !isConstructor && ((Method) executable).getReturnType() == String.class;
                    boolean isVoid = !isConstructor && ((Method) executable).getReturnType() == Void.class;
                    String methodName = getExecutableName(executable);
                    String returnTypeName = isConstructor
                            ? clazz.getSimpleName()
                            : ((Method) executable).getReturnType().getSimpleName();
                    List<String> parameterNames = Stream.of(executable.getParameters())
                            .map(this::getParameterName)
                            .collect(Collectors.toList());
                    lines.add(StringUtils.EMPTY);
                    if (isVoid || isPrimitive || isString) {
                        lines.add(String.format("  pub fn %s<'local>(", methodName));
                    } else {
                        lines.add(String.format("  pub fn %s<'local, 'a>(", methodName));
                    }
                    lines.add("    &self,");
                    lines.add("    env: &mut JNIEnv<'local>,");
                    if (!isStatic) {
                        if (!isConstructor) {
                            lines.add("    obj: &JObject<'_>,");
                        }
                    }
                    Set<String> rustTypeSet = new HashSet<>();
                    for (Parameter parameter : executable.getParameters()) {
                        Class<?> parameterType = parameter.getType();
                        boolean isOptional = false;
                        boolean isSyntaxContext = false;
                        boolean isCustomRustType = false;
                        String rustType = null;
                        if (AnnotationUtils.isAnnotationPresent(parameter, Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = AnnotationUtils.getAnnotation(parameter, Jni2RustParam.class);
                            isOptional = Objects.requireNonNull(jni2RustParam).optional();
                            isSyntaxContext = Objects.requireNonNull(jni2RustParam).syntaxContext();
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
                            StringBuilder sb = new StringBuilder(name).append(": ");
                            if (parameterType.isPrimitive()) {
                                if (parameterType == int.class && isSyntaxContext) {
                                    sb.append("SyntaxContext");
                                } else {
                                    sb.append(options.getJavaTypeToRustTypeMap().get(parameterType.getName()));
                                }
                            } else if (parameterType == String.class) {
                                if (isOptional) {
                                    sb.append("&Option<String>");
                                } else {
                                    sb.append("&str");
                                }
                            } else {
                                if (isOptional) {
                                    sb.append("&Option<JObject>");
                                } else {
                                    sb.append("&JObject<'_>");
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
                        lines.add(String.format("  ) -> Result<%s>",
                                options.getJavaTypeToRustTypeMap().get(((Method) executable).getReturnType().getName())));
                    } else if (isString) {
                        if (jni2RustMethodUtils.isOptional()) {
                            lines.add("  ) -> Result<Option<String>>");
                        } else {
                            lines.add("  ) -> Result<String>");
                        }
                    } else {
                        if (jni2RustMethodUtils.isOptional()) {
                            lines.add("  ) -> Result<Option<JObject<'a>>>");
                        } else {
                            lines.add("  ) -> Result<JObject<'a>>");
                        }
                        lines.add("  where");
                        lines.add("    'local: 'a,");
                    }
                    lines.add("  {");
                    // pre-call
                    for (Parameter parameter : executable.getParameters()) {
                        String name = getParameterName(parameter);
                        Class<?> parameterType = parameter.getType();
                        boolean isOptional = false;
                        boolean isSyntaxContext = false;
                        String[] preCalls = null;
                        if (AnnotationUtils.isAnnotationPresent(parameter, Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = AnnotationUtils.getAnnotation(parameter, Jni2RustParam.class);
                            isOptional = Objects.requireNonNull(jni2RustParam).optional();
                            isSyntaxContext = Objects.requireNonNull(jni2RustParam).syntaxContext();
                            if (ArrayUtils.isNotEmpty(jni2RustParam.preCalls())) {
                                preCalls = jni2RustParam.preCalls();
                            }
                        }
                        if (ArrayUtils.isEmpty(preCalls)) {
                            if (parameterType.isPrimitive()) {
                                if (parameterType == int.class && isSyntaxContext) {
                                    lines.add(String.format("    let %s = %s.as_u32() as i32;", name, name));
                                }
                                lines.add(String.format("    let %s = %s_to_jvalue!(%s);", name, parameterType.getName(), name));
                            } else if (parameterType == String.class) {
                                if (isOptional) {
                                    lines.add(String.format("    let java_%s = optional_string_to_jstring!(env, &%s);", name, name));
                                } else {
                                    lines.add(String.format("    let java_%s = string_to_jstring!(env, &%s);", name, name));
                                }
                                lines.add(String.format("    let %s = object_to_jvalue!(java_%s);", name, name));
                            } else {
                                if (isOptional) {
                                    lines.add(String.format("    let %s = optional_object_to_jvalue!(%s);", name, name));
                                } else {
                                    lines.add(String.format("    let %s = object_to_jvalue!(%s);", name, name));
                                }
                            }
                        } else {
                            Collections.addAll(lines, preCalls);
                        }
                    }
                    // call
                    String returnType;
                    if (isVoid) {
                        returnType = "void";
                    } else if (isPrimitive) {
                        returnType = ((Method) executable).getReturnType().getName();
                    } else {
                        returnType = "object";
                    }
                    if (isStatic) {
                        lines.add(String.format("    let return_value = call_static_as_%s!(", returnType));
                        lines.add("        env,");
                        lines.add("        &self.class,");
                    } else if (isConstructor) {
                        lines.add("    let return_value = call_as_construct!(");
                        lines.add("        env,");
                        lines.add("        &self.class,");
                    } else {
                        lines.add(String.format("    let return_value = call_as_%s!(", returnType));
                        lines.add("        env,");
                        lines.add("        obj,");
                    }
                    lines.add(String.format("        self.method_%s,", methodName));
                    lines.add(String.format("        &[%s],", StringUtils.join(", ", parameterNames)));
                    lines.add(String.format("        \"%s %s()\"", returnTypeName, methodName));
                    lines.add("      )?;");
                    if (isPrimitive) {
                        // Do nothing
                    } else if (isString) {
                        lines.add("    let java_return_value = return_value;");
                        if (jni2RustMethodUtils.isOptional()) {
                            lines.add("    let return_value = jstring_to_optional_string!(env, java_return_value.as_raw())?;");
                        } else {
                            lines.add("    let return_value: Result<String> = jstring_to_string!(env, java_return_value.as_raw());");
                            lines.add("    let return_value = return_value?;");
                        }
                        lines.add("    delete_local_ref!(env, java_return_value);");
                    } else {
                        if (jni2RustMethodUtils.isOptional()) {
                            lines.add("    let return_value = if return_value.is_null() {");
                            lines.add("      None");
                            lines.add("    } else {");
                            lines.add("      Some(return_value)");
                            lines.add("    };");
                        }
                    }
                    // post-call
                    for (Parameter parameter : executable.getParameters()) {
                        String name = getParameterName(parameter);
                        Class<?> parameterType = parameter.getType();
                        String[] postCalls = null;
                        if (AnnotationUtils.isAnnotationPresent(parameter, Jni2RustParam.class)) {
                            Jni2RustParam jni2RustParam = AnnotationUtils.getAnnotation(parameter, Jni2RustParam.class);
                            if (ArrayUtils.isNotEmpty(Objects.requireNonNull(jni2RustParam).postCalls())) {
                                postCalls = jni2RustParam.postCalls();
                            }
                        }
                        if (ArrayUtils.isEmpty(postCalls)) {
                            if (parameterType == String.class) {
                                lines.add(String.format("    delete_local_ref!(env, java_%s);", name));
                            }
                        } else {
                            Collections.addAll(lines, postCalls);
                        }
                    }
                    // return
                    lines.add("    Ok(return_value)");
                    lines.add("  }");
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
        List<Executable> executables = new ArrayList<>();
        if (constructor != null) {
            executables.add(constructor);
        }
        executables.addAll(methods);
        executables.forEach(executable -> {
            String executableName = getExecutableName(executable);
            boolean isConstructor = executable instanceof Constructor;
            if (executable instanceof Method) {
                lines.add(String.format("    let method_%s = env", executableName));
            } else if (isConstructor) {
                lines.add("    let method_construct = env");
            }
            if (Modifier.isStatic(executable.getModifiers())) {
                lines.add("      .get_static_method_id(");
            } else {
                lines.add("      .get_method_id(");
            }
            lines.add("        &class,");
            if (executable instanceof Method) {
                lines.add(String.format("        \"%s\",", executable.getName()));
            } else if (isConstructor) {
                lines.add("        \"<init>\",");
            }
            StringBuilder sb = new StringBuilder("(");
            for (Class<?> parameterClass : executable.getParameterTypes()) {
                sb.append(getSignature(parameterClass.getName()));
            }
            sb.append(")");
            if (executable instanceof Method) {
                sb.append(getSignature(((Method) executable).getReturnType().getName()));
            } else if (isConstructor) {
                sb.append("V");
            }
            lines.add(String.format("        \"%s\",", sb));
            lines.add("      )");
            if (executable instanceof Method) {
                lines.add(String.format("      .expect(\"Couldn't find method %s.%s\");", clazz.getSimpleName(), executable.getName()));
            } else if (isConstructor) {
                lines.add(String.format("      .expect(\"Couldn't find method %s::new\");", clazz.getSimpleName()));
            }
        });
        lines.add(String.format("    %s {", structName));
        lines.add("      class,");
        if (constructor != null) {
            lines.add("      method_construct,");
        }
        methods.stream()
                .map(this::getExecutableName)
                .forEach(methodName -> lines.add(String.format("      method_%s,", methodName)));
        lines.add("    }");
        lines.add("  }");
    }

    protected void getCodeStruct(List<String> lines) {
        lines.add("#[allow(dead_code)]");
        lines.add(String.format("struct %s {", structName));
        lines.add("  class: GlobalRef,");
        if (constructor != null) {
            lines.add("  method_construct: JMethodID,");
        }
        methods.forEach(method -> {
            String methodName = getExecutableName(method);
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

    protected String getExecutableName(Executable executable) {
        return executable instanceof Constructor ? "construct" : StringUtils.toSnakeCase(executable.getName());
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        getCodeStruct(lines);
        getCodeImpl(lines);
        return lines;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Jni2RustOptions getOptions() {
        return options;
    }

    protected String getParameterName(Parameter parameter) {
        String parameterName = null;
        if (AnnotationUtils.isAnnotationPresent(parameter, Jni2RustParam.class)) {
            parameterName = Objects.requireNonNull(AnnotationUtils.getAnnotation(parameter, Jni2RustParam.class)).name();
        }
        if (StringUtils.isEmpty(parameterName)) {
            parameterName = StringUtils.toSnakeCase(parameter.getName());
        }
        return parameterName;
    }

    protected String getSignature(String javaType) {
        String jniType = options.getJavaTypeToJniSimpleTypeMap().get(Objects.requireNonNull(javaType));
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
        Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
        // filePath and structName
        setFilePath(jni2RustClassUtils.getFilePath().getFilePath());
        setStructName(PREFIX_NAME + clazz.getSimpleName());
        // constructor
        constructor = (Constructor<T>) Stream.of(clazz.getConstructors())
                .filter(method -> AnnotationUtils.isAnnotationPresent(method, Jni2RustMethod.class))
                .findFirst()
                .orElse(null);
        // methods
        methods.clear();
        Stream.of(clazz.getMethods())
                .filter(method -> AnnotationUtils.isAnnotationPresent(method, Jni2RustMethod.class))
                .sorted(Comparator.comparing(Method::getName))
                .forEach(methods::add);
        boolean manualMethodFound = Optional.ofNullable(constructor)
                .map(method -> AnnotationUtils.getAnnotation(method, Jni2RustMethod.class))
                .map(annotation -> annotation.mode() == Jni2RustMethodMode.Manual)
                .filter(found -> found)
                .orElse(false);
        manualMethodFound = manualMethodFound || methods.stream()
                .map(method -> AnnotationUtils.getAnnotation(method, Jni2RustMethod.class))
                .filter(Objects::nonNull)
                .map(annotation -> annotation.mode() == Jni2RustMethodMode.Manual)
                .filter(found -> found)
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
        String endSign = String.format("/* %s End */\n", structName);
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
        sb.append(StringUtils.join("\n", getLines()));
        sb.append("\n");
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
