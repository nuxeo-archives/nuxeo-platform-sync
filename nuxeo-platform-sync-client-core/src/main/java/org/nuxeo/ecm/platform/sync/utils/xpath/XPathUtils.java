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

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility to manage the XPath creation.
 * 
 * @author price
 */
public class XPathUtils {

    /**
     * Checks to see if parent node has more of these nodes looks at tagName
     * 
     * @param node
     * @return
     */
    private static final boolean parentNodeHasMoreOfThese(Element node) {
        int count = 0;
        NodeList children = node.getParentNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getTagName().equals(((Element) child).getTagName())) {
                    count++;
                    if (count > 1)
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Resolves the xpath of an element.
     * 
     * @param elt
     * @return
     * @throws IOException
     */
    public static final String getElementXPath(Node elt) throws IOException {
        String path = "";

        Node currentNode = elt;
        while (!(currentNode instanceof Document)) {
            Element parent = (Element) currentNode;
            if (!parent.getTagName().equals("schema")) {
                if (!parentNodeHasMoreOfThese((Element) currentNode)) {
                    path = '/' + parent.getTagName() + path;
                } else {
                    path = '/' + parent.getTagName() + '[' + getElementIdx(parent) + ']' + path;
                }
            } else {
                String schema = parent.getAttribute("name");
                String[] segments = path.substring(1).split(":", 2);
                return schema + ':' + segments[segments.length - 1];
            }

            currentNode = currentNode.getParentNode();
        }

        throw new IOException("Failed to parse document.");
    }

    /**
     * Returns the index of a node .
     * 
     * @param elt
     * @return
     */
    public static final int getElementIdx(Node elt) {
        int count = 0;
        for (Node sib = elt.getPreviousSibling(); sib != null; sib = sib.getPreviousSibling()) {
            if (sib.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) sib).getTagName().equals(((Element) elt).getTagName()))
                count++;
        }

        return count;
    }
}
