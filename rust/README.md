# swc4j Rust Library

The swc4j Rust Library is the native library that wraps SWC and Deno AST providing the compilation and bundling features.

This doc is a guide to how to build this library.

## Preparation

### Install Targets

Follow the [Cross-compilation](https://rust-lang.github.io/rustup/cross-compilation.html) to add the following [supported platforms](https://doc.rust-lang.org/nightly/rustc/platform-support.html).

```sh
# 64-bit MSVC (Windows 10+)
rustup target add x86_64-pc-windows-msvc
# ARM64 Windows MSVC
rustup target add aarch64-pc-windows-msvc

# 64-bit Linux (kernel 3.2+, glibc 2.17+)
rustup target add x86_64-unknown-linux-gnu
# ARM64 Linux (kernel 4.1, glibc 2.17+)
rustup target add aarch64-unknown-linux-gnu

# 64-bit macOS (10.12+, Sierra+)
rustup target add x86_64-apple-darwin
# ARM64 macOS (11.0+, Big Sur+)
rustup target add aarch64-apple-darwin

# 32-bit x86 Android
rustup target add i686-linux-android
# 64-bit x86 Android
rustup target add x86_64-linux-android
# ARMv7-A Android
rustup target add armv7-linux-androideabi
# ARM64 Android
rustup target add aarch64-linux-android
```

### Install Android NDK

* Install a proper Android NDK.
* `export ANDROID_NDK_HOME=${where-ndk-is-installed}`

### Install cargo-ndk on Linux

```sh
cargo install cargo-ndk
```

## Build

```sh
# Windows Native
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o windows -a x86_64
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o windows -a arm64
# Windows Cross-compile
cargo build --release --target x86_64-pc-windows-msvc && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o windows -a x86_64
cargo build --release --target aarch64-pc-windows-msvc && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o windows -a arm64

# Linux Native
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o linux -a x86_64
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o linux -a arm64
# Linux Cross-compile
cargo build --release --target x86_64-unknown-linux-gnu && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o linux -a x86_64
cargo build --release --target aarch64-unknown-linux-gnu && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o linux -a arm64

# MacOS Native
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o macos -a x86_64
cargo build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o macos -a arm64
# MacOS Cross-compile
cargo build --release --target x86_64-apple-darwin && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o macos -a x86_64
cargo build --release --target aarch64-apple-darwin && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o macos -a arm64

# Android Cross-compile
cargo ndk --target i686-linux-android build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o android -a x86
cargo ndk --target x86_64-linux-android build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o android -a x86_64
cargo ndk --target armv7-linux-androideabi build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o android -a arm
cargo ndk --target aarch64-linux-android build --release && deno run --allow-all ../scripts/deno/copy_swc4j_lib.ts -o android -a arm64
```

## Linux Runtime Dependencies

The x86_64 and arm64 swc4j shared libraries depend on the GCC runtime, `libgcc_s.so.1`. Both the project's Ubuntu 20.04 builder images and its `gradle:9.9.2-jdk17-corretto` test image include this library on both architectures. Applications using those images can ship the swc4j `.so` without bundling a separate copy of the GCC runtime.

The results below apply to both x86_64 and arm64 unless a row specifies one architecture. To check another glibc-based image, run `ldd` on the matching swc4j shared library inside that image.

| Base image | `libgcc_s.so.1` in the image | swc4j v2.3.0 dependency check |
| --- | --- | --- |
| Ubuntu 20.04, 22.04, 24.04, 26.04 | Included (`libgcc-s1`) | All dependencies resolved |
| Debian 12 and 13 (`slim`) | Included (`libgcc-s1`) | All dependencies resolved |
| Fedora 45, Rocky Linux 9, AlmaLinux 9, Amazon Linux 2023 | Included (`libgcc`) | All dependencies resolved |
| Red Hat UBI 9 and 10 (`ubi-minimal`) | Included (`libgcc`) | All dependencies resolved |
| Red Hat UBI 8 (`ubi-minimal`) | Included (`libgcc`) | glibc 2.28 lacks required symbols, including `GLIBC_2.29` |
| openSUSE Leap 16.0 | Included (`libgcc_s1`) | All dependencies resolved |
| Arch Linux (`base`, x86_64) | Included (`libgcc`) | All dependencies resolved |
| Distroless `java17-debian13` | Included | All dependencies resolved |
| Distroless `base-debian13` | Absent | Loader reported a missing `libgcc_s.so.1` |
| Alpine 3.24 | Available via `apk add libgcc` | The base image uses musl and lacks the glibc loader required by these binaries |

## Logging

The debug log can be turned on as follows.

```sh
# Linux / MacOS
export RUST_LOG=debug
# Windows
set RUST_LOG=debug
```
