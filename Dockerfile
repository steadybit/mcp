FROM azul/zulu-openjdk-debian:24

ARG VERSION=unknown
ARG REVISION=unknown

LABEL "steadybit.com.discovery-disabled"="true"
LABEL "version"="${VERSION}"
LABEL "revision"="${REVISION}"
RUN echo "$VERSION" > /version.txt && echo "$REVISION" > /revision.txt

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends coreutils bash procps && \
    apt-get clean

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]