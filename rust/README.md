# swc4j Rust Library

## Preparation

### Install Targets

Follow the [Cross-compilation](https://rust-lang.github.io/rustup/cross-compilation.html) to add the following [supported platforms](https://doc.rust-lang.org/nightly/rustc/platform-support.html).

```sh
# 64-bit MSVC (Windows 7+)
rustup target add x86_64-pc-windows-msvc

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
# Windows
cargo build --release --target x86_64-pc-windows-msvc

# Linux
cargo build --release --target x86_64-unknown-linux-gnu
cargo build --release --target aarch64-unknown-linux-gnu

# MacOS
cargo build --release --target x86_64-apple-darwin
cargo build --release --target aarch64-apple-darwin

# Android
cargo build --release --target i686-linux-android
cargo build --release --target x86_64-linux-android
cargo build --release --target armv7-linux-androideabi
cargo build --release --target aarch64-linux-android
```
