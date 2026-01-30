/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.interfaces.ISwc4jLibLoadingListener;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestSwc4jLibLoadingListener {
    @Test
    public void testCustomListener() {
        ISwc4jLibLoadingListener originalListener = Swc4jLibLoader.getLibLoadingListener();
        assertThat(originalListener).isNotNull();

        // Test setting a custom listener
        ISwc4jLibLoadingListener customListener = new ISwc4jLibLoadingListener() {
            @Override
            public boolean isDeploy() {
                return false;
            }
        };
        Swc4jLibLoader.setLibLoadingListener(customListener);
        assertThat(Swc4jLibLoader.getLibLoadingListener()).isEqualTo(customListener);
        assertThat(Swc4jLibLoader.getLibLoadingListener().isDeploy()).isFalse();

        // Restore original listener
        Swc4jLibLoader.setLibLoadingListener(originalListener);
    }

    @Test
    public void testGetProperty() {
        assertThat(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH)).isNull();
        assertThat(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE)).isNull();

        assertThat(new Swc4jLibLoadingListener().getLibPath().getName()).isEqualTo("swc4j");
        System.setProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH, "/abc");
        assertThat(new Swc4jLibLoadingListener().getLibPath().getName()).isEqualTo("abc");
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH);

        assertThat(new Swc4jLibLoadingListener().isLibInSystemPath()).isFalse();
        assertThat(new Swc4jLibLoadingListener().isDeploy()).isTrue();
        System.setProperty(
                Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE,
                Swc4jLibLoadingListener.SWC4J_LIB_LOADING_TYPE_SYSTEM);
        assertThat(new Swc4jLibLoadingListener().isLibInSystemPath()).isTrue();
        assertThat(new Swc4jLibLoadingListener().isDeploy()).isFalse();
        System.setProperty(
                Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE,
                Swc4jLibLoadingListener.SWC4J_LIB_LOADING_TYPE_CUSTOM);
        assertThat(new Swc4jLibLoadingListener().isLibInSystemPath()).isFalse();
        assertThat(new Swc4jLibLoadingListener().isDeploy()).isFalse();
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE);
    }

    @Test
    public void testSetNullListener() {
        assertThatThrownBy(() -> Swc4jLibLoader.setLibLoadingListener(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSuppressError() {
        assertThat(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR)).isNull();
        assertThat(new Swc4jLibLoadingListener().isSuppressingError()).isFalse();

        System.setProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR, "true");
        assertThat(new Swc4jLibLoadingListener().isSuppressingError()).isTrue();
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR);
    }
}
