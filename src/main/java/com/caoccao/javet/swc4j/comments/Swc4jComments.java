/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethodMode;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Jni2RustClass(filePath = Jni2RustFilePath.CommentUtils)
public class Swc4jComments {
    protected final Map<Integer, List<Swc4jComment>> leading;
    protected final Map<Integer, List<Swc4jComment>> trailing;

    @Jni2RustMethod(mode = Jni2RustMethodMode.Manual)
    public Swc4jComments(Map<Integer, List<Swc4jComment>> leading, Map<Integer, List<Swc4jComment>> trailing) {
        this.leading = SimpleMap.immutable(AssertionUtils.notNull(leading, "Leading"));
        this.trailing = SimpleMap.immutable(AssertionUtils.notNull(trailing, "Trailing"));
    }

    public List<Swc4jComment> getComments() {
        List<Swc4jComment> comments = new ArrayList<>();
        leading.values().forEach(comments::addAll);
        trailing.values().forEach(comments::addAll);
        comments.sort(Comparator.comparingInt(comment -> comment.getSpan().getStart()));
        return comments;
    }

    public Map<Integer, List<Swc4jComment>> getLeading() {
        return leading;
    }

    public Map<Integer, List<Swc4jComment>> getTrailing() {
        return trailing;
    }

    public boolean hasLeading(Swc4jSpan span) {
        return hasLeading(span.getStart());
    }

    public boolean hasLeading(int start) {
        return leading.containsKey(start);
    }

    public boolean hasTrailing(Swc4jSpan span) {
        return hasTrailing(span.getEnd());
    }

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
