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

package com.caoccao.javet.swc4j.compiler.jdk17;

public enum ReturnType {
    BOOLEAN(3),
    BYTE(3),
    CHAR(3),
    DOUBLE(4),
    FLOAT(3),
    INT(3),
    LONG(4),
    OBJECT(3),
    SHORT(3),
    STRING(3),
    VOID(0),
    ;

    private final int minStack;

    ReturnType(int minStack) {
        this.minStack = minStack;
    }

    public int getMinStack() {
        return minStack;
    }
}
