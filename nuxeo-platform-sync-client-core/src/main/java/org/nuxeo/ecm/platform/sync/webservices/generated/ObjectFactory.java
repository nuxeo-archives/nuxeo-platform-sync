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
 *     Quentin Lamerand
 */
package org.nuxeo.ecm.platform.sync.webservices.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.nuxeo.ecm.platform.sync.webservices.generated package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetQueryAvailableDocumentListWithQuery_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getQueryAvailableDocumentListWithQuery");

    private final static QName _KeepAliveResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "keepAliveResponse");

    private final static QName _KeepAlive_QNAME = new QName("http://webservices.server.sync.platform.ecm.nuxeo.org/",
            "keepAlive");

    private final static QName _DestroySessionResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "destroySessionResponse");

    private final static QName _DestroySession_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "destroySession");

    private final static QName _GetAvailableDocumentListWithQuery_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getAvailableDocumentListWithQuery");

    private final static QName _GetDocumentByIdWithoutBlob_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getDocumentByIdWithoutBlob");

    private final static QName _GetDocumentByIdWithoutBlobResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getDocumentByIdWithoutBlobResponse");

    private final static QName _ClientException_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "ClientException");

    private final static QName _GetQueryAvailableDocumentListResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getQueryAvailableDocumentListResponse");

    private final static QName _GetAvailableDocumentListWithQueryResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getAvailableDocumentListWithQueryResponse");

    private final static QName _GetAvailableDocumentList_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getAvailableDocumentList");

    private final static QName _GetAvailableDocumentListResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getAvailableDocumentListResponse");

    private final static QName _GetQueryAvailableDocumentListWithQueryResponse_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getQueryAvailableDocumentListWithQueryResponse");

    private final static QName _GetQueryAvailableDocumentList_QNAME = new QName(
            "http://webservices.server.sync.platform.ecm.nuxeo.org/", "getQueryAvailableDocumentList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.nuxeo.ecm.platform.sync.webservices.generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DocumentSnapshot }
     */
    public DocumentSnapshot createDocumentSnapshot() {
        return new DocumentSnapshot();
    }

    /**
     * Create an instance of {@link GetAvailableDocumentListResponse }
     */
    public GetAvailableDocumentListResponse createGetAvailableDocumentListResponse() {
        return new GetAvailableDocumentListResponse();
    }

    /**
     * Create an instance of {@link NuxeoSynchroTuple }
     */
    public NuxeoSynchroTuple createNuxeoSynchroTuple() {
        return new NuxeoSynchroTuple();
    }

    /**
     * Create an instance of {@link GetAvailableDocumentList }
     */
    public GetAvailableDocumentList createGetAvailableDocumentList() {
        return new GetAvailableDocumentList();
    }

    /**
     * Create an instance of {@link GetQueryAvailableDocumentListWithQueryResponse }
     */
    public GetQueryAvailableDocumentListWithQueryResponse createGetQueryAvailableDocumentListWithQueryResponse() {
        return new GetQueryAvailableDocumentListWithQueryResponse();
    }

    /**
     * Create an instance of {@link DestroySession }
     */
    public DestroySession createDestroySession() {
        return new DestroySession();
    }

    /**
     * Create an instance of {@link GetAvailableDocumentListWithQueryResponse }
     */
    public GetAvailableDocumentListWithQueryResponse createGetAvailableDocumentListWithQueryResponse() {
        return new GetAvailableDocumentListWithQueryResponse();
    }

    /**
     * Create an instance of {@link GetDocumentByIdWithoutBlob }
     */
    public GetDocumentByIdWithoutBlob createGetDocumentByIdWithoutBlob() {
        return new GetDocumentByIdWithoutBlob();
    }

    /**
     * Create an instance of {@link GetQueryAvailableDocumentListWithQuery }
     */
    public GetQueryAvailableDocumentListWithQuery createGetQueryAvailableDocumentListWithQuery() {
        return new GetQueryAvailableDocumentListWithQuery();
    }

    /**
     * Create an instance of {@link WsACE }
     */
    public WsACE createWsACE() {
        return new WsACE();
    }

    /**
     * Create an instance of {@link GetDocumentByIdWithoutBlobResponse }
     */
    public GetDocumentByIdWithoutBlobResponse createGetDocumentByIdWithoutBlobResponse() {
        return new GetDocumentByIdWithoutBlobResponse();
    }

    /**
     * Create an instance of {@link DestroySessionResponse }
     */
    public DestroySessionResponse createDestroySessionResponse() {
        return new DestroySessionResponse();
    }

    /**
     * Create an instance of {@link KeepAlive }
     */
    public KeepAlive createKeepAlive() {
        return new KeepAlive();
    }

    /**
     * Create an instance of {@link ContextDataInfo }
     */
    public ContextDataInfo createContextDataInfo() {
        return new ContextDataInfo();
    }

    /**
     * Create an instance of {@link GetQueryAvailableDocumentList }
     */
    public GetQueryAvailableDocumentList createGetQueryAvailableDocumentList() {
        return new GetQueryAvailableDocumentList();
    }

    /**
     * Create an instance of {@link FlagedDocumentSnapshot }
     */
    public FlagedDocumentSnapshot createFlagedDocumentSnapshot() {
        return new FlagedDocumentSnapshot();
    }

    /**
     * Create an instance of {@link DocumentBlob }
     */
    public DocumentBlob createDocumentBlob() {
        return new DocumentBlob();
    }

    /**
     * Create an instance of {@link ClientException }
     */
    public ClientException createClientException() {
        return new ClientException();
    }

    /**
     * Create an instance of {@link DocumentProperty }
     */
    public DocumentProperty createDocumentProperty() {
        return new DocumentProperty();
    }

    /**
     * Create an instance of {@link GetAvailableDocumentListWithQuery }
     */
    public GetAvailableDocumentListWithQuery createGetAvailableDocumentListWithQuery() {
        return new GetAvailableDocumentListWithQuery();
    }

    /**
     * Create an instance of {@link KeepAliveResponse }
     */
    public KeepAliveResponse createKeepAliveResponse() {
        return new KeepAliveResponse();
    }

    /**
     * Create an instance of {@link GetQueryAvailableDocumentListResponse }
     */
    public GetQueryAvailableDocumentListResponse createGetQueryAvailableDocumentListResponse() {
        return new GetQueryAvailableDocumentListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQueryAvailableDocumentListWithQuery }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getQueryAvailableDocumentListWithQuery")
    public JAXBElement<GetQueryAvailableDocumentListWithQuery> createGetQueryAvailableDocumentListWithQuery(
            GetQueryAvailableDocumentListWithQuery value) {
        return new JAXBElement<GetQueryAvailableDocumentListWithQuery>(_GetQueryAvailableDocumentListWithQuery_QNAME,
                GetQueryAvailableDocumentListWithQuery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeepAliveResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "keepAliveResponse")
    public JAXBElement<KeepAliveResponse> createKeepAliveResponse(KeepAliveResponse value) {
        return new JAXBElement<KeepAliveResponse>(_KeepAliveResponse_QNAME, KeepAliveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeepAlive }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "keepAlive")
    public JAXBElement<KeepAlive> createKeepAlive(KeepAlive value) {
        return new JAXBElement<KeepAlive>(_KeepAlive_QNAME, KeepAlive.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DestroySessionResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "destroySessionResponse")
    public JAXBElement<DestroySessionResponse> createDestroySessionResponse(DestroySessionResponse value) {
        return new JAXBElement<DestroySessionResponse>(_DestroySessionResponse_QNAME, DestroySessionResponse.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DestroySession }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "destroySession")
    public JAXBElement<DestroySession> createDestroySession(DestroySession value) {
        return new JAXBElement<DestroySession>(_DestroySession_QNAME, DestroySession.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableDocumentListWithQuery }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getAvailableDocumentListWithQuery")
    public JAXBElement<GetAvailableDocumentListWithQuery> createGetAvailableDocumentListWithQuery(
            GetAvailableDocumentListWithQuery value) {
        return new JAXBElement<GetAvailableDocumentListWithQuery>(_GetAvailableDocumentListWithQuery_QNAME,
                GetAvailableDocumentListWithQuery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentByIdWithoutBlob }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getDocumentByIdWithoutBlob")
    public JAXBElement<GetDocumentByIdWithoutBlob> createGetDocumentByIdWithoutBlob(GetDocumentByIdWithoutBlob value) {
        return new JAXBElement<GetDocumentByIdWithoutBlob>(_GetDocumentByIdWithoutBlob_QNAME,
                GetDocumentByIdWithoutBlob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentByIdWithoutBlobResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getDocumentByIdWithoutBlobResponse")
    public JAXBElement<GetDocumentByIdWithoutBlobResponse> createGetDocumentByIdWithoutBlobResponse(
            GetDocumentByIdWithoutBlobResponse value) {
        return new JAXBElement<GetDocumentByIdWithoutBlobResponse>(_GetDocumentByIdWithoutBlobResponse_QNAME,
                GetDocumentByIdWithoutBlobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClientException }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "ClientException")
    public JAXBElement<ClientException> createClientException(ClientException value) {
        return new JAXBElement<ClientException>(_ClientException_QNAME, ClientException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQueryAvailableDocumentListResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getQueryAvailableDocumentListResponse")
    public JAXBElement<GetQueryAvailableDocumentListResponse> createGetQueryAvailableDocumentListResponse(
            GetQueryAvailableDocumentListResponse value) {
        return new JAXBElement<GetQueryAvailableDocumentListResponse>(_GetQueryAvailableDocumentListResponse_QNAME,
                GetQueryAvailableDocumentListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableDocumentListWithQueryResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getAvailableDocumentListWithQueryResponse")
    public JAXBElement<GetAvailableDocumentListWithQueryResponse> createGetAvailableDocumentListWithQueryResponse(
            GetAvailableDocumentListWithQueryResponse value) {
        return new JAXBElement<GetAvailableDocumentListWithQueryResponse>(
                _GetAvailableDocumentListWithQueryResponse_QNAME, GetAvailableDocumentListWithQueryResponse.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableDocumentList }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getAvailableDocumentList")
    public JAXBElement<GetAvailableDocumentList> createGetAvailableDocumentList(GetAvailableDocumentList value) {
        return new JAXBElement<GetAvailableDocumentList>(_GetAvailableDocumentList_QNAME,
                GetAvailableDocumentList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableDocumentListResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getAvailableDocumentListResponse")
    public JAXBElement<GetAvailableDocumentListResponse> createGetAvailableDocumentListResponse(
            GetAvailableDocumentListResponse value) {
        return new JAXBElement<GetAvailableDocumentListResponse>(_GetAvailableDocumentListResponse_QNAME,
                GetAvailableDocumentListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQueryAvailableDocumentListWithQueryResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getQueryAvailableDocumentListWithQueryResponse")
    public JAXBElement<GetQueryAvailableDocumentListWithQueryResponse> createGetQueryAvailableDocumentListWithQueryResponse(
            GetQueryAvailableDocumentListWithQueryResponse value) {
        return new JAXBElement<GetQueryAvailableDocumentListWithQueryResponse>(
                _GetQueryAvailableDocumentListWithQueryResponse_QNAME,
                GetQueryAvailableDocumentListWithQueryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQueryAvailableDocumentList }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", name = "getQueryAvailableDocumentList")
    public JAXBElement<GetQueryAvailableDocumentList> createGetQueryAvailableDocumentList(
            GetQueryAvailableDocumentList value) {
        return new JAXBElement<GetQueryAvailableDocumentList>(_GetQueryAvailableDocumentList_QNAME,
                GetQueryAvailableDocumentList.class, null, value);
    }

}
