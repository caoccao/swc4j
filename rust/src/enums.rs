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

pub use deno_ast::{ImportsNotUsedAsValues, MediaType};

pub trait ParseById<T> {
  fn parse_by_id(id: i32) -> T;
}

impl ParseById<ImportsNotUsedAsValues> for ImportsNotUsedAsValues {
  fn parse_by_id(id: i32) -> ImportsNotUsedAsValues {
    match id {
      1 => ImportsNotUsedAsValues::Remove,
      2 => ImportsNotUsedAsValues::Preserve,
      _ => ImportsNotUsedAsValues::Error,
    }
  }
}

impl ParseById<MediaType> for MediaType {
  fn parse_by_id(id: i32) -> MediaType {
    match id {
      0 => MediaType::JavaScript,
      1 => MediaType::Jsx,
      2 => MediaType::Mjs,
      3 => MediaType::Cjs,
      4 => MediaType::TypeScript,
      5 => MediaType::Mts,
      6 => MediaType::Cts,
      7 => MediaType::Dts,
      8 => MediaType::Dmts,
      9 => MediaType::Dcts,
      10 => MediaType::Tsx,
      11 => MediaType::Json,
      12 => MediaType::Wasm,
      13 => MediaType::TsBuildInfo,
      14 => MediaType::SourceMap,
      _ => MediaType::Unknown,
    }
  }
}

#[derive(Debug, Copy, Clone)]
pub enum ParseMode {
  Module,
  Script,
}

impl ParseById<ParseMode> for ParseMode {
  fn parse_by_id(id: i32) -> ParseMode {
    match id {
      1 => ParseMode::Script,
      _ => ParseMode::Module,
    }
  }
}
