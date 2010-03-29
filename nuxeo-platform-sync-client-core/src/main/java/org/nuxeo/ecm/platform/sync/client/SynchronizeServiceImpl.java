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
 */

package org.nuxeo.ecm.platform.sync.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.manager.DocumentDifferencesPolicy;
import org.nuxeo.ecm.platform.sync.manager.DocumentsSynchronizeManager;
import org.nuxeo.ecm.platform.sync.manager.RelationsSynchronizeManager;
import org.nuxeo.ecm.platform.sync.manager.VocabularySynchronizeManager;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoWSMainEntrancePointService;
import org.nuxeo.ecm.platform.sync.webservices.generated.WSSynchroServerModuleService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * The Synchronize Service implementation.
 *
 * @author rux
 */
public class SynchronizeServiceImpl extends DefaultComponent implements SynchronizeService {

    private static final String IMPORT_CONFIGURATION_EP = "importConfiguration";

    private static final String DISABLE_READ_SP_EP = "disableReadSecurityPolicy";

    private static final String DEFAULT_SYNCHRONIZE_DETAILS_EP = "defaultSynchronizeDetails";

    private static final String DOCUMENT_DIFFERENCES_POLICY_EP = "documentDifferencesPolicy";

    private static final Logger log = Logger.getLogger(SynchronizeServiceImpl.class);

    private UserManager userManager;

    private ImportConfiguration importConfiguration;

    private DisableReadSecurityPolicyDescriptor disableReadSecurityPolicyDescriptor;

    private SynchronizeDetails defaultSynchronizeDetails;

    private DocumentDifferencesPolicy documentDifferencesPolicy;

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
        if (IMPORT_CONFIGURATION_EP.equals(extensionPoint)) {
            importConfiguration = (ImportConfiguration) contribution;
        } else if (DISABLE_READ_SP_EP.equals(extensionPoint)) {
            disableReadSecurityPolicyDescriptor = (DisableReadSecurityPolicyDescriptor) contribution;
        } else if (DEFAULT_SYNCHRONIZE_DETAILS_EP.equals(extensionPoint)) {
            SynchronizeDetailsDescriptor desc = (SynchronizeDetailsDescriptor) contribution;
            defaultSynchronizeDetails = new SynchronizeDetails(
                    desc.getUsername(), desc.getPassword(), desc.getHost(),
                    desc.getPort());
        } else if (DOCUMENT_DIFFERENCES_POLICY_EP.equals(extensionPoint)) {
            DocumentDifferencesPolicyDescriptor desc = (DocumentDifferencesPolicyDescriptor) contribution;
            documentDifferencesPolicy = desc.getPolicyClass().newInstance();
        }
    }

    public void doSynchronizeDocuments(CoreSession session,
            SynchronizeDetails details) throws Exception {
        doSynchronizeDocuments(session, details, null);
    }

    public void doSynchronizeDocuments(CoreSession session) throws Exception {
        doSynchronizeDocuments(session, getDefaultSynchronizeDetails(), null);
    }

    public void doSynchronizeDocuments(CoreSession session, SynchronizeDetails details, String queryName) throws Exception {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        URL baseUrl;
        baseUrl = NuxeoWSMainEntrancePointService.class.getResource(".");
        URL url = new URL(baseUrl, "http://" + details.getHost() + ":"
                + details.getPort() + VirtualHostHelper.getContextPathProperty() + "/webservices/wssyncroentry?wsdl");
        NuxeoWSMainEntrancePointService.NUXEOWSMAINENTRANCEPOINTSERVICE_WSDL_LOCATION = url;

        baseUrl = WSSynchroServerModuleService.class.getResource(".");
        url = new URL(baseUrl, "http://" + details.getHost() + ":"
                + details.getPort() + VirtualHostHelper.getContextPathProperty() + "/webservices/wssyncroserver?wsdl");
        WSSynchroServerModuleService.WSSYNCHROSERVERMODULESERVICE_WSDL_LOCATION = url;
        if (getUserManager().getPrincipal(details.getUsername()) == null) {
            log.debug("In case user does not exists on the local machine, then register it...");
            DocumentModel userModel = getUserManager().getBareUserModel();

            String userSchemaName = getUserManager().getUserSchemaName();
            userModel.setProperty(userSchemaName, "username",
                    details.getUsername());
            userModel.setProperty(userSchemaName, "password",
                    details.getPassword());
            userModel.setProperty(userSchemaName, "groups",
                    new String[] { "administrators" });
            getUserManager().createUser(userModel);
        }
        DocumentsSynchronizeManager documentSynchronizeManager = new DocumentsSynchronizeManager(
                session, details, queryName, importConfiguration, documentDifferencesPolicy);
        documentSynchronizeManager.run();
    }

    public void doSynchronizeDocuments(CoreSession session, String queryName) throws Exception {
        doSynchronizeDocuments(session, getDefaultSynchronizeDetails(), queryName);
    }

    public void doSynchronizeRelations(SynchronizeDetails details)
            throws ClientException {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        RelationsSynchronizeManager relationSynchronizer = new RelationsSynchronizeManager(
                details);
        relationSynchronizer.performChanges();
    }

    public void doSynchronizeRelations() throws ClientException {
        doSynchronizeRelations(getDefaultSynchronizeDetails());
    }

    public void doSynchronizeVocabularies(SynchronizeDetails details)
            throws ClientException {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        VocabularySynchronizeManager vocabularySynchronizer = new VocabularySynchronizeManager(
                details);
        vocabularySynchronizer.performChanges();
    }

    public void doSynchronizeVocabularies() throws ClientException {
        doSynchronizeVocabularies(getDefaultSynchronizeDetails());
    }

    public void doSynchronize(CoreSession session, SynchronizeDetails details)
            throws Exception {
        doSynchronizeDocuments(session, details);
        doSynchronizeRelations(details);
        doSynchronizeVocabularies(details);
    }

    public void doSynchronize(CoreSession session) throws Exception {
        doSynchronize(session, getDefaultSynchronizeDetails());
    }

    public void doSynchronize(CoreSession session, SynchronizeDetails details, String queryName) throws Exception {
        doSynchronizeDocuments(session, details, queryName);
        doSynchronizeRelations(details);
        doSynchronizeVocabularies(details);
    }

    public void doSynchronize(CoreSession session, String queryName) throws Exception {
        doSynchronize(session, getDefaultSynchronizeDetails(), queryName);
    }

    protected UserManager getUserManager() throws Exception {
        if (userManager == null) {
            userManager = Framework.getLocalService(UserManager.class);
        }
        return userManager;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }

    public SynchronizeDetails getDefaultSynchronizeDetails() {
        return defaultSynchronizeDetails;
    }

    public boolean shouldDisableReadSP(String docPath, String permission) {
        if (disableReadSecurityPolicyDescriptor != null) {
            return disableReadSecurityPolicyDescriptor.shouldDisable(docPath, permission);
        }
        return false;
    }

}
