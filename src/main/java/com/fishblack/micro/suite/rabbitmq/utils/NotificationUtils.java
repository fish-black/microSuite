package com.fishblack.micro.suite.rabbitmq.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * Helper methods to create Notificaiton object.
 */
public class NotificationUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Convert epoch into ISO 8601.
     *
     * @param epoch The epoch value
     * @return String
     */
    public static String formatDate(Long epoch) {
        return sdf.format(new Date(epoch));
    }

    /**
     * Convert String date into epoch.
     *
     * @param timestamp The timestamp in string format
     * @return String
     */
    public static Long convertStringDateToEpoch(String timestamp) throws ParseException {
        Date d = sdf.parse(timestamp);
        return d.getTime();
    }

    /**
     * Get current date in String
     *
     * @return String
     */
    public static String getCurrentDateInString() {
        return sdf.format(new Date());
    }

    /**
     * Create propel specific order URI
     *
     * @param guid The order guid as String
     * @return String
     */
    public static String createOrderUri(String guid) {
        return "order://" + guid;
    }

    /**
     * Create propel specific request URI
     *
     * @param guid The request guid as String
     * @return String
     */
    public static String createRequestUri(String guid) {
        return "request://" + guid;
    }

    /**
     * Create propel specific external request URI
     *
     * @param guid The request guid as String
     * @return String
     */
    public static String createExternalRequestUri(String guid) {
        return "external://" + guid;
    }

    /**
     * Create propel specific approval URI
     *
     * @param guid The approval guid as String
     * @return String
     */
    public static String createApprovalUri(String guid) {
        return "approval://" + guid;
    }

    /**
     * Create propel specific comment URI
     *
     * @param guid The comment guid as String
     * @return String
     */
    public static String createCommentUri(String guid) {
        return "comment://" + guid;
    }

    /**
     * Create propel specific support request URI
     *
     * @param guid The support request guid as String
     * @return String
     */
    public static String createSupportRequestUri(String guid) {
        return "ticket://" + guid;
    }

    /**
     * handle User's case to be always lowercase
     *
     * @param user The user's name
     * @return String
     */
    public static String handleUserCase(String user) {
        if (user != null && !user.trim().isEmpty()) {
            return user.toLowerCase();
        } else {
            return user;
        }
    }

    /**
     * handle Tenant's case to be always uppercase
     *
     * @param tenant The tenant's name
     * @return String
     */
    public static String handleTenantCase(String tenant) {
        if (tenant != null && !tenant.trim().isEmpty()) {
            return tenant.toUpperCase();
        } else {
            return tenant;
        }
    }
}
