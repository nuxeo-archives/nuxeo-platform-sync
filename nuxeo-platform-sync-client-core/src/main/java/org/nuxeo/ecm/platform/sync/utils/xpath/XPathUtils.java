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

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility to manage the XPath creation.
 * 
 * @author price
 *
 */
public class XPathUtils {

    /**
     * Checks to see if parent node has more of these nodes 
     * looks at tagName
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
                    path = '/' + parent.getTagName() + '['
                            + getElementIdx(parent) + ']' + path;
                }
            } else {
                String[] segments = path.substring(1).split(":", 2);
                return parent.getAttribute("name") + ':' +  segments[segments.length - 1];
            }

            currentNode = currentNode.getParentNode();
        }

        throw new IOException("Failed to parse document.");
    }

    /**
     * Returns the index of a node .
     * @param elt
     * @return
     */
    public static final int getElementIdx(Node elt) {
        int count = 0;
        for (Node sib = elt.getPreviousSibling(); sib != null; sib = sib.getPreviousSibling()) {
            if (sib.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) sib).getTagName().equals(
                            ((Element) elt).getTagName()))
                count++;
        }

        return count;
    }
}
