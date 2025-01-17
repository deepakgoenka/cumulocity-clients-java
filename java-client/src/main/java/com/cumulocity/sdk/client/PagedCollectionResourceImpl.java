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

import static com.cumulocity.rest.pagination.RestPageRequest.DEFAULT_PAGE_SIZE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.BaseCollectionRepresentation;
import com.cumulocity.rest.representation.CumulocityMediaType;

public abstract class PagedCollectionResourceImpl<T, C extends BaseCollectionRepresentation<T>, I extends C>
        implements PagedCollectionResource<T, I> {

    private static final Logger LOG = LoggerFactory.getLogger(PagedCollectionResourceImpl.class);

    private UrlProcessor urlProcessor = new UrlProcessor();

    protected int pageSize = 5;
    
    private final String url;

    protected final RestConnector restConnector;

    protected abstract CumulocityMediaType getMediaType();

    protected abstract Class<C> getResponseClass();

    public PagedCollectionResourceImpl(RestConnector restConnector, String url, int pageSize) {
        this.restConnector = restConnector;
        this.url = url;
        this.pageSize = pageSize;
    }

    @Override
    public I getPage(BaseCollectionRepresentation collectionRepresentation, int pageNumber) throws SDKException {
        if (collectionRepresentation == null) {
            throw new SDKException("Unable to determin the Resource URL. ");
        }
        return getPage(collectionRepresentation, pageNumber, collectionRepresentation.getPageStatistics() == null ?
                DEFAULT_PAGE_SIZE : collectionRepresentation.getPageStatistics().getPageSize());
    }

    @Override
    public I getPage(BaseCollectionRepresentation collectionRepresentation, int pageNumber, int pageSize) throws SDKException {
        String pageUrl = getPageUrl(collectionRepresentation, pageNumber, pageSize);
		return getCollection(pageUrl);
    }

	String getPageUrl(BaseCollectionRepresentation collectionRepresentation, int pageNumber, int pageSize) {
	    if (collectionRepresentation == null || collectionRepresentation.getSelf() == null) {
            throw new SDKException("Unable to determin the Resource URL. ");
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(PAGE_SIZE_KEY, String.valueOf(pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize));
        params.put(PAGE_NUMBER_KEY, String.valueOf(pageNumber));

        String url = urlProcessor.replaceOrAddQueryParam(collectionRepresentation.getSelf(), params);

        LOG.debug(" URL : " + url);
	    return url;
    }

    protected I getCollection(String url) throws SDKException {
        return wrap(restConnector.get(url, getMediaType(), getResponseClass()));
    }

    protected abstract I wrap(C collection);

    @Override
    public I getNextPage(BaseCollectionRepresentation collectionRepresentation) throws SDKException {
        if (collectionRepresentation == null) {
            throw new SDKException("Unable to determin the Resource URL. ");
        }
        if (collectionRepresentation.getNext() == null) {
            return null;
        }
        return getCollection(collectionRepresentation.getNext());
    }

    @Override
    public I getPreviousPage(BaseCollectionRepresentation collectionRepresentation) throws SDKException {
        if (collectionRepresentation == null) {
            throw new SDKException("Unable to determin the Resource URL. ");
        }
        if (collectionRepresentation.getPrev() == null) {
            return null;
        }
        return getCollection(collectionRepresentation.getPrev());
    }

    @Override
    public I get(QueryParam... queryParams) throws SDKException {
        return get(pageSize, queryParams);
    }

    @Override
    public I get(int pageSize, QueryParam... queryParams) throws SDKException {
        Map<String, String> params = prepareGetParams(pageSize);
        for (QueryParam param : queryParams) {
            params.put(param.getKey().getName(), param.getValue());
        }
    	return get(params);
    }
    
    protected I get(Map<String, String> params) throws SDKException {
    	String urlToCall = urlProcessor.replaceOrAddQueryParam(url, params);
    	return wrap(restConnector.get(urlToCall, getMediaType(), getResponseClass()));
    }

	protected Map<String, String> prepareGetParams(int pageSize) {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(PAGE_SIZE_KEY, String.valueOf(pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize));
		return result;
	}
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PagedCollectionResourceImpl)) {
            return false;
        }

        try {
            @SuppressWarnings("rawtypes")
            PagedCollectionResourceImpl another = (PagedCollectionResourceImpl) obj;
            return getMediaType().equals(another.getMediaType()) && getResponseClass().equals(another.getResponseClass())
                    && pageSize == another.pageSize && url.equals(another.url);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int hashCode() {
        return getMediaType().hashCode() ^ getResponseClass().hashCode() ^ url.hashCode() ^ pageSize;
    }
}
