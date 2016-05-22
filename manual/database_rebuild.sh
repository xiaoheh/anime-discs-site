#!/usr/bin/env bash
echo 'Enter database root password:'
mysql -uroot -p < manual/database_rebuild.sql