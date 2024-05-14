/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.options;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.visitors.IJavetSanitizerVisitor;
import com.caoccao.javet.sanitizer.visitors.JavetSanitizerVisitor;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.options.Swc4jOptions;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import com.caoccao.javet.swc4j.utils.SimpleSet;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

/**
 * The type Javet sanitizer option.
 *
 * @since 0.7.0
 */
public class JavetSanitizerOptions {
    /**
     * Default name of global identifier.
     *
     * @since 0.7.0
     */
    public static final String DEFAULT_GLOBAL_IDENTIFIER = "globalThis";

    /**
     * Default reserved function identifier set.
     *
     * @since 0.7.0
     */
    public static final Set<String> DEFAULT_RESERVED_FUNCTION_IDENTIFIER_SET = SimpleSet.immutableOf("main");
    /**
     * Default reserved identifier set.
     *
     * @since 0.7.0
     */
    public static final Set<String> DEFAULT_RESERVED_IDENTIFIER_SET = SimpleSet.immutableOf();
    /**
     * Default reserved mutable identifier set.
     *
     * @since 0.7.0
     */
    public static final Set<String> DEFAULT_RESERVED_MUTABLE_IDENTIFIER_SET = SimpleSet.immutableOf();
    /**
     * The Built-in object set.
     *
     * <pre>
     * Object.getOwnPropertyNames(global)
     *   .concat(['Decimal'])
     *   .sort()
     *   .forEach(o =&gt; console.log(o))
     * </pre>
     *
     * @since 0.7.0
     */
    public static final Set<String> DEFAULT_BUILT_IN_OBJECT_SET = SimpleSet.immutableOf(
            "AbortController", "AbortSignal", "AggregateError", "Array", "ArrayBuffer", "Atomics",
            "BigInt", "BigInt64Array", "BigUint64Array", "Boolean", "Buffer",
            "DataView", "Date", "Decimal",
            "Error", "EvalError", "Event", "EventTarget",
            "FinalizationRegistry", "Float32Array", "Float64Array", "Function",
            "Infinity", "Int16Array", "Int32Array", "Int8Array", "Intl",
            "JSON",
            "Map", "Math", "MessageChannel", "MessageEvent", "MessagePort",
            "NaN", "Number",
            "Object",
            "Promise", "Proxy",
            "RangeError", "ReferenceError", "Reflect", "RegExp",
            "Set", "SharedArrayBuffer", "String", "Symbol", "SyntaxError",
            "TextDecoder", "TextEncoder", "TypeError",
            "URIError", "URL", "URLSearchParams", "Uint16Array", "Uint32Array", "Uint8Array", "Uint8ClampedArray",
            "WeakMap", "WeakRef", "WeakSet", "WebAssembly",
            "_", "_error",
            "assert",
            "async_hooks", "atob",
            "btoa", "buffer",
            "child_process", "clearImmediate", "clearInterval", "clearTimeout", "cluster", "console", "constants", "crypto",
            "decodeURI", "decodeURIComponent", "dgram", "diagnostics_channel", "dns", "domain",
            "encodeURI", "encodeURIComponent", "escape", "eval", "events",
            "fs",
            "global", "globalThis",
            "http", "http2", "https",
            "inspector", "isFinite", "isNaN",
            "module",
            "net",
            "os",
            "parseFloat", "parseInt", "path", "perf_hooks", "performance", "process",
            "punycode",
            "querystring", "queueMicrotask",
            "readline", "repl", "require",
            "setImmediate", "setInterval", "setTimeout", "stream", "string_decoder", "sys",
            "timers", "tls", "trace_events", "tty",
            "undefined", "unescape", "url", "util",
            "v8", "vm",
            "wasi", "worker_threads",
            "zlib");
    /**
     * The Disallowed identifier set.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects">JS Global Objects</a>
     *
     * @since 0.7.0
     */
    public static final Set<String> DEFAULT_DISALLOWED_IDENTIFIER_SET = SimpleSet.immutableOf(
            "__proto__",
            "apply", "AsyncFunction", "AsyncGenerator", "AsyncGeneratorFunction",
            "bind",
            "call", "clearInterval", "clearTimeout",
            "defineProperty", "defineProperties",
            "eval",
            "Function",
            "global", "globalThis", "getPrototypeOf",
            "Generator", "GeneratorFunction",
            "Intl",
            "prototype", "Proxy",
            "Promise",
            "require",
            "Reflect",
            "setImmediate", "setInterval", "setTimeout", "setPrototypeOf", "Symbol",
            "uneval",
            "XMLHttpRequest",
            "WebAssembly", "window");
    /**
     * Default media type is JavaScript.
     *
     * @since 0.7.0
     */
    public static final Swc4jMediaType DEFAULT_MEDIA_TYPE = Swc4jMediaType.JavaScript;
    /**
     * Default parse mode is Program.
     *
     * @since 0.7.0
     */
    public static final Swc4jParseMode DEFAULT_PARSE_MODE = Swc4jParseMode.Program;
    /**
     * The default to be deleted objects from V8.
     *
     * @since 0.7.0
     */
    public static final List<String> DEFAULT_TO_BE_DELETED_OBJECT_LIST = SimpleList.immutableOf(
            "eval",
            "Function",
            "WebAssembly");
    /**
     * The default to be frozen objects from V8.
     *
     * @since 0.7.0
     */
    public static final List<String> DEFAULT_TO_BE_FROZEN_OBJECT_LIST = SimpleList.immutableOf(
            "Object", // Object must be the first.
            "AggregateError",
            "Array",
            "ArrayBuffer",
            "Atomics",
            "BigInt",
            "BigInt64Array",
            "BigUint64Array",
            "Boolean",
            "DataView",
            "Date",
            "decodeURI",
            "decodeURIComponent",
            "encodeURI",
            "encodeURIComponent",
            "Error",
            "escape",
            "EvalError",
            "FinalizationRegistry",
            "Float32Array",
            "Float64Array",
            "Int8Array",
            "Int16Array",
            "Int32Array",
            "isFinite",
            "isNaN",
            "JSON",
            "Map",
            "Math",
            "Number",
            "parseFloat",
            "parseInt",
            "Promise",
            "Proxy",
            "RangeError",
            "ReferenceError",
            "Reflect",
            "RegExp",
            "Set",
            "SharedArrayBuffer",
            "String",
            "Symbol",
            "SyntaxError",
            "TypeError",
            "Uint8Array",
            "Uint8ClampedArray",
            "Uint16Array",
            "Uint32Array",
            "unescape",
            "URIError",
            "WeakMap",
            "WeakRef",
            "WeakSet");
    /**
     * Default option is the most strict and secure option.
     * Most built-in objects and keywords are disabled.
     *
     * @since 0.7.0
     */
    public static final JavetSanitizerOptions Default = new JavetSanitizerOptions("Default").seal();
    private Map<String, Object> argumentMap;
    private Set<String> builtInObjectSet;
    private Set<String> disallowedIdentifierSet;
    private String globalIdentifier;
    private boolean keywordAsyncEnabled;
    private boolean keywordAwaitEnabled;
    private boolean keywordDebuggerEnabled;
    private boolean keywordExportEnabled;
    private boolean keywordImportEnabled;
    private boolean keywordVarEnabled;
    private boolean keywordWithEnabled;
    private boolean keywordYieldEnabled;
    private Swc4jMediaType mediaType;
    private String name;
    private Swc4jParseMode parseMode;
    private Set<String> reservedFunctionIdentifierSet;
    private Function<String, Boolean> reservedIdentifierMatcher;
    private Set<String> reservedIdentifierSet;
    private Set<String> reservedMutableIdentifierSet;
    private boolean sealed;
    private boolean shebangEnabled;
    private URL specifier;
    private List<String> toBeDeletedIdentifierList;
    private List<String> toBeFrozenIdentifierList;
    private IJavetSanitizerVisitor visitor;
    private Function<JavetSanitizerOptions, IJavetSanitizerVisitor> visitorConstructor;

