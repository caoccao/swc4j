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
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -a arm64
deno run --allow-all scripts/ts/copy_swc4j_lib.ts -d -a arm64
*/

import * as flags from "https://deno.land/std/flags/mod.ts"
import * as fs from "https://deno.land/std/fs/mod.ts"
import * as path from "https://deno.land/std/path/mod.ts"

const NAMES = ['swc4j', 'libswc4j']
const VERSION = '0.1.0'
const OS_AND_PREFIX_MAP = {
  '.dll': {
    prefix: 'lib',
    os: 'windows',
  },
  '.so': {
    prefix: '',
    os: 'linux',
  },
  '.dylib': {
    prefix: '',
    os: 'macos',
  },
}

async function copy(debug: boolean = false, arch: string = 'x86_64'): Promise<number> {
  const scriptDirPath = path.dirname(path.fromFileUrl(import.meta.url))
  const sourceDirPath = path.join(scriptDirPath, '../../rust/target', debug ? 'debug' : 'release')
  const targetDirPath = path.join(scriptDirPath, '../../src/main/resources')
  if (!fs.existsSync(targetDirPath)) {
    Deno.mkdirSync(targetDirPath, { recursive: true });
  }
  for await (const { isFile, name } of Deno.readDir(sourceDirPath)) {
    if (isFile) {
      const parsedName = path.parse(name)
      if (NAMES.includes(parsedName.name) && parsedName.ext in OS_AND_PREFIX_MAP) {
        const osAndPrefix = OS_AND_PREFIX_MAP[parsedName.ext]
        const sourceFilePath = path.join(sourceDirPath, name)
        const targetFilePath = path.join(targetDirPath, `${osAndPrefix.prefix}${parsedName.name}-${osAndPrefix.os}-${arch}.v.${VERSION}${parsedName.ext}`)
        console.info(`Copy from ${sourceFilePath} to ${targetFilePath}.`)
        fs.copySync(sourceFilePath, targetFilePath, { overwrite: true })
      }
    }
  }
  return 0
}

const args = flags.parse(Deno.args, {
  alias: {
    "arch": "a",
    "debug": "d",
    "help": "h",
    "version": "v",
  },
  boolean: [
    "debug",
    "help",
    "version",
  ],
  string: [
    "arch",
  ],
  default: {
    arch: "x86_64",
    debug: false,
    help: false,
    version: false,
  },
})

if (args.help) {
  console.info(`Usage: copy_swc4j_lib.ts
    -a, --arch      CPU arch (default: x86_64)
    -d, --debug     Copy the debug lib (default: false)
    -h, --help      Print this help page
    -v, --version   Print version
  `)
} else if (args.version) {
  console.info(`Version: ${VERSION}`)
} else {
  Deno.exit(await copy(args.debug, args.arch))
}
