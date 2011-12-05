package org.nuxeo.ecm.platform.sync.processor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.URLBlob;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXFunctionResolver;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXNSContext;
import org.nuxeo.ecm.platform.sync.utils.xpath.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TestXPathUtils extends SQLRepositoryTestCase {

    private static final Log log = LogFactory.getLog(TestXPathUtils.class);

    private List<String> xpathList;

    NodeList nodes;

    URL documentUrl = getClass().getClassLoader().getResource(
            "org/nuxeo/test/document.xml");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.core");
        deployContrib("org.nuxeo.ecm.platform.sync.client.core.test",
                "OSGI-INF/test-core-contrib.xml");
        openSession();

        xpathList = new ArrayList<String>();

        nodes = getNodesFromDocument();
    }

    @Override
    public void tearDown() throws Exception {
        closeSession();
        super.tearDown();
    }

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

    public void testSetBlobProperties() throws Exception {

        // There should be 4 'blob' nodes in document.xml
        for (int i = 0; i < nodes.getLength(); i++) {
            xpathList.add(XPathUtils.getElementXPath(nodes.item(i)));
        }

        // set properties
        DocumentModel sampleDoc = session.createDocumentModel("Sample");
        sampleDoc.setPathInfo(session.getRootDocument().getPathAsString(),
                "sample");
        URLBlob blob = new URLBlob(documentUrl);
        blob.setEncoding("UTF-8");
        blob.setFilename("document.xml");
        blob.setMimeType("application/xml");
        for (String xpath : xpathList) {
            sampleDoc.setPropertyValue(correctXPath(xpath, sampleDoc), blob);
        }

        sampleDoc = session.createDocument(sampleDoc);
        session.save();

        sampleDoc = session.getDocument(sampleDoc.getRef());

        assertEquals(2941,
                ((Blob) sampleDoc.getPropertyValue("file:content")).getLength());
        assertEquals(
                "document.xml",
                ((Blob) sampleDoc.getPropertyValue("multifile:originalfile")).getFilename());
        assertEquals(
                "application/xml",
                ((Blob) sampleDoc.getPropertyValue("multifile:pdffile")).getMimeType());
        assertEquals(
                "UTF-8",
                ((Blob) sampleDoc.getPropertyValue("pict:pictfile")).getEncoding());
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
        Object result = xPathExpression.evaluate(document,
                XPathConstants.NODESET);
        return (NodeList) result;
    }

    /**
     * Copy of TupleProcessorUpdate#correctXPath which is not static due to a
     * reference on localDocument
     *
     * @param initialXPath
     * @param localDocument
     * @return
     * @throws ClientException
     */
    protected String correctXPath(String initialXPath,
            DocumentModel localDocument) throws ClientException {
        // get schema name: it has to be the first part before :
        String[] tokens = initialXPath.split(":");
        if (tokens.length != 2) {
            // strange, no schema name? skip
            log.warn(initialXPath + " no XPath: no schema found");
            return null;
        }
        // get the segments of the property name
        String[] segments = tokens[1].split("/");
        if (segments.length == 0) {
            // strange, no segments, only schema name? skip
            log.warn(initialXPath + " no XPath: no segments found");
            return null;
        }
        StringBuilder correctedXPath = new StringBuilder(tokens[0] + ":"
                + segments[0]);
        DocumentPart part = localDocument.getPart(tokens[0]);
        correctedXPath.append(TupleProcessorUpdate.recursiveCorrectPath(
                part.getSchema().getField(segments[0]).getType(), segments, 1));
        return correctedXPath.toString();
    }

}