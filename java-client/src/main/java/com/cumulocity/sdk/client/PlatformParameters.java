/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cumulocity.sdk.client;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.base.Supplier;
import com.cumulocity.sdk.client.base.Suppliers;
import com.cumulocity.sdk.client.buffering.*;
import com.cumulocity.sdk.client.interceptor.HttpClientInterceptor;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps credentials and client configuration.
 * Creates processor responsible for handling buffered requests.
 * Important to call close() method on shutdown to finish processing.
 */
public class PlatformParameters {

    public final static int DEFAULT_PAGE_SIZE = 5;

    private String host;

    private String proxyHost;

    @Getter
    private CumulocityCredentials cumulocityCredentials;

    private int proxyPort = -1;

    private String proxyUserId;

    private String proxyPassword;

    private boolean requireResponseBody = true;

    private boolean forceInitialHost = false;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private BufferRequestService bufferRequestService;

    private BufferProcessor bufferProcessor;

    private RestConnector restConnector;

    private ClientConfiguration clientConfiguration;

    private Supplier<String> tfaToken;

    private ResponseMapper responseMapper;

    private HttpClientConfig httpClientConfig = HttpClientConfig.httpConfig().build();

    Set<HttpClientInterceptor> interceptorSet = Collections.newSetFromMap(new ConcurrentHashMap());

    public PlatformParameters() {
        //empty constructor for spring based initialization
    }

    public PlatformParameters(String host, CumulocityCredentials credentials, ClientConfiguration clientConfiguration) {
        this(host, credentials, clientConfiguration, DEFAULT_PAGE_SIZE);
    }

    private void setMandatoryFields(String host, CumulocityCredentials credentials) {
        if (host.charAt(host.length() - 1) != '/') {
            host = host + "/";
        }
        this.host = host;
        this.cumulocityCredentials = credentials;
    }

    public PlatformParameters(String host, CumulocityCredentials credentials, ClientConfiguration clientConfiguration, int pageSize) {
        this.pageSize = pageSize;
        this.clientConfiguration = clientConfiguration;
        setMandatoryFields(host, credentials);
    }

    private void startBufferProcessing() {
        if (clientConfiguration.isAsyncEnabled()) {
            PersistentProvider persistentProvider = clientConfiguration.getPersistentProvider();
            bufferRequestService = new BufferRequestServiceImpl(persistentProvider);
            bufferProcessor = new BufferProcessor(persistentProvider, bufferRequestService, restConnector);
            bufferProcessor.startProcessing();
        } else {
            bufferRequestService = new DisabledBufferRequestService();
        }
    }

    public synchronized RestConnector createRestConnector() {
        if (restConnector == null) {
            restConnector = new RestConnector(this, new ResponseParser(responseMapper));
        }
        return restConnector;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getHost() {
        return host;
    }

    public String getTenantId() {
        return cumulocityCredentials.getTenantId();
    }

    public String getUser() {
        return cumulocityCredentials.getUsername();
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUserId() {
        return proxyUserId;
    }

    public void setProxyUserId(String proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getApplicationKey() {
        return cumulocityCredentials == null ? null : cumulocityCredentials.getApplicationKey();
    }

    public void setRequireResponseBody(boolean requireResponseBody) {
        this.requireResponseBody = requireResponseBody;
    }

    public boolean requireResponseBody() {
        return requireResponseBody;
    }

    public boolean isForceInitialHost() {
        return forceInitialHost;
    }

    public void setForceInitialHost(boolean forceInitialHost) {
        this.forceInitialHost = forceInitialHost;
    }

    public boolean isAlwaysCloseConnection() {
        return !httpClientConfig.getPool().isEnabled();
    }

    public HttpClientConfig getHttpClientConfig() {
        return httpClientConfig;
    }

    /**
     * Pass the configuration for underlying http client
     * Example:
     * platform.setHttpClientConfig(
     * HttpClientConfig.httpConfig()
     * .pool(ConnectionPoolConfig.connectionPool()
     *      .perHost(100)
     *      .max(200)
     *      .awaitTimeout(60000)
     *      .build())
     *  .build()
     * );
     * @param httpClientConfig
     */
    public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    /**
     * Set header to the http client to close connection always.
     *
     * @param alwaysCloseConnection
     */
    public void setAlwaysCloseConnection(boolean alwaysCloseConnection) {
        httpClientConfig = httpClientConfig.toBuilder().pool(httpClientConfig.getPool().toBuilder().enabled(!alwaysCloseConnection).build()).build();
    }

    public String getTfaToken() {
        if (tfaToken == null) {
            return null;
        }
        return tfaToken.get();
    }

    public void setTfaToken(final String tfaToken) {
        this.tfaToken = Suppliers.ofInstance(tfaToken);
    }

    public void setTfaToken(final Supplier<String> tfaToken) {
        this.tfaToken = tfaToken;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRequestOrigin() {
        return cumulocityCredentials == null ? null : cumulocityCredentials.getRequestOrigin();
    }

    BufferRequestService createBufferRequestService() {
        if (bufferRequestService == null) {
            startBufferProcessing();
        }
        return bufferRequestService;
    }

    public void close() {
        if (bufferProcessor != null) {
            bufferProcessor.shutdown();
        }
    }

    public boolean registerInterceptor(HttpClientInterceptor interceptor) {
        return interceptorSet.add(interceptor);
    }

    public boolean unregisterInterceptor(HttpClientInterceptor interceptor) {
        return interceptorSet.remove(interceptor);
    }

    public ResponseMapper getResponseMapper() {
        return responseMapper;
    }

    public void setResponseMapper(ResponseMapper responseMapper) {
        this.responseMapper = responseMapper;
    }

    private class DisabledBufferRequestService implements BufferRequestService {

        @Override
        public Future create(BufferedRequest request) {
            throw new RuntimeException("Async feature is disabled in this platform client instance.");
        }

        @Override
        public void addResponse(long requestId, Result result) {
        }

    }

}
