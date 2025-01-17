package com.cumulocity.me.agent.smartrest.model;

import com.cumulocity.me.smartrest.client.SmartRequest;
import com.cumulocity.me.smartrest.client.SmartResponse;
import com.cumulocity.me.smartrest.client.SmartResponseEvaluator;
import com.cumulocity.me.smartrest.client.impl.SmartRequestImpl;

import net.sf.microlog.core.Logger;
import net.sf.microlog.core.LoggerFactory;

import java.util.Enumeration;
import java.util.Hashtable;

public class BulkRequest {
    private static final Logger LOG = LoggerFactory.getLogger(BulkRequest.class);
	
    private final Hashtable requestMap = new Hashtable();
    
    public BulkRequest(RequestBufferItem[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            RequestBufferItem item = buffer[i];
            putIntoMap(item.getXId(), item);
        }
    }
    
    private synchronized void putIntoMap(String key, RequestBufferItem valueItem){
        Object previous = requestMap.get(key);
        if (previous == null) {
            RequestBufferItemList value = new RequestBufferItemList();
            value.addElement(valueItem);
            requestMap.put(key, value);
        } else {
            RequestBufferItemList castPrevious = (RequestBufferItemList) previous;
            castPrevious.addElement(valueItem);
        }
    }
    
    public SmartRequest buildSmartRequest(){
        StringBuffer data = new StringBuffer();
        Enumeration xIdEnumeration = requestMap.keys();
        int line = 1;
        while (xIdEnumeration.hasMoreElements()) {
            String nextXId = (String) xIdEnumeration.nextElement();
            data.append(MessageId.SET_XID_REQUEST.getValue()).append(",").append(nextXId).append("\r\n");
            line++;
            RequestBufferItemList nextList = (RequestBufferItemList) requestMap.get(nextXId);
            Enumeration requestsEnumeration = nextList.elements();
            while (requestsEnumeration.hasMoreElements()) {
                RequestBufferItem nextItem = (RequestBufferItem) requestsEnumeration.nextElement();
                nextItem.setLineNumber(line);
                data.append(nextItem.getRequest().getData()).append("\r\n");
                line++;
            }
        }
        return new SmartRequestImpl(data.toString());
    }
    
    public void callEvaluator(int line, SmartResponse response){
        LOG.debug("Calling evaluator for line " + line);
    	Enumeration xIdEnumeration = requestMap.elements();
        while (xIdEnumeration.hasMoreElements()) {
            RequestBufferItemList nextList = (RequestBufferItemList) xIdEnumeration.nextElement();
            Enumeration requestEnumeration = nextList.elements();
            while (requestEnumeration.hasMoreElements()) {
                RequestBufferItem nextItem = (RequestBufferItem) requestEnumeration.nextElement();
                if (line == nextItem.getLineNumber()) {
                    callInNewThread(nextItem.getCallback(), response);
                    return;
                }
            }
        }
    }

    private void callInNewThread(final SmartResponseEvaluator evaluator, final SmartResponse response){
        if (evaluator != null) {
            new Thread(new Runnable() {
                public void run() {
                    evaluator.evaluate(response);
                }
            }).start();
        }
    }
}
