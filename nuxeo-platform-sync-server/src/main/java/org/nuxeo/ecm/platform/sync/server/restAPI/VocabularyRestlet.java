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
package org.nuxeo.ecm.platform.sync.server.restAPI;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.ecm.core.schema.types.primitives.IntegerType;
import org.nuxeo.ecm.core.schema.types.primitives.LongType;
import org.nuxeo.ecm.core.schema.types.primitives.StringType;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.w3c.dom.Element;

/**
 * Simple restlet to export vocabularies content. This reslet is similar to {@link VocabularyRestlet} but as the goals
 * are different they can't be intercharged.
 *
 * @author mariana
 * @author Sun Seng David TAN <stan@nuxeo.com>
 */
public class VocabularyRestlet extends BaseStatelessNuxeoRestlet {

    public static final String VOCABULARY_TYPE_HIER = "xvocabulary";

    public static final String VOCABULARY_TYPE_SIMPLE = "vocabulary";

    public static final Log log = LogFactory.getLog(VocabularyRestlet.class);

    @Override
    public void handle(Request req, Response res) {

        DOMDocumentFactory domfactory = new DOMDocumentFactory();
        DOMDocument result = (DOMDocument) domfactory.createDocument();

        DirectoryService directoryService;
        try {
            directoryService = Framework.getService(DirectoryService.class);
            if (directoryService == null) {
                handleError(result, res, "Unable to get Directory Service");
                return;
            }
        } catch (Exception e) {
            handleError(result, res, e);
            return;
        }

        String vocName = (String) req.getAttributes().get("vocName");
        if ("".equals(vocName)) {
            handleError(result, res, "You must specify a vocabulary name");
            return;
        }

        Session dirSession;
        try {
            dirSession = directoryService.open(vocName);
            String directorySchema = directoryService.getDirectorySchema(vocName);
            Element current = result.createElement("entries");
            result.setRootElement((org.dom4j.Element) current);
            if (directorySchema.equals(VOCABULARY_TYPE_SIMPLE) || directorySchema.equals(VOCABULARY_TYPE_HIER)) {

                SchemaManager schemaManager = Framework.getLocalService(SchemaManager.class);
                Schema vocSchema = schemaManager.getSchema(directorySchema);
                Collection<Field> vocSchemaField = vocSchema.getFields();

                for (DocumentModel entry : dirSession.getEntries()) {
                    Element el = result.createElement("entry");
                    for (Field field : vocSchemaField) {
                        String fieldName = field.getName().getLocalName();
                        Type type = field.getType();
                        if (type instanceof StringType) {
                            el.setAttribute(fieldName, (String) entry.getProperty(directorySchema, fieldName));
                        } else if (type instanceof LongType) {
                            el.setAttribute(
                                    fieldName,
                                    ((Long) (entry.getProperty(directorySchema, fieldName) != null ? entry.getProperty(
                                            directorySchema, fieldName) : 0)).toString());
                        } else if (type instanceof IntegerType) {
                            el.setAttribute(
                                    fieldName,
                                    ((Integer) (entry.getProperty(directorySchema, fieldName) != null ? entry.getProperty(
                                            directorySchema, fieldName) : 0)).toString());
                        } else {
                            log.warn("Vocabulary Restlet serializer only serialize int, long or string fields type. "
                                    + fieldName + "(" + type.getName() + ") has been ignored");
                        }
                    }

                    current.appendChild(el);
                }
            } else {
                handleError(result, res, "Selected directory is not a vocabulary");
                return;
            }
        } catch (NuxeoException e) {
            handleError(result, res, e);
            return;
        }
        try {
            dirSession.close();
        } catch (DirectoryException e) {
            handleError(result, res, e);
            return;
        }
        res.setEntity(result.asXML(), MediaType.TEXT_XML);
    }

}
