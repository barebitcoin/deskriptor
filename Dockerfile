FROM gradle:7-jdk11 AS build

WORKDIR /workdir

COPY . .
RUN gradle build

# TODO: parameterize the version?
RUN unzip build/distributions/deskriptor-1.0-SNAPSHOT.zip

FROM openjdk:11-jre-slim

# TODO: parameterize the version?
COPY --from=build /workdir/deskriptor-1.0-SNAPSHOT /deskriptor

ENTRYPOINT [ "/deskriptor/bin/deskriptor"]
