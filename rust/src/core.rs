/*
* Copyright (c) 2024-2025. caoccao.com Sam Cao
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

use anyhow::{Error, Result};
use base64::Engine;
use deno_ast::swc::ast::{Module, Program, Script};
use deno_ast::swc::codegen::{text_writer::JsWriter, Config, Emitter};
use deno_ast::swc::common::{sync::Lrc, FileName, FilePathMapping, SourceMap};
use deno_ast::*;
use swc::common::util::take::Take;

use crate::{enums, options, outputs, plugin_utils};

const VERSION: &'static str = "1.6.0";

fn parse_by_mode(
  parse_params: ParseParams,
  parse_mode: enums::ParseMode,
  plugin_host: &mut Option<plugin_utils::PluginHost>,
) -> Result<ParsedSource> {
  log::debug!("parse_by_mode({:?})", parse_mode);
  let result = if let Some(plugin_host) = plugin_host {
    let code: &str = &parse_params.text.to_owned();
    let mut error: Option<Error> = None;
    let result = match parse_mode {
      enums::ParseMode::Module => parse_module_with_post_process(parse_params, |module, _| {
        match plugin_host.process_module(code, module) {
          Ok(module) => module,
          Err(err) => {
            error = Some(err);
            Module::dummy()
          }
        }
      }),
      enums::ParseMode::Script => parse_script_with_post_process(parse_params, |script, _| {
        match plugin_host.process_script(code, script) {
          Ok(script) => script,
          Err(err) => {
            error = Some(err);
            Script::dummy()
          }
        }
      }),
      _ => parse_program_with_post_process(parse_params, |program, _| {
        match plugin_host.process_program(code, program) {
          Ok(program) => program,
          Err(err) => {
            error = Some(err);
            Program::Script(Script::dummy())
          }
        }
      }),
    };
    if let Some(error) = error {
      return Err(error);
    } else {
      result
    }
  } else {
    match parse_mode {
      enums::ParseMode::Module => parse_module(parse_params),
      enums::ParseMode::Script => parse_script(parse_params),
      _ => parse_program(parse_params),
    }
  };
  result.map_err(Error::msg)
}

pub fn parse<'local>(code: String, options: options::ParseOptions) -> Result<outputs::ParseOutput> {
  log::debug!("parse()");
  log::debug!("{:?}", options);
  let specifier = options.get_specifier()?;
  let parse_params = ParseParams {
    specifier,
    text: code.into(),
    media_type: options.media_type,
    capture_tokens: options.capture_tokens,
    maybe_syntax: None,
    scope_analysis: options.scope_analysis,
  };
  let mut plugin_host = options.plugin_host.clone();
  let parsed_source = parse_by_mode(parse_params, options.parse_mode, &mut plugin_host)?;
  Ok(outputs::ParseOutput::new(&options, &parsed_source))
}

pub fn transform<'local>(code: String, options: options::TransformOptions) -> Result<outputs::TransformOutput> {
  log::debug!("transform()");
  log::debug!("{:?}", options);
  let specifier = options.get_specifier()?;
  let parse_params = ParseParams {
    specifier: specifier.clone(),
    text: code.into(),
    media_type: options.media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  };
  let mut plugin_host = options.plugin_host.clone();
  let code: String = parse_params.text.clone().to_string();
  let parsed_source = parse_by_mode(parse_params, options.parse_mode, &mut plugin_host)?;
  let source_map = Lrc::new(SourceMap::new(FilePathMapping::empty()));
  let filename = Lrc::new(FileName::Url(specifier));
  source_map.new_source_file(filename, code);
  let mut buffer = vec![];
  let mut source_map_buffer = vec![];
  let mut writer = Box::new(JsWriter::new(
    source_map.clone(),
    "\n",
    &mut buffer,
    Some(&mut source_map_buffer),
  ));
  writer.set_indent_str("  "); // two spaces
  let config = Config::default()
    .with_minify(options.minify)
    .with_ascii_only(options.ascii_only)
    .with_omit_last_semi(options.omit_last_semi)
    .with_target(options.target)
    .with_emit_assert_for_import_attributes(options.emit_assert_for_import_attributes);
  let swc_comments = parsed_source.comments().as_single_threaded();
  let mut emitter = Emitter {
    cfg: config,
    comments: if options.keep_comments {
      Some(&swc_comments)
    } else {
      None
    },
    cm: source_map.clone(),
    wr: writer,
  };
  match parsed_source.program_ref() {
    ProgramRef::Module(module) => emitter.emit_module(module)?,
    ProgramRef::Script(script) => emitter.emit_script(script)?,
  };
  let mut code = String::from_utf8(buffer.to_vec()).map_err(Error::msg)?;
  if options.omit_last_semi && code.ends_with(";") {
    code.truncate(code.len() - 1);
  }
  let source_map: Option<String> = if options.source_map != SourceMapOption::None {
    let mut buffer = Vec::new();
    let source_map_config = SourceMapConfig {
      inline_sources: options.inline_sources,
      maybe_base: None,
    };
    source_map
      .build_source_map_with_config(&source_map_buffer, None, source_map_config)
      .to_writer(&mut buffer)?;
    if options.source_map == SourceMapOption::Inline {
      if !code.ends_with("\n") {
        code.push_str("\n");
      }
      code.push_str("//# sourceMappingURL=data:application/json;base64,");
      base64::prelude::BASE64_STANDARD.encode_string(buffer, &mut code);
      None
    } else {
      Some(String::from_utf8(buffer).map_err(Error::msg)?)
    }
  } else {
    None
  };
  Ok(outputs::TransformOutput::new(&parsed_source, code, source_map))
}

pub fn transpile<'local>(code: String, options: options::TranspileOptions) -> Result<outputs::TranspileOutput> {
  log::debug!("transpile()");
  log::debug!("{:?}", options);
  let specifier = options.get_specifier()?;
  let parse_params = ParseParams {
    specifier,
    text: code.into(),
    media_type: options.media_type,
    capture_tokens: options.capture_tokens,
    maybe_syntax: None,
    scope_analysis: options.scope_analysis,
  };
  let mut plugin_host = options.plugin_host.clone();
  let parsed_source = parse_by_mode(parse_params, options.parse_mode, &mut plugin_host)?;
  let transpile_options = TranspileOptions {
    emit_metadata: options.emit_metadata,
    imports_not_used_as_values: options.imports_not_used_as_values.to_owned(),
    jsx_automatic: options.jsx_automatic,
    jsx_development: options.jsx_development,
    jsx_factory: options.jsx_factory.to_owned(),
    jsx_fragment_factory: options.jsx_fragment_factory.to_owned(),
    jsx_import_source: options.jsx_import_source.to_owned(),
    precompile_jsx: options.precompile_jsx,
    precompile_jsx_dynamic_props: options.precompile_jsx_dynamic_props.to_owned(),
    precompile_jsx_skip_elements: options.precompile_jsx_skip_elements.to_owned(),
    transform_jsx: options.transform_jsx,
    var_decl_imports: options.var_decl_imports,
    verbatim_module_syntax: options.verbatim_module_syntax,
    use_decorators_proposal: options.use_decorators_proposal,
    use_ts_decorators: options.use_ts_decorators,
  };
  let transpile_module_options = TranspileModuleOptions {
    module_kind: match options.module_kind {
      enums::ModuleKind::Auto => None,
      enums::ModuleKind::Esm => Some(ModuleKind::Esm),
      enums::ModuleKind::Cjs => Some(ModuleKind::Cjs),
    },
  };
  let emit_options = EmitOptions {
    inline_sources: options.inline_sources,
    remove_comments: !options.keep_comments,
    source_map: options.source_map,
    source_map_base: None,
    source_map_file: None,
  };
  parsed_source
    .clone()
    .transpile(&transpile_options, &transpile_module_options, &emit_options)
    .map(|transpile_result| outputs::TranspileOutput::new(&options, &parsed_source, &transpile_result))
    .map_err(Error::msg)
}

pub fn get_version<'local>() -> &'local str {
  VERSION
}
