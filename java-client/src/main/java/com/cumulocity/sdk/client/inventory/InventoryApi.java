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

package com.cumulocity.sdk.client.inventory;

import java.util.List;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * API for creating, and retrieving managed objects resources from the platform. Delete and update functionality is implemented on the
 * returned managed object.
 */
public interface InventoryApi {

    /**
     * Returns the Managed Object of the Resource.
     *
     * @return ManagedObjectRepresentation
     * @throws SDKException
     */
    ManagedObjectRepresentation get(GId id) throws SDKException;
    
    /**
     * Deletes the Managed Object from the Cumulocity Server.
     *
     * @throws SDKException
     */
    void delete(GId id) throws SDKException;

    /**
     * This update the ManagedObject for the operationCollection. Cannot update the ID.
     *
     * @param managedObjectRepresentation
     * @return ManagedObjectRepresentation updated ManagedObject.
     * @throws SDKException
     */
    ManagedObjectRepresentation update(ManagedObjectRepresentation managedObjectRepresentation) throws SDKException;

    
    /**
     * Gets managed object resource by id. To get the managed object representation you have to call {@code get()} on the returned resource.
     *
     * @param gid id of the managed object to search for
     * @return the managed object resource associated with the given id
     * @throws SDKException if the query failed
     */
    ManagedObject getManagedObjectApi(GId gid) throws SDKException;

    /**
     * Creates managed object in the platform. The id of the managed object must not be set, since it will be generated by the platform
     *
     * @param managedObject the managed object to be created
     * @return the created managed object with the generated id
     * @throws SDKException if the managed object could not be created
     */
    ManagedObjectRepresentation create(ManagedObjectRepresentation managedObject) throws SDKException;

    /**
     * Gets the all the managed object in the platform
     *
     * @return collection of managed objects with paging functionality
     * @throws SDKException if the query failed
     */
    ManagedObjectCollection getManagedObjects() throws SDKException;

    /**
     * Gets the managed objects from the platform based on specified filter. Query based on {@code type} and {@code fragmentType} is
     * not supported.
     *
     * @param filter the filter criteria(s)
     * @return collection of managed objects matched by the filter with paging functionality
     * @throws SDKException             if the query failed
     * @throws IllegalArgumentException if both {@code type} and {@code fragmentType} are specified in the filter
     */
    ManagedObjectCollection getManagedObjectsByFilter(InventoryFilter filter) throws SDKException;

    /**
     * Gets the managed objects from the platform based on the given ids
     *
     * @param ids the list of ids of the managed objects to search for
     * @return collection of managed objects matched in order of the given ids
     * @throws SDKException if the query failed
     */
    @Deprecated
    ManagedObjectCollection getManagedObjectsByListOfIds(List<GId> ids) throws SDKException;
    
    /**
     * Gets managed object resource by id. To get the managed object representation you have to call {@code get()} on the returned resource.
     *
     * @param gid id of the managed object to search for
     * @return the managed object resource associated with the given id
     * @throws SDKException if the query failed
     */
    @Deprecated
    ManagedObject getManagedObject(GId gid) throws SDKException;
}
