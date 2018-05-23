@echo off

rem apollo portal db info
set apollo_portal_db_url="jdbc:mysql://192.168.1.180:3306/ApolloPortalDB?characterEncoding=utf8"
set apollo_portal_db_username="root"
set apollo_portal_db_password="Root@123456"

rem meta server url, different environments should have different meta server addresses
set dev_meta="http://192.168.1.182:8180"
set fat_meta="http://192.168.101.145:8180"
set uat_meta="http://10.72.38.229:8180"
set pro_meta="http://10.72.38.49"

set META_SERVERS_OPTS=-Ddev_meta=%dev_meta% -Dfat_meta=%fat_meta% -Duat_meta=%uat_meta% -Dpro_meta=%pro_meta%

rem =============== Please do not modify the following content =============== 
rem go to script directory
cd "%~dp0"

cd ..


echo "==== starting to build portal ===="

call mvn clean package -DskipTests -pl apollo-portal -am -Dapollo_profile=github,auth -Dspring_datasource_url=%apollo_portal_db_url% -Dspring_datasource_username=%apollo_portal_db_username% -Dspring_datasource_password=%apollo_portal_db_password% %META_SERVERS_OPTS%

echo "==== building portal finished ===="