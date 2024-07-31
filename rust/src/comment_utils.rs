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

use deno_ast::swc::common::comments::Comment;
use deno_ast::MultiThreadedComments;

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::JNIEnv;

use crate::jni_utils::*;
use crate::span_utils::{ByteToIndexMap, ToJavaWithMap};

impl ToJavaWithMap<ByteToIndexMap> for Comment {
  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> JObject<'a>
  where
    'local: 'a,
  {
    let text = &self.text;
    let java_kind = self.kind.to_java(env);
    let java_span = map.get_span_ex_by_span(&self.span).to_java(env);
    let return_value = unsafe { JAVA_COMMENT.as_ref().unwrap() }.construct(env, text, &java_kind, &java_span);
    delete_local_ref!(env, java_kind);
    delete_local_ref!(env, java_span);
    return_value
  }
}

impl ToJavaWithMap<ByteToIndexMap> for MultiThreadedComments {
  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> JObject<'a>
  where
    'local: 'a,
  {
    let leading = self.leading_map();
    let trailing = self.trailing_map();
    let java_leading = map_new(env, leading.len());
    leading.iter().for_each(|(key, value)| {
      let key_span_ex = map.get_span_ex_by_byte_pos(&key);
      let java_position = integer_value_of(env, key_span_ex.start as i32);
      let java_comments = list_new(env, value.len());
      value.iter().for_each(|comment| {
        let java_comment = comment.to_java_with_map(env, map);
        list_add(env, &java_comments, &java_comment);
        delete_local_ref!(env, java_comment);
      });
      let java_return_value = map_put(env, &java_leading, &java_position, &java_comments);
      delete_local_ref!(env, java_position);
      delete_local_ref!(env, java_comments);
      delete_local_ref!(env, java_return_value);
    });
    let java_trailing = map_new(env, trailing.len());
    trailing.iter().for_each(|(key, value)| {
      let key_span_ex = map.get_span_ex_by_byte_pos(&key);
      let java_position = integer_value_of(env, key_span_ex.start as i32);
      let java_comments = list_new(env, value.len());
      value.iter().for_each(|comment| {
        let java_comment = comment.to_java_with_map(env, map);
        list_add(env, &java_comments, &java_comment);
        delete_local_ref!(env, java_comment);
      });
      let java_return_value = map_put(env, &java_trailing, &java_position, &java_comments);
      delete_local_ref!(env, java_position);
      delete_local_ref!(env, java_comments);
      delete_local_ref!(env, java_return_value);
    });
    let return_value = unsafe { JAVA_COMMENTS.as_ref().unwrap() }.construct(env, &java_leading, &java_trailing);
    delete_local_ref!(env, java_leading);
    delete_local_ref!(env, java_trailing);
    return_value
  }
}

/* JavaSwc4jComment Begin */
#[allow(dead_code)]
struct JavaSwc4jComment {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jComment {}
unsafe impl Sync for JavaSwc4jComment {}

#[allow(dead_code)]
impl JavaSwc4jComment {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/comments/Swc4jComment")
      .expect("Couldn't find class Swc4jComment");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jComment");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/comments/Swc4jCommentKind;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;)V",
      )
      .expect("Couldn't find method Swc4jComment::new");
    JavaSwc4jComment {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    kind: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let kind = object_to_jvalue!(kind);
    let span = object_to_jvalue!(span);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[text, kind, span],
        "Swc4jComment construct()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }
}
/* JavaSwc4jComment End */

/* JavaSwc4jComments Begin */
#[allow(dead_code)]
struct JavaSwc4jComments {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jComments {}
unsafe impl Sync for JavaSwc4jComments {}

#[allow(dead_code)]
impl JavaSwc4jComments {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/comments/Swc4jComments")
      .expect("Couldn't find class Swc4jComments");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jComments");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Ljava/util/Map;Ljava/util/Map;)V",
      )
      .expect("Couldn't find method Swc4jComments::new");
    JavaSwc4jComments {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    leading: &JObject<'_>,
    trailing: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let leading = object_to_jvalue!(leading);
    let trailing = object_to_jvalue!(trailing);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[leading, trailing],
        "Swc4jComments construct()"
      );
    return_value
  }
}
/* JavaSwc4jComments End */

static mut JAVA_COMMENT: Option<JavaSwc4jComment> = None;
static mut JAVA_COMMENTS: Option<JavaSwc4jComments> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_COMMENT = Some(JavaSwc4jComment::new(env));
    JAVA_COMMENTS = Some(JavaSwc4jComments::new(env));
  }
}

pub fn comments_new<'local, 'a>(
  env: &mut JNIEnv<'local>,
  comments: &MultiThreadedComments,
  map: &ByteToIndexMap,
) -> JObject<'a>
where
  'local: 'a,
{
  comments.to_java_with_map(env, map)
}
