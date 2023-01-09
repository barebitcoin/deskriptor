# After spending lots of time in Docker hell I don't have any more apetite for
# this. I'm lost in a sea of different JRE/JDK distributions with strange names,
# and they all give me different linker errors (at runtime!) it's completely
# impossible to debug. God, I hate the JVM.
# If someone who actually knows what they're doing comes around, it'd be great
# to separate this out to a builder/runner staged build. This leads to a
# horrendously large image.
FROM gradle:7-jdk11 AS build

WORKDIR /workdir

COPY . .
RUN gradle build

# TODO: parameterize the version?
RUN unzip build/distributions/deskriptor-1.0-SNAPSHOT.zip && \
    cp -r /workdir/deskriptor-1.0-SNAPSHOT /deskriptor

ENTRYPOINT [ "/deskriptor/bin/deskriptor"]