    /**
     * Instantiates a new Javet sanitizer option.
     *
     * @param name the name
     */
    public JavetSanitizerOptions(String name) {
        argumentMap = new HashMap<>();
        builtInObjectSet = new HashSet<>(DEFAULT_BUILT_IN_OBJECT_SET);
        disallowedIdentifierSet = new HashSet<>(DEFAULT_DISALLOWED_IDENTIFIER_SET);
        globalIdentifier = DEFAULT_GLOBAL_IDENTIFIER;
        keywordAsyncEnabled = false;
        keywordAwaitEnabled = false;
        keywordDebuggerEnabled = false;
        keywordExportEnabled = false;
        keywordImportEnabled = false;
        keywordVarEnabled = false;
        keywordWithEnabled = false;
        keywordYieldEnabled = false;
        mediaType = DEFAULT_MEDIA_TYPE;
        this.name = AssertionUtils.notNull(name, "Name");
        parseMode = DEFAULT_PARSE_MODE;
        reservedFunctionIdentifierSet = new HashSet<>(DEFAULT_RESERVED_FUNCTION_IDENTIFIER_SET);
        reservedIdentifierMatcher = identifier -> false;
        reservedIdentifierSet = new HashSet<>(DEFAULT_RESERVED_IDENTIFIER_SET);
        reservedMutableIdentifierSet = new HashSet<>(DEFAULT_RESERVED_MUTABLE_IDENTIFIER_SET);
        shebangEnabled = false;
        specifier = Swc4jOptions.DEFAULT_SPECIFIER;
        toBeDeletedIdentifierList = new ArrayList<>(DEFAULT_TO_BE_DELETED_OBJECT_LIST);
        toBeFrozenIdentifierList = new ArrayList<>(DEFAULT_TO_BE_FROZEN_OBJECT_LIST);
        sealed = false;
        visitor = null;
        visitorConstructor = JavetSanitizerVisitor::new;
    }

