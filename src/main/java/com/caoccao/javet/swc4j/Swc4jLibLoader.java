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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.exceptions.Swc4jLibException;
import com.caoccao.javet.swc4j.interfaces.ISwc4jLogger;
import com.caoccao.javet.swc4j.utils.ArrayUtils;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
import com.caoccao.javet.swc4j.utils.Swc4jDefaultLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * The type Swc4j lib loader.
 *
 * @since 0.1.0
 */
final class Swc4jLibLoader {
    private static final String ANDROID_ABI_ARM = "armeabi-v7a";
    private static final String ANDROID_ABI_ARM64 = "arm64-v8a";
    private static final String ANDROID_ABI_X86 = "x86";
    private static final String ANDROID_ABI_X86_64 = "x86_64";
    private static final String ARCH_ARM = "arm";
    private static final String ARCH_ARM64 = "arm64";
    private static final String ARCH_X86 = "x86";
    private static final String ARCH_X86_64 = "x86_64";
    private static final int BUFFER_LENGTH = 4096;
    private static final String CHMOD = "chmod";
    private static final String LIB_FILE_EXTENSION_ANDROID = "so";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_MACOS = "dylib";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String LIB_FILE_NAME_FORMAT = "libswc4j-{0}-{1}.v.{2}.{3}";
    private static final String LIB_FILE_NAME_FOR_ANDROID_FORMAT = "libswc4j-{0}.v.{1}.{2}";
    private static final String LIB_FILE_NAME_PREFIX = "lib";
    private static final String LIB_NAME = "swc4j";
    private static final String LIB_VERSION = "0.10.0";
    private static final ISwc4jLogger LOGGER = new Swc4jDefaultLogger(Swc4jLibLoader.class.getName());
    private static final long MIN_LAST_MODIFIED_GAP_IN_MILLIS = 60L * 1000L; // 1 minute
    private static final String OS_ANDROID = "android";
    private static final String OS_LINUX = "linux";
    private static final String OS_MACOS = "macos";
    private static final String OS_WINDOWS = "windows";
    private static final String RESOURCE_NAME_FORMAT = "/{0}";
    private static final String XRR = "755";

    /**
     * Instantiates a new Swc4j lib loader.
     *
     * @since 0.1.0
     */
    Swc4jLibLoader() {
    }

