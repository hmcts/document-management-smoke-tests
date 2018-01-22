#!/bin/sh
curl --noproxy "*" -s -H 'Content-Type: application/json' -d '{ "email":"'"${1}"'", "forename":"test","surname":"test","password":"'"${2}"'"}' ${3}/testing-support/accounts
