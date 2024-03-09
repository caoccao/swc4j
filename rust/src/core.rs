/*
* Copyright (c) 2024. caoccao.com Sam Cao
* All rights reserved.

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

use deno_ast::*;

use crate::{options, outputs};

const VERSION: &'static str = "0.1.0";

pub fn transpile<'local>(code: String, options: options::TranspileOptions) -> Result<outputs::TranspileOutput, String> {
  match parse_module(ParseParams {
    specifier: options.specifier,
    text_info: SourceTextInfo::from_string(code.to_string()),
    media_type: options.media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  }) {
    Ok(parsed_source) => match parsed_source.transpile(&EmitOptions::default()) {
      Ok(transpiled_js_code) => Ok(outputs::TranspileOutput {
        code: transpiled_js_code.text,
        module: parsed_source.is_module(),
        source_map: transpiled_js_code.source_map,
      }),
      Err(e) => Err(e.to_string()),
    },
    Err(e) => Err(e.to_string()),
  }
}

pub fn get_version<'local>() -> &'local str {
  VERSION
}
