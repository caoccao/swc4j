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

use jni::objects::JString;
use jni::sys::{jboolean, jstring};
use jni::JNIEnv;

pub fn jstring_to_optional_string<'local>(env: &mut JNIEnv<'local>, s: jstring) -> Option<String> {
  if s.is_null() {
    None
  } else {
    unsafe {
      match env.get_string(&JString::from_raw(s)) {
        Ok(s) => Some(s.into()),
        Err(_) => None,
      }
    }
  }
}

pub fn jstring_to_string<'local>(env: &mut JNIEnv<'local>, s: jstring) -> String {
  match jstring_to_optional_string(env, s) {
    Some(s) => s,
    None => "".to_owned(),
  }
}

pub fn string_to_jstring<'local, 'a>(env: &JNIEnv<'local>, s: &str) -> JString<'a>
where
  'local: 'a,
{
  match env.new_string(s) {
    Ok(s) => s,
    Err(_) => Default::default(),
  }
}
