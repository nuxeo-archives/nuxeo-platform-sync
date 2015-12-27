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
 */

package org.nuxeo.ecm.platform.sync.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.SynchronizeService;
import org.nuxeo.ecm.platform.sync.api.util.MonitorProvider;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.manager.DefaultDocumentDifferencesPolicy;
import org.nuxeo.ecm.platform.sync.manager.DocumentDifferencesPolicy;
import org.nuxeo.ecm.platform.sync.manager.DocumentsSynchronizeManager;
import org.nuxeo.ecm.platform.sync.manager.RelationsSynchronizeManager;
import org.nuxeo.ecm.platform.sync.manager.VocabularySynchronizeManager;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoWSMainEntrancePointService;
import org.nuxeo.ecm.platform.sync.webservices.generated.WSSynchroServerModuleService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
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

    private SynchronizeDetails defaultSynchronizeDetails = SynchronizeDetails.DEFAULTS;

    private DocumentDifferencesPolicy diffPolicy = new DefaultDocumentDifferencesPolicy();

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (IMPORT_CONFIGURATION_EP.equals(extensionPoint)) {
            importConfiguration = (ImportConfiguration) contribution;
        } else if (DISABLE_READ_SP_EP.equals(extensionPoint)) {
            disableReadSecurityPolicyDescriptor = (DisableReadSecurityPolicyDescriptor) contribution;
        } else if (DEFAULT_SYNCHRONIZE_DETAILS_EP.equals(extensionPoint)) {
            SynchronizeDetailsDescriptor desc = (SynchronizeDetailsDescriptor) contribution;
            defaultSynchronizeDetails = new SynchronizeDetails(desc.getUsername(), desc.getPassword(),
                    desc.getProtocol(), desc.getHost(), desc.getPort(), desc.getContextPath());
        } else if (DOCUMENT_DIFFERENCES_POLICY_EP.equals(extensionPoint)) {
            DocumentDifferencesPolicyDescriptor desc = (DocumentDifferencesPolicyDescriptor) contribution;
            try {
                diffPolicy = desc.getPolicyClass().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public SynchronizeReport synchronizeDocuments(CoreSession session, SynchronizeDetails details, String queryName) {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        MonitorProvider.getMonitor().setTaskName("Synchronizing Documents");

        URL baseUrl;
        baseUrl = NuxeoWSMainEntrancePointService.class.getResource(".");

        String baseSpec = details.getUrl();

        URL url;
        try {
            url = new URL(baseUrl, baseSpec + "/webservices/wssyncroentry?wsdl");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        NuxeoWSMainEntrancePointService.NUXEOWSMAINENTRANCEPOINTSERVICE_WSDL_LOCATION = url;

        baseUrl = WSSynchroServerModuleService.class.getResource(".");
        try {
            url = new URL(baseUrl, baseSpec + "/webservices/wssyncroserver?wsdl");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        WSSynchroServerModuleService.WSSYNCHROSERVERMODULESERVICE_WSDL_LOCATION = url;
        if (getUserManager().getPrincipal(details.getUsername()) == null) {
            log.debug("In case user does not exists on the local machine, then register it...");
            DocumentModel userModel = getUserManager().getBareUserModel();

            String userSchemaName = getUserManager().getUserSchemaName();
            userModel.setProperty(userSchemaName, "username", details.getUsername());
            userModel.setProperty(userSchemaName, "password", details.getPassword());
            userModel.setProperty(userSchemaName, "groups", getUserManager().getAdministratorsGroups().toArray());
            getUserManager().createUser(userModel);
        }
        DocumentsSynchronizeManager documentSynchronizeManager = new DocumentsSynchronizeManager(session, details,
                queryName, importConfiguration, diffPolicy);
        documentSynchronizeManager.run();
        return documentSynchronizeManager.getReport();
    }

    @Override
    public SynchronizeReport synchronizeRelations(SynchronizeDetails details) {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        MonitorProvider.getMonitor().setTaskName("Synchronizing relations");

        RelationsSynchronizeManager relationSynchronizer = new RelationsSynchronizeManager(details);
        return relationSynchronizer.performChanges();
    }

    @Override
    public SynchronizeReport synchronizeVocabularies(SynchronizeDetails details) {
        if (details == null) {
            throw new IllegalArgumentException("Cannot synchronize without synchronization details");
        }

        MonitorProvider.getMonitor().setTaskName("Synchronizing Vocabularies");

        VocabularySynchronizeManager vocabularySynchronizer = new VocabularySynchronizeManager(details);
        return vocabularySynchronizer.performChanges();
    }

    @Override
    public SynchronizeReport synchronize(CoreSession session, SynchronizeDetails details, String queryName) {
        return synchronizeDocuments(session, details, queryName).merge(synchronizeRelations(details)).merge(
                synchronizeVocabularies(details));
    }

    protected UserManager getUserManager() {
        if (userManager == null) {
            userManager = Framework.getLocalService(UserManager.class);
        }
        return userManager;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }

    @Override
    public SynchronizeDetails getDefaultDetails() {
        return defaultSynchronizeDetails;
    }

    @Override
    public boolean shouldDisableReadSP(String docPath, String permission) {
        if (disableReadSecurityPolicyDescriptor != null) {
            return disableReadSecurityPolicyDescriptor.shouldDisable(docPath, permission);
        }
        return false;
    }

}
