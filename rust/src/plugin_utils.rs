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

use deno_ast::swc::ast::{Module, Program, Script};

use crate::jni_utils::*;
use crate::span_utils::{ByteToIndexMap, RegisterWithMap, ToJavaWithMap};

#[derive(Debug)]
pub struct PluginHost<'local> {
  env: JNIEnv<'local>,
  host: GlobalRef,
}

impl<'local> PluginHost<'local> {
  pub fn new(env: &mut JNIEnv<'local>, host: GlobalRef) -> Self {
    PluginHost {
      env: unsafe { env.unsafe_clone() },
      host,
    }
  }

  pub fn process_module(&mut self, s: &str, module: Module) -> Module {
    let java_class = unsafe { JAVA_CLASS_I_PLUGIN_HOST.as_ref().unwrap() };
    let mut map = ByteToIndexMap::new();
    module.register_with_map(&mut map);
    map.update_by_str(s);
    let java_module = module.to_java_with_map(&mut self.env, &map);
    let module = if java_class.process(&mut self.env, &self.host, &java_module) {
      Module::from_java(&mut self.env, &java_module)
    } else {
      module
    };
    delete_local_ref!(self.env, java_module);
    module
  }

  pub fn process_program(&mut self, s: &str, program: Program) -> Program {
    let java_class = unsafe { JAVA_CLASS_I_PLUGIN_HOST.as_ref().unwrap() };
    let mut map = ByteToIndexMap::new();
    program.register_with_map(&mut map);
    map.update_by_str(s);
    let java_program = program.to_java_with_map(&mut self.env, &map);
    let program = if java_class.process(&mut self.env, &self.host, &java_program) {
      Program::from_java(&mut self.env, &java_program)
    } else {
      program
    };
    delete_local_ref!(self.env, java_program);
    program
  }

  pub fn process_script(&mut self, s: &str, script: Script) -> Script {
    let java_class = unsafe { JAVA_CLASS_I_PLUGIN_HOST.as_ref().unwrap() };
    let mut map = ByteToIndexMap::new();
    script.register_with_map(&mut map);
    map.update_by_str(s);
    let java_script = script.to_java_with_map(&mut self.env, &map);
    let script = if java_class.process(&mut self.env, &self.host, &java_script) {
      Script::from_java(&mut self.env, &java_script)
    } else {
      script
    };
    delete_local_ref!(self.env, java_script);
    script
  }
}

impl<'local> Clone for PluginHost<'local> {
  fn clone(&self) -> Self {
    PluginHost {
      env: unsafe { self.env.unsafe_clone() },
      host: self.host.clone(),
    }
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
