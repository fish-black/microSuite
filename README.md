# Fishblack microSuite 
MicroSuite is a service oriented micro-service framework.

## Developer Guide

### Prerequisites

* [PostgreSQL 9.5](http://www.postgresql.org/download/) and higher installed
* In postgres create user `postgres` with `postgres` password.
* In postgres create database `servicedb` owned by user `postgres`

### Build

```
mvn clean package
```

### Run the server
```
cd microSuite
java -jar target/microSuite-1.0.0.jar  server service.yml
```
