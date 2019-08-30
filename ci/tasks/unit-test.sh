#!/bin/bash

set -ex

cd code-repo
./mvnw clean verify
