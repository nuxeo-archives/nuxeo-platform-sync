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
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.sync.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.ecm.core.schema.types.primitives.IntegerType;
import org.nuxeo.ecm.core.schema.types.primitives.LongType;
import org.nuxeo.ecm.core.schema.types.primitives.StringType;
import org.nuxeo.ecm.platform.sync.api.SynchronizeReport;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;
import org.nuxeo.ecm.platform.sync.utils.SynchHttpClient;
import org.nuxeo.ecm.platform.sync.utils.VocabularyUtils;
import org.nuxeo.runtime.api.Framework;

/**
 * The manager to take care the vocabulary set synchronization. It simple replaces the local vocabularies with the ones
 * from server.
 *
 * @author mcedica
 */
public class VocabularySynchronizeManager {
    SynchHttpClient httpClient = null;

    private static final Log log = LogFactory.getLog(VocabularySynchronizeManager.class);

    public VocabularySynchronizeManager(SynchronizeDetails synchronizeDetails) {
        httpClient = new SynchHttpClient(synchronizeDetails);

    }

    public SynchronizeReport performChanges() {
        // get all vocabularies
        List<String> vocabularyNames = VocabularyUtils.getAllVocabularies();
        for (String vocabularyName : vocabularyNames) {
            // delete all entries for this one
            VocabularyUtils.clearVocabulary(vocabularyName);
            // get all the entries from server
            List<String> pathParams = Arrays.asList("vocabularyRestlet", vocabularyName);
            syncronize(vocabularyName, pathParams);
        }

        return SynchronizeReport.newVocabulariesReport(vocabularyNames);
    }

    private void syncronize(String vocabularyName, List<String> pathParams) {

        try {
            InputStream inputStream = httpClient.executeGetCall(pathParams, null);

            byte[] bytesRead = IOUtils.toByteArray(inputStream);

            if (bytesRead == null) {
                return;
            }
            String xmlRep = null;
            xmlRep = new String(bytesRead);

            List<Map<String, Object>> mappedEntries = getMappedEntries(vocabularyName, xmlRep);
            for (Map<String, Object> map : mappedEntries) {
                VocabularyUtils.addVocabularyEntry(vocabularyName, map);
            }

        } catch (Exception e) {
            log.error("Unable to syncronize vocabularies...", e);
        } finally {
            // close connection
            httpClient.closeConnection();
        }

    }

    protected List<Map<String, Object>> getMappedEntries(String vocabularyName, String res) {
        try {
            String directorySchema = VocabularyUtils.getDirectorySchema(vocabularyName);
            return extractVocabulary(directorySchema, res);
        } catch (DocumentException e) {
            throw new Error("Unexpected error occured while parsing the vocabularies", e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected List<Map<String, Object>> extractVocabulary(String directorySchema, String res) throws DocumentException {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Document domDoc = DocumentHelper.parseText(res);
        List nodes = domDoc.selectNodes("//entries/entry");

        SchemaManager schemaManager = Framework.getLocalService(SchemaManager.class);
        Schema vocSchema = schemaManager.getSchema(directorySchema);
        Collection<Field> vocSchemaField = vocSchema.getFields();
        for (Object object : nodes) {
            Node nodeEntry = ((Node) object);
            Map<String, Object> entryMap = new HashMap<String, Object>();
            for (Field field : vocSchemaField) {
                String fieldName = field.getName().getLocalName();
                Type type = field.getType();
                String entryValue = nodeEntry.valueOf("@" + fieldName);
                if (!entryValue.trim().isEmpty()) {
                    if (type instanceof StringType) {
                        entryMap.put(fieldName, entryValue);
                    } else if (type instanceof LongType) {
                        Long longEntry = Long.valueOf(entryValue);
                        entryMap.put(fieldName, longEntry);
                    } else if (type instanceof IntegerType) {
                        Integer integerEntry = Integer.valueOf(entryValue);
                        entryMap.put(fieldName, integerEntry);
                    } else {
                        log.warn("Vocabulary sychronizer only serialize int, long or string fields type. " + fieldName
                                + "(" + type.getName() + ") has been ignored");
                    }
                }
            }

            list.add(entryMap);
        }
        return list;
    }

}
