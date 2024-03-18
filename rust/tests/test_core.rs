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
  assert_eq!(core::get_version(), "0.2.0");
}

#[test]
fn test_parse_jsx_with_default_options() {
  let code = String::from("import React from 'react';\n")
    + "import './App.css';\n"
    + "function App() {\n"
    + "    return (\n"
    + "        <h1> Hello World! </h1>\n"
    + "    );\n"
    + "}\n"
    + "export default App;";
  let options = options::ParseOptions {
    media_type: MediaType::Jsx,
    ..Default::default()
  };
  let output = core::parse(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  assert!(!output.script);
}

#[test]
fn test_parse_wrong_media_type() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_error = String::from("Expected ',', got ':' at file:///main.js:1:15\n")
    + "\n"
    + "  function add(a:number, b:number) { return a+b; }\n"
    + "                ~";
  let options = options::ParseOptions {
    media_type: MediaType::JavaScript,
    ..Default::default()
  };
  let output = core::parse(code.to_owned(), options);
  assert!(output.is_err());
  let output_error = output.err().unwrap();
  assert_eq!(expected_error, output_error);
}

#[test]
fn test_transpile_jsx_with_custom_jsx_factory() {
  let code = String::from("import React from 'react';\n")
    + "import './App.css';\n"
    + "function App() {\n"
    + "    return (\n"
    + "        <h1> Hello World! </h1>\n"
    + "    );\n"
    + "}\n"
    + "export default App;";
  let expected_code = String::from("import React from 'react';\n")
    + "import './App.css';\n"
    + "function App() {\n"
    + "  return /*#__PURE__*/ CustomJsxFactory.createElement(\"h1\", null, \" Hello World! \");\n"
    + "}\n"
    + "export default App;\n";
  let expected_source_map_prefix = "//# sourceMappingURL=data:application/json;base64,";
  let options = options::TranspileOptions {
    jsx_factory: "CustomJsxFactory.createElement".into(),
    media_type: MediaType::Jsx,
    ..Default::default()
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  assert!(!output.script);
  let output_code = output.code;
  assert_eq!(expected_code, &output_code[0..expected_code.len()]);
  assert!(output_code[expected_code.len()..].starts_with(expected_source_map_prefix));
}

#[test]
fn test_transpile_jsx_with_default_options() {
  let code = String::from("import React from 'react';\n")
    + "import './App.css';\n"
    + "function App() {\n"
    + "    return (\n"
    + "        <h1> Hello World! </h1>\n"
    + "    );\n"
    + "}\n"
    + "export default App;";
  let expected_code = String::from("import React from 'react';\n")
    + "import './App.css';\n"
    + "function App() {\n"
    + "  return /*#__PURE__*/ React.createElement(\"h1\", null, \" Hello World! \");\n"
    + "}\n"
    + "export default App;\n";
  let expected_source_map_prefix = "//# sourceMappingURL=data:application/json;base64,";
  let options = options::TranspileOptions {
    media_type: MediaType::Jsx,
    ..Default::default()
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  assert!(!output.script);
  let output_code = output.code;
  assert_eq!(expected_code, &output_code[0..expected_code.len()]);
  assert!(output_code[expected_code.len()..].starts_with(expected_source_map_prefix));
}

#[test]
fn test_transpile_type_script_with_inline_source_map() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_code = "function add(a, b) {\n  return a + b;\n}\n";
  let expected_source_map_prefix = "//# sourceMappingURL=data:application/json;base64,";
  let options = options::TranspileOptions::default();
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(output.module);
  assert!(!output.script);
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
    "file:///main.ts",
    "names",
    "mappings",
  ];
  vec![enums::ParseMode::Module, enums::ParseMode::Script]
    .iter()
    .for_each(|parse_mode| {
      let options = options::TranspileOptions {
        inline_source_map: false,
        parse_mode: parse_mode.clone(),
        source_map: true,
        specifier: "file:///main.ts".to_owned(),
        ..Default::default()
      };
      let output = core::transpile(code.to_owned(), options);
      assert!(output.is_ok());
      let output = output.unwrap();
      match parse_mode {
        enums::ParseMode::Script => {
          assert!(!output.module);
          assert!(output.script);
        }
        _ => {
          assert!(output.module);
          assert!(!output.script);
        }
      }
      let output_code = output.code;
      assert_eq!(expected_code, output_code);
      let source_map = output.source_map.unwrap();
      expected_properties
        .iter()
        .for_each(|p| assert!(source_map.contains(p), "{} is not found", p));
    });
}

#[test]
fn test_transpile_wrong_media_type() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_error = String::from("Expected ',', got ':' at file:///main.js:1:15\n")
    + "\n"
    + "  function add(a:number, b:number) { return a+b; }\n"
    + "                ~";
  let options = options::TranspileOptions {
    media_type: MediaType::JavaScript,
    ..Default::default()
  };
  let output = core::transpile(code.to_owned(), options);
  assert!(output.is_err());
  let output_error = output.err().unwrap();
  assert_eq!(expected_error, output_error);
}
