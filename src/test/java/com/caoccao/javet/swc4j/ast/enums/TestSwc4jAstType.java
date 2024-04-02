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

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.utils.OSUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestSwc4jAstType {
    @Test
    public void testTypeMatch() throws IOException {
        Path rootPath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve("src/main/java/com/caoccao/javet/swc4j/ast");
        Pattern patternFileName = Pattern.compile("^Swc4jAst(\\w+)\\.java$");
        Pattern patternReturnType = Pattern.compile("return Swc4jAstType\\.(\\w+);");
        Matcher matcher = patternReturnType.matcher("    @Override\n" +
                "    public Swc4jAstType getType() {\n" +
                "        return Swc4jAstType.VarDeclarator;\n" +
                "    }\n");
        Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).forEach(filePath -> {
            File file = filePath.toFile();
            if (file.isFile()) {
                String fileName = file.getName();
                Matcher matcherFileName = patternFileName.matcher(fileName);
                if (matcherFileName.matches()) {
                    String expectedReturnType = matcherFileName.group(1);
                    try {
                        String content = new String(Files.readAllBytes(filePath));
                        Matcher matcherReturnType = patternReturnType.matcher(content);
                        if (matcherReturnType.find()) {
                            String returnType = matcherReturnType.group(1);
                            assertEquals(
                                    expectedReturnType,
                                    returnType,
                                    "Type of " + fileName + " should match");
                        }
                    } catch (IOException e) {
                        fail(e);
                    }
                }
            }
        });
    }
}
