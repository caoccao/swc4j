/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.tutorials;

import com.caoccao.javet.sanitizer.checkers.JavetSanitizerModuleChecker;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;

public class TutorialSanitizer02BuiltInObjects {
    public static void main(String[] args) throws JavetSanitizerException {
        String codeString = "function main() { Date.parse = () => {}; }";
        // Check the script with the default options.
        try {
            new JavetSanitizerModuleChecker().check(codeString);
        } catch (JavetSanitizerException e) {
            System.out.println("/******************************************************/");
            System.out.println(codeString + " // Invalid");
            System.out.println("/******************************************************/");
            System.out.println(e.getDetailedMessage());
        }

        // Create a new options with built-in object Data allowed.
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
        options.getBuiltInObjectSet().remove("Date");
        options.seal();
        // Check the script with the new options.
        new JavetSanitizerModuleChecker(options).check(codeString);
        System.out.println("/******************************************************/");
        System.out.println(codeString + " // Valid");
        System.out.println("/******************************************************/");
    }
}
