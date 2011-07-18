/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Sun Seng David TAN <stan@nuxeo.com>
 */
package org.nuxeo.ecm.platform.sync.manager;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.ecm.core.schema", "org.nuxeo.ecm.directory.types.contrib" })
public class TestVocabularySynchronizeManager {

    /**
     * Unit Testing extractVocabulary, having a schema extract the values to a
     * list of map entry. (key = property, value=property value)
     *
     * @throws DocumentException
     */
    @Test
    public void testExtractVocabulary() throws DocumentException {
        // open vocabulary as xml
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<entries>"
                + "<entry id=\"entry id 0\" obsolete=\"0\" ordering=\"10000000\" label=\"Entry Label 0\" />"
                + "<entry id=\"entry id 1\" obsolete=\"1\" ordering=\"10000000\" label=\"Entry Label 1\" />"
                + "</entries>";

        VocabularySynchronizeManager classToTest = new VocabularySynchronizeManager(
                new SynchronizeDetails());

        List<Map<String, Object>> entries = classToTest.extractVocabulary(
                "vocabulary", xml);
        assertEquals("The entries size is ", 2, entries.size());
        Map<String, Object> entry0 = entries.get(0);
        assertEquals("entry id 0", entry0.get("id"));
        assertEquals("Entry Label 0", entry0.get("label"));
        assertEquals(10000000L, entry0.get("ordering"));
        assertEquals(0L, entry0.get("obsolete"));

        Map<String, Object> entry1 = entries.get(1);
        assertEquals("entry id 1", entry1.get("id"));
        assertEquals("Entry Label 1", entry1.get("label"));
        assertEquals(10000000L, entry1.get("ordering"));
        assertEquals(1L, entry1.get("obsolete"));

        // testing xvocabulary
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<entries>"
                + "<entry id=\"xentry id 0\" label=\"XEntry 0\" obsolete=\"0\" parent=\"entry id 0\"/>"
                + "<entry id=\"xentry id 1\" label=\"XEntry 1\" obsolete=\"0\" parent=\"entry id 1\"/>"
                + "<entry id=\"xentry id 2\" label=\"XEntry 2\" obsolete=\"0\" parent=\"entry id 0\"/>"
                + "</entries>";

        entries = classToTest.extractVocabulary("xvocabulary", xml);
        assertEquals("The entries size is ", 3, entries.size());
        Map<String, Object> xentry0 = entries.get(0);
        assertEquals("xentry id 0", xentry0.get("id"));
        assertEquals("XEntry 0", xentry0.get("label"));
        assertEquals("entry id 0", xentry0.get("parent"));
        assertEquals(0L, xentry0.get("obsolete"));

        Map<String, Object> xentry1 = entries.get(1);
        assertEquals("xentry id 1", xentry1.get("id"));
        assertEquals("XEntry 1", xentry1.get("label"));
        assertEquals("entry id 1", xentry1.get("parent"));

    }

}
