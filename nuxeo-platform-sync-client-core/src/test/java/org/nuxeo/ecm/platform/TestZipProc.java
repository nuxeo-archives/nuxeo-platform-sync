/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     Quentin Lamerand
 */
package org.nuxeo.ecm.platform;

import org.apache.log4j.BasicConfigurator;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestZipProc {

    public TestZipProc() {
        BasicConfigurator.configure();
    }

    @Test
    public void testOpenZip() throws Exception {
        // URL archiveUrl = getClass().getClassLoader().getResource(
        // "org/nuxeo/test/document.zip");
        // TupleProcessorUpdate.checkZippedDocument(new File(archiveUrl.getFile()));
    }
}
