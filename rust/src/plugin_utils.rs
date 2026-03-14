/*
* Copyright (c) 2024-2026. caoccao.com Sam Cao
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
use jni::objects::{Global, JClass, JMethodID, JObject};
use jni::signature::RuntimeMethodSignature;
use jni::strings::JNIString;
use jni::{AttachGuard, Env};

use crate::jni_utils::*;
use crate::span_utils::{ByteToIndexMap, RegisterWithMap, ToJavaWithMap};

#[derive(Debug)]
pub struct PluginHost {
  env: *mut jni::sys::JNIEnv,
  host: Global<JObject<'static>>,
}
unsafe impl Send for PluginHost {}
unsafe impl Sync for PluginHost {}

impl PluginHost {
  pub fn new(env: &Env<'_>, host: Global<JObject<'static>>) -> Self {
    PluginHost {
      env: env.get_raw(),
      host,
    }
  }

  /// Reconstructs a JNI `Env` from the stored raw pointer.
  ///
  /// # Safety
  /// This is safe as long as the raw pointer is still valid (i.e., we are
  /// on the same JNI-attached thread that created this PluginHost). This is
  /// the jni 0.22 equivalent of the removed `JNIEnv::unsafe_clone()`.
  fn guard(&self) -> AttachGuard<'_> {
    unsafe { AttachGuard::from_unowned(self.env) }
  }

  pub fn process_module(&mut self, s: &str, module: Module) -> Result<Module> {
    log::debug!("process_module()");
    let mut guard = self.guard();
    let env = guard.borrow_env_mut();
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    module.register_with_map(&mut map);
    map.update_by_str(s);
    let java_module = module.to_java_with_map(env, &map)?;
    match java_class.process(env, &self.host, &java_module) {
      Ok(result) => {
        if result {
          let module = Module::from_java(env, &java_module);
          delete_local_ref!(env, java_module);
          Ok(*module?)
        } else {
          delete_local_ref!(env, java_module);
          Ok(module)
        }
      }
      Err(err) => {
        delete_local_ref!(env, java_module);
        Err(err)
      }
    }
  }

  pub fn process_program(&mut self, s: &str, program: Program) -> Result<Program> {
    log::debug!("process_program()");
    let mut guard = self.guard();
    let env = guard.borrow_env_mut();
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    program.register_with_map(&mut map);
    map.update_by_str(s);
    let java_program = program.to_java_with_map(env, &map)?;
    match java_class.process(env, &self.host, &java_program) {
      Ok(result) => {
        if result {
          let program = Program::from_java(env, &java_program);
          delete_local_ref!(env, java_program);
          Ok(*program?)
        } else {
          delete_local_ref!(env, java_program);
          Ok(program)
        }
      }
      Err(err) => {
        delete_local_ref!(env, java_program);
        Err(err)
      }
    }
  }

  pub fn process_script(&mut self, s: &str, script: Script) -> Result<Script> {
    log::debug!("process_script()");
    let mut guard = self.guard();
    let env = guard.borrow_env_mut();
    let java_class = JAVA_CLASS_I_PLUGIN_HOST.get().unwrap();
    let mut map = ByteToIndexMap::new();
    script.register_with_map(&mut map);
    map.update_by_str(s);
    let java_script = script.to_java_with_map(env, &map)?;
    match java_class.process(env, &self.host, &java_script) {
      Ok(result) => {
        if result {
          let script = Script::from_java(env, &java_script);
          delete_local_ref!(env, java_script);
          Ok(*script?)
        } else {
          delete_local_ref!(env, java_script);
          Ok(script)
        }
      }
      Err(err) => {
        delete_local_ref!(env, java_script);
        Err(err)
      }
    }
  }
}

/* JavaISwc4jPluginHost Begin */
#[allow(dead_code)]
struct JavaISwc4jPluginHost {
  class: Global<JClass<'static>>,
  method_process: JMethodID,
}

#[allow(dead_code)]
impl JavaISwc4jPluginHost {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/plugins/ISwc4jPluginHost"))
      .expect("Couldn't find class ISwc4jPluginHost");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class ISwc4jPluginHost");
    let method_process = env
      .get_method_id(
        &class,
        JNIString::from("process"),
        RuntimeMethodSignature::from_str("(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;)Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method ISwc4jPluginHost.process");
    JavaISwc4jPluginHost {
      class,
      method_process,
    }
  }

  pub fn process<'local>(
    &self,
    env: &mut Env<'local>,
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

pub fn init<'local>(env: &mut Env<'local>) {
  log::debug!("init()");
  unsafe {
    JAVA_CLASS_I_PLUGIN_HOST
      .set(JavaISwc4jPluginHost::new(env))
      .unwrap_unchecked();
  }
}
