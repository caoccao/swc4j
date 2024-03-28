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

use jni::objects::{GlobalRef, JClass, JMethodID, JObject};
use jni::signature::{Primitive, ReturnType};
use jni::sys::jvalue;
use jni::JNIEnv;

use crate::converter;

macro_rules! delete_local_ref {
  ($env: ident, $name: expr) => {
    $env
      .delete_local_ref($name)
      .expect(&format!("Couldn't delete local {}", stringify!($name)));
  };
}
pub(crate) use delete_local_ref;

pub trait ToJniType {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a;
}

pub struct JavaArrayList {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
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
    let method_construct = env
      .get_method_id(&class, "<init>", "(I)V")
      .expect("Couldn't find method ArrayList::new");
    let method_add = env
      .get_method_id(&class, "add", "(Ljava/lang/Object;)Z")
      .expect("Couldn't find method ArrayList.add");
    JavaArrayList {
      class,
      method_construct,
      method_add,
    }
  }

  pub fn construct<'local, 'a>(&self, env: &mut JNIEnv<'local>, initial_capacity: usize) -> JObject<'a>
  where
    'local: 'a,
  {
    let initial_capacity = jvalue {
      i: initial_capacity as i32,
    };
    call_as_construct(
      env,
      &self.class,
      &self.method_construct,
      &[initial_capacity],
      "ArrayList",
    )
  }

  pub fn add<'local>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'_>, element: &JObject<'_>) -> bool {
    let element = jvalue { l: element.as_raw() };
    call_as_boolean(env, obj, &self.method_add, &[element], "add()")
  }
}

pub static mut JAVA_ARRAY_LIST: Option<JavaArrayList> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_ARRAY_LIST = Some(JavaArrayList::new(env));
  }
}

pub fn call_as_boolean<'local>(
  env: &mut JNIEnv<'local>,
  obj: &JObject<'_>,
  method: &JMethodID,
  args: &[jvalue],
  name: &str,
) -> bool {
  unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Boolean), args)
      .expect(&format!("Couldn't call {}", name))
      .z()
      .expect(&format!("Couldn't convert {} to bool", name))
  }
}

pub fn call_as_construct<'local, 'a>(
  env: &mut JNIEnv<'local>,
  class: &GlobalRef,
  method: &JMethodID,
  args: &[jvalue],
  name: &str,
) -> JObject<'a>
where
  'local: 'a,
{
  unsafe {
    env
      .new_object_unchecked(class, *method, args)
      .expect(&format!("Couldn't construct {}", name))
  }
}

pub fn call_as_int<'local>(
  env: &mut JNIEnv<'local>,
  obj: &JObject<'_>,
  method: JMethodID,
  args: &[jvalue],
  name: &str,
) -> i32 {
  unsafe {
    env
      .call_method_unchecked(&obj, method, ReturnType::Primitive(Primitive::Int), &[])
      .expect(&format!("Couldn't call {}", name))
      .i()
      .expect(&format!("Couldn't convert {} to int", name))
  }
}
