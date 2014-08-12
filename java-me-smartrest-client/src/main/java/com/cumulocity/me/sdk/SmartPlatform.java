package com.cumulocity.me.sdk;

import com.cumulocity.me.smartrest.client.SmartRequest;
import com.cumulocity.me.smartrest.client.SmartResponse;
import com.cumulocity.me.smartrest.client.impl.SmartRequestConnector;
import com.cumulocity.me.smartrest.client.impl.SmartRequestImpl;
import com.cumulocity.me.util.StringUtils;

public class SmartPlatform {

    private final String host;
    
    private String authorization;
    
    private String xid;
    
    private SmartRequestConnector connector;

    public SmartPlatform(String host, String xid) {
        this.host = host;
        this.xid = xid;
        this.connector = new SmartRequestConnector(this);
    }
    
    public SmartPlatform(String host, String xid, String authorization) {
        this.host = host;
        this.xid = xid;
        this.authorization = authorization;
        this.connector = new SmartRequestConnector(this);
    }
    
    public SmartResponse request(SmartRequest request) {
        connector.close();
        return connector.executeRequest(request);
    }
    
    public void bootstrap() {
        // TODO
    }
    
    public String registerTemplates(String templateString) {
        SmartResponse respCheckRegistration = connector.executeRequest();
        int codeCheck = respCheckRegistration.getDataRows()[0].getMessageId();
        if (codeCheck != 20) {
            SmartRequest request = new SmartRequestImpl(templateString.getBytes());
            SmartResponse respRegister = connector.executeRequest(request);
            int codeRegister = respRegister.getDataRows()[0].getMessageId();
            if (codeRegister != 20) {
                throw new SDKException(codeRegister, respRegister.getDataRows()[0].getData()[0]);
            }
            this.xid = String.valueOf(respRegister.getDataRows()[0].getRowNumber());
        } else {
            this.xid = String.valueOf(respCheckRegistration.getDataRows()[0].getRowNumber());
        } 
        return xid;
    }
    
    public void subscribe() {
        // TODO
    }
    
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getHost() {
        return host;
    }
    
    public String getXid() {
        return xid;
    }
}
