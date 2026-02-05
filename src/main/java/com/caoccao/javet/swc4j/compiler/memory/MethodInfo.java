/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.memory;

/**
 * Stores metadata about a Java method.
 *
 * @param methodName the name of the method
 * @param descriptor the JVM method descriptor (e.g., "(I)V")
 * @param returnType the JVM return type descriptor (e.g., "I", "Ljava/lang/String;")
 * @param isStatic   true if the method is static
 * @param isVarArgs  true if the method accepts variable arguments
 */
public record MethodInfo(String methodName, String descriptor, String returnType, boolean isStatic, boolean isVarArgs) {
}
