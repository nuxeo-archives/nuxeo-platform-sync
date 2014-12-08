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

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An xpath function that validates if a data node has a blob.
 * 
 * @author price
 */
public class BlobValidator implements XPathFunction {

    public static final Logger LOG = Logger.getLogger(BlobValidator.class);

    @Override
    public Object evaluate(@SuppressWarnings("rawtypes") List args) throws XPathFunctionException {
        if (args.size() > 1 || args.size() == 0)
            throw new XPathFunctionException("Wrong parameters count.");
        NodeList nl = ((NodeList) args.get(0));
        Node element = (Node) nl.item(0);

        return (element.getTextContent().endsWith(".blob"));

    }
}
