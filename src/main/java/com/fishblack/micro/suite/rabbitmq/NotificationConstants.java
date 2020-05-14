package com.fishblack.micro.suite.rabbitmq;

public class NotificationConstants {

    public static String CONSUME_EXCHANGE = "consumeExchange";
    public static String CONSUME_QUEUE = "consumeQueue";
    public static String PUBLISH_EXCHANGE = "publishExchange";
    public static String CONNECT_RETRY_INTERVAL = "connectRetryInterval";

    public static String HOSTNAME = "hostname";
    public static String PORT = "port";
    public static String VIRTUALHOST = "virtualhost";
    public static String USER = "user";
    public static String PASSWORD = "password";
    public static String USE_SSL = "useSsl";

    public static String CONSUME_ACKS = "consumeAcks";
    public static String PUBLISH_ACKS = "publishAcks";

    public static String THREAD_POOL_SIZE = "threadpoolsize";

    /* Whenever defining a queue for notifications, append the constant NOTIFICATION_SERVICE_APPEND_DEFAULT as prefix mentioned below.
       This helps to mirror all HA related policies defined for notification related queues.
       If the queue is to be used for notification-svc then also append the constant NOTIFICATION_SERVICE_QUEUE mentioned below and after that define your own queue.

       Eg. NOTIFICATION_SERVICE_APPEND_DEFAULT + "." + NOTIFICATION_SERVICE_QUEUE + "." + <your_queue_name> i.e. propel_notification.notification-svc.myTestQueue
    */
    public static String NOTIFICATION_SERVICE_APPEND_DEFAULT = "propel_notification";
    public static String NOTIFICATION_SERVICE_QUEUE = "notification-svc";

    public static int NETWORK_RECOVERY_INTERVAL = 30000;

    // request types
    public static final String ORDER_TYPE = "ORDER";
    public static final String SUPPORT_TYPE = "SUPPORT";
    public static final String EXTERNAL_TYPE = "EXTERNAL";
    public static final String BUNDLE_ORDER_TYPE = "BUNDLE_ORDER";
    public static final String ORDER_CLUSTER_ROOT_TYPE = "ORDER_CLUSTER_ROOT";
}
