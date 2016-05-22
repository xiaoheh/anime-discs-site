#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)/..
datetext=$(date +%Y%m%d)
timetext=$(date +%H%M%S)
backroot=${basepath}/backup
mkdir -p ${backroot}/${datetext}
mysqldump -uroot -pfuhaiwei animediscs > ${backroot}/backup.sql
cp ${backroot}/backup.sql ${backroot}/${datetext}/backup-${datetext}-${timetext}.sql