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

package com.caoccao.javet.swc4j.comments;

import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type swc4j comments.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.CommentUtils)
public class Swc4jComments {
    /**
     * The Leading.
     */
    protected final Map<Integer, List<Swc4jComment>> leading;
    /**
     * The Trailing.
     */
    protected final Map<Integer, List<Swc4jComment>> trailing;

    /**
     * Instantiates a new swc4j comments.
     *
     * @param leading  the leading
     * @param trailing the trailing
     */
    @Jni2RustMethod
    public Swc4jComments(Map<Integer, List<Swc4jComment>> leading, Map<Integer, List<Swc4jComment>> trailing) {
        this.leading = SimpleMap.immutable(AssertionUtils.notNull(leading, "Leading"));
        this.trailing = SimpleMap.immutable(AssertionUtils.notNull(trailing, "Trailing"));
    }

    /**
     * Gets comments.
     *
     * @return the comments
     */
    public List<Swc4jComment> getComments() {
        List<Swc4jComment> comments = new ArrayList<>();
        leading.values().forEach(comments::addAll);
        trailing.values().forEach(comments::addAll);
        comments.sort(Comparator.comparingInt(comment -> comment.getSpan().getStart()));
        return comments;
    }

    /**
     * Gets leading.
     *
     * @return the leading
     */
    public Map<Integer, List<Swc4jComment>> getLeading() {
        return leading;
    }

    /**
     * Gets leading.
     *
     * @param span the span
     * @return the leading
     */
    public List<Swc4jComment> getLeading(Swc4jSpan span) {
        return getLeading(span.getStart());
    }

    /**
     * Gets leading.
     *
     * @param start the start
     * @return the leading
     */
    public List<Swc4jComment> getLeading(int start) {
        return leading.get(start);
    }

    /**
     * Gets trailing.
     *
     * @return the trailing
     */
    public Map<Integer, List<Swc4jComment>> getTrailing() {
        return trailing;
    }

    /**
     * Gets trailing.
     *
     * @param span the span
     * @return the trailing
     */
    public List<Swc4jComment> getTrailing(Swc4jSpan span) {
        return getTrailing(span.getEnd());
    }

    /**
     * Gets trailing.
     *
     * @param end the end
     * @return the trailing
     */
    public List<Swc4jComment> getTrailing(int end) {
        return trailing.get(end);
    }

    /**
     * Has leading boolean.
     *
     * @param span the span
     * @return the boolean
     */
    public boolean hasLeading(Swc4jSpan span) {
        return hasLeading(span.getStart());
    }

    /**
     * Has leading boolean.
     *
     * @param start the start
     * @return the boolean
     */
    public boolean hasLeading(int start) {
        return leading.containsKey(start);
    }

    /**
     * Has trailing boolean.
     *
     * @param span the span
     * @return the boolean
     */
    public boolean hasTrailing(Swc4jSpan span) {
        return hasTrailing(span.getEnd());
    }

    /**
     * Has trailing boolean.
     *
     * @param end the end
     * @return the boolean
     */
    public boolean hasTrailing(int end) {
        return trailing.containsKey(end);
    }

    @Override
    public String toString() {
        return getComments().stream()
                .map(Swc4jComment::toString)
                .collect(Collectors.joining("\n"));
    }
}
