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

use anyhow::Result;
use deno_ast::swc::common::source_map::SmallPos;
use deno_ast::swc::common::{BytePos, Span};
use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::JNIEnv;
use std::collections::BTreeMap;

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

impl ToJava for SpanEx {
  fn to_java<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    unsafe { JAVA_CLASS_SPAN.as_ref().unwrap() }.construct(
      env,
      self.start as i32,
      self.end as i32,
      self.line as i32,
      self.column as i32,
    )
  }
}

pub trait RegisterWithMap<Map> {
  fn register_with_map<'local>(&self, map: &'_ mut Map);
}

pub trait ToJavaWithMap<Map> {
  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ Map) -> Result<JObject<'a>>
  where
    'local: 'a;
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

  pub fn update_by_str(&mut self, s: &str) {
    let mut utf8_byte_length: usize = 0;
    let mut char_count = 0u32;
    let mut line = 1u32;
    let mut column = 1u32;
    s.chars().for_each(|c| {
      self.update(&utf8_byte_length, char_count, line, column);
      utf8_byte_length += c.len_utf8();
      char_count += 1;
      column = if c == '\n' {
        line += 1;
        1
      } else {
        column + 1
      }
    });
    column = 1;
    self.update(&utf8_byte_length, char_count, line, column);
  }
}

/* JavaSwc4jSpan Begin */
#[allow(dead_code)]
struct JavaSwc4jSpan {
  class: GlobalRef,
  method_construct: JMethodID,
  method_get_column: JMethodID,
  method_get_end: JMethodID,
  method_get_line: JMethodID,
  method_get_start: JMethodID,
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
    let method_get_column = env
      .get_method_id(
        &class,
        "getColumn",
        "()I",
      )
      .expect("Couldn't find method Swc4jSpan.getColumn");
    let method_get_end = env
      .get_method_id(
        &class,
        "getEnd",
        "()I",
      )
      .expect("Couldn't find method Swc4jSpan.getEnd");
    let method_get_line = env
      .get_method_id(
        &class,
        "getLine",
        "()I",
      )
      .expect("Couldn't find method Swc4jSpan.getLine");
    let method_get_start = env
      .get_method_id(
        &class,
        "getStart",
        "()I",
      )
      .expect("Couldn't find method Swc4jSpan.getStart");
    JavaSwc4jSpan {
      class,
      method_construct,
      method_get_column,
      method_get_end,
      method_get_line,
      method_get_start,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    start: i32,
    end: i32,
    line: i32,
    column: i32,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let start = int_to_jvalue!(start);
    let end = int_to_jvalue!(end);
    let line = int_to_jvalue!(line);
    let column = int_to_jvalue!(column);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[start, end, line, column],
        "Swc4jSpan construct()"
      )?;
    Ok(return_value)
  }

  pub fn get_column<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<i32>
  {
    let return_value = call_as_int!(
        env,
        obj,
        self.method_get_column,
        &[],
        "int get_column()"
      )?;
    Ok(return_value)
  }

  pub fn get_end<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<i32>
  {
    let return_value = call_as_int!(
        env,
        obj,
        self.method_get_end,
        &[],
        "int get_end()"
      )?;
    Ok(return_value)
  }

  pub fn get_line<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<i32>
  {
    let return_value = call_as_int!(
        env,
        obj,
        self.method_get_line,
        &[],
        "int get_line()"
      )?;
    Ok(return_value)
  }

  pub fn get_start<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<i32>
  {
    let return_value = call_as_int!(
        env,
        obj,
        self.method_get_start,
        &[],
        "int get_start()"
      )?;
    Ok(return_value)
  }
}
/* JavaSwc4jSpan End */

static mut JAVA_CLASS_SPAN: Option<JavaSwc4jSpan> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  log::debug!("init()");
  unsafe {
    JAVA_CLASS_SPAN = Some(JavaSwc4jSpan::new(env));
  }
}
