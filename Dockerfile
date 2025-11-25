FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the jar produced by the package stage
COPY target/task-manager-cli-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
