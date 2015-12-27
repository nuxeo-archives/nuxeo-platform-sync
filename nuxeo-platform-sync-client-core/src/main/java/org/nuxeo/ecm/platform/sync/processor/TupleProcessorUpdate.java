/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 */
package org.nuxeo.ecm.platform.sync.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.nuxeo.common.collections.PrimitiveArrays;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleException;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.lifecycle.LifeCycle;
import org.nuxeo.ecm.core.lifecycle.LifeCycleService;
import org.nuxeo.ecm.core.schema.types.ComplexType;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.JavaTypes;
import org.nuxeo.ecm.core.schema.types.ListType;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.utils.SynchHttpClient;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXFunctionResolver;
import org.nuxeo.ecm.platform.sync.utils.xpath.NXNSContext;
import org.nuxeo.ecm.platform.sync.utils.xpath.XPathUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.DocumentProperty;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;
import org.nuxeo.ecm.platform.sync.webservices.generated.WsACE;
import org.nuxeo.runtime.api.Framework;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The base class for processing a tuple when updating a new document. It holds the common processing for this case.
 *
 * @author rux
 */
public abstract class TupleProcessorUpdate extends TupleProcessor {

    private static final Logger log = Logger.getLogger(TupleProcessorUpdate.class);

    public TupleProcessorUpdate(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    /**
     * Sets the ACE on localDocument as super user.
     *
     */
    protected void setACE() {
        ACL newACL = localDocument.getACP().getOrCreateACL();
        newACL.clear();
        try {
            List<WsACE> aces = getDocumentSnapshot().getAcl();
            for (WsACE wsACE : aces) {
                ACE ace = new ACE(wsACE.getUsername(), wsACE.getPermission(), wsACE.isGranted());
                newACL.add(ace);
            }
        } catch (Exception e) {
            log.error("Could not set ACE for document " + localDocument.getPathAsString());
        }
    }

    /**
     * Sets the properties on localDocument as super user. Lifecycle details are also included here.
     *
     */
    protected void setProperties() {
        setLifeCycle();

        try {
            setPropertiesOnDocument();
        } catch (Exception e) {
            log.error("Could not set properties on document " + localDocument.getPathAsString(), e);
        }

        if (documentSnapshot != null && documentSnapshot.isHasBlobs()) {
            updateBlobs();
        }
    }

    /**
     * Sets life cycle details on localDocument as super user.
     *
     */
    protected void setLifeCycle() {
        String lifeCyclePolicy = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LIFECYCLE_POLICY);
        String destState = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LIFECYCLE_STATE);
        if (importConfiguration != null) {
            String importLC = importConfiguration.getClientLifeCycleStateFor(destState);
            if (importLC != null && importLC.length() > 0)
                destState = importLC;
        }
        String origState = localDocument.getCurrentLifeCycleState();

