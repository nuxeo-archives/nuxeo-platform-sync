/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.w3c.dom.Element;

/**
 * Simple restlet to export vocabularies content. This reslet is similar to
 * {@link VocabularyRestlet} but as the goals are different they can't be
 * intercharged.
 *
 * @author mariana
 */
public class VocabularyRestlet extends BaseStatelessNuxeoRestlet {

    public static final String VOCABULARY_PARENT = "parent";

    public static final String VOCABULARY_TYPE_HIER = "xvocabulary";

    public static final String VOCABULARY_TYPE_SIMPLE = "vocabulary";

    public static final String VOCABULARY_ID = "id";

    public static final String VOCABULARY_LABEL = "label";

    public static final String VOCABULARY_OBSOLETE = "obsolete";

    public static final String VOCABULARY_ORDERING = "ordering";

    public static final Integer DEFAULT_OBSOLETE = 0;

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
            if (directorySchema.equals(VOCABULARY_TYPE_SIMPLE)) {

                for (DocumentModel entry : dirSession.getEntries()) {
                    Element el = result.createElement("entry");
                    el.setAttribute(VOCABULARY_ID, entry.getId());
                    el.setAttribute(VOCABULARY_LABEL,
                            (String) entry.getProperty(VOCABULARY_TYPE_SIMPLE,
                                    VOCABULARY_LABEL));
                    Long obsolete = (Long) (entry.getProperty(
                            VOCABULARY_TYPE_SIMPLE, VOCABULARY_OBSOLETE) != null ? entry.getProperty(
                            VOCABULARY_TYPE_SIMPLE, VOCABULARY_OBSOLETE) : 0);

                    el.setAttribute(VOCABULARY_OBSOLETE,
                            Long.toString(obsolete));
                    current.appendChild(el);
                }
            } else if (directorySchema.equals(VOCABULARY_TYPE_HIER)) {
                for (DocumentModel entry : dirSession.getEntries()) {
                    Element el = result.createElement("entry");
                    el.setAttribute(VOCABULARY_ID, entry.getId());
                    el.setAttribute(VOCABULARY_LABEL,
                            (String) entry.getProperty(VOCABULARY_TYPE_HIER,
                                    VOCABULARY_LABEL));
                    Long obsolete = (Long) (entry.getProperty(
                            VOCABULARY_TYPE_HIER, VOCABULARY_OBSOLETE) != null ? entry.getProperty(
                            VOCABULARY_TYPE_HIER, VOCABULARY_OBSOLETE) : 0);
                    el.setAttribute(VOCABULARY_OBSOLETE,
                            Long.toString(obsolete));
                    el.setAttribute(VOCABULARY_PARENT,
                            (String) entry.getProperty(VOCABULARY_TYPE_HIER,
                                    VOCABULARY_PARENT));
                    current.appendChild(el);
                }
            } else {
                handleError(result, res,
                        "Selected directory is not a vocabulary");
                return;
            }
        } catch (ClientException e) {
            handleError(result, res, e);
            return;
        }
        try {
            dirSession.close();
        } catch (ClientException e) {
            handleError(result, res, e);
            return;
        }
        res.setEntity(result.asXML(), MediaType.TEXT_XML);
    }

}
