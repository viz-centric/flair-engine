package com.fbi.engine.web.rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
/**
 * Utility class for HTTP headers creation.
 */
@Slf4j
public final class HeaderUtil {

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-fbiengineApp-alert", message);
        headers.add("X-fbiengineApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity processing failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-fbiengineApp-error", defaultMessage);
        headers.add("X-fbiengineApp-params", entityName);
        return headers;
    }
}
