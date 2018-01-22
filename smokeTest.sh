#!/bin/sh
echo "GRADLE VAR = ${GRADLE_OPTS} \n"
wget --proxy=off --retry-connrefused --tries=120 --waitretry=1 -O /dev/null ${DM_STORE_BASE_URI}/health
$(./idam-create-user.sh ${TEST_USERNAME} ${TEST_PASSWORD} ${IDAM_USER_BASE_URI})
export TEST_TOKEN=$(./idam-get-token.sh ${TEST_USERNAME} ${TEST_PASSWORD} ${IDAM_USER_BASE_URI})
echo "JWT Token = ${TEST_TOKEN} \n"
./gradlew ${GRADLE_OPTS} clean test --info;
