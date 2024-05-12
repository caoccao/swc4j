/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

import com.caoccao.javet.sanitizer.checkers.JavetSanitizerStatementListChecker;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;

public class TutorialSanitizer01QuickStart {
    public static void main(String[] args) throws JavetSanitizerException {
        JavetSanitizerStatementListChecker checker = new JavetSanitizerStatementListChecker();

        // 1. Check if keyword const can be used.
        String codeString = "const a = 1;";
        checker.check(codeString);
        System.out.println("1. " + codeString + " // Valid.");

        // 2. Check if keyword var can be used.
        codeString = "var a = 1;";
        try {
            checker.check(codeString);
        } catch (JavetSanitizerException e) {
            System.out.println("2. " + codeString + " // Invalid: " + e.getMessage());
        }

        // 3. Check if Object is mutable.
        codeString = "Object = {};";
        try {
            checker.check(codeString);
        } catch (JavetSanitizerException e) {
            System.out.println("3. " + codeString + " // Invalid: " + e.getMessage());
        }
    }
}
