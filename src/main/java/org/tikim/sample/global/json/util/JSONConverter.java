package org.tikim.sample.global.json.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tikim.sample.global.json.adapter.DurationAdapter;
import org.tikim.sample.global.json.adapter.LocalDateAdapter;
import org.tikim.sample.global.json.adapter.LocalDateTimeAdapter;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class JSONConverter {

    private final ObjectMapper objectMapperInstance;
    private final ObjectMapper objectMapperInstanceNoIndent;

    private static ObjectMapper objectMapper;
    private static ObjectMapper objectMapperNoIndent;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public JSONConverter(
            @Qualifier("objectMapper") ObjectMapper objectMapperInstance,
            @Qualifier("objectMapperNoIndent") ObjectMapper objectMapperInstanceNoIndent
    ) {
        this.objectMapperInstance = objectMapperInstance;
        this.objectMapperInstanceNoIndent = objectMapperInstanceNoIndent;
    }

    @PostConstruct
    private void init() {
        objectMapper = this.objectMapperInstance;
        objectMapperNoIndent = this.objectMapperInstanceNoIndent;
    }

    /* ---------- Gson ---------- */

    public static String writeValueAsStringByGSon(Object value) {
        return gson.toJson(value);
    }

    public static <T> T readValueByGson(String content, Class<T> valueType) {
        if (content == null) return null;
        return gson.fromJson(content, valueType);
    }

    public static <T> T readValueByGson(String content, Type type) {
        if (content == null) return null;
        return gson.fromJson(content, type);
    }

    /* ---------- Jackson ---------- */

    public static <T> T readValue(String content, Class<T> valueType) {
        if (content == null) return null;
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            throw new RuntimeException("JSON readValue(Class) failed", e);
        }
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        if (content == null) return null;
        try {
            return objectMapper.readValue(content, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException("JSON readValue(TypeReference) failed", e);
        }
    }

    public static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("JSON writeValueAsString failed", e);
        }
    }

    public static String writeValueAsStringNoIndent(Object value) {
        try {
            return objectMapperNoIndent.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("JSON writeValueAsStringNoIndent failed", e);
        }
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        try {
            return objectMapper.convertValue(fromValue, toValueTypeRef);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JSON convertValue failed", e);
        }
    }
}
