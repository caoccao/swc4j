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
use jni::signature::{Primitive, ReturnType};
use jni::sys::jvalue;
use jni::JNIEnv;

use crate::converter;

pub trait ToJniType {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a;
}

pub struct JavaArrayList {
  #[allow(dead_code)]
  class: GlobalRef,
  method_constructor: JMethodID,
  method_add: JMethodID,
}
unsafe impl Send for JavaArrayList {}
unsafe impl Sync for JavaArrayList {}

impl JavaArrayList {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("java/util/ArrayList")
      .expect("Couldn't find class ArrayList");
    let class = env.new_global_ref(class).expect("Couldn't globalize class ArrayList");
    let method_constructor = env
      .get_method_id(&class, "<init>", "(I)V")
      .expect("Couldn't find method ArrayList::new");
    let method_add = env
      .get_method_id(&class, "add", "(Ljava/lang/Object;)Z")
      .expect("Couldn't find method ArrayList.add");
    JavaArrayList {
      class,
      method_constructor,
      method_add,
    }
  }

  pub fn create<'local, 'a>(&self, env: &mut JNIEnv<'local>, initial_capacity: usize) -> JObject<'a>
  where
    'local: 'a,
  {
    let initial_capacity = jvalue {
      i: initial_capacity as i32,
    };
    unsafe {
      env
        .new_object_unchecked(&self.class, self.method_constructor, &[initial_capacity])
        .expect("Couldn't create ArrayList")
    }
  }

  pub fn add<'local>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'_>, element: &JObject<'_>) -> bool {
    let element = jvalue { l: element.as_raw() };
    let b = unsafe {
      env
        .call_method_unchecked(
          &obj,
          self.method_add,
          ReturnType::Primitive(Primitive::Boolean),
          &[element],
        )
        .expect("boolean is expected")
        .as_jni()
        .z
    };
    converter::jboolean_to_bool(b)
  }
}

pub static mut JAVA_ARRAY_LIST: Option<JavaArrayList> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_ARRAY_LIST = Some(JavaArrayList::new(env));
  }
}

pub fn get_as_boolean<'local>(env: &mut JNIEnv<'local>, obj: &JObject<'_>, method: JMethodID) -> bool {
  let b = unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Boolean), &[])
      .expect("boolean is expected")
      .as_jni()
      .z
  };
  converter::jboolean_to_bool(b)
}

pub fn get_as_int<'local>(env: &mut JNIEnv<'local>, obj: &JObject<'_>, method: JMethodID) -> i32 {
  unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Int), &[])
      .expect("int is expected")
      .as_jni()
      .i
  }
}

pub fn get_as_jobject<'local, 'a>(env: &mut JNIEnv<'local>, obj: &JObject<'_>, method: JMethodID) -> JObject<'a>
where
  'local: 'a,
{
  unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Object, &[])
      .expect("Object is expected")
      .l()
      .expect("Object is expected")
  }
}

pub fn get_as_optional_string<'local>(
  env: &mut JNIEnv<'local>,
  obj: &JObject<'_>,
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

pub fn get_as_string<'local>(env: &mut JNIEnv<'local>, obj: &JObject<'_>, method: JMethodID) -> String {
  let s = unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Object, &[])
      .expect("String is expected")
      .as_jni()
      .l
  };
  converter::jstring_to_string(env, s)
}
