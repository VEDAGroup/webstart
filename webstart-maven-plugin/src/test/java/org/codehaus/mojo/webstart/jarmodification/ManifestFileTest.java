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
 * Tests behaviour of ManifestFile
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>,Udo Gabel-Panfil / UGA
 * @version 57.21.x
 * @since 21.02.2018
 */
public class ManifestFileTest extends PlexusTestCase {

    public void testNoDuplicateEntries() throws Exception {

        final ManifestFile manifest = new ManifestFile();

        manifest.addEntry(new ManifestEntry("Foo", "bar"));
        manifest.addEntry(new ManifestEntry("Foo", "bar"));
        assertEquals(1, manifest.getEntries().size());

        manifest.addEntry(new ManifestEntry("Foo2", "bar"));
        assertEquals(2, manifest.getEntries().size());
    }

    public void testOverrideExistingEntries(){
        final ManifestFile manifest = new ManifestFile();

        manifest.addEntry(new ManifestEntry("Foo", "bar"));
        manifest.addEntry(new ManifestEntry("Foo", "test"));

        assertEquals("test", manifest.getEntry("Foo").getValue());
    }

    public void testHasEntry() {
        final ManifestFile manifest = new ManifestFile();
        manifest.addEntry(new ManifestEntry("Foo", "bar"));

        assertTrue(manifest.hasEntry("Foo"));
        assertFalse(manifest.hasEntry("Foo2"));
    }
}
