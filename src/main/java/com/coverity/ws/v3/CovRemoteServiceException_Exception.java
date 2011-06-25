package com.coverity.ws.v3;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "CoverityFault", targetNamespace = "http://ws.coverity.com/v3")
public class CovRemoteServiceException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private CovRemoteServiceException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public CovRemoteServiceException_Exception(String message, CovRemoteServiceException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public CovRemoteServiceException_Exception(String message, CovRemoteServiceException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.coverity.ws.v3.CovRemoteServiceException
     */
    public CovRemoteServiceException getFaultInfo() {
        return faultInfo;
    }

}
