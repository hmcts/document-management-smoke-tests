FROM java:8-jdk

MAINTAINER "HMCTS Evidence Team <https://github.com/hmcts>"
LABEL maintainer="HMCTS Evidence Team <https://github.com/hmcts>"

ENV http_proxy $http_proxy
ENV https_proxy $https_proxy
ENV no_proxy $no_proxy

RUN echo $http_proxy
RUN echo $https_proxy
RUN echo $no_proxy

RUN apt-get update
RUN apt-get install -y curl jq

RUN mkdir -p tests
WORKDIR /tests
COPY . .

RUN unset http_proxy
RUN unset https_proxy
RUN unset no_proxy

ENTRYPOINT ./smokeTest.sh
