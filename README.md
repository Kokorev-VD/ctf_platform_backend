# CTF Platform Backend

This is the backend component of the [CTF Platform](https://github.com/Kokorev-VD/ctf_platform_backend) project.

## 🏗️ Architecture and Technologies

The backend is built using the Spring Boot framework and follows monolithic layer architecture principles.

### 📚 Technology Stack:

- [Kotlin](https://kotlinlang.org/) - Programming language for the JVM
- [Spring Boot](https://spring.io/projects/spring-boot) - Framework for building Java-based applications
- [JUnit 5](https://junit.org/junit5/) - Testing framework for Java applications

## ⚙️ Configuration

The project uses Gradle as the build tool. It includes configurations for code formatting and testing.

## 🛠️ Getting Started

Follow these steps to get the backend up and running:

Clone this repository.

Create database image

```bash
   docker pull postgres
   ```

Run database

```bash
   docker run --hostname=b24dc3c5b97c --env=POSTGRES_PASSWORD=password --env=POSTGRES_USER=user --env=DATABASE_NAME=CTF --env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/postgresql/16/bin --env=GOSU_VERSION=1.16 --env=LANG=en_US.utf8 --env=PG_MAJOR=16 --env=PG_VERSION=16.1-1.pgdg120+1 --env=PGDATA=/var/lib/postgresql/data --volume=/var/lib/postgresql/data -p 5430:5432 --runtime=runc -d postgres:latest
   ```

Run project

```bash
   .\gradlew bootrun

   ```
 
## 📁 Project Structure

The backend is organized into multiple Layers. Here's a breakdown of them and their responsibilities:

`Controller`: responsible for receiving requests

`Security`: responsible for user authorization by jwt token

`Service`: responsible for the business logic of the project. There are 4 main project services: User, Team, Auth and Admin services

`Database`: responsible for communicating with the database and creating tables in it

`Errors`: responsible for exception handling

`Config`: responsible for security and CORS configuration

`Models`: responsible for request sand response models

You can find openapi file at `src/main/resources/openapi.yaml`

## 📚 Main Libraries

The project leverages several key libraries to ensure efficient development, maintainability, and deployment. Here are the main libraries used in project:

### Kotlin and Spring Boot

- **Kotlin**: The project is built using Kotlin, a modern programming language that offers concise syntax and powerful
  features. It enhances developer productivity and promotes safer code practices.

- **Spring Boot**: Spring Boot provides a robust foundation for building microservices with minimal configuration. It
  offers various components for web development, security, actuation, and more.

### Testing and Quality

- **JUnit 5**: JUnit 5 is used for writing and executing unit and integration tests. It offers a comprehensive testing
  framework that ensures the reliability and correctness of the application's components.

- **Ktlint**: The Ktlint plugin analyzes the Kotlin codebase for code smells and potential issues. It helps maintain
  code quality and consistency throughout the project.

These libraries collectively empower the project to deliver scalable, maintainable, 
