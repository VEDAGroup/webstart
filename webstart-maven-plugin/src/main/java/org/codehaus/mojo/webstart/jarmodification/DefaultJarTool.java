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

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * Default implementation of the {@link JarTool}.
 *
 * @author Christian Plätzinger <christian@plaetzinger.de>, Udo Gabel-Panfil / UGA
 * @version 57.21.x
 * @plexus.component role-hint="default"
 * @since 21.02.2018
 */
public class DefaultJarTool implements JarTool {

    private static final String JNLP_TEMPLATE_FILE_NAME = "APPLICATION_TEMPLATE.JNLP";
    private static final String JNLP_INF_DIR = "JNLP-INF";
    private static final Object JNLP_FILE_NAME = "APPLICATION.JNLP";
    private static final String JNLP_PATH_IN_ZIP = JNLP_INF_DIR + "/" + JNLP_FILE_NAME;
    private static final String JNLP_TEMPLATE_PATH_IN_ZIP = JNLP_INF_DIR + "/" + JNLP_TEMPLATE_FILE_NAME;
    private static final String MANIFEST_FILE_NAME = "MANIFEST.MF";
    private static final String META_INF_DIR = "META-INF";
    private static final String MANIFEST_PATH_IN_ZIP = META_INF_DIR + "/" + MANIFEST_FILE_NAME;

    public ManifestFile readManifestFromJar(File jarFile) throws MojoExecutionException {

        // Check if JAR has a MANIFEST file
        checkIfManifestIsAvailable(jarFile);

        final File entry = getManifestPathInJar(jarFile);

        // Read the MANIFEST file from the JAR
        return internalRead(entry);
    }

    /**
     * Returns the default path to the Manifest file inside the JAR file.
     *
     * @param jarFile The path the the jar file.
     * @return Returns the default path to the Manifest file inside the JAR file.
     */
    private File getManifestPathInJar(File jarFile) {
        return new TFile(jarFile.getAbsoluteFile() + "/" + MANIFEST_PATH_IN_ZIP);
    }

    private void checkIfManifestIsAvailable(File jarFile) throws MojoExecutionException {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);
            final ZipEntry manifestEntry = zipFile.getEntry(MANIFEST_PATH_IN_ZIP);
            if (manifestEntry == null) {
                throw new MojoExecutionException("No Manifest found in JAR");
            }
        } catch (ZipException e) {
            throw new MojoExecutionException("JAR file has wrong format: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read from JAR file: " + e.getMessage(), e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    // ignore on close
                }
            }
        }
    }

    private static ManifestFile internalRead(final File entry) throws MojoExecutionException {

        final ManifestFile manifest = new ManifestFile();

        // Read existing MANIFEST
        Reader reader = null;
        BufferedReader buffer = null;
        try {
            reader = new TFileReader(entry);
            buffer = new BufferedReader(reader);

            String line = buffer.readLine();
            while (line != null) {
                // Skip empty lines (e.g the last line is always empty)
                if (line.length() > 0) {
                    manifest.addEntry(ManifestEntry.parseLine(line));
                }
                line = buffer.readLine();
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read Manifest from JAR: " + e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
                // Ignore on close
            }
        }

        return manifest;
    }

    public void writeManifestToJar(ManifestFile manifest, File jarFile) throws MojoExecutionException {

        Writer writer = null;
        BufferedWriter buffer = null;
        try {
            writer = new TFileWriter(getManifestPathInJar(jarFile), false);
            buffer = new BufferedWriter(writer);
            for (ManifestEntry entry : manifest.getEntries()) {
                buffer.write(entry.toString());
                buffer.newLine();
            }
        } catch (FileNotFoundException e) {
            // Should not happen due to previous check. But we handle it anyway.
            throw new MojoExecutionException("JAR File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write Manifest file to JAR: " + e.getMessage(), e);
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                // Ignore on close
            }
        }
    }

    public void finalizeOperations() throws MojoExecutionException {
        try {
            TVFS.umount();
        } catch (FsSyncException e) {
            // We should fail here. Otherwise the jars can not be accessed by the following steps like signing.
            throw new MojoExecutionException("Failed to finalize Manifest operations: " + e.getMessage(), e);
        }
    }

    public void addJnlpToJar(File jarFile, File jnlpFile, IncludeJnlpType includeJnlpType) throws MojoExecutionException {

        final TFile applicationJnlp;
        switch (includeJnlpType) {
            case APPLICATION:
            default:
                applicationJnlp = new TFile(jarFile.getAbsoluteFile() + "/" + JNLP_PATH_IN_ZIP);
                break;
            case APPLICATION_TEMPLATE:
                applicationJnlp = new TFile(jarFile.getAbsoluteFile() + "/" + JNLP_TEMPLATE_PATH_IN_ZIP);
                break;
        }

        final TFile jnlpFileAsTFile = new TFile(jnlpFile);
        try {
            jnlpFileAsTFile.cp(applicationJnlp);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to include JNLP into jar file: " + e.getMessage(), e);
        }
    }
}

