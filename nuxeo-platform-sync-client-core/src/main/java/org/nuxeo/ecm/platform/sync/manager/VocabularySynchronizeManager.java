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
package org.nuxeo.ecm.platform.sync.manager;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.utils.SynchHttpClient;
import org.nuxeo.ecm.platform.sync.utils.VocabularyEntry;
import org.nuxeo.ecm.platform.sync.utils.VocabularyUtils;
import org.nuxeo.ecm.platform.sync.vocabularies.generated.Entries;
import org.nuxeo.ecm.platform.sync.vocabularies.generated.Entry;

/**
 * The manager to take care the vocabulary set synchronization. It simple
 * replaces the local vocabularies with the ones from server.
 *
 * @author mcedica
 *
 */
public class VocabularySynchronizeManager {
    SynchHttpClient httpClient = null;

    private static final Log log = LogFactory.getLog(VocabularySynchronizeManager.class);

    public VocabularySynchronizeManager(SynchronizeDetails synchronizeDetails) {
        httpClient = new SynchHttpClient(synchronizeDetails);

    }

    public void performChanges() throws ClientException {
        // get all vocabularies
        List<String> vocabularyNames = VocabularyUtils.getAllVocabularies();
        for (String vocabularyName : vocabularyNames) {
            // delete all entries for this one
            VocabularyUtils.clearVocabulary(vocabularyName);
            // get all the entries from server
            List<String> pathParams = Arrays.asList("vocabularyRestlet",
                    vocabularyName);
            syncronize(vocabularyName, pathParams);
        }

    }

    private void syncronize(String vocabularyName, List<String> pathParams)
            throws ClientException {

        try {
            InputStream inputStream = httpClient.executeGetCall(pathParams,
                    null);

            byte[] bytesRead = IOUtils.toByteArray(inputStream);

            if (bytesRead == null) {
                return;
            }
            String xmlRep = null;
            xmlRep = new String(bytesRead);
            // System.out.print(xmlRep);
            String directorySchema = VocabularyUtils.getDirectorySchema(vocabularyName);

            List<Entry> entries = getEntries(vocabularyName, xmlRep);
            for (Entry entry : entries) {
                Integer obsolete = entry.getObsolete();
                if (obsolete == null) {
                    obsolete = 0;
                }
                if (directorySchema.equals("vocabulary")) {
                    VocabularyEntry ventry = new VocabularyEntry(entry.getId(),
                            entry.getLabel());
                    ventry.setObsolete(obsolete == 1 ? true : false);
                    VocabularyUtils.addVocabularyEntry(vocabularyName, ventry);
                }
                if (directorySchema.equals("xvocabulary")) {
                    VocabularyEntry ventry = new VocabularyEntry(entry.getId(),
                            entry.getLabel(), entry.getParent());
                    ventry.setObsolete(obsolete == 1 ? true : false);
                    VocabularyUtils.addVocabularyEntry(vocabularyName, ventry);
                }
            }
        } catch (Exception e) {
            log.error("Unable to syncronize vocabularies...", e);
        } finally {
            // close connection
            httpClient.closeConnection();
        }

    }

    private List<Entry> getEntries(String vocabularyName, String res)
            throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("org.nuxeo.ecm.platform.sync.vocabularies.generated");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if (res == null) {
            log.error("Unable to read vocabulary " + vocabularyName);
            return null;
        }
        Entries vocabularyXML = (Entries) ((JAXBElement<Entries>) unmarshaller.unmarshal(
                new StreamSource(new StringReader(res)), Entries.class)).getValue();
        return vocabularyXML.getEntry();
    }

}
