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

package com.caoccao.javet.swc4j.comments;

import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

@Jni2RustClass(filePath = Jni2RustFilePath.CommentUtils)
public class Swc4jComment {
    protected final Swc4jCommentKind kind;
    protected final Swc4jSpan span;
    protected final String text;

    @Jni2RustMethod
    public Swc4jComment(String text, Swc4jCommentKind kind, Swc4jSpan span) {
        this.kind = AssertionUtils.notNull(kind, "Kind");
        this.span = AssertionUtils.notNull(span, "Span");
        this.text = AssertionUtils.notNull(text, "Text");
    }

    public Swc4jCommentKind getKind() {
        return kind;
    }

    public Swc4jSpan getSpan() {
        return span;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append("kind: ").append(kind.name()).append(", ");
        sb.append("span: ").append(span).append(", ");
        sb.append("text: ").append(text);
        sb.append(" }");
        return sb.toString();
    }
}
