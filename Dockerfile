FROM java:8-jdk

MAINTAINER "HMCTS Evidence Team <https://github.com/hmcts>"
LABEL maintainer="HMCTS Evidence Team <https://github.com/hmcts>"

RUN apt-get update
RUN apt-get install -y curl jq

RUN mkdir -p tests
WORKDIR /tests
COPY . .

ENTRYPOINT ./smokeTest.sh
