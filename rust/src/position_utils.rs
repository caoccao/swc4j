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
use std::ops::Range;

use deno_ast::swc::common::source_map::Pos;
use deno_ast::swc::common::Span;

pub struct ByteToIndexMap {
  map: BTreeMap<usize, usize>,
}

impl ByteToIndexMap {
  pub fn new() -> Self {
    ByteToIndexMap { map: BTreeMap::new() }
  }

  pub fn get_range_by_span(&self, span: &Span) -> Range<usize> {
    Range {
      start: *self
        .map
        .get(&(span.lo().to_usize() - 1))
        .expect("Couldn't find start index"),
      end: *self
        .map
        .get(&(span.hi().to_usize() - 1))
        .expect("Couldn't find end index"),
    }
  }

  pub fn register_by_span(&mut self, span: &Span) {
    [span.lo().to_usize() - 1, span.hi().to_usize() - 1]
      .into_iter()
      .for_each(|position| {
        if !self.map.contains_key(&position) {
          self.map.insert(position, 0);
        }
      });
  }

  pub fn update(&mut self, key: &usize, value: usize) {
    self.map.get_mut(&key).map(|v| *v = value);
  }
}
