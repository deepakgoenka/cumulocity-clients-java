package com.cumulocity.sdk.client;

import java.io.InputStream;

public interface ResponseMapper {
    <T> T read(InputStream stream, Class<T> clazz);
}
