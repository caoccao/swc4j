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

use deno_ast::swc::{
  atoms::JsWord,
  common::{comments::CommentKind, BytePos, Span, Spanned},
  parser::token::{IdentLike, Keyword, Token, Word},
};

use enums::*;
use swc4j::*;

#[test]
fn test_get_version() {
  assert_eq!(core::get_version(), "0.5.0");
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
  assert!(matches!(output.parse_mode, ParseMode::Module));
  assert_eq!(MediaType::Jsx, output.media_type);
}

#[test]
fn test_parse_typescript_with_capture_tokens() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let options = options::ParseOptions {
    capture_tokens: true,
    media_type: MediaType::TypeScript,
    ..Default::default()
  };
  let output = core::parse(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(matches!(output.parse_mode, ParseMode::Module));
  assert!(output.tokens.is_some());
  let tokens = output.tokens.unwrap();
  /*
   * TokenAndSpan { token: function, had_line_break: true, span: Span { lo: BytePos(1), hi: BytePos(9), ctxt: #0 } }
   * TokenAndSpan { token: add, had_line_break: false, span: Span { lo: BytePos(10), hi: BytePos(13), ctxt: #0 } }
   * TokenAndSpan { token: (, had_line_break: false, span: Span { lo: BytePos(13), hi: BytePos(14), ctxt: #0 } }
   * TokenAndSpan { token: a, had_line_break: false, span: Span { lo: BytePos(14), hi: BytePos(15), ctxt: #0 } }
   * TokenAndSpan { token: :, had_line_break: false, span: Span { lo: BytePos(15), hi: BytePos(16), ctxt: #0 } }
   * TokenAndSpan { token: number, had_line_break: false, span: Span { lo: BytePos(16), hi: BytePos(22), ctxt: #0 } }
   * TokenAndSpan { token: ,, had_line_break: false, span: Span { lo: BytePos(22), hi: BytePos(23), ctxt: #0 } }
   * TokenAndSpan { token: b, had_line_break: false, span: Span { lo: BytePos(24), hi: BytePos(25), ctxt: #0 } }
   * TokenAndSpan { token: :, had_line_break: false, span: Span { lo: BytePos(25), hi: BytePos(26), ctxt: #0 } }
   * TokenAndSpan { token: number, had_line_break: false, span: Span { lo: BytePos(26), hi: BytePos(32), ctxt: #0 } }
   * TokenAndSpan { token: ), had_line_break: false, span: Span { lo: BytePos(32), hi: BytePos(33), ctxt: #0 } }
   * TokenAndSpan { token: {, had_line_break: false, span: Span { lo: BytePos(34), hi: BytePos(35), ctxt: #0 } }
   * TokenAndSpan { token: return, had_line_break: false, span: Span { lo: BytePos(36), hi: BytePos(42), ctxt: #0 } }
   * TokenAndSpan { token: a, had_line_break: false, span: Span { lo: BytePos(43), hi: BytePos(44), ctxt: #0 } }
   * TokenAndSpan { token: +, had_line_break: false, span: Span { lo: BytePos(44), hi: BytePos(45), ctxt: #0 } }
   * TokenAndSpan { token: b, had_line_break: false, span: Span { lo: BytePos(45), hi: BytePos(46), ctxt: #0 } }
   * TokenAndSpan { token: ;, had_line_break: false, span: Span { lo: BytePos(46), hi: BytePos(47), ctxt: #0 } }
   * TokenAndSpan { token: }, had_line_break: false, span: Span { lo: BytePos(48), hi: BytePos(49), ctxt: #0 } }
   */
  let t0 = &tokens[0];
  let t1 = &tokens[1];
  let t2 = &tokens[2];
  let t3 = &tokens[3];
  let t12 = &tokens[12];
  assert_eq!(Token::Word(Word::Keyword(Keyword::Function)), t0.token);
  assert_eq!(Token::Word(Word::Ident(IdentLike::Other(JsWord::new("add")))), t1.token);
  assert_eq!(Token::LParen, t2.token);
  assert_eq!(Token::Word(Word::Ident(IdentLike::Other(JsWord::new("a")))), t3.token);
  assert_eq!(Token::Word(Word::Keyword(Keyword::Return)), t12.token);
  assert!(t0.had_line_break);
  assert!(!t1.had_line_break);
  assert!(!t2.had_line_break);
  assert!(!t3.had_line_break);
  assert!(!t12.had_line_break);
  assert_eq!(BytePos(1), t0.span_lo());
  assert_eq!(BytePos(9), t0.span_hi());
}