    private void deployLibFile(String resourceFileName, File libFile) {
        boolean isLibFileLocked = false;
        if (libFile.exists() && libFile.canWrite()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                libFile.delete();
            } catch (Throwable t) {
                isLibFileLocked = true;
                LOGGER.logError("Failed to delete {0} because it is locked.", libFile.getAbsolutePath());
            }
        }
        if (!isLibFileLocked) {
            byte[] buffer = new byte[BUFFER_LENGTH];
            try (InputStream inputStream = Swc4jNative.class.getResourceAsStream(resourceFileName);
                 FileOutputStream outputStream = new FileOutputStream(libFile.getAbsolutePath())) {
                if (inputStream != null) {
                    while (true) {
                        int length = inputStream.read(buffer);
                        if (length == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, length);
                    }
                    if (OSUtils.IS_LINUX || OSUtils.IS_MACOS || OSUtils.IS_ANDROID) {
                        try {
                            Runtime.getRuntime().exec(new String[]{CHMOD, XRR, libFile.getAbsolutePath()}).waitFor();
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (Throwable t) {
                LOGGER.logError("Failed to write to {0} because it is locked.", libFile.getAbsolutePath());
            }
        }
    }

    private String getAndroidABI() {
        if (OSUtils.IS_ANDROID) {
            if (OSUtils.IS_ARM) {
                return ANDROID_ABI_ARM;
            } else if (OSUtils.IS_ARM64) {
                return ANDROID_ABI_ARM64;
            } else if (OSUtils.IS_X86) {
                return ANDROID_ABI_X86;
            } else if (OSUtils.IS_X86_64) {
                return ANDROID_ABI_X86_64;
            }
        }
        return null;
    }

    private String getFileExtension() {
        if (OSUtils.IS_WINDOWS) {
            return LIB_FILE_EXTENSION_WINDOWS;
        } else if (OSUtils.IS_LINUX) {
            return LIB_FILE_EXTENSION_LINUX;
        } else if (OSUtils.IS_MACOS) {
            return LIB_FILE_EXTENSION_MACOS;
        } else if (OSUtils.IS_ANDROID) {
            return LIB_FILE_EXTENSION_ANDROID;
        }
        return null;
    }

    private String getLibFileName() throws Swc4jLibException {
        String fileExtension = getFileExtension();
        String osName = getOSName();
        if (fileExtension == null || osName == null) {
            throw Swc4jLibException.osNotSupported(OSUtils.OS_NAME);
        }
        String osArch = getOSArch();
        if (osArch == null) {
            throw Swc4jLibException.archNotSupported(OSUtils.OS_ARCH);
        }
        if (OSUtils.IS_ANDROID) {
            return MessageFormat.format(
                    LIB_FILE_NAME_FOR_ANDROID_FORMAT,
                    osName,
                    LIB_VERSION,
                    fileExtension);
        } else {
            return MessageFormat.format(
                    LIB_FILE_NAME_FORMAT,
                    osName,
                    osArch,
                    LIB_VERSION,
                    fileExtension);
        }
    }

    private String getOSArch() {
        if (OSUtils.IS_WINDOWS) {
            return ARCH_X86_64;
        } else if (OSUtils.IS_LINUX) {
            return OSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (OSUtils.IS_MACOS) {
            return OSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (OSUtils.IS_ANDROID) {
            if (OSUtils.IS_ARM) {
                return ARCH_ARM;
            } else if (OSUtils.IS_ARM64) {
                return ARCH_ARM64;
            } else if (OSUtils.IS_X86) {
                return ARCH_X86;
            } else if (OSUtils.IS_X86_64) {
                return ARCH_X86_64;
            }
        }
        return null;
    }

    private String getOSName() {
        if (OSUtils.IS_WINDOWS) {
            return OS_WINDOWS;
        } else if (OSUtils.IS_LINUX) {
            return OS_LINUX;
        } else if (OSUtils.IS_MACOS) {
            return OS_MACOS;
        } else if (OSUtils.IS_ANDROID) {
            return OS_ANDROID;
        }
        return null;
    }

    private String getResourceFileName() throws Swc4jLibException {
        String resourceFileName = MessageFormat.format(RESOURCE_NAME_FORMAT, OSUtils.IS_ANDROID
                ? StringUtils.join("/", LIB_FILE_NAME_PREFIX, getAndroidABI(), getLibFileName())
                : getLibFileName());
        if (Swc4jNative.class.getResource(resourceFileName) == null) {
            throw Swc4jLibException.libNotFound(resourceFileName);
        }
        return resourceFileName;
    }

    /**
     * Load the native library.
     *
     * @since 0.1.0
     */
    void load() {
        String libFilePath = null;
        try {
            File libPath = new File(OSUtils.TEMP_DIRECTORY, LIB_NAME);
            purge(libPath);
            File rootLibPath;
            if (OSUtils.IS_ANDROID) {
                rootLibPath = libPath;
            } else {
                rootLibPath = new File(libPath, Long.toString(OSUtils.PROCESS_ID));
            }
            if (!rootLibPath.exists()) {
                if (!rootLibPath.mkdirs()) {
                    throw Swc4jLibException.libNotCreated(rootLibPath.getAbsolutePath());
                }
            }
            String resourceFileName = getResourceFileName();
            File libFile = new File(rootLibPath, getLibFileName()).getAbsoluteFile();
            libFilePath = libFile.getAbsolutePath();
            deployLibFile(resourceFileName, libFile);
            System.load(libFilePath);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    private void purge(File rootLibPath) {
        try {
            if (rootLibPath.exists()) {
                if (rootLibPath.isDirectory()) {
                    File[] files = rootLibPath.listFiles();
                    if (ArrayUtils.isNotEmpty(files)) {
                        for (File libFileOrPath : files) {
                            if (libFileOrPath.lastModified() + MIN_LAST_MODIFIED_GAP_IN_MILLIS > System.currentTimeMillis()) {
                                continue;
                            }
                            boolean toBeDeleted = false;
                            if (libFileOrPath.isDirectory()) {
                                try {
                                    File[] libFiles = libFileOrPath.listFiles();
                                    if (libFiles != null && libFiles.length > 0) {
                                        for (File libFile : libFiles) {
                                            if (libFile.delete()) {
                                                LOGGER.logDebug("Deleted {0}.", libFile.getAbsolutePath());
                                            } else {
                                                LOGGER.logDebug("{0} is locked.", libFile.getAbsolutePath());
                                                toBeDeleted = true;
                                                break;
                                            }
                                        }
                                    } else {
                                        toBeDeleted = true;
                                    }
                                } catch (Throwable t) {
                                    LOGGER.logError("Failed to delete {0}.", libFileOrPath.getAbsolutePath());
                                }
                            } else if (libFileOrPath.isFile()) {
                                toBeDeleted = true;
                            }
                            if (toBeDeleted) {
                                if (libFileOrPath.delete()) {
                                    LOGGER.logDebug("Deleted {0}.", libFileOrPath.getAbsolutePath());
                                } else {
                                    LOGGER.logDebug("{0} is locked.", libFileOrPath.getAbsolutePath());
                                }
                            }
                        }
                    }
                } else {
                    if (!rootLibPath.delete()) {
                        LOGGER.logError("Failed to delete {0}.", rootLibPath.getAbsolutePath());
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.logError("Failed to clean up {0}.", rootLibPath.getAbsolutePath());
        }
    }
}
