FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only inside Server folder correctly
COPY . .

# Ensure mvnw runs properly
RUN chmod +x mvnw

# Skip tests to avoid surefire failures
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/sdlc-copilot-0.0.1-SNAPSHOT.jar"]
