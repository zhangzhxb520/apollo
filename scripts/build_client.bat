@echo off

rem meta server url, different environments should have different meta server addresses
set dev_meta="http://192.168.1.182:8180"
set fat_meta="http://192.168.101.145:8180"
set uat_meta="http://10.72.38.229:8180"
set pro_meta="http://10.72.38.49"
set cat_auto="false"

set META_SERVERS_OPTS=-Ddev_meta=%dev_meta% -Dfat_meta=%fat_meta% -Duat_meta=%uat_meta% -Dpro_meta=%pro_meta% -Dcat_auto=%cat_auto%

rem =============== Please do not modify the following content =============== 
rem go to script directory
cd "%~dp0"

cd ..

echo "==== starting to build client ===="

call mvn clean deploy -DskipTests -pl apollo-client -am %META_SERVERS_OPTS%

echo "==== building client finished ===="