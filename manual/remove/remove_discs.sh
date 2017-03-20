#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)

echo 'DELETE FROM disc_list_discs WHERE discs_id = '$1';' > ${basepath}/remove_discs.sql
echo 'DELETE FROM disc_record WHERE disc_id = '$1';' >> ${basepath}/remove_discs.sql
echo 'DELETE FROM disc_sakura WHERE disc_id = '$1';' >> ${basepath}/remove_discs.sql
echo 'DELETE FROM disc_rank WHERE disc_id = '$1';' >> ${basepath}/remove_discs.sql
echo 'DELETE FROM disc WHERE id = '$1';' >> ${basepath}/remove_discs.sql

cat ${basepath}/remove_discs.sql
echo `mysql -uroot -p -e 'select title from animediscs.disc where id = '$1`

echo 'Enter database root password:'
mysql -uroot -p animediscs < ${basepath}/remove_discs.sql

rm ${basepath}/remove_discs.sql
