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
package org.nuxeo.ecm.platform.sync.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * Utility class used to ease the vocabulary management.
 *
 * @author mcedica
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

    public static List<String> getAllVocabularies() {
        List<String> vocabularyNames = new ArrayList<String>();
        for (String string : getDirectoryService().getDirectoryNames()) {
            String type = getDirectoryService().getDirectorySchema(string);
            if (type.equals(VOCABULARY_TYPE_HIER) || type.equals(VOCABULARY_TYPE_SIMPLE)) {
                vocabularyNames.add(string);
            }
        }
        return vocabularyNames;
    }

    public static void deleteVocabularyEntry(String vocabularyName, VocabularyEntry selectedVocabularyEntry) {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                if (getDirectoryService().getDirectorySchema(vocabularyName).equals(VOCABULARY_TYPE_HIER)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(VOCABULARY_PARENT, selectedVocabularyEntry.getParent());
                    vocabulary.deleteEntry(selectedVocabularyEntry.getId(), map);
                } else {
                    vocabulary.deleteEntry(selectedVocabularyEntry.getId());
                }
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

    public static void addVocabularyEntry(String vocabularyName, Map<String, Object> entry) {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                vocabulary.createEntry(entry);
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

    public static void clearVocabulary(String vocabularyName) {
        Session vocabulary = null;
        try {
            vocabulary = openVocabulary(vocabularyName);
            if (vocabulary != null) {
                for (DocumentModel entry : vocabulary.getEntries()) {
                    vocabulary.deleteEntry(entry.getId());
                }
            }
        } finally {
            if (vocabulary != null) {
                vocabulary.close();
            }
        }
    }

    public static String getDirectorySchema(String vocabularyName) {
        return getDirectoryService().getDirectorySchema(vocabularyName);
    }

    public static Session openVocabulary(String vocabularyName) {
        return getDirectoryService().open(vocabularyName);
    }

    public static DirectoryService getDirectoryService() {
        if (dirService == null) {
            dirService = Framework.getLocalService(DirectoryService.class);
        }
        if (dirService == null) {
            throw new NuxeoException("Unable to get directoryService...");
        }
        return dirService;
    }

}
