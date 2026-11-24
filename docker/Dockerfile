FROM ubuntu:20.04 AS native-builder

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        binutils build-essential ca-certificates curl jq \
    && rm -rf /var/lib/apt/lists/*

ENV RUSTUP_HOME=/opt/rustup \
    CARGO_HOME=/opt/cargo \
    PATH=/opt/cargo/bin:$PATH

WORKDIR /opt/swc4j
COPY rust/rust-toolchain.toml .
RUN curl --proto '=https' --tlsv1.2 --retry 3 -fsSLo /tmp/rustup-init.sh https://sh.rustup.rs \
    && sh /tmp/rustup-init.sh -y --no-modify-path --default-toolchain none \
    && rm /tmp/rustup-init.sh \
    && rustc --version

COPY --chmod=755 docker/build-native.sh /usr/local/bin/build-native
ENV CARGO_HOME=/cache/cargo \
    CARGO_TARGET_DIR=/cache/target
WORKDIR /workspace/rust
ENTRYPOINT ["/usr/local/bin/build-native"]

FROM native-builder AS native-build
RUN --mount=type=bind,source=rust,target=/workspace/rust \
    --mount=type=cache,target=/cache/cargo \
    mkdir -p /out && build-native && rm -rf "$CARGO_TARGET_DIR"

FROM gradle:9.9.2-jdk17-corretto AS java-test
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
COPY --from=native-build /out/ src/main/resources/
RUN gradle --no-daemon test

FROM scratch AS tested-library
COPY --from=java-test /home/gradle/project/src/main/resources/libswc4j-linux-*.so /

# Keep the default image runnable against a bind-mounted checkout.
FROM native-builder AS builder
