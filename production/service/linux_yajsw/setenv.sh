#!/bin/bash
# -----------------------------------------------------------------------------
# Set java exe and conf file for all scripts
#
# -----------------------------------------------------------------------------

# resolve links - $0 may be a softlink

current=`pwd`/service/linux_yajsw
#cd `dirname "$0"`
PRGDIR=`pwd`/service/linux_yajsw
cd "$current"
echo "$current"
yajsw_home="$PRGDIR"
export yajsw_home

yajsw_jar="$yajsw_home"/wrapper.jar
export yajsw_jar

yajsw_app_jar="$yajsw_home"/wrapperApp.jar
export yajsw_app_jar

yajsw_java_options=-Xmx15m
export yajsw_java_options

java_exe=java
export java_exe

# show java version
"$java_exe" -version

conf_file="$yajsw_home"/wrapper.conf
export conf_file

conf_default_file="$yajsw_home"/wrapper.conf.default
export conf_default_file

source ../../exec/searchenv.sh

confPath=`pwd`

java -classpath ../yajsw_conf.jar com.websqrd.fastcat.yajsw.ConfWriter "$confPath"/wrapper.conf ../../ "$yajsw_java_options" "$yajsw_account_user" "$yajsw_account_password"

chmod +x wrapper.sh
