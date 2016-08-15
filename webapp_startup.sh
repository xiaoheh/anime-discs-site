#!/usr/bin/env bash
cd `dirname $0`
mvn jetty:run > webapp.log & exit