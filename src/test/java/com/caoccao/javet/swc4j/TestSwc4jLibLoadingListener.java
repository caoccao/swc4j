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

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jLibLoadingListener {
    @Test
    public void testCustomListener() {
        ISwc4jLibLoadingListener originalListener = Swc4jLibLoader.getLibLoadingListener();
        assertNotNull(originalListener);

        // Test setting a custom listener
        ISwc4jLibLoadingListener customListener = new ISwc4jLibLoadingListener() {
            @Override
            public boolean isDeploy() {
                return false;
            }
        };
        Swc4jLibLoader.setLibLoadingListener(customListener);
        assertEquals(customListener, Swc4jLibLoader.getLibLoadingListener());
        assertFalse(Swc4jLibLoader.getLibLoadingListener().isDeploy());

        // Restore original listener
        Swc4jLibLoader.setLibLoadingListener(originalListener);
    }

    @Test
    public void testGetProperty() {
        assertNull(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH));
        assertNull(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE));

        assertEquals("swc4j", new Swc4jLibLoadingListener().getLibPath().getName());
        System.setProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH, "/abc");
        assertEquals("abc", new Swc4jLibLoadingListener().getLibPath().getName());
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_PATH);

        assertFalse(new Swc4jLibLoadingListener().isLibInSystemPath());
        assertTrue(new Swc4jLibLoadingListener().isDeploy());
        System.setProperty(
                Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE,
                Swc4jLibLoadingListener.SWC4J_LIB_LOADING_TYPE_SYSTEM);
        assertTrue(new Swc4jLibLoadingListener().isLibInSystemPath());
        assertFalse(new Swc4jLibLoadingListener().isDeploy());
        System.setProperty(
                Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE,
                Swc4jLibLoadingListener.SWC4J_LIB_LOADING_TYPE_CUSTOM);
        assertFalse(new Swc4jLibLoadingListener().isLibInSystemPath());
        assertFalse(new Swc4jLibLoadingListener().isDeploy());
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE);
    }

    @Test
    public void testSetNullListener() {
        assertThrows(NullPointerException.class, () -> Swc4jLibLoader.setLibLoadingListener(null));
    }

    @Test
    public void testSuppressError() {
        assertNull(System.getProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR));
        assertFalse(new Swc4jLibLoadingListener().isSuppressingError());

        System.setProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR, "true");
        assertTrue(new Swc4jLibLoadingListener().isSuppressingError());
        System.clearProperty(Swc4jLibLoadingListener.PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR);
    }
}