#[test]
fn test_parse_typescript_with_default_options() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let options = options::ParseOptions {
    media_type: MediaType::TypeScript,
    ..Default::default()
  };
  let output = core::parse(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert!(matches!(output.parse_mode, ParseMode::Module));
  assert!(output.tokens.is_none());
}

#[test]
fn test_parse_typescript_with_comments() {
  let code = "let a: /* Comment 1 */ number = 1; // Comment 2";
  let options = options::ParseOptions {
    media_type: MediaType::TypeScript,
    capture_comments: true,
    ..Default::default()
  };
  let output = core::parse(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  let comments = output.comments.unwrap();
  let all_comments = comments.get_vec();
  assert_eq!(2, all_comments.len());
  assert_eq!(CommentKind::Block, all_comments[0].kind);
  assert_eq!(
    Span {
      lo: BytePos(8),
      hi: BytePos(23),
      ..Default::default()
    },
    all_comments[0].span
  );
  assert_eq!(" Comment 1 ", all_comments[0].text.as_str());
  assert_eq!(CommentKind::Line, all_comments[1].kind);
  assert_eq!(
    Span {
      lo: BytePos(36),
      hi: BytePos(48),
      ..Default::default()
    },
    all_comments[1].span
  );
  assert_eq!(" Comment 2", all_comments[1].text.as_str());
  let leading_comment_map = comments.leading_map();
  assert_eq!(1, leading_comment_map.len());
  let tailing_comment_map = comments.trailing_map();
  assert_eq!(1, tailing_comment_map.len());
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
fn test_transform_with_default_options() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_code ="function add(a:number,b:number){return a+b;}\n";
  let expected_source_map_prefix = "//# sourceMappingURL=data:application/json;base64,";
  let options = options::TransformOptions {
    media_type: MediaType::TypeScript,
    ..Default::default()
  };
  let output = core::transform(code.to_owned(), options);
  assert!(output.is_ok());
  let output = output.unwrap();
  assert_eq!(MediaType::TypeScript, output.media_type);
  assert!(matches!(output.parse_mode, ParseMode::Module));
  let output_code = output.code;
  assert_eq!(expected_code, &output_code[0..expected_code.len()]);
  assert!(output_code[expected_code.len()..].starts_with(expected_source_map_prefix));
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
    + "  return CustomJsxFactory.createElement(\"h1\", null, \" Hello World! \");\n"
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
  assert!(matches!(output.parse_output.parse_mode, ParseMode::Module));
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
    + "  return React.createElement(\"h1\", null, \" Hello World! \");\n"
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
  assert!(matches!(output.parse_output.parse_mode, ParseMode::Module));
  assert_eq!(MediaType::Jsx, output.parse_output.media_type);
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
  assert!(matches!(output.parse_output.parse_mode, ParseMode::Module));
  let output_code = output.code;
  assert_eq!(expected_code, &output_code[0..expected_code.len()]);
  assert!(output_code[expected_code.len()..].starts_with(expected_source_map_prefix));
}

#[test]
fn test_transpile_type_script_without_inline_source_map() {
  let code = "function add(a:number, b:number) { return a+b; }";
  let expected_code = "function add(a, b) {\n  return a + b;\n}\n";
  vec![enums::ParseMode::Module, enums::ParseMode::Script]
    .iter()
    .for_each(|parse_mode| {
      let options = options::TranspileOptions {
        parse_mode: parse_mode.clone(),
        source_map: enums::SourceMapOption::None,
        specifier: "file:///main.ts".to_owned(),
        ..Default::default()
      };
      let output = core::transpile(code.to_owned(), options);
      assert!(output.is_ok());
      let output = output.unwrap();
      let output_code = output.code;
      assert_eq!(expected_code, output_code);
      assert!(output.source_map.is_none());
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
