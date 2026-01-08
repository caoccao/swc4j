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

import org.gradle.internal.os.OperatingSystem

object Config {
    const val GROUP_ID = "com.caoccao.javet"
    const val NAME = "swc4j"
    const val VERSION = Versions.SWC4J
    const val URL = "https://github.com/caoccao/swc4j"

    const val MODULE_NAME = "$GROUP_ID.$NAME"

    object Pom {
        const val ARTIFACT_ID = "swc4j"
        const val DESCRIPTION =
            "swc4j (SWC for Java) is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM."

        object Developer {
            const val ID = "caoccao"
            const val EMAIL = "sjtucaocao@gmail.com"
            const val NAME = "Sam Cao"
            const val ORGANIZATION = "caoccao.com"
            const val ORGANIZATION_URL = "https://www.caoccao.com"
        }

        object License {
            const val NAME = "APACHE LICENSE, VERSION 2.0"
            const val URL = "https://github.com/caoccao/swc4j/blob/main/LICENSE"
        }

        object Scm {
            const val CONNECTION = "scm:git:git://github.com/swc4j.git"
            const val DEVELOPER_CONNECTION = "scm:git:ssh://github.com/swc4j.git"
        }
    }

    object Projects {
        const val JAVET = "com.caoccao.javet:javet:${Versions.JAVET}"
        val JAVET_BINARY = {
            val os = OperatingSystem.current()
            val arch = System.getProperty("os.arch")
            val osType = if (os.isWindows) "windows" else
                if (os.isMacOsX) "macos" else
                    if (os.isLinux) "linux" else ""
            val archType = if (arch == "aarch64" || arch == "arm64") "arm64" else "x86_64"
            "com.caoccao.javet:javet-v8-$osType-$archType:${Versions.JAVET}"
        }
    }

    object Versions {
        const val JAVA_VERSION = "1.8"
        const val JAVET = "4.1.0"
        const val JUNIT = "5.11.3"
        const val SWC4J = "2.0.0"
    }
}

val buildDir = layout.buildDirectory.get().toString()

plugins {
    java
    `java-library`
    `maven-publish`
}

group = Config.GROUP_ID
version = Config.VERSION

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(Config.Projects.JAVET)
    testImplementation(Config.Projects.JAVET_BINARY())
}

afterEvaluate {
    tasks.withType(JavaCompile::class) {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-parameters")
    }
}

tasks.register<Exec>("buildJNIHeaders") {
    mkdir("$buildDir/generated/tmp/jni")
    providers.exec {
        workingDir("$projectDir")
        commandLine(
            "javac",
            "-h",
            "rust/src/jni",
            "-d",
            "$buildDir/generated/tmp/jni",
            "src/main/java/com/caoccao/javet/swc4j/Swc4jNative.java",
        )
    }
}

tasks.jar {
    manifest {
        attributes["Automatic-Module-Name"] = Config.MODULE_NAME
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter(Config.Versions.JUNIT)
        }
    }
}

tasks.test {
    useJUnitPlatform {
        excludeTags("performance")
    }
}

tasks.register<Test>("performanceTest") {
    useJUnitPlatform {
        includeTags("performance")
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
    withType<Test> {
        systemProperty("file.encoding", "UTF-8")
    }
    withType<GenerateMavenPom> {
        destination = file("$buildDir/libs/${Config.Pom.ARTIFACT_ID}-${Config.VERSION}.pom")
    }
}

publishing {
    publications {
        create<MavenPublication>("generatePom") {
            from(components["java"])
            pom {
                artifactId = Config.Pom.ARTIFACT_ID
                description.set(Config.Pom.DESCRIPTION)
                groupId = Config.GROUP_ID
                name.set(Config.NAME)
                url.set(Config.URL)
                version = Config.VERSION
                licenses {
                    license {
                        name.set(Config.Pom.License.NAME)
                        url.set(Config.Pom.License.URL)
                    }
                }
                developers {
                    developer {
                        id.set(Config.Pom.Developer.ID)
                        email.set(Config.Pom.Developer.EMAIL)
                        name.set(Config.Pom.Developer.NAME)
                        organization.set(Config.Pom.Developer.ORGANIZATION)
                        organizationUrl.set(Config.Pom.Developer.ORGANIZATION_URL)
                    }
                }
                scm {
                    connection.set(Config.Pom.Scm.CONNECTION)
                    developerConnection.set(Config.Pom.Scm.DEVELOPER_CONNECTION)
                    tag.set(Config.Versions.SWC4J)
                    url.set(Config.URL)
                }
                properties.set(
                    mapOf(
                        "maven.compiler.source" to Config.Versions.JAVA_VERSION,
                        "maven.compiler.target" to Config.Versions.JAVA_VERSION,
                    )
                )
            }
        }
    }
}
