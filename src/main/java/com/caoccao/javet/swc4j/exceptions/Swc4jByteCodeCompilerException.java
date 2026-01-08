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

package com.caoccao.javet.swc4j.exceptions;

public final class Swc4jByteCodeCompilerException extends Swc4jException {
    public Swc4jByteCodeCompilerException(String message) {
        super(message);
    }

    public Swc4jByteCodeCompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public Swc4jByteCodeCompilerException(Throwable cause) {
        super(cause);
    }
}
