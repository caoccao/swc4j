/*
* Copyright (c) 2024-2025. caoccao.com Sam Cao
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

use std::sync::OnceLock;

use anyhow::Result;
use deno_ast::swc::ast::{Module, Program, Script};
use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::JNIEnv;

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

  pub fn process_module(&mut self, s: &str, module: Module) -> Result<Module> {
    log::debug!("process_module()");
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    module.register_with_map(&mut map);
    map.update_by_str(s);
    let java_module = module.to_java_with_map(&mut self.env, &map)?;
    match java_class.process(&mut self.env, &self.host, &java_module) {
      Ok(result) => {
        if result {
          let module = Module::from_java(&mut self.env, &java_module);
          delete_local_ref!(self.env, java_module);
          Ok(*module?)
        } else {
          delete_local_ref!(self.env, java_module);
          Ok(module)
        }
      }
      Err(err) => {
        delete_local_ref!(self.env, java_module);
        Err(err)
      }
    }
  }

  pub fn process_program(&mut self, s: &str, program: Program) -> Result<Program> {
    log::debug!("process_program()");
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    program.register_with_map(&mut map);
    map.update_by_str(s);
    let java_program = program.to_java_with_map(&mut self.env, &map)?;
    match java_class.process(&mut self.env, &self.host, &java_program) {
      Ok(result) => {
        if result {
          let program = Program::from_java(&mut self.env, &java_program);
          delete_local_ref!(self.env, java_program);
          Ok(*program?)
        } else {
          delete_local_ref!(self.env, java_program);
          Ok(program)
        }
      }
      Err(err) => {
        delete_local_ref!(self.env, java_program);
        Err(err)
      }
    }
  }

  pub fn process_script(&mut self, s: &str, script: Script) -> Result<Script> {
    log::debug!("process_script()");
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    script.register_with_map(&mut map);
    map.update_by_str(s);
    let java_script = script.to_java_with_map(&mut self.env, &map)?;
    match java_class.process(&mut self.env, &self.host, &java_script) {
      Ok(result) => {
        if result {
          let script = Script::from_java(&mut self.env, &java_script);
          delete_local_ref!(self.env, java_script);
          Ok(*script?)
        } else {
          delete_local_ref!(self.env, java_script);
          Ok(script)
        }
      }
      Err(err) => {
        delete_local_ref!(self.env, java_script);
        Err(err)
      }
    }
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
  ) -> Result<bool>
  {
    let program = object_to_jvalue!(program);
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_process,
        &[program],
        "boolean process()"
      )?;
    Ok(return_value)
  }
}
/* JavaISwc4jPluginHost End */

static JAVA_CLASS_I_PLUGIN_HOST: OnceLock<JavaISwc4jPluginHost> = OnceLock::new();

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  log::debug!("init()");
  unsafe {
    JAVA_CLASS_I_PLUGIN_HOST
      .set(JavaISwc4jPluginHost::new(env))
      .unwrap_unchecked();
  }
}
