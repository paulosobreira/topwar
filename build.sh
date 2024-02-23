#!/bin/bash
docker compose down
mvn clean package
mvn war:war
docker build -f topwar.dockerfile . -t sowbreira/topwar
docker compose up
