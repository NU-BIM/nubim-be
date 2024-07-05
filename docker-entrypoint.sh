#!/bin/sh
set -e

java -jar ./app.jar --spring.profiles.active=${PROFILE}