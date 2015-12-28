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
package org.nuxeo.ecm.platform.sync.utils.xpath;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Context resolver for the DOM parser.
 *
 * @author price
 */
public class NXNSContext implements NamespaceContext {
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals("nx"))
            return "http://www.nuxeo.org/blob";
        else
            return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespace) {
        if (namespace.equals("http://www.nuxeo.org/blob"))
            return "nx";
        else
            return null;
    }

    public Iterator<?> getPrefixes(String namespace) {
        return null;
    }
}
