/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.sync;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.sync.adapter.SynchronizableDocument;

public class TestSynchronizableDocument extends SQLRepositoryTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.core");
        deployBundle("org.nuxeo.ecm.platform.sync.common");
        deployContrib("org.nuxeo.ecm.platform.sync.common.test", "syncdocument-test-contrib.xml");
        openSession();
    }

    @After
    public void tearDown() throws Exception {
        closeSession();
        super.tearDown();
    }

    @Test
    public void testDefaultSyncDoc() throws ClientException {
        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);
        SynchronizableDocument synDoc = folder.getAdapter(SynchronizableDocument.class);
        assertEquals(folder.getId(), synDoc.getId());
    }

    @Test
    public void testCustomSyncDoc() throws Exception {
        DocumentModel file = session.createDocumentModel("/", "file", "File");
        file.setPropertyValue("uid:uid", "1234567890");
        file = session.createDocument(file);
        SynchronizableDocument synDoc = file.getAdapter(SynchronizableDocument.class);
        assertEquals("1234567890", synDoc.getId());
    }

}
