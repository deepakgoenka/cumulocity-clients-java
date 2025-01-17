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

package com.cumulocity.me.sdk.client;

import com.cumulocity.me.rest.convert.JsonConversionService;
import com.cumulocity.me.rest.validate.RepresentationValidationService;
import com.cumulocity.me.sdk.client.alarm.AlarmApi;
import com.cumulocity.me.sdk.client.audit.AuditRecordApi;
import com.cumulocity.me.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.me.sdk.client.event.EventApi;
import com.cumulocity.me.sdk.client.identity.IdentityApi;
import com.cumulocity.me.sdk.client.inventory.InventoryApi;
import com.cumulocity.me.sdk.client.measurement.MeasurementApi;

public interface Platform {

    InventoryApi getInventoryApi();

    MeasurementApi getMeasurementApi();

    AlarmApi getAlarmApi();

    IdentityApi getIdentityApi();

    DeviceControlApi getDeviceControlApi();

    EventApi getEventApi();

    AuditRecordApi getAuditRecordApi();

    void setConversionService(JsonConversionService conversionService);

    JsonConversionService getConversionService();

    void setValidationService(RepresentationValidationService validationService);

    RepresentationValidationService getValidationService();
}