    /**
     * Gets argument map.
     *
     * @return the argument map
     * @since 0.7.0
     */
    public Map<String, Object> getArgumentMap() {
        return argumentMap;
    }

    /**
     * Gets built-in object set.
     *
     * @return the built-in object set
     * @since 0.7.0
     */
    public Set<String> getBuiltInObjectSet() {
        return builtInObjectSet;
    }

    /**
     * Gets disallowed identifier set.
     *
     * @return the disallowed identifier set
     * @since 0.7.0
     */
    public Set<String> getDisallowedIdentifierSet() {
        return disallowedIdentifierSet;
    }

    /**
     * Gets global identifier.
     *
     * @return the global identifier
     * @since 0.7.0
     */
    public String getGlobalIdentifier() {
        return globalIdentifier;
    }

    /**
     * Gets media type.
     *
     * @return the media type
     * @since 0.7.0
     */
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.7.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets parse mode.
     *
     * @return the parse mode
     * @since 0.7.0
     */
    public Swc4jParseMode getParseMode() {
        return parseMode;
    }

    /**
     * Gets reserved function identifier set.
     *
     * @return the reserved function identifier set
     * @since 0.7.0
     */
    public Set<String> getReservedFunctionIdentifierSet() {
        return reservedFunctionIdentifierSet;
    }

    /**
     * Gets reserved identifier matcher.
     *
     * @return the reserved identifier matcher
     * @since 0.7.0
     */
    public Function<String, Boolean> getReservedIdentifierMatcher() {
        return reservedIdentifierMatcher;
    }

    /**
     * Gets reserved identifier set.
     *
     * @return the reserved identifier set
     * @since 0.7.0
     */
    public Set<String> getReservedIdentifierSet() {
        return reservedIdentifierSet;
    }

    /**
     * Gets reserved mutable identifier set.
     *
     * @return the reserved mutable identifier set
     * @since 0.7.0
     */
    public Set<String> getReservedMutableIdentifierSet() {
        return reservedMutableIdentifierSet;
    }

    /**
     * Gets specifier.
     *
     * @return the specifier
     * @since 0.7.0
     */
    public URL getSpecifier() {
        return specifier;
    }

    /**
     * Gets to be deleted identifier list.
     *
     * @return the to be deleted identifier list
     * @since 0.7.0
     */
    public List<String> getToBeDeletedIdentifierList() {
        return toBeDeletedIdentifierList;
    }

    /**
     * Gets to be frozen identifier list.
     *
     * @return the to be frozen identifier list
     * @since 0.7.0
     */
    public List<String> getToBeFrozenIdentifierList() {
        return toBeFrozenIdentifierList;
    }

