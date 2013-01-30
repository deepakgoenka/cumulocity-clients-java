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

package com.cumulocity.me.sdk.client.audit;

import com.cumulocity.me.model.idtype.GId;
import com.cumulocity.me.rest.representation.audit.AuditRecordRepresentation;
import com.cumulocity.me.sdk.SDKException;
import com.cumulocity.me.sdk.client.page.PagedCollectionResource;

/**
 * API for creating and retrieving audit records from the platform
 */
public interface AuditRecordApi {

    /**
     * Gets an audit record by id
     *
     * @param gid id of the audit record to search for
     * @return the audit record with the given id
     * @throws SDKException if the audit record is not found
     */
    AuditRecordRepresentation getAuditRecord(GId gid) throws SDKException;

    /**
     * Creates an audit record in the platform. The id of the audit record must not be set, since it will be generated by the platform
     *
     * @param auditRecord the audit record to be created
     * @return the created audit record with the generated id
     * @throws SDKException if the audit record could not be generated
     */
    AuditRecordRepresentation create(AuditRecordRepresentation auditRecord) throws SDKException;

    /**
     * Gets all audit records from the platform
     *
     * @return collection of audit records with paging functionality
     * @throws SDKException if the query failed
     */
    PagedCollectionResource getAuditRecords() throws SDKException;

    /**
     * Gets audit records from the platform based on the specified filter
     *
     * @param filter the filter criteria(s)
     * @return collection of audit records matched by the filter with paging functionality
     * @throws SDKException if the query failed
     */
    PagedCollectionResource getAuditRecordsByFilter(AuditRecordFilter filter) throws SDKException;
}
