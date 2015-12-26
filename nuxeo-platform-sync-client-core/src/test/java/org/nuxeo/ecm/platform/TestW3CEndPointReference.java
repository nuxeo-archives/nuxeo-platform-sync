/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thierry Martins
 */
package org.nuxeo.ecm.platform;

import java.lang.reflect.Field;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestW3CEndPointReference {

    @Test
    public void testSetPrivateField() throws Exception {
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.address("http://www.nuxeo.com");
        W3CEndpointReference ref = builder.build();
        SetPrivateAdressFromEndPointReference(ref, "http://www.nuxeo.org");
        String uri = getPrivateAdressFromEndPointReference(ref);
        assertEquals(uri, "http://www.nuxeo.org");

    }

    private void SetPrivateAdressFromEndPointReference(W3CEndpointReference ref, String value) {
        if (ref == null) {
            return;
        }
        Field[] fields = W3CEndpointReference.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("address".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    Object address = fields[i].get(ref);
                    Field uriField = fields[i].getType().getDeclaredField("uri");
                    uriField.setAccessible(true);
                    uriField.set(address, value);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPrivateAdressFromEndPointReference(W3CEndpointReference ref) {
        if (ref == null) {
            return null;
        }
        try {
            Field addressField = W3CEndpointReference.class.getDeclaredField("address");
            addressField.setAccessible(true);
            Object address = addressField.get(ref);
            Field uriField = addressField.getType().getDeclaredField("uri");
            uriField.setAccessible(true);
            return (String) uriField.get(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
