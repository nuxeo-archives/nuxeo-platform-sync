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
package org.nuxeo.ecm.platform.sync.beans;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.relations.web.listener.RelationActions;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:cbaican@nuxeo.com">Catalin Baican</a>
 */
@Scope(ScopeType.SESSION)
@Name("syncActions")
public class SyncActionsBean implements Serializable {

    private static final Logger log = Logger.getLogger(SyncActionsBean.class);

    private static final long serialVersionUID = 1L;

    @In(create = true)
    private transient NavigationContext navigationContext;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    @In(create = true, required = false)
    private transient RelationActions relationActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    /**
     * This will keep all the information about the synchronization process
     */
    private SynchronizeDetails synchronizeDetails;

    private SynchronizeService synchronizeService;

    @Create
    public void initialize() {
        log.debug("Initializing syncActions ...");
        synchronizeDetails = getSynchronizeService().getDefaultDetails();
        if (synchronizeDetails == null) {
            synchronizeDetails = new SynchronizeDetails();
        }
    }

    public String doSynchronize() {
        try {
            getSynchronizeService().synchronizeDocuments(documentManager, synchronizeDetails, "QUERY_ALL");
        } catch (Exception e) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "feedback.sync.error", e.getMessage());
            log.error("Sync error: ", e);
            return null;
        }
        return goHome();
    }

    public String doSynchronizeVocabularies() {
        getSynchronizeService().synchronizeVocabularies(synchronizeDetails);
        return goHome();
    }

    public String doSynchronizeRelations() {
        getSynchronizeService().synchronizeRelations(synchronizeDetails);
        relationActions.resetStatements();
        return goHome();
    }

    private String goHome() {
        facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "feedback.sync.success");
        DocumentModel root;
        try {
            root = documentManager.getDocument(new PathRef("/"));
            navigationContext.setCurrentDocument(root);
            return navigationContext.navigateToDocument(root);
        } catch (DocumentSecurityException e) {
            log.error("Couldn't navigate to root document", e);
        }
        return "home";
    }

    public SynchronizeDetails getSynchronizeDetails() {
        return synchronizeDetails;
    }

    public void setSynchronizeDetails(SynchronizeDetails synchronizeDetails) {
        this.synchronizeDetails = synchronizeDetails;
    }

    private SynchronizeService getSynchronizeService() {
        if (synchronizeService == null) {
            synchronizeService = Framework.getService(SynchronizeService.class);
        }
        return synchronizeService;
    }
}
