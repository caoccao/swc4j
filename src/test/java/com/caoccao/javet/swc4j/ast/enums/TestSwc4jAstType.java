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

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.ast.Swc4jAstStore;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstType {
    @Test
    public void testTypeMatch() {
        final AtomicInteger counter = new AtomicInteger();
        Pattern patternReturnType = Pattern.compile("return Swc4jAstType\\.(\\w+);");
        Swc4jAstStore.getInstance().getStructMap().forEach((expectedReturnType, clazz) -> {
            String relativeFilePath = clazz.getName().replace(".", "/") + Swc4jAstStore.JAVA_FILE_EXT;
            Path filePath = Swc4jAstStore.SOURCE_PATH.resolve(relativeFilePath);
            try {
                String content = Files.readString(filePath);
                Matcher matcherReturnType = patternReturnType.matcher(content);
                if (matcherReturnType.find()) {
                    String returnType = matcherReturnType.group(1);
                    assertEquals(
                            expectedReturnType,
                            returnType,
                            "Type of " + filePath.toFile().getName() + " should match");
                    counter.incrementAndGet();
                }
            } catch (Exception e) {
                fail(e);
            }
        });
        assertTrue(counter.get() > 0);
    }
}
