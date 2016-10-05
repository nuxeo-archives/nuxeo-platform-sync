/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thierry Martins
 */
package org.nuxeo.ecm.platform.sync.processor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.URLBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXFunctionResolver;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXNSContext;
import org.nuxeo.ecm.platform.sync.utils.xpath.XPathUtils;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
@LocalDeploy("org.nuxeo.ecm.platform.sync.client.core.test:OSGI-INF/test-core-contrib.xml")
public class TestXPathUtils {

    private static final Log log = LogFactory.getLog(TestXPathUtils.class);

    @Inject
    protected CoreSession session;

    private List<String> xpathList;

    NodeList nodes;

    URL documentUrl = getClass().getClassLoader().getResource("org/nuxeo/test/document.xml");

    @Before
    public void setUp() throws Exception {
        xpathList = new ArrayList<String>();
        nodes = getNodesFromDocument();
    }

    @Test
    public void testGetElementXPath() throws Exception {

        // There should be 4 'blob' nodes in document.xml
        for (int i = 0; i < nodes.getLength(); i++) {
            xpathList.add(XPathUtils.getElementXPath(nodes.item(i)));
        }

        // no prefix defined
        assertEquals("file:content/data", xpathList.get(0));
        // schema name different than prefix name
        assertEquals("multifile:pdffile/data", xpathList.get(1));
        assertEquals("multifile:originalfile/data", xpathList.get(2));
        // schema and prefix have the same name
        assertEquals("pict:pictfile/data", xpathList.get(3));

    }

    @Test
    public void testSetBlobProperties() throws Exception {

        // There should be 4 'blob' nodes in document.xml
        for (int i = 0; i < nodes.getLength(); i++) {
            xpathList.add(XPathUtils.getElementXPath(nodes.item(i)));
        }

        // set properties
        DocumentModel sampleDoc = session.createDocumentModel("Sample");
        sampleDoc.setPathInfo(session.getRootDocument().getPathAsString(), "sample");
        URLBlob blob = new URLBlob(documentUrl, "application/xml", "UTF-8");
        blob.setFilename("document.xml");
        for (String xpath : xpathList) {
            sampleDoc.setPropertyValue(TupleProcessorUpdate.correctXPath(xpath), blob);
        }

        sampleDoc = session.createDocument(sampleDoc);
        session.save();

        sampleDoc = session.getDocument(sampleDoc.getRef());

        assertEquals(new File(documentUrl.getFile()).length(),
                ((Blob) sampleDoc.getPropertyValue("file:content")).getLength());
        assertEquals("document.xml", ((Blob) sampleDoc.getPropertyValue("multifile:originalfile")).getFilename());
        assertEquals("application/xml", ((Blob) sampleDoc.getPropertyValue("multifile:pdffile")).getMimeType());
        assertEquals("UTF-8", ((Blob) sampleDoc.getPropertyValue("pict:pictfile")).getEncoding());
    }

    private NodeList getNodesFromDocument() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        // do XML filter
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new NXNSContext());
        xPath.setXPathFunctionResolver(new NXFunctionResolver());
        final String xPathString = "//*[name() = 'data' and nx:isBlob(text())]";

        Document document = docBuilder.parse(documentUrl.openStream());

        XPathExpression xPathExpression = xPath.compile(xPathString);
        Object result = xPathExpression.evaluate(document, XPathConstants.NODESET);
        return (NodeList) result;
    }

}
