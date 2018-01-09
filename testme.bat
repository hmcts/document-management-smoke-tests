#!/bin/sh
export DM_GW_BASE_URI=http://localhost:8080
export DM_STORE_BASE_URI=http://localhost:8083
export IDAM_USER_BASE_URI=http://localhost:8081
export IDAM_S2S_BASE_URI=http://localhost:8082
export TEST_USERNAME=test@TEST.COM
export TEST_PASSWORD=123

docker-compose down
docker-compose -f docker-compose.yml -f docker-compose-test.yml pull
docker-compose -f docker-compose.yml -f docker-compose-test.yml build
docker-compose up -d --build

wait for dm app to spin up
wget --retry-connrefused --tries=120 --waitretry=1 -O /dev/null ${DM_STORE_BASE_URI}/health

#create user
$(./idam-create-user.bat ${TEST_USERNAME} ${TEST_PASSWORD} ${IDAM_USER_BASE_URI})

#get user token
export TEST_TOKEN=$(./idam-get-token.bat ${TEST_USERNAME} ${TEST_PASSWORD} ${IDAM_USER_BASE_URI})

echo ${TEST_TOKEN}

./gradlew.bat clean test --info;

#docker-compose -f docker-compose.yml -f docker-compose-test.yml run -e GRADLE_OPTS document-management-store-smoke-tests

sensible-browser ./build/reports/tests/test/index.html
open ./build/reports/tests/test/index.html

docker-compose down
