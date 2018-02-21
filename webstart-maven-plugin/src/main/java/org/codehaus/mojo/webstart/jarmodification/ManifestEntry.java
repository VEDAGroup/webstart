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

/**
 * Simple bean object for an entry in the Manifest file.
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>, Udo Gabel-Panfil / UGA
 * @since 21.02.2018
 */
public class ManifestEntry {

    private final static String SEPARATOR = ":";
    private final static String SPACE = " ";
    private final String header;
    private final String value;

    public ManifestEntry(String header, String value) {
        this.header = header;
        this.value = value;
    }

    public static ManifestEntry parseLine(final String line) {

        if (line == null) {
            throw new IllegalArgumentException("NULL value not allowed");
        }

        int colonPos = line.indexOf(":");
        final String header;
        final String value;
        if (colonPos < 0) {
            header = line;
            value = null;
        } else {
            header = line.substring(0, colonPos);
            value = line.substring(colonPos + 1).trim();
        }

        return new ManifestEntry(header, value);
    }

    public String toString() {
        if (value == null) {
            return header;
        } else {
            return header + SEPARATOR + SPACE + value;
        }
    }

    public String getHeader() {
        return header;
    }

    public String getValue() {
        return value;
    }
}
