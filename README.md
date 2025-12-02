# Task Manager CLI

Task Manager CLI is a minimal Java 17 command-line application that provides CRUD operations for **Tasks** and **Categories**.
The project is intentionally lightweight, with a clear separation between domain, application logic and CLI interface, allowing the focus to be placed on a fully automated and well-structured CI/CD pipeline.

---

## Team Members

* **Ethan Gabriel Leskovec** – 886040 – [e.leskovec@campus.unimib.it](mailto:e.leskovec@campus.unimib.it)
* **Riccardo Pretali** – 870452 – [r.pretali@campus.unimib.it](mailto:r.pretali@campus.unimib.it)

---

## Repository Links

* **GitHub**: [https://github.com/rPretali/task-manager-cli.git](https://github.com/rPretali/task-manager-cli.git)
* **GitLab**: [https://gitlab.com/rpretali-group/task-manager-cli.git](https://gitlab.com/rpretali-group/task-manager-cli.git)

---

# Project Overview

The application supports basic task management through a simple text-based interface.
Users can do:

**Task Management:**
* **Create tasks** with customizable titles, descriptions, and category assignments
* **List all tasks** with filtering options to view tasks by status (done/pending) or category
* **Update task details** including titles, descriptions, and category associations
* **Mark tasks** as complete or revert them to pending status
* **Delete tasks** individually when no longer needed
* **Status tracking** to distinguish between active and completed work items

**Category Management:**
* **Create categories** to organize and group related tasks
* **List all categories** with counts of associated tasks
* **Update category names** and properties
* **Delete categories** with proper handling of task associations
* **Category relationships** to enable logical organization of work

## Application Architecture

The implementation follows a three-tier architecture:

1. **Domain Layer** – Core entities representing Tasks and Categories with their properties and validation rules
2. **Application Service Layer** – Business logic handling task and category operations, maintaining data consistency
3. **CLI Interface Layer** – Text-based user interaction providing an intuitive menu-driven interface

A full JUnit 5 test suite verifies the core application logic, while static analysis enforces code quality.

---

# CI/CD Pipeline Overview

A complete CI/CD pipeline is implemented using GitLab CI/CD, covering the entire lifecycle:

```
Commit
 → Build
 → Verify (parallel: Checkstyle, SpotBugs, Verify Tests)
 → Test
 → Package
 → Release
 → Docs (GitLab Pages)
```

The pipeline ensures:

* consistent and reproducible builds
* early quality feedback via static and dynamic analysis
* automated packaging and versioned artifacts
* Docker image creation and distribution
* automatically published documentation

---

# CI/CD Stages

## Build

**Command:** `mvn clean compile`
Compiles the codebase and resolves project dependencies.

**Details:**

* Maven 3.9.6 + Eclipse Temurin JDK 17
* Cleans and compiles
* Uses a cached local Maven repository
* Artifacts retained for 1 day

---

## Verify (parallel static & dynamic analysis)

### Checkstyle

**Command:** `mvn checkstyle:check`

* Style validation
* `allow_failure: true`
* Artifact: `target/checkstyle-result.xml`

### SpotBugs

**Command:** `mvn spotbugs:check`

* Bytecode bug detection
* `allow_failure: true`
* Artifact: `target/spotbugsXml.xml`

### Verify Tests

**Command:** `mvn test`

* Runs JUnit tests
* JUnit XML reports stored for 30 days
* Runs in parallel with Checkstyle and SpotBugs

---

## Test

**Command:** `mvn test`
A dedicated stage that re-executes the entire test suite to ensure consistent results.
Produces full JUnit reports visible in GitLab’s Test Reports interface.

---

## Package

**Command:** `mvn package -DskipTests`
Builds an executable JAR.

**Details:**

* Tests skipped because they were already executed
* Output: `task-manager-cli-*.jar`
* Artifact named with commit SHA for traceability
* Executed only on branch `main`
* Retained for 90 days

---

## Release

Builds and publishes a Docker image using Docker-in-Docker.

**Details:**

* Image: `docker:27.3.1`
* Service: `docker:27.3.1-dind`
* Commands:

  * `docker build -t $CONTAINER_IMAGE .`
  * `docker push $CONTAINER_IMAGE`
* Uses:

  * `DOCKER_DRIVER=overlay2`
  * `DOCKER_HOST=tcp://docker:2375`
  * `DOCKER_TLS_CERTDIR=""`
* Executed only on `main`

---

## Docs

**Command:** `mvn javadoc:javadoc`
Generates and publishes HTML API documentation.

**Details:**

* Output is moved to the `public/` directory 
* GitLab Pages publishes automatically
* Executed only on `main`
* Artifacts retained for 30 days

---

# Artifacts and Reports

### Compiled Classes
* Location: `target/classes/`
* Content: Compiled Java bytecode for the entire project. Compiled by Maven.
* Generated by: `mvn clean compile`

### Unit Tests

* GitLab → Pipeline → Test Reports
* Files: `target/surefire-reports/TEST-*.xml`
* Execution Framework: JUnit 5 (Jupiter) v5.9.2
* Retention: 30 days

### Static Analysis

**Checkstyle Report**
* Location: `target/checkstyle-result.xml`
* Analysis: Code style validation using Google Java Style Guide conventions

**SpotBugs Report**
* Location: `target/spotbugsXml.xml`
* Analysis: Bytecode-level defect detection identifying potential bugs and code quality issues

### Build Artifact

* Executable JAR Archive
* Location: `target/task-manager-cli-*.jar`
* Retention: 90 days

### Documentation

* Generated by: `mvn javadoc:javadoc`
* Published through GitLab Pages from the `public/` directory.
* Retention: 30 days
---

# Running the Application Locally

### Build

```
mvn clean compile
```

### Run Tests

```
mvn test
```

### Static Analysis

```
mvn checkstyle:check
mvn spotbugs:check
```

### Package

```
mvn package
```

### Run

```
java -jar target/task-manager-cli-1.0.0.jar
```

### Generate Documentation

```
mvn javadoc:javadoc
open target/site/apidocs/index.html
```

---

### Technologies Used

* **Language: Java 17**
  Primary programming language used to implement the CLI application.

* **Build Tool: Maven 3.9.6**
  Handles compilation, dependency management, packaging and plugin execution.

* **Testing Framework: JUnit 5 (Jupiter)**
  Used to write and execute unit tests for the application logic.

* **Static Style Analysis: Checkstyle**
  Ensures code style consistency according to standard Java conventions.

* **Static Bug Analysis: SpotBugs**
  Detects potential defects by analyzing the compiled bytecode.

* **CI/CD Platform: GitLab CI/CD**
  Automates the entire pipeline including build, verification, testing, packaging, release and documentation.

* **Containerization: Docker**
  Used to build and publish the application as a Docker image in the release stage.

* **Documentation Hosting: GitLab Pages**
  Publishes the generated Javadoc as a static documentation website.

---
