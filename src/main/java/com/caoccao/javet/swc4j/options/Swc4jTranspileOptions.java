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

package com.caoccao.javet.swc4j.options;

import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

public final class Swc4jTranspileOptions {
    public static final String DEFAULT_FILE_NAME = "main.js";
    private String fileName;
    private Swc4jMediaType mediaType;

    public Swc4jTranspileOptions() {
        setFileName(DEFAULT_FILE_NAME);
        setMediaType(Swc4jMediaType.JavaScript);
    }

    public String getFileName() {
        return fileName;
    }

    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    public Swc4jTranspileOptions setFileName(String fileName) {
        this.fileName = AssertionUtils.notNull(fileName, "File name");
        return this;
    }

    public Swc4jTranspileOptions setMediaType(Swc4jMediaType mediaType) {
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        return this;
    }
}
