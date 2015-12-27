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

package org.nuxeo.ecm.platform.sync.api;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;

/**
 * The Synchronize Service interface. It contains the service API. <li>synchronize documents <li>synchronize relations
 * <li>synchronize vocabularies
 *
 * @author rux
 */
public interface SynchronizeService {

    /**
     * Synchronizes all the documents by using the synchronization details.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     * @param queryName - the query name to use server side
     */
    SynchronizeReport synchronizeDocuments(CoreSession session, SynchronizeDetails details, String queryName);

    /**
     * Synchronizes all the relations defined on the documents by using the synchronization details.This should happen
     * after the documents synchronization.
     *
     * @param details - the details about the synchronization process
     */
    SynchronizeReport synchronizeRelations(SynchronizeDetails details);

    /**
     * Synchronizes all the vocabularies by using the synchronization details.This should happen after the documents
     * synchronization.
     *
     * @param details
     */
    SynchronizeReport synchronizeVocabularies(SynchronizeDetails details);

    /**
     * Performs the whole synchronization process including the documents relations and vocabularies synchronizations.
     *
     * @param session - the Nuxeo core session received from the web context
     * @param details - the details about the synchronization process
     */
    SynchronizeReport synchronize(CoreSession session, SynchronizeDetails details, String queryName);

    /**
     * Return the pre-configured connection parameters
     *
     * @return
     */
    SynchronizeDetails getDefaultDetails();

    /**
     * Test if the ReadSecurityPolicy should be disabled or not.
     *
     * @param docPath the document path to test
     * @param permission the permission to test
     * @return {@code true} if the ReadSecurityPolicy must be disabled, {@code false otherwise}
     */
    boolean shouldDisableReadSP(String docPath, String permission);
}
