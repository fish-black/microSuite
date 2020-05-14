# Fishblack microSuite 
MicroSuite is a service oriented micro-service framework.

## Developer Guide

### Prerequisites

* [PostgreSQL 9.5](http://www.postgresql.org/download/) and higher installed
* In postgres create user `notification` with `changeit` password.
* In postgres create database `notificationdb` owned by user `notification`

### Build

```
mvn clean package
```

### Create notification table
```
java -jar target/notification-svc-1.0.0.jar db migrate notification.yml
```

### Run the server
```
cd notification-svc
java -jar target/notification-svc-1.0.0.jar  server notification.yml
```