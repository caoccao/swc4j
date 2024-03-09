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

use jni::objects::{JMethodID, JObject};
use jni::signature::{Primitive, ReturnType};
use jni::JNIEnv;

use crate::converter;

pub fn get_as_boolean<'local, 'a>(env: &mut JNIEnv<'local>, obj: &JObject<'a>, method: JMethodID) -> bool {
  let b = unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Boolean), &[])
      .expect("boolean is expected")
      .as_jni()
      .z
  };
  converter::jboolean_to_bool(b)
}

pub fn get_as_int<'local, 'a>(env: &mut JNIEnv<'local>, obj: &JObject<'a>, method: JMethodID) -> i32 {
  unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Int), &[])
      .expect("int is expected")
      .as_jni()
      .i
  }
}

pub fn get_as_jobject<'local, 'a, 'b>(env: &mut JNIEnv<'local>, obj: &JObject<'a>, method: JMethodID) -> JObject<'b> {
  unsafe {
    JObject::from_raw(
      env
        .call_method_unchecked(&obj, method, ReturnType::Object, &[])
        .expect("Object is expected")
        .as_jni()
        .l,
    )
  }
}

pub fn get_as_optional_string<'local, 'a>(
  env: &mut JNIEnv<'local>,
  obj: &JObject<'a>,
  method: JMethodID,
) -> Option<String> {
  let s = unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Object, &[])
      .expect("String is expected")
      .as_jni()
      .l
  };
  converter::jstring_to_optional_string(env, s)
}

pub fn get_as_string<'local, 'a>(env: &mut JNIEnv<'local>, obj: &JObject<'a>, method: JMethodID) -> String {
  let s = unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Object, &[])
      .expect("String is expected")
      .as_jni()
      .l
  };
  converter::jstring_to_string(env, s)
}
