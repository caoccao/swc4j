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

pub const VERSION: &'static str = "0.1.0";

pub fn transpile<'local>(code: String, media_type: MediaType, file_name: String) {
  let url = ModuleSpecifier::parse(&format!("file:///{}", file_name)).unwrap();
  println!("url: {}", url.to_string());
  println!("source: {}", code.to_string());
  println!("media_type: {}", media_type.to_string());
  let parsed_source = parse_module(ParseParams {
    specifier: url.to_string(),
    text_info: SourceTextInfo::from_string(code.to_string()),
    media_type: media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  })
  .unwrap();
  println!("module: {}", parsed_source.is_module());
  println!("script: {}", parsed_source.is_script());
  let transpiled_js_code = parsed_source.transpile(&EmitOptions::default()).unwrap();
  println!("{}", transpiled_js_code.text);
}

pub fn get_version<'local>() -> String {
  VERSION.to_string()
}
