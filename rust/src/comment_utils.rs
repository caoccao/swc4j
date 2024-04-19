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
use jni::sys::jvalue;
use jni::JNIEnv;

use crate::enums::*;
use crate::jni_utils::*;
use crate::span_utils::ByteToIndexMap;

/* JavaSwc4jComment Begin */
struct JavaSwc4jComment {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jComment {}
unsafe impl Sync for JavaSwc4jComment {}

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
/* JavaSwc4jComment End */

  pub fn construct<'local, 'a>(&self, env: &mut JNIEnv<'local>, comment: &Comment, map: &ByteToIndexMap) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_class_comment_kind = unsafe { JAVA_COMMENT_KIND.as_ref().unwrap() };
    let java_text = string_to_jstring!(env, &comment.text);
    let text = object_to_jvalue!(java_text);
    let java_comment_kind = java_class_comment_kind.parse(env, comment.kind.get_id());
    let comment_kind = object_to_jvalue!(java_comment_kind);
    let java_span_ex = map.get_span_ex_by_span(&comment.span).to_jni_type(env);
    let span_ex = object_to_jvalue!(java_span_ex);
    let return_value = call_as_construct!(
      env,
      &self.class,
      self.method_construct,
      &[text, comment_kind, span_ex],
      "Swc4jComment"
    );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_comment_kind);
    delete_local_ref!(env, java_span_ex);
    return_value
  }
}

/* JavaSwc4jComments Begin */
struct JavaSwc4jComments {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jComments {}
unsafe impl Sync for JavaSwc4jComments {}

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
/* JavaSwc4jComments End */

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    comments: &MultiThreadedComments,
    map: &ByteToIndexMap,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let leading = comments.leading_map();
    let trailing = comments.trailing_map();
    let java_leading = map_new(env, leading.len());
    leading.iter().for_each(|(key, value)| {
      let key_span_ex = map.get_span_ex_by_byte_pos(&key);
      let java_position = integer_value_of(env, key_span_ex.start as i32);
      let java_comments = list_new(env, value.len());
      value.iter().for_each(|comment| {
        let java_comment = unsafe { JAVA_COMMENT.as_ref().unwrap() }.construct(env, comment, map);
        list_add(env, &java_comments, &java_comment);
        delete_local_ref!(env, java_comment);
      });
      let java_return_value = map_put(env, &java_leading, &java_position, &java_comments);
      delete_local_ref!(env, java_position);
      delete_local_ref!(env, java_comments);
      delete_local_ref!(env, java_return_value);
    });
    let leading = object_to_jvalue!(&java_leading);
    let java_trailing = map_new(env, trailing.len());
    trailing.iter().for_each(|(key, value)| {
      let key_span_ex = map.get_span_ex_by_byte_pos(&key);
      let java_position = integer_value_of(env, key_span_ex.start as i32);
      let java_comments = list_new(env, value.len());
      value.iter().for_each(|comment| {
        let java_comment = unsafe { JAVA_COMMENT.as_ref().unwrap() }.construct(env, comment, map);
        list_add(env, &java_comments, &java_comment);
        delete_local_ref!(env, java_comment);
      });
      let java_return_value = map_put(env, &java_trailing, &java_position, &java_comments);
      delete_local_ref!(env, java_position);
      delete_local_ref!(env, java_comments);
      delete_local_ref!(env, java_return_value);
    });
    let trailing = object_to_jvalue!(&java_trailing);
    let return_value = call_as_construct!(
      env,
      &self.class,
      self.method_construct,
      &[leading, trailing],
      "Swc4jComments"
    );
    delete_local_ref!(env, java_leading);
    delete_local_ref!(env, java_trailing);
    return_value
  }
}

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
  unsafe { JAVA_COMMENTS.as_ref().unwrap() }.construct(env, comments, map)
}
