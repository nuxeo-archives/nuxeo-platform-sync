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

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

/**
 * Resolver for the DOM parser.
 *
 * @author price
 *
 */
public class NXFunctionResolver implements XPathFunctionResolver {

    private static final QName name = new QName("http://www.nuxeo.org/blob",
            "isBlob");

    @Override
    public XPathFunction resolveFunction(QName fName, int arity) {
        if (name.equals(fName) && arity == 1) {
            return new BlobValidator();
        }

        return null;
    }

}