    /**
     * Gets visitor.
     *
     * @return the visitor
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    public IJavetSanitizerVisitor getVisitor() throws JavetSanitizerException {
        if (visitor == null) {
            try {
                visitor = visitorConstructor.apply(this);
            } catch (Throwable t) {
                throw JavetSanitizerException.visitorNotFound(visitorConstructor.toString(), t);
            }
        }
        return visitor;
    }

    /**
     * Is keyword async enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordAsyncEnabled() {
        return keywordAsyncEnabled;
    }

    /**
     * Is keyword await enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordAwaitEnabled() {
        return keywordAwaitEnabled;
    }

    /**
     * Is keyword debugger enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordDebuggerEnabled() {
        return keywordDebuggerEnabled;
    }

    /**
     * Is keyword export enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordExportEnabled() {
        return keywordExportEnabled;
    }

    /**
     * Is keyword import enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordImportEnabled() {
        return keywordImportEnabled;
    }

    /**
     * Is keyword var enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordVarEnabled() {
        return keywordVarEnabled;
    }

    /**
     * Is keyword with enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordWithEnabled() {
        return keywordWithEnabled;
    }

    /**
     * Is keyword yield enabled.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isKeywordYieldEnabled() {
        return keywordYieldEnabled;
    }

    /**
     * Is sealed.
     *
     * @return true : yes, false: no
     * @since 0.7.0
     */
    public boolean isSealed() {
        return sealed;
    }

    /**
     * Is shebang enabled.
     *
     * @return true : yes, false: no
     * @since 0.8.0
     */
    public boolean isShebangEnabled() {
        return shebangEnabled;
    }

    /**
     * Seal javet sanitizer option. After it is sealed, it will be immutable.
     *
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions seal() {
        argumentMap = SimpleMap.immutable(argumentMap);
        builtInObjectSet = SimpleSet.immutable(builtInObjectSet);
        disallowedIdentifierSet = SimpleSet.immutable(disallowedIdentifierSet);
        reservedFunctionIdentifierSet = SimpleSet.immutable(reservedFunctionIdentifierSet);
        reservedIdentifierSet = SimpleSet.immutable(reservedIdentifierSet);
        reservedMutableIdentifierSet = SimpleSet.immutable(reservedMutableIdentifierSet);
        toBeDeletedIdentifierList = SimpleList.immutable(toBeDeletedIdentifierList);
        toBeFrozenIdentifierList = SimpleList.immutable(toBeFrozenIdentifierList);
        sealed = true;
        return this;
    }

    /**
     * Sets global identifier.
     *
     * @param globalIdentifier the global identifier
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setGlobalIdentifier(String globalIdentifier) {
        if (!sealed) {
            this.globalIdentifier = globalIdentifier;
        }
        return this;
    }

    /**
     * Sets keyword async enabled.
     *
     * @param keywordAsyncEnabled the keyword async enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordAsyncEnabled(boolean keywordAsyncEnabled) {
        if (!sealed) {
            this.keywordAsyncEnabled = keywordAsyncEnabled;
        }
        return this;
    }

    /**
     * Sets keyword await enabled.
     *
     * @param keywordAwaitEnabled the keyword await enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordAwaitEnabled(boolean keywordAwaitEnabled) {
        if (!sealed) {
            this.keywordAwaitEnabled = keywordAwaitEnabled;
        }
        return this;
    }

    /**
     * Sets keyword debugger enabled.
     *
     * @param keywordDebuggerEnabled the keyword debugger enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordDebuggerEnabled(boolean keywordDebuggerEnabled) {
        if (!sealed) {
            this.keywordDebuggerEnabled = keywordDebuggerEnabled;
        }
        return this;
    }

    /**
     * Sets keyword export enabled.
     *
     * @param keywordExportEnabled the keyword export enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordExportEnabled(boolean keywordExportEnabled) {
        if (!sealed) {
            this.keywordExportEnabled = keywordExportEnabled;
        }
        return this;
    }

    /**
     * Sets keyword import enabled.
     *
     * @param keywordImportEnabled the keyword import enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordImportEnabled(boolean keywordImportEnabled) {
        if (!sealed) {
            this.keywordImportEnabled = keywordImportEnabled;
        }
        return this;
    }

    /**
     * Sets keyword var enabled.
     *
     * @param keywordVarEnabled the keyword var enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordVarEnabled(boolean keywordVarEnabled) {
        if (!sealed) {
            this.keywordVarEnabled = keywordVarEnabled;
        }
        return this;
    }

    /**
     * Sets keyword with enabled.
     *
     * @param keywordWithEnabled the keyword with enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordWithEnabled(boolean keywordWithEnabled) {
        if (!sealed) {
            this.keywordWithEnabled = keywordWithEnabled;
        }
        return this;
    }

    /**
     * Sets keyword yield enabled.
     *
     * @param keywordYieldEnabled the keyword yield enabled
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setKeywordYieldEnabled(boolean keywordYieldEnabled) {
        if (!sealed) {
            this.keywordYieldEnabled = keywordYieldEnabled;
        }
        return this;
    }

    /**
     * Sets media type.
     *
     * @param mediaType the media type
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setMediaType(Swc4jMediaType mediaType) {
        if (!sealed) {
            this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        }
        return this;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setName(String name) {
        if (!sealed) {
            this.name = AssertionUtils.notNull(name, "Name");
        }
        return this;
    }

    /**
     * Sets parse mode.
     *
     * @param parseMode the parse mode
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setParseMode(Swc4jParseMode parseMode) {
        if (!sealed) {
            this.parseMode = AssertionUtils.notNull(parseMode, "Parse mode");
        }
        return this;
    }

    /**
     * Sets reserved identifier matcher.
     *
     * @param reservedIdentifierMatcher the reserved identifier matcher
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setReservedIdentifierMatcher(Function<String, Boolean> reservedIdentifierMatcher) {
        if (!sealed) {
            this.reservedIdentifierMatcher = AssertionUtils.notNull(reservedIdentifierMatcher, "Reserved identifier matcher");
        }
        return this;
    }

    /**
     * Sets shebang enabled.
     *
     * @param shebangEnabled the shebang enabled
     * @since 0.8.0
     */
    public JavetSanitizerOptions setShebangEnabled(boolean shebangEnabled) {
        if (!sealed) {
            this.shebangEnabled = shebangEnabled;
        }
        return this;
    }

