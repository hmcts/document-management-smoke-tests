#!/bin/sh
clear;
docker-compose down
docker-compose pull
docker-compose -f docker-compose.yml -f docker-compose-dev.yml up --build
