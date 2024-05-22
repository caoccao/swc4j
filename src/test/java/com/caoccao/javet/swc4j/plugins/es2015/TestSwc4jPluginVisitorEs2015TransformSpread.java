/*
 * Copyright (c) 2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.plugins.es2015;

import com.caoccao.javet.swc4j.plugins.BaseTestSuiteSwc4jPlugin;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginHost;
import com.caoccao.javet.swc4j.plugins.Swc4jPluginVisitors;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSwc4jPluginVisitorEs2015TransformSpread extends BaseTestSuiteSwc4jPlugin {

    @BeforeEach
    @Override
    protected void beforeEach() {
        super.beforeEach();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors()
                .add(new Swc4jPluginVisitorEs2015TransformSpread());
        Swc4jPluginHost pluginHost = new Swc4jPluginHost()
                .add(pluginVisitors);
        jsScriptTransformOptions.setPluginHost(pluginHost);
    }

    @Test
    public void testArrayLit() {
        assertTransformAndEvalAsString(SimpleMap.of(
                "const a = [1,2]; const b = [3,4]; JSON.stringify([...a, ...b]);",
                "const a=[1,2];const b=[3,4];JSON.stringify(a.concat(b));",
                "const a = [1,2]; const b = [3,4]; JSON.stringify([...a, ...b, ...b, ...a]);",
                "const a=[1,2];const b=[3,4];JSON.stringify(a.concat(b,b,a));",
                "const a = [1,2]; const b = [3,4]; JSON.stringify(['x', ...a, ...b, 'y']);",
                "const a=[1,2];const b=[3,4];JSON.stringify([\"x\"].concat(a,b,[\"y\"]));",
                "const a = [1,2]; const b = [3,4]; JSON.stringify(['x', 'y', ...a, ...b]);",
                "const a=[1,2];const b=[3,4];JSON.stringify([\"x\",\"y\"].concat(a,b));",
                "const a = [1,2]; const b = [3,4]; JSON.stringify([...a, ...[...a, ...b], ...b]);",
                "const a=[1,2];const b=[3,4];JSON.stringify(a.concat(a.concat(b),b));",
                "const a = [1,2]; JSON.stringify([...a]);",
                "const a=[1,2];JSON.stringify(a);"));
    }
}
