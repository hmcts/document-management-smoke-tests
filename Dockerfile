FROM java:8-jdk

MAINTAINER "HMCTS Evidence Team <https://github.com/hmcts>"
LABEL maintainer="HMCTS Evidence Team <https://github.com/hmcts>"

sudo apt-get install -y curl jq

RUN mkdir -p tests
COPY . tests



ENTRYPOINT ./smokeTest.sh