    /**
     * Sets specifier.
     *
     * @param specifier the specifier
     * @return the specifier
     * @since 0.7.0
     */
    public JavetSanitizerOptions setSpecifier(URL specifier) {
        if (!sealed) {
            this.specifier = AssertionUtils.notNull(specifier, "Specifier");
        }
        return this;
    }

    /**
     * Sets visitor constructor.
     *
     * @param visitorConstructor the visitor constructor
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerOptions setVisitorConstructor(
            Function<JavetSanitizerOptions, IJavetSanitizerVisitor> visitorConstructor) {
        if (!sealed) {
            this.visitorConstructor = AssertionUtils.notNull(visitorConstructor, "Visitor constructor");
        }
        return this;
    }

    /**
     * To clone javet sanitizer option.
     *
     * @return the new javet sanitizer option
     * @since 0.7.0
     */
    public JavetSanitizerOptions toClone() {
        JavetSanitizerOptions options = new JavetSanitizerOptions(name);
        options.argumentMap.clear();
        options.argumentMap.putAll(argumentMap);
        options.builtInObjectSet.clear();
        options.builtInObjectSet.addAll(builtInObjectSet);
        options.disallowedIdentifierSet.clear();
        options.disallowedIdentifierSet.addAll(disallowedIdentifierSet);
        options.globalIdentifier = globalIdentifier;
        options.keywordAsyncEnabled = keywordAsyncEnabled;
        options.keywordAwaitEnabled = keywordAwaitEnabled;
        options.keywordDebuggerEnabled = keywordDebuggerEnabled;
        options.keywordExportEnabled = keywordExportEnabled;
        options.keywordImportEnabled = keywordImportEnabled;
        options.keywordWithEnabled = keywordWithEnabled;
        options.keywordYieldEnabled = keywordYieldEnabled;
        options.mediaType = mediaType;
        options.parseMode = parseMode;
        options.reservedFunctionIdentifierSet.clear();
        options.reservedFunctionIdentifierSet.addAll(reservedFunctionIdentifierSet);
        options.reservedIdentifierMatcher = reservedIdentifierMatcher;
        options.reservedIdentifierSet.clear();
        options.reservedIdentifierSet.addAll(reservedIdentifierSet);
        options.reservedMutableIdentifierSet.clear();
        options.reservedMutableIdentifierSet.addAll(reservedMutableIdentifierSet);
        options.shebangEnabled = shebangEnabled;
        options.specifier = specifier;
        options.toBeDeletedIdentifierList.clear();
        options.toBeDeletedIdentifierList.addAll(toBeDeletedIdentifierList);
        options.toBeFrozenIdentifierList.clear();
        options.toBeFrozenIdentifierList.addAll(toBeFrozenIdentifierList);
        options.visitorConstructor = visitorConstructor;
        return options;
    }
}
