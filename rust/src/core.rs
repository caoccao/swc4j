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
    text_info: SourceTextInfo::from_string(code),
    media_type: options.media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  }) {
    Ok(parsed_source) => {
      let emit_options = EmitOptions {
        emit_metadata: options.emit_metadata,
        // imports_not_used_as_values: options.imports_not_used_as_values,
        inline_source_map: options.inline_source_map,
        inline_sources: options.inline_sources,
        jsx_automatic: options.jsx_automatic,
        jsx_development: options.jsx_development,
        jsx_factory: options.jsx_factory,
        jsx_fragment_factory: options.jsx_fragment_factory,
        jsx_import_source: options.jsx_import_source,
        precompile_jsx: options.precompile_jsx,
        source_map: options.source_map,
        transform_jsx: options.transform_jsx,
        var_decl_imports: options.var_decl_imports,
        ..Default::default()
      };
      match parsed_source.transpile(&emit_options) {
        Ok(transpiled_js_code) => Ok(outputs::TranspileOutput {
          code: transpiled_js_code.text,
          module: parsed_source.is_module(),
          source_map: transpiled_js_code.source_map,
        }),
        Err(e) => Err(e.to_string()),
      }
    }
    Err(e) => Err(e.to_string()),
  }
}

pub fn get_version<'local>() -> &'local str {
  VERSION
}
