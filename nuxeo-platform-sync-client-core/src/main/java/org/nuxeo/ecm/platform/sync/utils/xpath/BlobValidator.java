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
