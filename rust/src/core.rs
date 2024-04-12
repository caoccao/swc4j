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

use crate::{enums, options, outputs};

const VERSION: &'static str = "0.4.0";

pub fn parse<'local>(code: String, options: options::ParseOptions) -> Result<outputs::ParseOutput, String> {
  let parse_params = ParseParams {
    specifier: ModuleSpecifier::parse(&options.specifier).unwrap(),
    text_info: SourceTextInfo::from_string(code),
    media_type: options.media_type,
    capture_tokens: options.capture_tokens,
    maybe_syntax: None,
    scope_analysis: options.scope_analysis,
  };
  let result = match options.parse_mode {
    enums::ParseMode::Script => parse_script(parse_params),
    _ => parse_module(parse_params),
  };
  match result {
    Ok(parsed_source) => Ok(outputs::ParseOutput::new(&options, &parsed_source)),
    Err(e) => Err(e.to_string()),
  }
}

pub fn transpile<'local>(code: String, options: options::TranspileOptions) -> Result<outputs::TranspileOutput, String> {
  let parse_params = ParseParams {
    specifier: ModuleSpecifier::parse(&options.specifier).unwrap(),
    text_info: SourceTextInfo::from_string(code),
    media_type: options.media_type,
    capture_tokens: options.capture_tokens,
    maybe_syntax: None,
    scope_analysis: options.scope_analysis,
  };
  let result = match options.parse_mode {
    enums::ParseMode::Script => parse_script(parse_params),
    _ => parse_module(parse_params),
  };
  match result {
    Ok(parsed_source) => {
      let transpile_options = TranspileOptions {
        emit_metadata: options.emit_metadata,
        imports_not_used_as_values: options.imports_not_used_as_values.to_owned(),
        jsx_automatic: options.jsx_automatic,
        jsx_development: options.jsx_development,
        jsx_factory: options.jsx_factory.to_owned(),
        jsx_fragment_factory: options.jsx_fragment_factory.to_owned(),
        jsx_import_source: options.jsx_import_source.to_owned(),
        precompile_jsx: options.precompile_jsx,
        transform_jsx: options.transform_jsx,
        var_decl_imports: options.var_decl_imports,
        use_decorators_proposal: options.use_decorators_proposal,
        use_ts_decorators: options.use_ts_decorators,
      };
      let emit_options = EmitOptions {
        inline_sources: options.inline_sources,
        keep_comments: options.keep_comments,
        source_map: options.source_map,
      };
      match parsed_source.transpile(&transpile_options, &emit_options) {
        Ok(emitted_source) => Ok(outputs::TranspileOutput::new(
          &options,
          &parsed_source,
          &emitted_source,
        )),
        Err(e) => Err(e.to_string()),
      }
    }
    Err(e) => Err(e.to_string()),
  }
}

pub fn get_version<'local>() -> &'local str {
  VERSION
}
