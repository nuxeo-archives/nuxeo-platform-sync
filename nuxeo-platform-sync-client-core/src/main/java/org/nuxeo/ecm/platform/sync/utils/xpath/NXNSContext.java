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
package org.nuxeo.ecm.platform.sync.utils.xpath;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Context resolver for the DOM parser.
 *
 * @author price
 *
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

    public Iterator getPrefixes(String namespace) {
        return null;
    }
}
