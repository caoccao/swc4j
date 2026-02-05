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

package com.caoccao.javet.swc4j.ast.interfaces;

/**
 * Interface for AST nodes that can be coerced to primitive types.
 */
public interface ISwc4jAstCoercionPrimitive {
    /**
     * Converts this node to a boolean value.
     *
     * @return the boolean value
     */
    boolean asBoolean();

    /**
     * Converts this node to a byte value.
     *
     * @return the byte value
     */
    byte asByte();

    /**
     * Converts this node to a double value.
     *
     * @return the double value
     */
    double asDouble();

    /**
     * Converts this node to a float value.
     *
     * @return the float value
     */
    float asFloat();

    /**
     * Converts this node to an int value.
     *
     * @return the int value
     */
    int asInt();

    /**
     * Converts this node to a long value.
     *
     * @return the long value
     */
    long asLong();

    /**
     * Converts this node to a short value.
     *
     * @return the short value
     */
    short asShort();

    /**
     * Converts this node to a string value.
     *
     * @return the string value
     */
    String asString();
}
