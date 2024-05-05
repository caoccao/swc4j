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

use crate::jni_utils::*;

#[derive(Debug)]
pub struct PluginHost {
  host: GlobalRef,
}

impl PluginHost {
  pub fn new(host: GlobalRef) -> Self {
    PluginHost { host }
  }
}

/* JavaISwc4jPluginHost Begin */
#[allow(dead_code)]
struct JavaISwc4jPluginHost {
  class: GlobalRef,
  method_process: JMethodID,
}
unsafe impl Send for JavaISwc4jPluginHost {}
unsafe impl Sync for JavaISwc4jPluginHost {}

#[allow(dead_code)]
impl JavaISwc4jPluginHost {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/plugins/ISwc4jPluginHost")
      .expect("Couldn't find class ISwc4jPluginHost");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class ISwc4jPluginHost");
    let method_process = env
      .get_method_id(
        &class,
        "process",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;)Z",
      )
      .expect("Couldn't find method ISwc4jPluginHost.process");
    JavaISwc4jPluginHost {
      class,
      method_process,
    }
  }

  pub fn process<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
    program: &JObject<'_>,
  ) -> bool
  {
    let program = object_to_jvalue!(program);
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_process,
        &[program],
        "boolean process()"
      );
    return_value
  }
}
/* JavaISwc4jPluginHost End */

static mut JAVA_CLASS_I_PLUGIN_HOST: Option<JavaISwc4jPluginHost> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_CLASS_I_PLUGIN_HOST = Some(JavaISwc4jPluginHost::new(env));
  }
}
