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
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.processor;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.sync.utils.ImportUtils;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * The base class for processing a tuple when adding a new document. It holds the common processing for this case.
 *
 * @author rux
 */
public abstract class TupleProcessorAdd extends TupleProcessorUpdate {

    private static final Logger log = Logger.getLogger(TupleProcessorAdd.class);

    protected String propertyValue;

    public TupleProcessorAdd(CoreSession session, NuxeoSynchroTuple tuple) {
        super(session, tuple);
    }

    /**
     * Runs the import of a new document in unrestricted mode.
     *
     */
    protected void runUnrestrictedImport() {
        new UnrestrictedImport(session, localDocument).runUnrestricted();
    }

    /**
     * The unrestricted runner for running the import of a document.
     *
     * @author rux
     */
    protected static class UnrestrictedImport extends UnrestrictedSessionRunner {
        DocumentModel documentModel;

        public UnrestrictedImport(CoreSession session, DocumentModel documentModel) {
            super(session);
            this.documentModel = documentModel;
        }

        @Override
        public void run() {
            try {
                if (documentModel.getId() != null && session.exists(new IdRef(documentModel.getId()))) {
                    log.error("Doc " + documentModel.getId() + " already exists, not importing it");
                } else {
                    log.debug("Importing doc " + documentModel.getId());
                    session.importDocuments(Collections.singletonList(documentModel));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            session.save();
        }
    }

    /**
     * Sets life cycle details on localDocument as super user.
     */
    @Override
    protected void setLifeCycle() {
        String lifecycle = ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LIFECYCLE_STATE);
        if (importConfiguration != null) {
            String importLC = importConfiguration.getClientLifeCycleStateFor(lifecycle);
            if (importLC != null && importLC.length() > 0)
                lifecycle = importLC;
        }
        localDocument.putContextData(CoreSession.IMPORT_LIFECYCLE_STATE, lifecycle);
        localDocument.putContextData(CoreSession.IMPORT_LIFECYCLE_POLICY,
                ImportUtils.getContextDataInfo(contextData, CoreSession.IMPORT_LIFECYCLE_POLICY));
    }

}
