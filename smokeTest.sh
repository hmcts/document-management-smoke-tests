#!/bin/sh
echo "GRADLE VAR = ${GRADLE_OPTS} \n"
wget --retry-connrefused --tries=120 --waitretry=1 -O /dev/null ${DM_STORE_BASE_URI}/health
cd tests
./idam-create-user.sh
export TEST_JWT ./idam-get-jwt.sh
./gradlew $GRADLE_OPTS clean test --info;
