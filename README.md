# Midas API

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## ⚠️ Requirements

| Name | Build | Run | Download |
|----------|----------|----------|----------|
| JDK 21 | ✅ | ✅ | [Adoptium](https://adoptium.net/temurin/releases/?os=any&arch=any&version=21) |
| Maven | ✅ | ❌ | [Apache](https://maven.apache.org/download.cgi) |
| Docker | (✅) | (✅) | (see below) |

For Docker, you have 2 options:

- [Docker Engine](https://docs.docker.com/engine/install/)
- or [Docker Desktop](https://docs.docker.com/desktop/) (includes Docker Engine)

Docker is only required for builds if you wish to run tests. For running the application, Docker isn't technically required either, but highly recommended.

## 🔧 Preparation

Edit `src/main/resources/application.properties` to set the database connection properties.

If you wish to use PostgreSQL instead of MariaDB, you can uncomment the PostgreSQL dependency in the `pom.xml` file, while commenting out the MariaDB dependency.

```shell script
./mvnw clean install -DskipTests
```

### 🧪 Tests

To run the tests, you can either omit the `-DskipTests` flag when using the command above, or execute the following command:

```shell script
./mvnw test
```

### 🐘 Database

> **_NOTE:_** If you do **NOT** provide a JDBC-URL, Quarkus Dev Services will automagically provide a database container (only in development or test mode). Therefore, it is only set in the respective `api.yml` file rather than `application.properties` by default.

MariaDB and PostgreSQL are supported.

It is recommended to use Docker Compose to run the (production) database.

```shell script
docker compose -f compose/mariadb/db.yml up -d
```

or

```shell script
docker compose -f compose/postgres/db.yml up -d
```

## 🚀 Running the application

### 🧑‍💻 Development mode

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

### 🏭 Production mode

> **_NOTE:_** The application must be packaged before doing this (see below).

It is recommended to use Docker Compose to run the application in production mode:

```shell script
docker compose -f compose/mariadb/api.yml up -d --build
```

or

```shell script
docker compose -f compose/postgres/api.yml up -d --build
```

## 📦 Packaging the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

### 🐳 Creating a Docker image

> **_NOTE:_** The application must be packaged before doing this (see above).

You can create a Docker image using:

```shell script
docker build . -f src/main/docker/Dockerfile.jvm -t midas-api:1.0.0-SNAPSHOT
```

If you want to learn more about building Docker images, please consult <https://quarkus.io/guides/container-image>.

### 🐇 Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/midas-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## 📖 Related Guides

- Quarkus ([guide](https://quarkus.io/guides/)): The main framework for building Java applications with a focus on cloud-native and microservices architectures.
- ArC ([guide](https://quarkus.io/guides/cdi-reference)): A dependency injection framework that is part of Quarkus, providing support for CDI (Contexts and Dependency Injection).
- Datasource ([guide](https://quarkus.io/guides/datasource)): A Quarkus extension for connecting to databases using JDBC, JPA, Hibernate ORM, and more.
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): A Quarkus extension that simplifies the use of Hibernate ORM with a focus on ease of use and productivity.
- Hibernate Validator ([guide](https://quarkus.io/guides/hibernate-validator)): A Quarkus extension that integrates Hibernate Validator for bean validation, allowing you to validate your data models easily.
- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): A Quarkus extension that provides support for JSON serialization and deserialization using Jackson, making it easy to work with JSON data in your RESTful APIs.
