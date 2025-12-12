# Task Manager CLI

Task Manager CLI is a minimal Java 17 command-line application that provides CRUD operations for **Tasks** and **Categories**.
The project is intentionally lightweight, with a clear separation between domain, application logic and CLI interface, allowing the focus to be placed on a fully automated and well-structured CI/CD pipeline.

## Team Members

* **Ethan Gabriel Leskovec** – 886040 – [e.leskovec@campus.unimib.it](mailto:e.leskovec@campus.unimib.it)
* **Riccardo Pretali** – 870452 – [r.pretali@campus.unimib.it](mailto:r.pretali@campus.unimib.it)

## Repository Links

* **GitHub**: [https://github.com/rPretali/task-manager-cli.git](https://github.com/rPretali/task-manager-cli.git)
* **GitLab**: [https://gitlab.com/rpretali-group/task-manager-cli.git](https://gitlab.com/rpretali-group/task-manager-cli.git)

---

# Project Overview

The application supports basic task management through a simple text-based interface.
Users can do:

**Task Management:**
* **Create tasks** with customizable titles, descriptions and category assignments
* **List all tasks** with filtering options to view tasks by status (done/pending) or category
* **Update task details** including titles, descriptions and category associations
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

# Architectural and Technological Choices

## Technology Stack

### Java 17
Java 17 is a Long-Term Support (LTS) release providing stability and compatibility with the Maven ecosystem and CI/CD tools. Its maturity ensures reliable compilation and testing within automated pipelines.

### Maven 3.9.6
Maven is the de-facto standard build tool for Java projects, chosen for its:
- Standardized build lifecycle that maps cleanly to CI/CD stages
- Extensive plugin ecosystem (Checkstyle, SpotBugs, JaCoCo, Javadoc)
- Dependency caching capabilities that optimize pipeline execution times
- Native integration with GitLab CI/CD runners

### GitLab CI/CD
GitLab provides an integrated DevOps platform that covers the entire pipeline lifecycle:
- Native CI/CD with YAML-based configuration
- Built-in Container Registry for Docker image storage
- GitLab Pages for automatic documentation publishing
- Caching mechanisms for dependency optimization
- Test Reports integration for visual test result display
- Artifact management with configurable retention policies

## Pipeline Design Philosophy

The pipeline stages are deliberately structured to balance speed, thoroughness and resource efficiency:

1. **Parallel execution in Verify stage**: Static analysis (Checkstyle, SpotBugs) and dynamic analysis (JaCoCo) run simultaneously to provide rapid feedback on different quality aspects without blocking each other.

2. **Sequential execution in Test stage**: Unit tests run before integration tests (fail-fast approach) to catch fundamental issues quickly before executing more complex and time-consuming integration tests.

3. **Selective execution**: Package, Release and Docs stages run only on the `main` branch to avoid unnecessary artifact generation and storage consumption for feature branches.

4. **Cache optimization**: Maven dependencies are cached with the Build stage as the sole writer, while downstream stages use read-only access to ensure consistency and reduce build times.

---

# CI/CD Pipeline Overview

A complete CI/CD pipeline is implemented using GitLab CI/CD, covering the entire lifecycle:
```
Commit
 - Build
 - Verify (parallel: Checkstyle, SpotBugs, Code Coverage)
 - Test (Unit and Integration)
 - Package
 - Release
 - Docs (GitLab Pages)
```

The pipeline ensures:
* Consistent and reproducible builds
* Early quality feedback via static and dynamic analysis
* Automated packaging and versioned artifacts
* Docker image creation and distribution
* Automatically published documentation

---

# CI/CD Stages

**Pipeline Optimization:** The pipeline uses GitLab's caching mechanism to store Maven dependencies between runs. The cache key is based on `pom.xml`, ensuring dependencies are re-downloaded only when they change. The Build stage has write access to update the cache, while subsequent stages use read-only access for consistency.

## Build

**Command:** `mvn clean compile`

Compiles the codebase and resolves project dependencies.

The Build stage ensures that the code compiles successfully and all Maven dependencies are downloaded from central repositories. The `clean` goal removes artifacts from previous builds to guarantee a fresh compilation.

**Cache Strategy:**
This stage implements a `pull-push` cache policy on the `.m2/repository/` directory, keyed to the `pom.xml` file. This means the cache is updated only when dependencies change, avoiding redundant downloads. The Build stage is the only one with write permissions to prevent cache inconsistencies across parallel jobs.

**Artifacts:**
Compiled classes are retained in `target/` for 1 day, as they serve only as intermediate artifacts for downstream stages within the same pipeline execution.


## Verify

The Verify stage runs three jobs in parallel to provide rapid feedback on code quality from different perspectives: static style analysis, static bug detection and dynamic code coverage measurement.

### Checkstyle

**Command:** `mvn checkstyle:check`

Performs static style validation against Java coding standards. Checkstyle enforces consistent formatting, naming conventions and code structure.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

**allow_failure: true** – Since the focus of this assignment is on the pipeline rather than on refactoring the application code, Checkstyle violations are treated as warnings rather than blockers. The report remains available for inspection in `target/checkstyle-result.xml` (retained for 7 days), allowing quality monitoring without halting the pipeline.

### SpotBugs

**Command:** `mvn spotbugs:check`

Performs static analysis on compiled bytecode to detect potential bugs, code smells and suspicious patterns. SpotBugs identifies issues like null pointer dereferences, resource leaks and concurrency problems.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

**allow_failure: true** – Similar to Checkstyle, SpotBugs findings are treated as quality feedback rather than hard failures. This allows the pipeline to complete while still generating the analysis report in `target/spotbugsXml.xml` (retained for 7 days).

### Code Coverage

**Command:** `mvn test jacoco:report`

Executes the test suite with JaCoCo to measure code coverage. This job satisfies the dynamic analysis requirement by actually running the code and collecting runtime metrics.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

JaCoCo generates both HTML reports and CSV data showing line and branch coverage percentages. The coverage summary is printed to the console for immediate visibility in the pipeline logs, while detailed reports are stored in `target/site/jacoco/` (retained for 30 days).

**Retention:** Coverage reports are retained for 30 days, aligned with test reports retention, since coverage metrics are intrinsically tied to test execution and are useful for tracking coverage trends over time.

Running tests at this stage, in parallel with static analysis, provides early feedback on test coverage alongside style and bug reports. The tests will be executed again in the dedicated Test stage to ensure consistent validation in a clean environment.


## Test

This stage executes the full test suite, split into two sequential jobs to ensure rapid feedback and resource efficiency. It validates the application's core behavior after static analysis, produces detailed XML reports and ensures consistent results in a clean execution environment.

### Unit Tests

**Command:** `mvn test -Dtest='!*IntegrationTest'`

Executes isolated unit tests only, excluding any class ending in `IntegrationTest`. Unit tests verify individual components (services, repositories, domain logic) in isolation without external dependencies.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

**Fail-fast approach:** If unit tests fail, the pipeline stops immediately. This prevents wasting computational resources on integration tests when fundamental issues exist in the codebase. Unit tests are typically faster and more numerous, making them ideal for catching basic logic errors early.

**Artifacts:** JUnit XML reports are generated in `target/surefire-reports/` and automatically integrated into GitLab's Test Reports interface (accessible via Pipeline > Tests), providing a visual summary of test results directly in the pipeline view. Reports are retained for 30 days to allow retrospective analysis of test trends and regressions.

### Integration Tests

**Command:** `mvn test -Dtest='*IntegrationTest'`

Executes only classes ending with `IntegrationTest`, which verify the interaction between application components (e.g., service layer with repository layer).

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

**Sequential execution:** This job runs only if unit tests pass successfully. Integration tests are more complex and time-consuming than unit tests, so executing them only after confirming basic functionality saves pipeline execution time and resources.

**Dependency on unit-tests job:** The explicit `needs: [unit-tests]` dependency enforces the fail-fast strategy, ensuring integration tests never run when foundational unit tests are failing.


## Package

**Command:** `mvn package -DskipTests`

Builds an executable JAR file containing the compiled application ready for distribution.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

Tests are skipped at this stage because they have already been executed and validated in both the Verify and Test stages. Re-running tests would be redundant and would unnecessarily increase pipeline execution time.

**Branch restriction:** This stage runs only on the `main` branch. Feature branches and development work do not require packaged artifacts, so limiting execution to `main` reduces unnecessary artifact generation and storage consumption.

**Artifact naming:** The JAR file is named with the commit SHA (`task-manager-cli-$CI_COMMIT_SHORT_SHA`) to ensure traceability between deployed artifacts and their source code version. This naming convention enables quick identification of which commit produced a specific artifact.

**Retention:** The JAR artifact is retained for 90 days, providing a reasonable window for rollback scenarios or for retrieving previous versions if issues are discovered in production. This extended retention balances accessibility with storage costs.


## Release

Builds and publishes a Docker image containing the packaged application to the GitLab Container Registry.

**Docker-in-Docker:** The release stage uses `docker:27.3.1` as the base image with `docker:27.3.1-dind` (Docker-in-Docker) as a service. This configuration allows building Docker images within the GitLab CI pipeline environment.

**Image tagging:** Each Docker image is tagged with the commit SHA (`$CI_COMMIT_SHORT_SHA`), ensuring that every version of the application can be uniquely identified and traced back to its source commit. This tagging strategy supports versioning and enables precise rollback to any previous image if needed.

**Container Registry:** The image is pushed to GitLab's integrated Container Registry, which provides authenticated access and seamless integration with GitLab's authentication system. The registry URL and credentials are automatically provided by GitLab CI through environment variables (`$CI_REGISTRY`, `$CI_JOB_TOKEN`).

**Dependency on Package stage:** The release job explicitly depends on the `package` stage to retrieve the JAR file produced earlier. This JAR is copied into the Docker image during the build process, as specified in the `Dockerfile`.

**Branch restriction:** Like the Package stage, Release runs only on `main` to avoid creating unnecessary Docker images for every feature branch or development commit.


## Docs

**Command:** `mvn javadoc:javadoc`

Generates HTML API documentation from source code comments and publishes it automatically via GitLab Pages.

This stage uses read-only cache access (`pull` policy) to retrieve dependencies downloaded by the Build stage.

**Javadoc generation:** Maven's Javadoc plugin extracts documentation from Java source files, creating a navigable HTML website that documents all public classes, methods and interfaces. This provides developers with a reference guide for the application's API.

**GitLab Pages integration:** The generated documentation is moved from `target/site/apidocs` to the `public/` directory, which is the standard location GitLab Pages expects for static site content. GitLab automatically publishes everything in `public/` as a static website accessible via a public URL.

**Automatic updates:** Every time the pipeline runs on `main`, the documentation is regenerated and republished, ensuring the published API documentation always reflects the current state of the codebase.

**Branch restriction:** Documentation is published only from `main` to ensure that the publicly accessible documentation represents the stable, released version of the application rather than work-in-progress code from feature branches.

**Retention:** Documentation artifacts are retained for 30 days, providing historical access while balancing storage requirements.

---

**Note on test report visibility:** Test reports from both unit and integration tests are automatically aggregated and displayed in GitLab's Test Reports interface (Pipeline > Tests tab), providing a unified view of all test results without needing to download XML files.

---

# Running the Application Locally

To develop and test locally, execute the following commands in order. Each step mirrors a corresponding pipeline stage, allowing you to validate the same build and test procedures that run automatically in CI/CD.

### Build
```bash
mvn clean compile
```

Compiles the source code and downloads dependencies.

### Static Analysis
```bash
mvn checkstyle:check
mvn spotbugs:check
```

Runs code style validation and bug detection analysis.

### Generate Coverage Report
```bash
mvn test jacoco:report
```

Generates code coverage report in `target/site/jacoco/index.html`.

### Run Tests
```bash
mvn test
```

Executes the full test suite (unit and integration tests).

### Package
```bash
mvn package
```

Builds the executable JAR file.

### Run the Application
```bash
java -jar target/task-manager-cli-1.0.0.jar
```

Launches the CLI application.

### Generate Documentation
```bash
mvn javadoc:javadoc
```

Generates API documentation. Open `target/site/apidocs/index.html` in a browser to view it.

---
