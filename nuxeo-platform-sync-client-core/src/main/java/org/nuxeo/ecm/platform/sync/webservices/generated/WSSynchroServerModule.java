
package org.nuxeo.ecm.platform.sync.webservices.generated;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "WSSynchroServerModule", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WSSynchroServerModule {


    /**
     * 
     */
    @WebMethod
    @RequestWrapper(localName = "keepAlive", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.KeepAlive")
    @ResponseWrapper(localName = "keepAliveResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.KeepAliveResponse")
    public void keepAlive();

    /**
     * 
     */
    @WebMethod
    @RequestWrapper(localName = "destroySession", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.DestroySession")
    @ResponseWrapper(localName = "destroySessionResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.DestroySessionResponse")
    public void destroySession();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple>
     * @throws ClientException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAvailableDocumentListWithQuery", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetAvailableDocumentListWithQuery")
    @ResponseWrapper(localName = "getAvailableDocumentListWithQueryResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetAvailableDocumentListWithQueryResponse")
    public List<NuxeoSynchroTuple> getAvailableDocumentListWithQuery(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws ClientException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.util.List<org.nuxeo.ecm.platform.sync.webservices.generated.NuxeoSynchroTuple>
     * @throws ClientException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAvailableDocumentList", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetAvailableDocumentList")
    @ResponseWrapper(localName = "getAvailableDocumentListResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetAvailableDocumentListResponse")
    public List<NuxeoSynchroTuple> getAvailableDocumentList()
        throws ClientException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws ClientException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getQueryAvailableDocumentListWithQuery", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetQueryAvailableDocumentListWithQuery")
    @ResponseWrapper(localName = "getQueryAvailableDocumentListWithQueryResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetQueryAvailableDocumentListWithQueryResponse")
    public String getQueryAvailableDocumentListWithQuery(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws ClientException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.lang.String
     * @throws ClientException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getQueryAvailableDocumentList", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetQueryAvailableDocumentList")
    @ResponseWrapper(localName = "getQueryAvailableDocumentListResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetQueryAvailableDocumentListResponse")
    public String getQueryAvailableDocumentList()
        throws ClientException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns org.nuxeo.ecm.platform.sync.webservices.generated.FlagedDocumentSnapshot
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getDocumentByIdWithoutBlob", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetDocumentByIdWithoutBlob")
    @ResponseWrapper(localName = "getDocumentByIdWithoutBlobResponse", targetNamespace = "http://webservices.server.sync.platform.ecm.nuxeo.org/", className = "org.nuxeo.ecm.platform.sync.webservices.generated.GetDocumentByIdWithoutBlobResponse")
    public FlagedDocumentSnapshot getDocumentByIdWithoutBlob(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

}
