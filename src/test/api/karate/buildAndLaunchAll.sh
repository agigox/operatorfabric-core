#/bin/sh

# Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
# See AUTHORS.txt
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
# SPDX-License-Identifier: MPL-2.0
# This file is part of the OperatorFabric project.

rm -rf target

(cd ../../../../
echo "Stop java services"
bin/run_all.sh stop
sleep 5
echo "Hard stop for cleaning if needed"
bin/run_all.sh hardstop
echo "Start docker compose for building" 
docker-compose -f config/dev/docker-compose.yml  up -d 
echo "Build all" 
./gradlew clean dockerTagSnapshot
echo "Stop docker-compose for building"
docker-compose -f config/dev/docker-compose.yml  down  
echo "Start opfab"
cd config/docker
./docker-compose.sh
echo "Starting in progress..."
sleep 90 
echo "Start karate testing"
cd ../../src/test/api/karate
./launchAllBusinessconfig.sh
./launchAllUsers.sh
./launchAllCards.sh
google-chrome target/cucumber-html-reports/overview-features.html &
)