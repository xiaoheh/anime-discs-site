#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)
echo 'Enter database root password:'
mysql -uroot -p animediscs < ${basepath}/remove_discs.sql