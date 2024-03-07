/*
* Copyright (c) 2024. caoccao.com Sam Cao
* All rights reserved.

* Licensed under the Apache License, Version 2.0 (the "License")
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/*
Usage:
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -a x86_64
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -d -a x86_64
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -o macos -a arm64
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -d -o macos -a arm64
*/

import * as flags from "https://deno.land/std/flags/mod.ts"
import * as fs from "https://deno.land/std/fs/mod.ts"
import * as path from "https://deno.land/std/path/mod.ts"

const NAME = 'swc4j'
const VERSION = '0.1.0'
const OS_CONFIG_MAP = {
  'windows': {
    sourceName: NAME,
    targetName: `lib${NAME}`,
    ext: '.dll',
  },
  'linux': {
    sourceName: `lib${NAME}`,
    targetName: `lib${NAME}`,
    ext: '.so',
  },
  'macos': {
    sourceName: `lib${NAME}`,
    targetName: `lib${NAME}`,
    ext: '.dylib',
  },
}

function copy(debug: boolean = false, os: string = 'windows', arch: string = 'x86_64'): number {
  if (!(os in OS_CONFIG_MAP)) {
    console.error(`%c${os} is not supported.`, 'color: red')
    return 1;
  }
  const config = OS_CONFIG_MAP[os]
  const scriptDirPath = path.dirname(path.fromFileUrl(import.meta.url))
  const sourceDirPath = path.join(scriptDirPath, '../../rust/target', debug ? 'debug' : 'release')
  const sourceFilePath = path.join(sourceDirPath, `${config.sourceName}${config.ext}`)
  if (!fs.existsSync(sourceFilePath)) {
    console.error(`%c${sourceFilePath} is not found.`, 'color: red')
    return 1;
  }
  const targetDirPath = path.join(scriptDirPath, '../../src/main/resources')
  if (!fs.existsSync(targetDirPath)) {
    Deno.mkdirSync(targetDirPath, { recursive: true });
  }
  const targetFilePath = path.join(targetDirPath, `${config.targetName}-${os}-${arch}.v.${VERSION}${config.ext}`)
  console.info(`Copy from ${sourceFilePath} to ${targetFilePath}.`)
  fs.copySync(sourceFilePath, targetFilePath, { overwrite: true })
  return 0
}

const args = flags.parse(Deno.args, {
  alias: {
    "arch": "a",
    "debug": "d",
    "help": "h",
    "os": "o",
    "version": "v",
  },
  boolean: [
    "debug",
    "help",
    "version",
  ],
  string: [
    "arch",
    "os",
  ],
  default: {
    arch: "x86_64",
    debug: false,
    help: false,
    os: "windows",
    version: false,
  },
})

if (args.help) {
  console.info(`Usage: copy_swc4j_lib.ts
    -a, --arch      CPU arch [x86, x86_64, arm, arm64] (default: x86_64)
    -d, --debug     Copy the debug lib (default: false)
    -h, --help      Print this help page
    -o, --os        Operating system [windows, linux, macos] (default: windows)
    -v, --version   Print version
  `)
} else if (args.version) {
  console.info(`Version: ${VERSION}`)
} else {
  Deno.exit(copy(args.debug, args.os, args.arch))
}
