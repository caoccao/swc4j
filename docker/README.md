# Build the swc4j Linux libraries on Ubuntu 20.04

Run the commands below from the **repository root** with Docker Engine and Buildx. On an Apple Silicon Mac, Docker Desktop runs the arm64 builder natively in its Linux VM and runs the x86_64 builder under emulation. The first x86_64 build can take longer. Use `docker buildx inspect --bootstrap` to check for both `linux/arm64` and `linux/amd64` support.

## Build and test a release library

Use the `tested-library` target to run the same checks as CI. The first stage builds the library and runs the Rust tests on Ubuntu 20.04. The next stage copies that library into the Java project's resources and runs `gradle test` in `gradle:9.9.2-jdk17-corretto` on the same CPU architecture. The final stage exports the tested `.so` to the host only after both suites pass.

```sh
docker buildx build --platform linux/amd64 --target tested-library \
  --output type=local,dest=src/main/resources -f docker/Dockerfile .

docker buildx build --platform linux/arm64 --target tested-library \
  --output type=local,dest=src/main/resources -f docker/Dockerfile .
```

## Build the reusable Rust images

```sh
docker buildx build --platform linux/amd64 --load \
  -f docker/Dockerfile -t swc4j-builder:ubuntu20.04-x86_64 .

docker buildx build --platform linux/arm64 --load \
  -f docker/Dockerfile -t swc4j-builder:ubuntu20.04-arm64 .
```

The images include the Rust toolchain specified in `rust/rust-toolchain.toml`. Rebuild them after changing the toolchain pin, its components, the Dockerfile, or the entrypoint. For Rust source edits, rerun the appropriate container against your checkout.

## Run the Rust tests during development

```sh
docker run --rm --platform linux/amd64 \
  --mount "type=bind,src=$PWD,dst=/workspace,readonly" \
  --mount "type=bind,src=$PWD/src/main/resources,dst=/out" \
  --mount type=volume,src=swc4j-focal-x86_64-cache,dst=/cache \
  swc4j-builder:ubuntu20.04-x86_64

docker run --rm --platform linux/arm64 \
  --mount "type=bind,src=$PWD,dst=/workspace,readonly" \
  --mount "type=bind,src=$PWD/src/main/resources,dst=/out" \
  --mount type=volume,src=swc4j-focal-arm64-cache,dst=/cache \
  swc4j-builder:ubuntu20.04-arm64
```

Each run mounts the checkout read-only, builds the release library with the checked-in Cargo lockfile, and runs the release Rust tests. Each architecture has a separate Docker volume for Cargo downloads and compilation output. The volumes survive container removal and speed up subsequent builds.

After the tests pass and the ELF architecture matches the target, the builder replaces its versioned file in the host’s `src/main/resources/`. A failed build or test preserves the previous export. The filenames for swc4j 2.3.0 are shown below. The builder reads the version from Cargo metadata.

| Platform | Rust target | Exported filename |
| --- | --- | --- |
| `linux/amd64` | `x86_64-unknown-linux-gnu` | `libswc4j-linux-x86_64.v.2.3.0.so` |
| `linux/arm64` | `aarch64-unknown-linux-gnu` | `libswc4j-linux-arm64.v.2.3.0.so` |

Run `file src/main/resources/libswc4j-linux-*.so` on the host to inspect the exported architectures.

## GitHub Actions

The [Docker build workflow](../.github/workflows/swc4j_docker_build.yml) runs on the same pull request events and pushes to `main` as the existing build workflow. Its matrix uses `ubuntu-24.04` for x86_64 and `ubuntu-24.04-arm` for arm64. Each job exports the `tested-library` target to a fresh directory on its runner, copies the library to `src/main/resources/`, and uploads the file from that export. A failing Rust or Gradle test fails that job and skips its artifact upload.

Download `swc4j-linux-x86_64` or `swc4j-linux-arm64` from the workflow run’s artifacts to get the corresponding versioned library.
