package com.cumulocity.sdk.client.messaging.notifications;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.CumulocityMediaType;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;

import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.UrlProcessor;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class NotificationSubscriptionApiImpl implements NotificationSubscriptionApi {
    
    public static final CumulocityMediaType MEDIA_TYPE = new CumulocityMediaType("application", "json");

    public static final String REQUEST_URI = "reliablenotification/subscriptions";

    private final RestConnector restConnector;

    private final int pageSize;

    private final UrlProcessor urlProcessor;

    public NotificationSubscriptionApiImpl(
            RestConnector restConnector,
            UrlProcessor urlProcessor,
            int pageSize) {
        this.restConnector = requireNonNull(restConnector, "restConnector");
        this.urlProcessor = requireNonNull(urlProcessor, "urlProcessor");
        this.pageSize = pageSize;
    }
    
    @Override
    public NotificationSubscriptionRepresentation subscribe(NotificationSubscriptionRepresentation representation) throws SDKException {
        requireNonNull(representation, "representation");
        NotificationSubscriptionRepresentation result = restConnector.post(
                getSelfUri(),
                MEDIA_TYPE,
                representation
                );
        return result;
    }

    @Override
    public NotificationSubscriptionCollection getSubscriptions() throws SDKException {
        String url = getSelfUri();
        return new NotificationSubscriptionCollectionImpl(restConnector, url, pageSize);
    }

    @Override
    public NotificationSubscriptionCollection getSubscriptionsByFilter(NotificationSubscriptionFilter filter) throws SDKException {
        if (filter == null) {
            return getSubscriptions();
        }
        Map<String, String> params = filter.getQueryParams();
        return new NotificationSubscriptionCollectionImpl(restConnector, urlProcessor.replaceOrAddQueryParam(getSelfUri(), params), pageSize);
    }

    @Override
    public void delete(NotificationSubscriptionRepresentation subscription) throws SDKException {
        requireNonNull(subscription, "subscription");
        delete(subscription.getId().getValue());
    }
    
    @Override
    public void delete(String subscriptionId) {
        requireNonNull(subscriptionId, "subscriptionId");
        String url = getSelfUri() + "/" + subscriptionId;
        restConnector.delete(url);
    }
    
    @Override
    public void deleteByFilter(NotificationSubscriptionFilter filter) {
        requireNonNull(filter, "filter");
        Map<String, String> params= filter.getQueryParams();
        restConnector.delete(urlProcessor.replaceOrAddQueryParam(getSelfUri(), params));
     }
    
    @Override
    public void deleteBySource(String source) {
        requireNonNull(source, "source");
        NotificationSubscriptionFilter filter = new NotificationSubscriptionFilter().bySource(new GId(source));
        deleteByFilter(filter);
    }

    private String getSelfUri() throws SDKException {
        return restConnector.getPlatformParameters().getHost() + REQUEST_URI;
    }
}
