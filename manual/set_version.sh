#!/usr/bin/env bash

# 项目根目录
basepath=$(cd `dirname $0`; pwd)/..

# 更新 pom.xml 版本号
cd ${basepath}
mvn versions:set -DnewVersion=$1
mvn versions:commit
