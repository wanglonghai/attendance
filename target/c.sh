#!/bin/bash
nohup java -jar  lop-lecrep.jar --spring.profiles.active=prod --server.port=8093 >> /usr/local/attend/output.log 2>&1 &
