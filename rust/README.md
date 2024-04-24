# swc4j Rust Library

The swc4j Rust Library is the native library that wraps SWC and Deno AST providing the compilation and bundling features.

This doc is a guide to how to build this library.

## Preparation

### Install Targets

Follow the [Cross-compilation](https://rust-lang.github.io/rustup/cross-compilation.html) to add the following [supported platforms](https://doc.rust-lang.org/nightly/rustc/platform-support.html).

Note: The Android build hasn't been implemented yet.

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

Install a proper Android NDK.

## Build

```sh
# Windows Native
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o windows -a x86_64
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o windows -a arm64
# Windows Cross-compile
cargo build --release --target x86_64-pc-windows-msvc && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o windows -a x86_64
cargo build --release --target aarch64-pc-windows-msvc && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o windows -a arm64

# Linux Native
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o linux -a x86_64
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o linux -a arm64
# Linux Cross-compile
cargo build --release --target x86_64-unknown-linux-gnu && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o linux -a x86_64
cargo build --release --target aarch64-unknown-linux-gnu && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o linux -a arm64

# MacOS Native
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o macos -a x86_64
cargo build --release && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o macos -a arm64
# MacOS Cross-compile
cargo build --release --target x86_64-apple-darwin && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o macos -a x86_64
cargo build --release --target aarch64-apple-darwin && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o macos -a arm64

# Android Cross-compile
export PATH=${PATH}:${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/bin
export AR=llvm-ar

export CC=i686-linux-android24-clang
export CXX=i686-linux-android24-clang++
export RUSTFLAGS="-L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/lib64/clang/14.0.6/lib/linux/i386 -L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/lib/i686-linux-android/24 -C target-feature=+crt-static"
cargo build --release --target i686-linux-android && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o android -a x86

export CC=x86_64-linux-android24-clang
export CXX=x86_64-linux-android24-clang++
export RUSTFLAGS="-L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/lib64/clang/14.0.6/lib/linux/x86_64 -L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/lib/x86_64-linux-android/24 -C target-feature=+crt-static"
cargo build --release --target x86_64-linux-android && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o android -a x86_64

export CC=armv7a-linux-android24-clang
export CXX=armv7a-linux-android24-clang++
export RUSTFLAGS="-L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/lib64/clang/14.0.6/lib/linux/arm -L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/lib/arm-linux-android/24 -C target-feature=+crt-static"
cargo build --release --target armv7-linux-androideabi && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o android -a arm

export CC=aarch64-linux-android24-clang
export CXX=aarch64-linux-android24-clang++
export RUSTFLAGS="-L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/lib64/clang/14.0.6/lib/linux/aarch64 -L ${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/sysroot/usr/lib/aarch64-linux-android/24 -C target-feature=+crt-static"
cargo build --release --target aarch64-linux-android && deno run --allow-all ../scripts/ts/copy_swc4j_lib.ts -o android -a arm64
```
