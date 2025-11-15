# Custom Library Loading

swc4j provides flexible options for customizing how the native library is loaded, allowing you to control deployment location, skip deployment, or suppress errors based on your application's needs.

## Can swc4j Native Library be Deployed to a Custom Location?

Yes. By default, the native library is deployed to the system temp directory, which might not be accessible in some cases. Here is a simple way to tell swc4j where to deploy the library:

```java
Swc4jLibLoader.setLibLoadingListener(new ISwc4jLibLoadingListener() {
    @Override
    public File getLibPath() {
        return new File("/../anywhere");
    }
});
```

By default, the native library is deployed by swc4j. To bypass the deployment, one more function is required to be overridden. That also means the application is responsible for deploying the native library to the right location.

```java
Swc4jLibLoader.setLibLoadingListener(new ISwc4jLibLoadingListener() {
    @Override
    public File getLibPath() {
        return new File("/../anywhere");
    }

    @Override
    public boolean isDeploy() {
        return false;
    }
});
```

> **Caution:**
> - `Swc4jLibLoader.setLibLoadingListener()` must be called before swc4j is initialized, otherwise it won't take effect.
> - The return path from `getLibPath()` does not include the library file name because swc4j will prepare it.

## Can swc4j Native Library Deployment be Skipped?

Yes. In some cases, the native library can be directly deployed to the system library path to avoid dynamic deployment. This brings better performance and reduces jar file size. Here is a sample way of telling swc4j to skip the deployment:

```java
Swc4jLibLoader.setLibLoadingListener(new ISwc4jLibLoadingListener() {
    @Override
    public boolean isLibInSystemPath() {
        return true;
    }
});
```

> **Caution:**
>
> `Swc4jLibLoader.setLibLoadingListener()` must be called before swc4j is initialized, otherwise it won't take effect.

## Can "Already Loaded in Another Classloader" Error be Suppressed?

In some cases, applications are hosted by other classloaders (e.g., Maven plugin host, OSGi, etc.) that actively load and unload applications on demand. This causes the swc4j native library to be loaded repeatedly. However, JVM only allows one memory copy of a particular JNI library regardless of which classloader it resides in.

```
java.lang.UnsatisfiedLinkError: Native Library ***libswc4j*** already loaded in another classloader
```

By default, swc4j treats this as an error and prevents all APIs from working. However, applications may want to suppress this error because the swc4j native library has already been loaded. Yes, swc4j allows that. Here is a sample:

```java
Swc4jLibLoader.setLibLoadingListener(new ISwc4jLibLoadingListener() {
    @Override
    public boolean isSuppressingError() {
        return true;
    }
});
```

> **Caution:**
>
> `Swc4jLibLoader.setLibLoadingListener()` must be called before swc4j is initialized, otherwise it won't take effect.

## Can swc4j Lib Loading Listener Take System Properties?

Yes. In some cases, it is inconvenient to inject a listener. No worry, `Swc4jLibLoadingListener` can take `swc4j.lib.loading.path`, `swc4j.lib.loading.type`, and `swc4j.lib.loading.suppress.error` system properties so that applications can inject custom lib loading mechanisms without implementing a new listener.

```shell
# Load the swc4j library from /abc with auto-deployment
java ... -Dswc4j.lib.loading.path=/abc

# Load the swc4j library from /abc without auto-deployment
java ... -Dswc4j.lib.loading.path=/abc -Dswc4j.lib.loading.type=custom

# Load the swc4j library from system library path
java ... -Dswc4j.lib.loading.type=system

# Suppress the error in loading the library
java ... -Dswc4j.lib.loading.suppress.error=true
```

> **Caution:**
>
> This doesn't apply to Android.

## Loading Types

swc4j supports three loading types via the `swc4j.lib.loading.type` system property:

### Default (default)

The native library is deployed from the JAR to a temporary directory and then loaded. This is the default behavior.

- **Auto-deployment:** Yes
- **Custom path:** Supported via `swc4j.lib.loading.path`
- **Use case:** Standard deployment, default behavior

```shell
java ... -Dswc4j.lib.loading.type=default -Dswc4j.lib.loading.path=/custom/temp
```

### Custom (custom)

The native library is loaded from a custom path without deployment. The application is responsible for ensuring the library exists at the specified location.

- **Auto-deployment:** No
- **Custom path:** Required via `swc4j.lib.loading.path`
- **Use case:** Pre-deployed libraries, containerized environments

```shell
java ... -Dswc4j.lib.loading.type=custom -Dswc4j.lib.loading.path=/opt/swc4j/lib
```

### System (system)

The native library is loaded from the system library path using `System.loadLibrary()`. The library must be installed in a location on the system's library path (e.g., `/usr/lib`, `LD_LIBRARY_PATH`).

- **Auto-deployment:** No
- **Custom path:** Not applicable (uses system path)
- **Use case:** System-wide installations, better performance, smaller JAR

```shell
java ... -Dswc4j.lib.loading.type=system
```

## Implementation Details

The loading mechanism is implemented through the `ISwc4jLibLoadingListener` interface, which provides four methods:

- `getLibPath()`: Returns the directory where the library should be deployed/loaded from
- `isDeploy()`: Whether to deploy the library from JAR to filesystem
- `isLibInSystemPath()`: Whether to use `System.loadLibrary()` instead of `System.load()`
- `isSuppressingError()`: Whether to suppress library loading errors

The default implementation `Swc4jLibLoadingListener` reads system properties to configure behavior, but you can provide your own implementation for full control.

## Examples

### Custom Listener for Docker Containers

```java
Swc4jLibLoader.setLibLoadingListener(new ISwc4jLibLoadingListener() {
    @Override
    public File getLibPath() {
        // Use a volume-mounted directory
        return new File("/app/native-libs");
    }

    @Override
    public boolean isDeploy() {
        // Don't deploy, library is already in the container
        return false;
    }
});
```

### System Property Configuration for Kubernetes

```yaml
env:
  - name: JAVA_TOOL_OPTIONS
    value: "-Dswc4j.lib.loading.type=custom -Dswc4j.lib.loading.path=/mnt/native-libs"
```

### Programmatic Configuration

```java
// Set custom listener before any swc4j usage
Swc4jLibLoader.setLibLoadingListener(new Swc4jLibLoadingListener() {
    @Override
    public File getLibPath() {
        return new File(System.getProperty("user.home"), ".swc4j");
    }
});

// Now use swc4j normally
Swc4j swc4j = new Swc4j();
```
