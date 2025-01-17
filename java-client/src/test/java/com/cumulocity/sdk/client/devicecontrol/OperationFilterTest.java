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
package com.cumulocity.sdk.client.devicecontrol;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cumulocity.model.operation.OperationStatus;

public class OperationFilterTest {
    @Test
    public void shouldHaveStatusAndDeviceAndAgentAndFragment() throws Exception {
        // Given
        OperationStatus status = OperationStatus.EXECUTING;
        String deviceId = "deviceId";
        String agentId = "agentId";
        String fragmentType = "fragment_type";

        //When
        OperationFilter filter = new OperationFilter().byStatus(status).byDevice(deviceId).byAgent(agentId)
                .byFragmentType(fragmentType);

        //Then
        assertThat(filter.getStatus(), is(status.toString()));
        assertThat(filter.getDevice(), is(deviceId));
        assertThat(filter.getAgent(), is(agentId));
        assertThat(filter.getFragmentType(), is(fragmentType));
    }

}
