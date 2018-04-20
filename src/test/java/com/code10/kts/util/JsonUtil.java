package com.code10.kts.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Utility class with JSON-POJO mapping functionality.
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts a POJO to a JSON String.
     *
     * @param object POJO
     * @return JSON
     * @throws IOException when conversion fails.
     */
    public static String json(Object object) throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    /**
     * Converts a JSON String to a POJO.
     *
     * @param json      JSON String
     * @param valueType class of target POJO
     * @param <T>       POJO type
     * @return POJO
     * @throws IOException when conversion fails
     */
    public static <T> T pojo(String json, Class<T> valueType) throws IOException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, valueType);
    }

    /**
     * Converts a JSON String to a POJO.
     *
     * @param json         JSON String
     * @param valueTypeRef class of target POJO
     * @param <T>          POJO type
     * @return POJO
     * @throws IOException when conversion fails
     */
    public static <T> T pojo(String json, TypeReference valueTypeRef) throws IOException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, valueTypeRef);
    }
}
