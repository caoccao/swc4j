/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

import java.util.Map;

/**
 * The type Simple free marker format.
 *
 * @since 0.7.0
 */
public final class SimpleFreeMarkerFormat {
    /**
     * Private constructor to prevent instantiation.
     */
    private SimpleFreeMarkerFormat() {
    }

    /**
     * The constant STRING_NULL.
     *
     * @since 0.7.0
     */
    public static final String STRING_NULL = "<null>";
    private static final char CHAR_DOLLAR = '$';
    private static final char CHAR_VARIABLE_CLOSE = '}';
    private static final char CHAR_VARIABLE_OPEN = '{';

    /**
     * Format string.
     *
     * @param format     the format
     * @param parameters the parameters
     * @return the string
     * @since 0.7.0
     */
    public static String format(final String format, final Map<String, Object> parameters) {
        if (StringUtils.isEmpty(format) || parameters == null || parameters.isEmpty()) {
            return format;
        }
        final int length = format.length();
        StringBuilder stringBuilderMessage = new StringBuilder();
        StringBuilder stringBuilderVariable = new StringBuilder();
        State state = State.Text;
        for (int i = 0; i < length; ++i) {
            final char c = format.charAt(i);
            switch (c) {
                case CHAR_DOLLAR -> {
                    switch (state) {
                        case Text -> state = State.Dollar;
                        case Dollar -> {
                            state = State.Text;
                            stringBuilderMessage.append(CHAR_DOLLAR).append(c);
                        }
                        case Variable -> stringBuilderVariable.append(c);
                    }
                }
                case CHAR_VARIABLE_OPEN -> {
                    switch (state) {
                        case Dollar -> state = State.Variable;
                        case Variable -> stringBuilderVariable.append(c);
                        case Text -> stringBuilderMessage.append(c);
                    }
                }
                case CHAR_VARIABLE_CLOSE -> {
                    switch (state) {
                        case Variable -> {
                            String variableName = stringBuilderVariable.toString();
                            Object parameter = parameters.get(variableName);
                            if (parameter == null) {
                                parameter = STRING_NULL;
                            }
                            stringBuilderMessage.append(parameter);
                            stringBuilderVariable.setLength(0);
                            state = State.Text;
                        }
                        default -> stringBuilderMessage.append(c);
                    }
                }
                default -> {
                    switch (state) {
                        case Dollar -> {
                            state = State.Text;
                            stringBuilderMessage.append(CHAR_DOLLAR).append(c);
                        }
                        case Text -> stringBuilderMessage.append(c);
                        case Variable -> stringBuilderVariable.append(c);
                    }
                }
            }
        }
        switch (state) {
            case Dollar -> stringBuilderMessage.append(CHAR_DOLLAR);
            case Variable ->
                    stringBuilderMessage.append(CHAR_DOLLAR).append(CHAR_VARIABLE_OPEN).append(stringBuilderVariable);
        }
        return stringBuilderMessage.toString();
    }

    /**
     * The enum State.
     *
     * @since 0.7.0
     */
    enum State {
        /**
         * Text state.
         *
         * @since 0.7.0
         */
        Text,
        /**
         * Dollar state.
         *
         * @since 0.7.0
         */
        Dollar,
        /**
         * Variable state.
         *
         * @since 0.7.0
         */
        Variable,
    }
}
