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

package com.caoccao.javet.swc4j.span;

import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;

/**
 * The type Swc4j span.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.SpanUtils)
public class Swc4jSpan {
    /**
     * The constant DUMMY.
     *
     * @since 0.8.0
     */
    public static final Swc4jSpan DUMMY = new Swc4jSpan();
    /**
     * The Column.
     *
     * @since 0.3.0
     */
    protected final int column;
    /**
     * The End.
     *
     * @since 0.2.0
     */
    protected final int end;
    /**
     * The Line.
     *
     * @since 0.3.0
     */
    protected final int line;
    /**
     * The Start.
     *
     * @since 0.2.0
     */
    protected final int start;

    /**
     * Instantiates a new Swc4j span.
     *
     * @since 0.6.0
     */
    public Swc4jSpan() {
        this(-1, -1, -1, -1);
    }

    /**
     * Instantiates a new Swc4j span.
     *
     * @param start  the start
     * @param end    the end
     * @param line   the line
     * @param column the column
     * @since 0.2.0
     */
    @Jni2RustMethod
    public Swc4jSpan(int start, int end, int line, int column) {
        this.column = column;
        this.end = end;
        this.line = line;
        this.start = start;
    }

    /**
     * Gets column.
     *
     * @return the column
     * @since 0.3.0
     */
    @Jni2RustMethod
    public int getColumn() {
        return column;
    }

    /**
     * Gets end.
     *
     * @return the end
     * @since 0.2.0
     */
    @Jni2RustMethod
    public int getEnd() {
        return end;
    }

    /**
     * Gets line.
     *
     * @return the line
     * @since 0.3.0
     */
    @Jni2RustMethod
    public int getLine() {
        return line;
    }

    /**
     * Gets start.
     *
     * @return the start
     * @since 0.2.0
     */
    @Jni2RustMethod
    public int getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "{ start: " + start + ", end: " + end + ", line: " + line + ", column: " + column + " }";
    }
}
