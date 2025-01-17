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
package com.cumulocity.sdk.client.common;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.authentication.CumulocityOAuthCredentials;
import com.cumulocity.rest.representation.tenant.TenantMediaType;
import com.cumulocity.sdk.client.PlatformImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import gherkin.deps.com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TenantCreator {

    private static final String TENANT_URI = "tenant/tenants";

    private final PlatformImpl platform;

    @Autowired
    public TenantCreator(PlatformImpl platform) {
        this.platform = platform;
    }

    public void createTenant() {
        Client httpClient = new HttpClientFactory().createClient();
        try {
            createTenant(httpClient);
        } finally {
            httpClient.destroy();
        }
    }

    private void createTenant(Client httpClient) {
        ClientResponse tr = postNewTenant(httpClient);
        assertThat(tr.getStatus(), is(201));
    }

    public void removeTenant() {
        Client httpClient = new HttpClientFactory().createClient();
        try {
            removeTenant(httpClient);
        } finally {
            httpClient.destroy();
        }
    }

    private void removeTenant(Client httpClient) {
        ClientResponse tenantResponse = deleteTenant(httpClient);
        assertThat(tenantResponse.getStatus(), is(204));
    }

    private ClientResponse postNewTenant(Client httpClient) {
        String host = platform.getHost();
        final WebResource.Builder resource = httpClient.resource(host + TENANT_URI).type(TenantMediaType.TENANT_TYPE).accept(TenantMediaType.TENANT_TYPE);
        CumulocityCredentials.CumulocityCredentialsVisitor<ClientResponse> visitor = new CumulocityCredentials.CumulocityCredentialsVisitor<ClientResponse>() {
            @Override
            public ClientResponse visit(CumulocityBasicCredentials credentials) {
                String tenantJson = "{ " +
                        "\"id\": \"" + platform.getTenantId() + "\", " +
                        "\"domain\": \"sample-tenant.cumulocity.com\", " +
                        "\"company\": \"sample-tenant\", " +
                        "\"adminName\": \"" + credentials.getUsername() + "\", " +
                        "\"adminPass\": \"" + credentials.getPassword() + "\" " +
                        "}";
                ClientResponse result = resource.post(ClientResponse.class, tenantJson);
                String newTenant = result.getEntity(String.class);
                Gson gson = new Gson();
                HashMap map = gson.fromJson(newTenant, HashMap.class);
                String newTenantId = (String)map.get("id");
                ((CumulocityBasicCredentials)platform.getCumulocityCredentials()).setTenantId(newTenantId);
                return result;
            }

            @Override
            public ClientResponse visit(CumulocityOAuthCredentials credentials) {
                throw new IllegalStateException("Cannot use credentials other than oauth yet");
            }
        };
        return platform.getCumulocityCredentials().accept(visitor);
    }

    private ClientResponse deleteTenant(Client httpClient) {
        String host = platform.getHost();
        WebResource tenantResource = httpClient.resource(host + TENANT_URI + "/" + platform.getTenantId());
        return tenantResource.delete(ClientResponse.class);
    }
}
