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

use deno_ast::MediaType;

use swc4j::*;

#[test]
fn test_get_version() {
  assert_eq!(core::get_version(), "0.1.0");
}

#[test]
fn test_transpile_type_script_with_inline_source_map() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_code = "function add(a, b) {\n  return a + b;\n}\n";
  let expected_source_map_prefix = "//# sourceMappingURL=data:application/json;base64,";
  let options = options::TranspileOptions {
    inline_source_map: true,
    inline_sources: true,
    media_type: MediaType::TypeScript,
    source_map: false,
    specifier: "file:///abc.ts".to_owned(),
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  let output_code = output.code;
  assert_eq!(expected_code, &output_code[0..expected_code.len()]);
  assert!(output_code[expected_code.len()..].starts_with(expected_source_map_prefix));
}

#[test]
fn test_transpile_type_script_without_inline_source_map() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_code = "function add(a, b) {\n  return a + b;\n}\n";
  let expected_properties = vec![
    "version",
    "sources",
    "sourcesContent",
    "file:///abc.ts",
    "names",
    "mappings",
  ];
  let options = options::TranspileOptions {
    inline_source_map: false,
    inline_sources: true,
    media_type: MediaType::TypeScript,
    source_map: true,
    specifier: "file:///abc.ts".to_owned(),
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  let output_code = output.code;
  assert_eq!(expected_code, output_code);
  let source_map = output.source_map.unwrap();
  expected_properties.iter().for_each(|p| assert!(source_map.contains(p), "{} is not found", p));
}

#[test]
fn test_transpile_wrong_media_type() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_error = String::from("Expected ',', got ':' at file:///abc.ts:1:15\n")
    + "\n"
    + "  function add(a:number, b:number) { return a+b; }\n"
    + "                ~";
  let options = options::TranspileOptions {
    inline_source_map: true,
    inline_sources: true,
    media_type: MediaType::JavaScript,
    source_map: false,
    specifier: "file:///abc.ts".to_owned(),
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_err());
  let output_error = output.err().unwrap();
  assert_eq!(expected_error, output_error);
}
