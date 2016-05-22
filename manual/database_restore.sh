#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)/..
backroot=${basepath}/backup
mysql -uroot -p animediscs < ${backroot}/backup.sql