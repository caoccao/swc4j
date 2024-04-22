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

use base64::Engine;

use deno_ast::swc::ast::Program;
use deno_ast::swc::codegen::{text_writer::JsWriter, Config, Emitter};
use deno_ast::swc::common::{sync::Lrc, FileName, FilePathMapping, SourceMap};
use deno_ast::*;

use crate::{enums, options, outputs};

const VERSION: &'static str = "0.5.0";

pub fn minify<'local>(code: String, options: options::MinifyOptions) -> Result<outputs::MinifyOutput, String> {
  let parse_params = ParseParams {
    specifier: options.get_specifier(),
    text_info: SourceTextInfo::from_string(code.to_owned()),
    media_type: options.media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  };
  let result = match options.parse_mode {
    enums::ParseMode::Script => parse_script(parse_params),
    _ => parse_module(parse_params),
  };
  match result {
    Ok(parsed_source) => {
      let source_map = Lrc::new(SourceMap::new(FilePathMapping::empty()));
      source_map.new_source_file(FileName::Url(options.get_specifier()), code.to_owned());
      let mut minified_buffer = vec![];
      let mut minified_source_map_buffer = vec![];
      let mut writer = Box::new(JsWriter::new(
        source_map.clone(),
        "\n",
        &mut minified_buffer,
        Some(&mut minified_source_map_buffer),
      ));
      writer.set_indent_str("  "); // two spaces
      let config = Config::default()
        .with_minify(true)
        .with_ascii_only(options.ascii_only)
        .with_omit_last_semi(options.omit_last_semi)
        .with_target(options.target)
        .with_emit_assert_for_import_attributes(options.emit_assert_for_import_attributes);
      let mut emitter = Emitter {
        cfg: config,
        comments: None,
        cm: source_map.clone(),
        wr: writer,
      };
      let result = match parsed_source.program_ref() {
        Program::Module(module) => emitter.emit_module(module),
        Program::Script(script) => emitter.emit_script(script),
      };
      match result {
        Ok(_) => match String::from_utf8(minified_buffer.to_vec()) {
          Ok(mut code) => {
            let source_map: Option<String> = if options.source_map != SourceMapOption::None {
              let mut buffer = Vec::new();
              let source_map_config = SourceMapConfig {
                inline_sources: options.inline_sources,
              };
              match source_map
                .build_source_map_with_config(&minified_source_map_buffer, None, source_map_config)
                .to_writer(&mut buffer)
              {
                Ok(_) => {
                  if options.source_map == SourceMapOption::Inline {
                    if !code.ends_with("\n") {
                      code.push_str("\n");
                    }
                    code.push_str("//# sourceMappingURL=data:application/json;base64,");
                    base64::prelude::BASE64_STANDARD.encode_string(buffer, &mut code);
                    None
                  } else {
                    match String::from_utf8(buffer) {
                      Ok(source_map) => Some(source_map),
                      Err(_) => None,
                    }
                  }
                }
                Err(_) => None,
              }
            } else {
              None
            };
            Ok(outputs::MinifyOutput::new(&parsed_source, code, source_map))
          }
          Err(e) => Err(e.to_string()),
        },
        Err(e) => Err(e.to_string()),
      }
    }
    Err(e) => Err(e.to_string()),
  }
}

pub fn parse<'local>(code: String, options: options::ParseOptions) -> Result<outputs::ParseOutput, String> {
  let parse_params = ParseParams {
    specifier: options.get_specifier(),
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
    specifier: options.get_specifier(),
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
      match parsed_source.clone().transpile(&transpile_options, &emit_options) {
        Ok(transpile_result) => Ok(outputs::TranspileOutput::new(
          &options,
          &parsed_source,
          &transpile_result,
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
