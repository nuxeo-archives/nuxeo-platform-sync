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

package org.nuxeo.ecm.platform.sync.api;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;

import java.util.List;

/**
 * The Synchronize Service interface. It contains the service API. <li>
 * synchronize documents <li>synchronize relations <li>synchronize vocabularies
 *
 * @author rux
 */
public interface SynchronizeService {

    /**
     * Synchronizes all the documents by using the synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     * @throws ClientException
     */
    void doSynchronizeDocuments(CoreSession session, SynchronizeDetails details)
            throws Exception;

    /**
     * Synchronizes all the documents by using the default synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @throws ClientException
     */
    void doSynchronizeDocuments(CoreSession session) throws Exception;

    /**
     * Synchronizes all the documents by using the synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     * @param queryName - the query name to use server side
     * @throws ClientException
     */
    void doSynchronizeDocuments(CoreSession session, SynchronizeDetails details, String queryName)
            throws Exception;

    /**
     * Synchronizes all the documents by using the default synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param queryName - the query name to use server side
     * @throws ClientException
     */
    void doSynchronizeDocuments(CoreSession session, String queryName)
            throws Exception;

    /**
     * Synchronizes all the relations defined on the documents by using the
     * synchronization details.This should happen after the documents
     * synchronization.
     *
     * @param details - the details about the synchronization process
     * @throws ClientException
     */
    void doSynchronizeRelations(SynchronizeDetails details)
            throws ClientException;

    /**
     * Synchronizes all the relations defined on the documents by using the
     * default synchronization details.This should happen after the documents
     * synchronization.
     *
     * @throws ClientException
     */
    void doSynchronizeRelations() throws ClientException;

    /**
     * Synchronizes all the vocabularies by using the synchronization
     * details.This should happen after the documents synchronization.
     *
     * @param details
     * @throws ClientException
     */
    void doSynchronizeVocabularies(SynchronizeDetails details)
            throws ClientException;

    /**
     * Synchronizes all the vocabularies by using the default synchronization
     * details.This should happen after the documents synchronization.
     *
     * @throws ClientException
     */
    void doSynchronizeVocabularies() throws ClientException;

    /**
     * Performs the whole synchronization process including the documents
     * relations and vocabularies synchronizations.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     * @throws Exception
     */
    void doSynchronize(CoreSession session, SynchronizeDetails details)
            throws Exception;

    /**
     * Performs the whole synchronization process including the documents
     * relations and vocabularies synchronizations using the default
     * synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @throws Exception
     */
    void doSynchronize(CoreSession session) throws Exception;

    /**
     * Performs the whole synchronization process including the documents
     * relations and vocabularies synchronizations.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     * @throws Exception
     */
    void doSynchronize(CoreSession session, SynchronizeDetails details, String queryName)
            throws Exception;

    /**
     * Performs the whole synchronization process including the documents
     * relations and vocabularies synchronizations using the default
     * synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @throws Exception
     */
    void doSynchronize(CoreSession session, String queryName) throws Exception;

    List<String> getDocumentPathsToAvoidSecurityPolicy();

    SynchronizeDetails getDefaultSynchronizeDetails();
}
