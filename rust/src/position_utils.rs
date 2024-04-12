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

use std::collections::BTreeMap;

use deno_ast::swc::common::source_map::Pos;
use deno_ast::swc::common::Span;

pub struct SpanEx {
  pub start: u32,
  pub end: u32,
  pub line: u32,
  pub column: u32,
}

impl Default for SpanEx {
  fn default() -> Self {
    SpanEx {
      start: 0,
      end: 0,
      line: 0,
      column: 0,
    }
  }
}

pub struct ByteToIndexMap {
  map: BTreeMap<usize, SpanEx>,
}

impl ByteToIndexMap {
  pub fn new() -> Self {
    ByteToIndexMap { map: BTreeMap::new() }
  }

  pub fn get_span_ex_by_span(&self, span: &Span) -> SpanEx {
    let span_start = self.map.get(&(span.lo().to_usize() - 1)).expect("Couldn't find start");
    let span_end = self.map.get(&(span.hi().to_usize() - 1)).expect("Couldn't find end");
    SpanEx {
      start: span_start.start,
      end: span_end.end,
      line: span_start.line,
      column: span_start.column,
    }
  }

  pub fn register_by_span(&mut self, span: &Span) {
    [span.lo().to_usize() - 1, span.hi().to_usize() - 1]
      .into_iter()
      .for_each(|position| {
        if !self.map.contains_key(&position) {
          self.map.insert(position, Default::default());
        }
      });
  }

  pub fn update(&mut self, key: &usize, position: u32, line: u32, column: u32) {
    self.map.get_mut(&key).map(|v| {
      v.start = position;
      v.end = position;
      v.line = line;
      v.column = column;
    });
  }
}
