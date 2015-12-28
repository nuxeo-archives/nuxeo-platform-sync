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
package org.nuxeo.ecm.platform.sync.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.sync.api.util.SynchronizeDetails;

/**
 * simple http client using basic authentication that executes a get call !! Don't forget to close the connection after
 * using it
 *
 * @author mcedica
 */
public class SynchHttpClient {

    private String restPrefix = "restAPI";

    private String baseURL = "http://127.0.0.1:8080/nuxeo";

    private static final String NUXEO_REALM = "Nuxeo 5 EP";

    private SynchronizeDetails synchronizeDetails;

    private GetMethod httpGet;

    public SynchHttpClient(SynchronizeDetails synchronizeDetails) {
        this.synchronizeDetails = synchronizeDetails;
        this.baseURL = synchronizeDetails.getUrl();
        this.httpGet = null;

    }

    public InputStream executeGetCall(List<String> pathParams, Map<String, String> queryParams) {
        HttpClient client = new HttpClient();
        Credentials defaultcreds = new UsernamePasswordCredentials(synchronizeDetails.getUsername(),
                synchronizeDetails.getPassword());
        client.getState().setCredentials(
                new AuthScope(synchronizeDetails.getHost(), synchronizeDetails.getPort(), NUXEO_REALM), defaultcreds);
        httpGet = new GetMethod(constructUri(pathParams, queryParams));
        httpGet.setDoAuthentication(true);
        try {
            client.executeMethod(httpGet);
            return httpGet.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    public void closeConnection() {
        if (httpGet != null) {
            httpGet.releaseConnection();
            httpGet = null;
        }

    }

    private String constructUri(List<String> pathParams, Map<String, String> queryParams) {
        String path = "";
        StringBuffer pathBuffer = new StringBuffer();

        if (pathParams != null) {
            for (String p : pathParams) {
                pathBuffer.append(p);
                pathBuffer.append('/');
            }
            path = pathBuffer.toString();
        }
        return construct(path, queryParams);
    }

    private String construct(String subPath, Map<String, String> queryParams) {
        StringBuffer urlBuffer = new StringBuffer();

        if (subPath.startsWith("/")) {
            subPath = subPath.substring(1);
        }
        if (subPath.endsWith("/")) {
            subPath = subPath.substring(0, subPath.length() - 1);
        }
        urlBuffer.append(baseURL);
        urlBuffer.append('/');
        urlBuffer.append(restPrefix);
        urlBuffer.append('/');
        urlBuffer.append(subPath);

        if (queryParams != null) {
            urlBuffer.append('?');
            for (String qpName : queryParams.keySet()) {
                urlBuffer.append(qpName);
                urlBuffer.append('=');
                urlBuffer.append(queryParams.get(qpName).replaceAll(" ", "%20"));
                urlBuffer.append('&');
            }
        }
        String completeURL = urlBuffer.toString();
        return completeURL;
    }

}
