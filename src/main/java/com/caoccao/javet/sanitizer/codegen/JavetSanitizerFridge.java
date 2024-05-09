/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.codegen;

import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;

/**
 * The type Javet sanitizer fridge.
 *
 * @since 0.7.0
 */
public final class JavetSanitizerFridge {
    /**
     * Generate JS code to delete the objects and freeze .
     *
     * @param option the option
     * @return the string
     * @since 0.7.0
     */
    public static String generate(final JavetSanitizerOptions option) {
        StringBuilder sb = new StringBuilder();
        sb.append("/***** Delete ").append(option.getToBeDeletedIdentifierList().size()).append(" object(s). *****/\n\n");
        option.getToBeDeletedIdentifierList().forEach(object ->
                sb.append("delete ").append(option.getGlobalIdentifier()).append(".").append(object).append(";\n"));
        sb.append("\n");
        sb.append("/***** Freeze ").append(option.getToBeFrozenIdentifierList().size()).append(" object(s). *****/\n\n");
        option.getToBeFrozenIdentifierList().forEach(object -> {
            sb.append("// ").append(object).append("\n");
            sb.append("const ").append(object).append(" = (() => {\n");
            sb.append("  const _").append(object).append(" = ").append(option.getGlobalIdentifier()).append(".").append(object).append(";\n");
            sb.append("  delete ").append(option.getGlobalIdentifier()).append(".").append(object).append(";\n");
            sb.append("  return _").append(object).append(";\n");
            sb.append("})();\n");
            sb.append("Object.freeze(").append(object).append(");\n");
            sb.append("Object.freeze(").append(object).append(".prototype);\n\n");
        });
        return sb.toString();
    }
}
