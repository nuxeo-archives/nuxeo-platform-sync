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
 *     mcedica
 */
package org.nuxeo.ecm.platform.sync.server.tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.primitives.DateType;
import org.nuxeo.ecm.platform.api.ws.DocumentProperty;
import org.nuxeo.ecm.platform.api.ws.WsACE;
import org.nuxeo.ecm.platform.ws.NuxeoRemotingBean;
import org.nuxeo.runtime.api.Framework;

/**
 * Class factory for flagged document shanpshot initialization. The code is basically the same as in
 * {@link NuxeoRemotingBean} but: that code is private and also we need the hasBlobs flag.
 *
 * @author mcedica
 */
public class FlagedDocumentSnapshotFactory {

    private boolean hasBlobs;

    public FlagedDocumentSnapshotFactory() {
    }

    /**
     * Creates a new FlagedDocumentSnapshot based on the properties of the document.
     *
     * @param document
     * @return
     */
    public FlagedDocumentSnapshot newDocumentSnapshot(DocumentModel document) {
        hasBlobs = false;
        DocumentProperty[] props = getDocumentNoBlobProperties(document);
        ACE[] resACP = null;
        ACP acp = document.getACP();
        if (acp != null) {
            ACL acl = acp.getMergedACLs("MergedACL");
            resACP = acl.toArray(new ACE[acl.size()]);
        } else {
            resACP = new ACE[0];
        }
        return new FlagedDocumentSnapshot(props, null, document.getPathAsString(), WsACE.wrap(resACP), hasBlobs);
    }

    private DocumentProperty[] getDocumentNoBlobProperties(DocumentModel doc) {

        List<DocumentProperty> props = new ArrayList<DocumentProperty>();
        if (doc != null) {
            String[] schemas = doc.getSchemas();
            for (String schema : schemas) {
                DataModel dm = doc.getDataModel(schema);
                Map<String, Object> map = dm.getMap();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    collectNoBlobProperty(schema + ":", entry.getKey(), entry.getValue(), props);
                }
            }
        }
        return props.toArray(new DocumentProperty[props.size()]);
    }

    @SuppressWarnings("unchecked")
    private void collectNoBlobProperty(String prefix, String rawName, Object value, List<DocumentProperty> props)
            {
        // eliminate any prefix
        String[] tokens = rawName.split(":");
        String name = tokens[tokens.length - 1];
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            prefix = prefix + name + '/';
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                collectNoBlobProperty(prefix, entry.getKey(), entry.getValue(), props);
            }
        } else if (value instanceof List) {
            prefix = prefix + name + '/';
            List<Object> list = (List<Object>) value;
            for (int i = 0, len = list.size(); i < len; i++) {
                collectNoBlobProperty(prefix, String.valueOf(i), list.get(i), props);
            }
        } else if (!(value instanceof Blob)) {
            if (value == null) {
                props.add(new DocumentProperty(prefix + name, null));
            } else {
                collectProperty(prefix, name, value, props);
            }

        } else if (value instanceof Blob) {
            hasBlobs = true;
        }
    }

    @SuppressWarnings("unchecked")
    private void collectProperty(String prefix, String name, Object value, List<DocumentProperty> props)
            {
        final String STRINGS_LIST_SEP = ";";
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            prefix = prefix + name + '/';
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                collectProperty(prefix, entry.getKey(), entry.getValue(), props);
            }
        } else if (value instanceof List) {
            prefix = prefix + name + '/';
            List<Object> list = (List<Object>) value;
            for (int i = 0, len = list.size(); i < len; i++) {
                collectProperty(prefix, String.valueOf(i), list.get(i), props);
            }
        } else {
            String strValue = null;
            if (value != null) {
                if (value instanceof Blob) {
                    try {
                        // strValue = ((Blob) value).getString();
                        byte[] bytes = ((Blob) value).getByteArray();
                        strValue = Base64.encodeBase64String(bytes);
                    } catch (IOException e) {
                        throw new NuxeoException("Failed to get blob property value", e);
                    }
                } else if (value instanceof Calendar) {
                    strValue = new DateType().encode(value);
                } else if (value instanceof String[]) {
                    for (String each : (String[]) value) {
                        if (strValue == null) {
                            strValue = each;
                        } else {
                            strValue = strValue + STRINGS_LIST_SEP + each;
                        }
                    }
                } else if (value instanceof List) {
                    for (String each : (List<String>) value) {
                        if (strValue == null) {
                            strValue = each;
                        } else {
                            strValue = strValue + STRINGS_LIST_SEP + each;
                        }
                    }
                } else {
                    strValue = value.toString();
                }
            }
            props.add(new DocumentProperty(prefix + name, strValue));
        }
    }

    protected static SchemaManager getSchemaManager() {
        return Framework.getRuntime().getService(SchemaManager.class);
    }
}
