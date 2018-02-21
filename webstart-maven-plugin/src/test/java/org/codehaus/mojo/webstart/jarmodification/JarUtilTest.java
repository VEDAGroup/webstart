/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codehaus.mojo.webstart.jarmodification;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Tests behaviour of JarUtil
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>,Udo Gabel-Panfil / UGA
 * @version 57.21.x
 * @since 21.02.2018
 */
public class JarUtilTest extends PlexusTestCase {

    public void testGetApplicationNameFromJar()
            throws Exception {

        final String testName1 = "FooBar.jar";
        final String testName2 = "FooBar-SNAPSHOT.jar";
        final String testName3 = "FooBar-1.0.jar";
        final String testName4 = "FooBar-1-SNAPSHOT.jar";
        final String testName5 = "FooBar-1.9.0.1-SNAPSHOT.jar";
        final String testName6 = "FooBar-1.9.0.1-SNAPSHOT.JAR";

        final String expectedApplicationName = "FooBar";

        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName1));
        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName2));
        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName3));
        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName4));
        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName5));
        assertEquals(expectedApplicationName, JarUtil.getApplicationNameFromJar(testName6));
    }
}
