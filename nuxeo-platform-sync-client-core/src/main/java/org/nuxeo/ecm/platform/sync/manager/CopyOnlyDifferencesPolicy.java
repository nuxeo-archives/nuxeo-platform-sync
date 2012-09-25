/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     matic
 */
package org.nuxeo.ecm.platform.sync.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple;

/**
 * @author matic
 *
 */
public class CopyOnlyDifferencesPolicy extends DefaultDocumentDifferencesPolicy {


    public static final class CompoundError extends Exception {

        private static final long serialVersionUID = 1L;
        
 
        public CompoundError() {
            super("Compound errors, see nested errors");
        }
        
        protected final List<Exception> nestedErrors = new ArrayList<Exception>();

        public Exception[] getNested() {
            return nestedErrors.toArray(new Exception[nestedErrors.size()]);
        }
    }
    
    public static final class DeletedDocumentsError extends Exception {
        
        private static final long serialVersionUID = 1L;
        
        public final String[] ids;

        protected DeletedDocumentsError(List<String> ids) {
            super("Detected deleted documents, please check query results both sides (" + ToStringBuilder.reflectionToString(ids, ToStringStyle.SIMPLE_STYLE) + ")");
            this.ids = ids.toArray(new String[ids.size()]);
        }
    }
    
    public static class TuplesError extends Exception {

        private static final long serialVersionUID = 1L;
        
        public final NuxeoSynchroTuple[] tuples;
        
        protected TuplesError(String kind, List<NuxeoSynchroTuple> tuples) {
            super("Detected " + kind + " documents, please check query results both side (" + ToStringBuilder.reflectionToString(tuples, ToStringStyle.SIMPLE_STYLE) + ")");
            this.tuples = tuples.toArray(new NuxeoSynchroTuple[tuples.size()]);
        }
    }
    
    @Override
    public void process(DocumentModelList availableDocs,
            List<NuxeoSynchroTuple> tuples,
            List<NuxeoSynchroTuple> addedTuples,
            List<NuxeoSynchroTuple> modifiedTuples, List<String> deletedIds,
            List<NuxeoSynchroTuple> movedTuples) throws Exception {
        super.process(availableDocs, tuples, addedTuples, modifiedTuples, deletedIds,
                movedTuples);
        CompoundError warns = new CompoundError();
        if (!deletedIds.isEmpty()) {
            warns.nestedErrors.add(new IllegalStateException("Detected deleted documents, please query results both side"));
        }
        if (!modifiedTuples.isEmpty()) {
            warns.nestedErrors.add(new IllegalStateException("Detected modified documents, please query results both side"));
        }
        if (!movedTuples.isEmpty()) {
            warns.nestedErrors.add(new IllegalStateException("Detected moved documents, please query results both side"));
        }
        if (!warns.nestedErrors.isEmpty()) {
            throw warns;
        }
    }
}
