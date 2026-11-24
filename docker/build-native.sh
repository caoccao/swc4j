#!/usr/bin/env bash
set -euo pipefail
export LC_ALL=C

case "$(dpkg --print-architecture)" in
    amd64)
        platform=linux/amd64
        arch=x86_64
        target=x86_64-unknown-linux-gnu
        elf_machine="Advanced Micro Devices X86-64"
        ;;
    arm64)
        platform=linux/arm64
        arch=arm64
        target=aarch64-unknown-linux-gnu
        elf_machine=AArch64
        export CARGO_TARGET_AARCH64_UNKNOWN_LINUX_GNU_LINKER=cc
        ;;
    *)
        echo "Unsupported Ubuntu package architecture: $(dpkg --print-architecture)" >&2
        exit 1
        ;;
esac

cd /workspace/rust
if ! cmp -s rust-toolchain.toml /opt/swc4j/rust-toolchain.toml; then
    echo "The checkout needs a different Rust toolchain. Rebuild from the repository root:" >&2
    echo "docker buildx build --platform $platform --load -f docker/Dockerfile -t swc4j-builder:ubuntu20.04-$arch ." >&2
    exit 1
fi

host=$(rustc -vV | sed -n 's/^host: //p')
if [[ $host != "$target" ]]; then
    echo "Expected Rust host $target for $platform, found $host." >&2
    exit 1
fi

cargo build --release --locked --target "$target"
cargo test --release --locked --target "$target"

version=$(cargo metadata --format-version 1 --no-deps --locked | jq -er '.packages[] | select(.name == "swc4j") | .version')
library="$CARGO_TARGET_DIR/$target/release/libswc4j.so"
machine=$(readelf -h "$library" | sed -n 's/^ *Machine: *//p')
if [[ $machine != "$elf_machine" ]]; then
    echo "Expected $elf_machine for $library, found $machine." >&2
    exit 1
fi

output="/out/libswc4j-linux-$arch.v.$version.so"
temporary=$(mktemp "$output.XXXXXX")
trap 'rm -f -- "$temporary"' EXIT
install -m 0644 "$library" "$temporary"
mv -fT "$temporary" "$output"
echo "Exported $output"
