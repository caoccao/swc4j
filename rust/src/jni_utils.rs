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

macro_rules! call_as_boolean {
  ($env: ident, $obj: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.call_method_unchecked($obj, $method, ReturnType::Primitive(Primitive::Boolean), $args) } {
      Ok(java_object) => match java_object.z() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_as_boolean;

macro_rules! call_as_construct {
  ($env: ident, $class: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.new_object_unchecked($class, $method, $args) } {
      Ok(java_object) => java_object,
      Err(err) => panic!("Couldn't construct {} because {}", $name, err),
    }
  };
}
pub(crate) use call_as_construct;

macro_rules! call_as_int {
  ($env: ident, $obj: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.call_method_unchecked($obj, $method, ReturnType::Primitive(Primitive::Int), $args) } {
      Ok(java_object) => match java_object.i() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_as_int;

macro_rules! call_as_object {
  ($env: ident, $obj: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.call_method_unchecked($obj, $method, ReturnType::Object, $args) } {
      Ok(java_object) => match java_object.l() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_as_object;

macro_rules! call_static_as_boolean {
  ($env: ident, $class: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe {
      $env.call_static_method_unchecked($class, $method, ReturnType::Primitive(Primitive::Boolean), $args)
    } {
      Ok(java_object) => match java_object.z() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_static_as_boolean;

macro_rules! call_static_as_int {
  ($env: ident, $class: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.call_static_method_unchecked($class, $method, ReturnType::Primitive(Primitive::Int), $args) } {
      Ok(java_object) => match java_object.i() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_static_as_int;

macro_rules! call_static_as_object {
  ($env: ident, $class: expr, $method: expr, $args: expr, $name: literal) => {
    match unsafe { $env.call_static_method_unchecked($class, $method, ReturnType::Object, $args) } {
      Ok(java_object) => match java_object.l() {
        Ok(object) => object,
        Err(err) => panic!("Couldn't convert {} because {}", $name, err),
      },
      Err(err) => panic!("Couldn't call {} because {}", $name, err),
    }
  };
}
pub(crate) use call_static_as_object;

macro_rules! delete_local_ref {
  ($env: ident, $name: expr) => {
    match $env.delete_local_ref($name) {
      Ok(_) => {}
      Err(err) => panic!("Couldn't delete local {} because {}", stringify!($name), err),
    }
  };
}
pub(crate) use delete_local_ref;

macro_rules! jstring_to_optional_string {
  ($env: ident, $s: expr) => {
    if $s.is_null() {
      None
    } else {
      unsafe {
        match $env.get_string(&JString::from_raw($s)) {
          Ok(s) => Some(s.into()),
          Err(_) => None,
        }
      }
    }
  };
}
pub(crate) use jstring_to_optional_string;

macro_rules! jstring_to_string {
  ($env: ident, $s: expr) => {
    match jstring_to_optional_string!($env, $s) {
      Some(s) => s,
      None => "".to_owned(),
    }
  };
}
pub(crate) use jstring_to_string;

macro_rules! optional_string_to_jstring {
  ($env: ident, $s: expr) => {
    match $s {
      Some(s) => string_to_jstring!($env, s),
      None => Default::default(),
    }
  };
}
pub(crate) use optional_string_to_jstring;

macro_rules! string_to_jstring {
  ($env: ident, $s: expr) => {
    match $env.new_string($s) {
      Ok(s) => s,
      Err(_) => Default::default(),
    }
  };
}
pub(crate) use string_to_jstring;

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
    call_as_construct!(
      env,
      &self.class,
      self.method_construct,
      &[initial_capacity],
      "ArrayList"
    )
  }

  pub fn add<'local>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'_>, element: &JObject<'_>) -> bool {
    let element = jvalue { l: element.as_raw() };
    call_as_boolean!(env, obj, &self.method_add, &[element], "add()")
  }
}

pub static mut JAVA_ARRAY_LIST: Option<JavaArrayList> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_ARRAY_LIST = Some(JavaArrayList::new(env));
  }
}
