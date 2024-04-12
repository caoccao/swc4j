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

import * as fs from "https://deno.land/std/fs/mod.ts"
import * as path from "https://deno.land/std/path/mod.ts"

class ChangeSwc4jVersion {
  private productionVersion: string
  private rootDirPath: string
  private snapshotVersion: string

  constructor(productionVersion: string, snapshotVersion: string) {
    this.productionVersion = productionVersion
    this.snapshotVersion = snapshotVersion
    this.rootDirPath = path.join(path.dirname(path.fromFileUrl(import.meta.url)), '../../')
  }

  private _change(filePath: string, patterns: Array<RegExp>, isSnapshot = true) {
    const sourceFilePath = path.join(this.rootDirPath, filePath)
    if (fs.existsSync(sourceFilePath)) {
      console.info(`Processing ${sourceFilePath}.`)
    } else {
      console.error(`%c${sourceFilePath} is not found.`, 'color: red')
      return
    }
    const newVersion = isSnapshot ? this.snapshotVersion : this.productionVersion
    const positionGroups: Array<{ start: number, end: number }> = []
    const currentContent = Deno.readTextFileSync(sourceFilePath)
    patterns.map(pattern => {
      [...currentContent.matchAll(pattern)].map(match => {
        const matchedString = match[0]
        const currentVersion = match.groups['version']
        if (newVersion === currentVersion) {
          console.warn(`%c  Ignored ${matchedString}`, 'color: yellow')
        } else {
          console.info(`  ${matchedString} => ${newVersion}`)
          const start = match.index + matchedString.indexOf(currentVersion)
          const end = start + currentVersion.length
          positionGroups.push({ start: start, end: end })
        }
      })
    })
    if (positionGroups.length > 0) {
      let newContent = ''
      let lastEnd = 0
      positionGroups.sort((pg1, pg2) => pg1.start - pg2.start)
      positionGroups.map(positionGroup => {
        if (positionGroup.start > lastEnd) {
          newContent += currentContent.substring(lastEnd, positionGroup.start)
        }
        newContent += newVersion
        lastEnd = positionGroup.end
      })
      if (lastEnd < currentContent.length) {
        newContent += currentContent.substring(lastEnd, currentContent.length)
      }
      if (newContent !== currentContent) {
        Deno.writeTextFileSync(sourceFilePath, newContent)
      }
    }
  }

  change() {
    this._change('README.md', [
      /swc4j:(?<version>\d+\.\d+\.\d+)/ig,
      /<version>(?<version>\d+\.\d+\.\d+)<\/version>/ig,
    ], false)
    this._change('build.gradle.kts', [
      /SWC4J = "(?<version>\d+\.\d+\.\d+)"/ig,
    ])
    this._change('.github/workflows/swc4j_build.yml', [
      /SWC4J_VERSION:\s*(?<version>\d+\.\d+\.\d+)/ig,
    ])
    this._change('rust/Cargo.toml', [
      /name = "swc4j"\nversion = "(?<version>\d+\.\d+\.\d+)"/ig,
    ])
    this._change('rust/Cargo.lock', [
      /name = "swc4j"\nversion = "(?<version>\d+\.\d+\.\d+)"/ig,
    ])
    this._change('rust/src/core.rs', [
      /str = "(?<version>\d+\.\d+\.\d+)";/ig,
    ])
    this._change('rust/tests/test_core.rs', [
      /, "(?<version>\d+\.\d+\.\d+)"\)/ig,
    ])
    this._change('scripts/ts/copy_swc4j_lib.ts', [
      /VERSION = '(?<version>\d+\.\d+\.\d+)'/ig,
    ])
    this._change('src/main/java/com/caoccao/javet/swc4j/Swc4jLibLoader.java', [
      /LIB_VERSION = "(?<version>\d+\.\d+\.\d+)"/ig,
    ])
    this._change('src/test/java/com/caoccao/javet/swc4j/TestSwc4j.java', [
      /assertEquals\("(?<version>\d+\.\d+\.\d+)"/ig,
    ])
    this._change('src/test/java/com/caoccao/javet/swc4j/TestSwc4jNative.java', [
      /assertEquals\("(?<version>\d+\.\d+\.\d+)"/ig,
    ])
  }
}

const changeSwc4jVersion = new ChangeSwc4jVersion('0.3.0', '0.3.0');
changeSwc4jVersion.change()
