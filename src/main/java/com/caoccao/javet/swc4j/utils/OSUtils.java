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

package com.caoccao.javet.swc4j.utils;

/**
 * The type OS utils.
 *
 * @since 0.1.0
 */
public final class OSUtils {
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String JAVA_VM_NAME = System.getProperty("java.vm.name");
    public static final boolean IS_ANDROID = JAVA_VM_NAME.startsWith("Dalvik");
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux") && !IS_ANDROID;
    public static final boolean IS_MACOS = OS_NAME.startsWith("Mac OS") && !IS_ANDROID;
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows") && !IS_ANDROID;
    public static final boolean IS_ARM =
            OS_ARCH.startsWith("armeabi") || OS_ARCH.startsWith("armv7") ||
                    OS_ARCH.startsWith("arm32") || OS_ARCH.equals("arm");
    public static final boolean IS_ARM64 =
            OS_ARCH.startsWith("arm64") || OS_ARCH.startsWith("armv8") ||
                    OS_ARCH.equals("aarch64");
    public static final boolean IS_X86 = OS_ARCH.matches("^(x86_32|x8632|x86|i[3-6]86|ia32|x32)$");
    public static final boolean IS_X86_64 = OS_ARCH.matches("^(x86_64|x8664|amd64|ia32e|em64t|x64)$");
    public static final long PROCESS_ID;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    static {
        long processId = 1L;
        if (!IS_ANDROID) {
            try {
                Class<?> classManagementFactory = Class.forName("java.lang.management.ManagementFactory");
                Class<?> classRuntimeMXBean = Class.forName("java.lang.management.RuntimeMXBean");
                Object runtimeMXBean = classManagementFactory.getMethod("getRuntimeMXBean").invoke(null);
                String processName = (String) classRuntimeMXBean.getMethod("getName").invoke(runtimeMXBean);
                int positionOfSeparator = processName.indexOf("@");
                if (positionOfSeparator > 0) {
                    processName = processName.substring(0, positionOfSeparator);
                }
                processId = Long.parseLong(processName);
            } catch (Throwable ignore) {
            }
        }
        PROCESS_ID = processId;
    }

    private OSUtils() {
    }
}
