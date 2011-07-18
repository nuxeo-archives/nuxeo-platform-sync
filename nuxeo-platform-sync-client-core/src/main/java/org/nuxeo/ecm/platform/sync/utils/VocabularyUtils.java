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
package org.nuxeo.ecm.platform.sync.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * Utility class used to ease the vocabulary management.
 *
 * @author mcedica
 *
 */
public class VocabularyUtils {

    public static final String VOCABULARY_PARENT = "parent";

    public static final String VOCABULARY_TYPE_HIER = "xvocabulary";

    public static final String VOCABULARY_TYPE_SIMPLE = "vocabulary";

    public static final String VOCABULARY_ID = "id";

    public static final String VOCABULARY_LABEL = "label";

    public static final String VOCABULARY_OBSOLETE = "obsolete";

    public static final String VOCABULARY_ORDERING = "ordering";

    public static final Integer DEFAULT_OBSOLETE = 0;

    public static final Integer DEFAULT_VOCABULARY_ORDER = 10000000;

    static DirectoryService dirService = null;

    private static final Log log = LogFactory.getLog(VocabularyUtils.class);

    public static List<String> getAllVocabularies() throws ClientException {
        List<String> vocabularyNames = new ArrayList<String>();
        for (String string : getDirectoryService().getDirectoryNames()) {
            String type = getDirectoryService().getDirectorySchema(string);
            if (type.equals(VOCABULARY_TYPE_HIER)
                    || type.equals(VOCABULARY_TYPE_SIMPLE)) {
                vocabularyNames.add(string);
            }
        }
        return vocabularyNames;
    }

    public static void deleteVocabularyEntry(String vocabularyName,
            VocabularyEntry selectedVocabularyEntry) {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                if (getDirectoryService().getDirectorySchema(vocabularyName).equals(
                        VOCABULARY_TYPE_HIER)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(VOCABULARY_PARENT,
                            selectedVocabularyEntry.getParent());
                    vocabulary.deleteEntry(selectedVocabularyEntry.getId(), map);
                } else {
                    vocabulary.deleteEntry(selectedVocabularyEntry.getId());
                }
                vocabulary.commit();
            }
        } catch (Exception e) {
            log.error("Unable to delete entry vocabulary", e);
        } finally {
            if (vocabulary != null) {
                try {
                    vocabulary.close();
                } catch (DirectoryException e) {
                    log.error(e);
                }
            }
        }
    }

    public static void addVocabularyEntry(String vocabularyName,
            VocabularyEntry selectedVocabularyEntry) {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put(VOCABULARY_ID, selectedVocabularyEntry.getId());
                values.put(VOCABULARY_LABEL, selectedVocabularyEntry.getLabel());
                values.put(
                        VOCABULARY_OBSOLETE,
                        Boolean.TRUE.equals(selectedVocabularyEntry.getObsolete()) ? 1L
                                : DEFAULT_OBSOLETE);
                if (getDirectoryService().getDirectorySchema(vocabularyName).equals(
                        VOCABULARY_TYPE_HIER)) {
                    String parent = selectedVocabularyEntry.getParent();
                    if ("".equals(parent)) {
                        parent = null;
                    }
                    values.put(VOCABULARY_PARENT, parent);
                }
                values.put(
                        VOCABULARY_ORDERING,
                        selectedVocabularyEntry.getOrdering() != null ? selectedVocabularyEntry.getOrdering()
                                : DEFAULT_VOCABULARY_ORDER);
                vocabulary.createEntry(values);
                vocabulary.commit();
            }
        } catch (Exception e) {
            log.error("Unable to insert entry into vocabulary", e);
        } finally {
            if (vocabulary != null) {
                try {
                    vocabulary.close();
                } catch (DirectoryException e) {
                    log.error(e);
                }
            }
        }
    }

    public static void clearVocabulary(String vocabularyName)
            throws ClientException {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                for (DocumentModel entry : vocabulary.getEntries()) {
                    vocabulary.deleteEntry(entry.getId());
                }
                vocabulary.commit();
            }
        } finally {
            if (vocabulary != null) {
                vocabulary.close();
            }
        }
    }

    public static String getDirectorySchema(String vocabularyName)
            throws ClientException {
        return getDirectoryService().getDirectorySchema(vocabularyName);
    }

    public static Session openVocabulary(String vocabularyName)
            throws ClientException {
        return getDirectoryService().open(vocabularyName);
    }

    public static DirectoryService getDirectoryService() throws ClientException {
        if (dirService == null) {
            dirService = Framework.getLocalService(DirectoryService.class);
        }
        if (dirService == null) {
            throw new ClientException("Unable to get directoryService...");
        }
        return dirService;
    }

}
