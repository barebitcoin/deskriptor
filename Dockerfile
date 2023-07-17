# Example of custom Java runtime using jlink in a multi-stage container build
FROM eclipse-temurin:11 as jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --add-modules java.logging \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Stage 1: Build stage
FROM gradle:7-jdk11 AS build

WORKDIR /workdir

COPY . .
RUN gradle fatJar

# Stage 2: Runtime stage
# Needs to be a recent ish debian image to have a new enough
# libc. Still haven't understood the finer points here. 
FROM debian:bookworm-slim

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

WORKDIR /app

COPY --from=build /workdir/build/libs/deskriptor-1.0-SNAPSHOT-standalone.jar /app/
COPY logging.properties /logging.properties

ENTRYPOINT [ "java", "-jar", "/app/deskriptor-1.0-SNAPSHOT-standalone.jar" ]
