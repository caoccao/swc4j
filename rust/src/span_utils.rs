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

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::sys::jvalue;
use jni::JNIEnv;

use std::collections::BTreeMap;

use deno_ast::swc::common::source_map::Pos;
use deno_ast::swc::common::{BytePos, Span};

use crate::jni_utils::*;

#[derive(Debug, Copy, Clone)]
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

impl ToJniType for SpanEx {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_span = unsafe { JAVA_SPAN.as_ref().unwrap() };
    let start = int_to_jvalue!(self.start);
    let end = int_to_jvalue!(self.end);
    let line = int_to_jvalue!(self.line);
    let column = int_to_jvalue!(self.column);
    call_as_construct!(
      env,
      &java_span.class,
      java_span.method_construct,
      &[start, end, line, column],
      "Swc4jComment"
    )
  }
}

#[derive(Debug)]
pub struct ByteToIndexMap {
  map: BTreeMap<usize, SpanEx>,
}

impl ByteToIndexMap {
  pub fn new() -> Self {
    ByteToIndexMap { map: BTreeMap::new() }
  }

  pub fn get_span_ex_by_byte_pos(&self, byte_pos: &BytePos) -> SpanEx {
    self
      .map
      .get(&(byte_pos.to_usize() - 1))
      .expect(format!("Couldn't find {}", byte_pos.to_usize() - 1).as_str())
      .clone()
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

  pub fn register_by_byte_pos(&mut self, byte_pos: &BytePos) {
    let position = byte_pos.to_usize() - 1;
    if !self.map.contains_key(&position) {
      self.map.insert(position, Default::default());
    }
  }

  pub fn register_by_span(&mut self, span: &Span) {
    self.register_by_byte_pos(&span.lo());
    self.register_by_byte_pos(&span.hi());
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

/* JavaSwc4jSpan Begin */
#[allow(dead_code)]
struct JavaSwc4jSpan {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jSpan {}
unsafe impl Sync for JavaSwc4jSpan {}

#[allow(dead_code)]
impl JavaSwc4jSpan {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/span/Swc4jSpan")
      .expect("Couldn't find class Swc4jSpan");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jSpan");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(IIII)V",
      )
      .expect("Couldn't find method Swc4jSpan::new");
    JavaSwc4jSpan {
      class,
      method_construct,
    }
  }
}
/* JavaSwc4jSpan End */

static mut JAVA_SPAN: Option<JavaSwc4jSpan> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_SPAN = Some(JavaSwc4jSpan::new(env));
  }
}
