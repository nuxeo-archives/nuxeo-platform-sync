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
package org.nuxeo.ecm.platform.sync.processor;

import org.apache.log4j.Logger;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.schema.types.primitives.DateType;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * Implementing class for processing a tuple. It adds a new version document. The documents
 * are imported by the CoreSession using the contextual data provided by the server. They
 * need to be added before proxies import.
 *
 * @author rux
 *
 */
public class TupleProcessorAddVersion extends TupleProcessorAdd {

    private static final Logger log = Logger.getLogger(TupleProcessorAddVersion.class);

    public TupleProcessorAddVersion(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    @Override
    public void process() throws ClientException {
        log.debug("Starting the process of adding version on the client side: " + name);
        // in case there is a version, the document model will have parent ref null
        localDocument = new DocumentModelImpl((String) null, tuple.getType(),
                tuple.getClientId(), new Path(name), null, null, null, null, null,
                null, session.getRepositoryName());

        localDocument.putContextData(CoreSession.IMPORT_VERSION_VERSIONABLE_ID,
                ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_VERSION_VERSIONABLE_ID));
        localDocument.putContextData(CoreSession.IMPORT_VERSION_LABEL,
                ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_VERSION_LABEL));
        localDocument.putContextData(CoreSession.IMPORT_VERSION_DESCRIPTION,
                ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_VERSION_DESCRIPTION));

        propertyValue = new DateType().encode(ImportUtils.getContextDataInfo(
                contextData, CoreSession.IMPORT_VERSION_CREATED));
        localDocument.putContextData(CoreSession.IMPORT_VERSION_CREATED,
                propertyValue);
        localDocument.putContextData(CoreSession.IMPORT_VERSION_MAJOR,
                Long.valueOf(ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_VERSION_MAJOR)));
        localDocument.putContextData(CoreSession.IMPORT_VERSION_MINOR,
                Long.valueOf(ImportUtils.getContextDataInfo(contextData,
                        CoreSession.IMPORT_VERSION_MINOR)));

        setProperties();
        runUnrestrictedImport();
        log.debug("Finishing the process of adding on the client side: " + name);
    }
}
