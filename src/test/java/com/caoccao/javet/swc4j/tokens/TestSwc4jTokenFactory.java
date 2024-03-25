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

package com.caoccao.javet.swc4j.tokens;

import com.caoccao.javet.swc4j.codegen.Jni2Rust;
import com.caoccao.javet.swc4j.utils.OSUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jTokenFactory {
    @Test
    public void testCodeGen() throws IOException {
        String startSign = "\n/* JavaSwc4jTokenFactory Begin */\n";
        String endSign = "\n/* JavaSwc4jTokenFactory End */\n";
        File file = new File(OSUtils.WORKING_DIRECTORY, "rust/src/token_utils.rs");
        assertTrue(file.exists());
        assertTrue(file.canRead());
        assertTrue(file.canWrite());
        byte[] originalBuffer = Files.readAllBytes(file.toPath());
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        assertNotNull(fileContent);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.lastIndexOf(endSign);
        assertTrue(endPosition >= startPosition && startPosition > 0);
        StringBuilder sb = new StringBuilder(fileContent.length());
        Jni2Rust jni2Rust = new Jni2Rust(Swc4jTokenFactory.class);
        sb.append(fileContent, 0, startPosition);
        sb.append(jni2Rust.getCode());
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(file.toPath(), newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