        LifeCycleService service = NXCore.getLifeCycleService();
        if (service != null) {
            try {
                LifeCycle lifeCycle = service.getLifeCycleByName(lifeCyclePolicy);
                List<String> transitions = ImportUtils.getLifeCycleTransitions(lifeCycle, origState, destState);
                for (String transition : transitions) {
                    localDocument.followTransition(transition);
                }
            } catch (LifeCycleException e) {
                log.error("Unable to get transitions", e);
            }
        }
    }

    /**
     * Sets properties (non blobs only) on the local document.
     *
     */
    @SuppressWarnings("unchecked")
    protected void setPropertiesOnDocument() {
        // first prepare the list of properties as tree
        Map<String, Object> propertyTree = transformList(getDocumentSnapshot().getNoBlobProperties());
        // get the parts: one for each schema applied
        DocumentPart[] parts = localDocument.getParts();
        // for each part hunt the properties in document snapshot
        for (DocumentPart part : parts) {
            // the map to accumulate the data
            Map<String, Object> data = new HashMap<String, Object>();
            // now look for properties
            Map<String, Object> subTree = (Map<String, Object>) propertyTree.get(part.getName());
            if (subTree == null) {
                // no data for this map
                continue;
            }
            for (String topProperty : subTree.keySet()) {
                Field field = part.getSchema().getField(topProperty);
                if (field == null) {
                    // property required but not in schema
                    log.warn(topProperty + " not in schema" + part.getName());
                    continue;
                }
                if (importConfiguration != null) {
                    String xpath = field.getName().getPrefix();
                    if (xpath == null || "".equals(xpath)) {
                        xpath = part.getSchema().getName();
                    }
                    xpath += ":" + field.getName().getLocalName();
                    if (importConfiguration.getExcludedFields().contains(xpath)) {
                        continue;
                    }
                }
                Object value = null;
                try {
                    value = getSegmentData(subTree, topProperty, field.getType());
                } catch (NuxeoException ce) {
                    // don't break, go further
                    log.error(topProperty + " couldn't be imported", ce);
                    continue;
                }
                data.put(topProperty, value);
            }
            localDocument.setProperties(part.getName(), data);
        }
    }

    /**
     * Transforms the list of properties in a tree by schema names and segments.
     *
     * @param properties
     * @return Map<schemaName - Map <first name - Object>> where Object is either the value or the further Map <second
     *         name - Object> and so on
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> transformList(List<DocumentProperty> properties) {
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        for (DocumentProperty property : properties) {
            // get schema name: it has to be the first part before :
            String[] tokens = property.getName().split(":");
            if (tokens.length != 2) {
                // strange, no schema name? skip
                log.warn(property.getName() + " no schema found");
                continue;
            }
            // get the segments of the property name
            String[] segments = tokens[1].split("/");
            if (segments.length == 0) {
                // strange, no segments, only schema name? skip
                log.warn(property.getName() + " no segments found");
                continue;
            }
            // schema name - map
            if (ret.get(tokens[0]) == null) {
                ret.put(tokens[0], new LinkedHashMap<String, Object>());
            }
            Map<String, Object> tempMap = (Map<String, Object>) ret.get(tokens[0]);
            for (int i = 0;; i++) {
                if (i == (segments.length - 1)) {
                    // if it is the last segment, just set the value
                    tempMap.put(segments[i], property.getValue());
                    break;
                }
                // further segments, so need to have map
                if (tempMap.get(segments[i]) == null) {
                    // first time seen this property
                    tempMap.put(segments[i], new LinkedHashMap<String, Object>());
                }
                Object obj = tempMap.get(segments[i]);
                if (obj instanceof String) {
                    // oops, already had a shorter property, can't have both
                    log.warn(property.getName() + " already valued as " + segments[i]);
                    break;
                }
                tempMap = (Map<String, Object>) obj;
            }
        }
        return ret;
    }

    /**
     * Goes throught the segments recursively and create PropertyValue in data.
     *
     * @param data the accumulating map for entire part
     * @param segments the property name split in segments
     * @param index the current segment
     * @param value the value string encoded
     * @param part the schema
     */
    @SuppressWarnings("unchecked")
    private static Object getSegmentData(Map<String, Object> tree, String propertySegment, Type type)
            {
        Object obj = tree.get(propertySegment);
        if (obj == null) {
            // set null value
            return null;
        }
        if (type.isSimpleType()) {
            if (obj instanceof String) {
                return type.decode((String) obj);
            } else {
                throw new NuxeoException("Property " + propertySegment + " holding complex object");
            }
        } else if (type.isListType()) {
            ListType ltype = (ListType) type;
            List<Object> list = new ArrayList<Object>();
            if (obj instanceof String) {
                // one single value?
                list.add(ltype.getFieldType().decode((String) obj));
            } else {
                Map<String, Object> subTree = (Map<String, Object>) obj;
                for (String subSegment : subTree.keySet()) {
                    // add elements to list
                    try {
                        list.add(getSegmentData(subTree, subSegment, ltype.getFieldType()));
                    } catch (NuxeoException ce) {
                        // don't break, go further
                        log.warn(subSegment + " couldn't be imported", ce);
                        continue;
                    }
                }
            }
            Type ftype = ltype.getFieldType();
            if (ftype.isSimpleType()) { // these are stored as arrays
                Class<?> klass = JavaTypes.getClass(ftype);
                if (klass.isPrimitive()) {
                    return PrimitiveArrays.toPrimitiveArray(list, klass);
                } else {
                    return list.toArray((Object[]) Array.newInstance(klass, list.size()));
                }
            }
            return list;
        } else {
            if (obj instanceof String) {
                throw new NuxeoException("Property " + propertySegment + " not holding complex object");
            }
            Map<String, Object> subTree = (Map<String, Object>) obj;
            ComplexType ctype = (ComplexType) type;
            Map<String, Object> map = new HashMap<String, Object>();
            for (String subSegment : subTree.keySet()) {
                // put elements in map
                try {
                    map.put(subSegment, getSegmentData(subTree, subSegment, ctype.getField(subSegment).getType()));
                } catch (NuxeoException ce) {
                    // don't break, go further
                    log.warn(subSegment + " couldn't be imported", ce);
                    continue;
                }
            }
            return map;
        }

    }

    /**
     * Process the zip archive and also sets the blobs.
     *
     * @param zipHandle
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    @SuppressWarnings("unchecked")
    protected void processZippedDocument(File zipHandle) throws IOException, ParserConfigurationException,
            SAXException, TransformerException, XPathExpressionException {
        ZipFile zipFile = new ZipFile(zipHandle);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        // do XML filter
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new NXNSContext());
        xPath.setXPathFunctionResolver(new NXFunctionResolver());
        final String xPathString = "//*[name() = 'data' and nx:isBlob(text())]";

        // collect blobs data
        class XMLBlobData {
            public String enconding;

            public String mimeType;

            public String xpath;

            public File blobFile;
        }
        Map<String, XMLBlobData> collect = new HashMap<String, XMLBlobData>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith("document.xml")) {
                // document properties, collect blobs data from current node and neighbors
                Document document = docBuilder.parse(zipFile.getInputStream(entry));
                XPathExpression xPathExpression = xPath.compile(xPathString);
                Object result = xPathExpression.evaluate(document, XPathConstants.NODESET);
                NodeList nodes = (NodeList) result;
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    String blobName = element.getTextContent();
                    String[] tokens = blobName.split("\\.");
                    String blobId = tokens[0];
                    if (collect.get(blobId) == null) {
                        collect.put(blobId, new XMLBlobData());
                    }
                    XMLBlobData blobData = collect.get(blobId);
                    blobData.xpath = XPathUtils.getElementXPath(nodes.item(i));
                    NodeList neighbors = element.getParentNode().getChildNodes();
                    for (int j = 0; j < neighbors.getLength(); j++) {
                        Node neighborNode = neighbors.item(j);
                        if (neighborNode instanceof Element) {
                            Element neighbor = (Element) neighborNode;
                            if ("encoding".equals(neighbor.getNodeName())) {
                                blobData.enconding = neighbor.getTextContent();
                            } else if ("mime-type".equals(neighbor.getNodeName())) {
                                blobData.mimeType = neighbor.getTextContent();
                            }
                        }
                    }
                }
            } else if (entry.getName().endsWith(".blob")) {
                // blob entry, save it
                String blobName = entry.getName();
                File absoluteFileName = new File(blobName);
                blobName = absoluteFileName.getName();
                String[] tokens = blobName.split("\\.");
                String blobId = tokens[0];
                if (collect.get(blobId) == null) {
                    collect.put(blobId, new XMLBlobData());
                }
                XMLBlobData blobData = collect.get(blobId);
                File blobFile = File.createTempFile(blobId, "blob");
                FileUtils.copyToFile(zipFile.getInputStream(entry), blobFile);
                blobData.blobFile = blobFile;
                Framework.trackFile(blobFile, this);
            }
        }
        for (String blobId : collect.keySet()) {
            // set all collected data
            XMLBlobData blobData = collect.get(blobId);
            if (blobData.blobFile == null || blobData.xpath == null || blobData.mimeType == null) {
                log.warn("Can't import blob" + blobId + " for " + blobData.xpath);
                continue;
            }
            String firstLevelXPath = blobData.xpath.split("/")[0];
            if (importConfiguration == null || !importConfiguration.getExcludedFields().contains(firstLevelXPath)) {
                try (InputStream is = new FileInputStream(blobData.blobFile)) {
                    Blob blob = Blobs.createBlob(is, blobData.mimeType, blobData.enconding);
                    String correctedXPath = correctXPath(blobData.xpath);
                    if (correctedXPath != null) {
                        localDocument.setPropertyValue(correctedXPath, (Serializable) blob);
                    } else {
                        log.warn("Couldn't import blob " + blobData.xpath);
                    }
                }
            }
        }
        if (zipFile != null) {
            zipFile.close();
        }
    }

    /**
     * Corrects the xpath. The XPath collected from exported document.xml doesn't have a definition schema. So it is
     * impossible to tell which property is "list". Indeed, it is possible to identify "list" property if the elements
     * are repeating, but if the property exists only once in the context, as long as there is no XSD or other way to
     * define the XML. Looking in the document schemes and identifying the XPath, if a particular segment is List type,
     * and the [i] is missing, it is concluded that it is about a single item in list, which obviously is xpathed as [0]
     *
     * @param initialXPath
     * @return
     */
    protected String correctXPath(String initialXPath) {
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
        StringBuilder correctedXPath = new StringBuilder(tokens[0] + ":" + segments[0]);
        DocumentPart part = localDocument.getPart(tokens[0]);
        correctedXPath.append(recursiveCorrectPath(part.getSchema().getField(segments[0]).getType(), segments, 1));
        return correctedXPath.toString();
    }

    /**
     * It recursively goes with segments and operates the correction on list type segment.
     *
     * @param type
     * @param segments
     * @param index
     * @return
     */
    protected static String recursiveCorrectPath(Type type, String[] segments, int index) {
        if (index >= (segments.length - 1)) {
            // for blobs no need of last segment
            return "";
        }
        StringBuilder ret = new StringBuilder("/" + segments[index]);
        if (type.isSimpleType()) {
            return ret.toString();
        } else if (type.isListType()) {
            ListType ltype = (ListType) type;
            // we have a list type, see if it is indexed
            if (!segments[index].contains("[")) {
                // it isn't, just append [0]
                ret.append("[0]");
            }
            ret.append(recursiveCorrectPath(ltype.getFieldType(), segments, index + 1));
            return ret.toString();
        } else {
            ComplexType ctype = (ComplexType) type;
            ret.append(recursiveCorrectPath(ctype.getField(segments[index]).getType(), segments, index + 1));
            return ret.toString();
        }
    }

    /**
     * Updates the blobs into the localDocument. It first retrieves the document exported using the ExportRestlet, then
     * reads document.xml and blob files from zip archive, saves the blobs temporary, and sets the blobs in local
     * document.
     *
     */
    protected void updateBlobs() {
        // first get blobs through export restlet
        SynchHttpClient httpClient = new SynchHttpClient(synchronizeDetails);
        String repoName = session.getRepositoryName();
        String docId = tuple.getServerId();
        List<String> pathParams = Arrays.asList(repoName, docId, "exportSingle");
        Map<String, String> params = new HashMap<String, String>();
        params.put("format", "zip");

        boolean finished = false;
        File zipHandle = null;
        try {
            InputStream inputStream = httpClient.executeGetCall(pathParams, params);

            zipHandle = File.createTempFile("ZipFile", ".zip");
            FileUtils.copyToFile(inputStream, zipHandle);
            finished = true;
        } catch (Exception e) {
            log.warn(e);
        } finally {
            httpClient.closeConnection();
        }

        if (!finished) {
            log.warn("Couldn't proces the exported zip.");
            // problem storing the zip file
            return;
        }
        try {
            // process zip
            processZippedDocument(zipHandle);
        } catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException
                | TransformerException e) {
            throw new NuxeoException(e);
        } finally {
            if (zipHandle != null) {
                zipHandle.delete();
            }
        }
    }

    /**
     * Saves the permissions of a document in unrestricted mode.
     *
     */
    protected DocumentModel updateDocument() {
        new UnrestrictedSaveDocument(session, localDocument).runUnrestricted();
        return localDocument;
    }

    /**
     * The unrestricted runner for running the save of a document.
     *
     * @author rux
     */
    protected static class UnrestrictedSaveDocument extends UnrestrictedSessionRunner {
        DocumentModel documentModel;

        public UnrestrictedSaveDocument(CoreSession session, DocumentModel documentModel) {
            super(session);
            this.documentModel = documentModel;
        }

        @Override
        public void run() {
            session.saveDocument(documentModel);
            session.save();
        }
    }
}
