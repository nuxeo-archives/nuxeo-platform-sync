/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     matic
 */
package org.nuxeo.ecm.platform.sync.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * @author matic
 */
public class SynchronizeReport {

    @SuppressWarnings("unchecked")
    public static SynchronizeReport newDocumentsReport(List<DocumentRef> added, List<DocumentRef> removed,
            List<DocumentRef> updated, List<DocumentRef> moved) {
        return new SynchronizeReport(added, removed, updated, moved, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    @SuppressWarnings("unchecked")
    public static SynchronizeReport newRelationsReport(List<String> graphs) {
        return new SynchronizeReport(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, graphs, Collections.EMPTY_LIST);
    }

    @SuppressWarnings("unchecked")
    public static SynchronizeReport newVocabulariesReport(List<String> vocabularies) {
        return new SynchronizeReport(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                Collections.EMPTY_LIST, Collections.EMPTY_LIST, vocabularies);
    }

    protected SynchronizeReport(List<DocumentRef> added, List<DocumentRef> removed, List<DocumentRef> updated,
            List<DocumentRef> moved, List<String> graphs, List<String> vocabularies) {
        this.added = added.toArray(new DocumentRef[added.size()]);
        this.removed = removed.toArray(new DocumentRef[removed.size()]);
        this.updated = updated.toArray(new DocumentRef[updated.size()]);
        this.moved = moved.toArray(new DocumentRef[moved.size()]);
        this.graphs = graphs.toArray(new String[graphs.size()]);
        this.vocabularies = vocabularies.toArray(new String[vocabularies.size()]);
    }

    public final DocumentRef[] removed;

    public final DocumentRef[] updated;

    public final DocumentRef[] added;

    public final DocumentRef[] moved;

    public final String[] graphs;

    public final String[] vocabularies;

    public SynchronizeReport merge(SynchronizeReport other) {
        List<DocumentRef> added = Arrays.asList(this.added);
        added.addAll(Arrays.asList(other.added));
        List<DocumentRef> removed = Arrays.asList(this.added);
        removed.addAll(Arrays.asList(other.removed));
        List<DocumentRef> updated = Arrays.asList(this.added);
        updated.addAll(Arrays.asList(other.updated));
        List<DocumentRef> moved = Arrays.asList(this.added);
        moved.addAll(Arrays.asList(other.moved));
        List<String> graphs = Arrays.asList(this.graphs);
        graphs.addAll(Arrays.asList(other.graphs));
        List<String> vocabularies = Arrays.asList(this.vocabularies);
        vocabularies.addAll(Arrays.asList(other.vocabularies));
        return new SynchronizeReport(added, removed, updated, moved, graphs, vocabularies);
    }

    public DocumentRef[] getRemoved() {
        return removed;
    }

    public DocumentRef[] getUpdated() {
        return updated;
    }

    public DocumentRef[] getAdded() {
        return added;
    }

    public DocumentRef[] getMoved() {
        return moved;
    }

    public String[] getGraphs() {
        return graphs;
    }

    public String[] getVocabularies() {
        return vocabularies;
    }

}
