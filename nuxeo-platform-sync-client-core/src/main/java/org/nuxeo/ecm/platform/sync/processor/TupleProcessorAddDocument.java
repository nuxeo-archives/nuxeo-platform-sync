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

import org.apache.log4j.Logger;
import org.joda.time.format.ISODateTimeFormat;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It adds a new normal document. The documents are imported by the
 * CoreSession using the contextual data provided by the server.
 *
 * @author rux
 */
public class TupleProcessorAddDocument extends TupleProcessorAdd {

    private static final Logger log = Logger.getLogger(TupleProcessorAddDocument.class);

    public TupleProcessorAddDocument(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() {
        if (!session.exists(new PathRef(parentPath))) {
            log.warn("Parent path " + parentPath + " doesn't exist => Document " + tuple.getClientId()
                    + " will not be created");
            return;
        }
        log.debug("Starting the process of adding live document " + tuple.getClientId() + " on the client side: "
                + name);
        // a normal document model will be created
        localDocument = new DocumentModelImpl((String) null, tuple.getType(), tuple.getClientId(), new Path(name),
                null, null, new PathRef(parentPath), null, null, null, session.getRepositoryName());

        propertyValue = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LOCK_OWNER);
        if (propertyValue != null) {
            localDocument.putContextData(CoreSession.IMPORT_LOCK_OWNER, propertyValue);
            String createdString = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LOCK_CREATED);
            localDocument.putContextData(CoreSession.IMPORT_LOCK_CREATED,
                    ISODateTimeFormat.dateTimeParser().parseDateTime(createdString).toGregorianCalendar());
        }
        propertyValue = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_CHECKED_IN);
        if (propertyValue != null) {
            localDocument.putContextData(CoreSession.IMPORT_CHECKED_IN, new Boolean(propertyValue));
        }
        propertyValue = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_BASE_VERSION_ID);
        if (propertyValue != null) {
            localDocument.putContextData(CoreSession.IMPORT_BASE_VERSION_ID, propertyValue);
        }
        propertyValue = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_VERSION_MAJOR);
        if (propertyValue != null) {
            localDocument.putContextData(CoreSession.IMPORT_VERSION_MAJOR, Long.valueOf(propertyValue));
        }
        propertyValue = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_VERSION_MINOR);
        if (propertyValue != null) {
            localDocument.putContextData(CoreSession.IMPORT_VERSION_MINOR, Long.valueOf(propertyValue));
        }
        localDocument.setPathInfo(parentPath, name);
        setProperties();
        runUnrestrictedImport();
        setACE();
        updateDocument();
        log.debug("Finishing the process of adding live document " + tuple.getClientId() + " on the client side: "
                + name);
    }

}
