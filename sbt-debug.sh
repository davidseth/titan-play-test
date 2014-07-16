#!/bin/sh
test -f ~/.sbtconfig && . ~/.sbtconfig

SBT_LAUNCH=$(grep -oE '/[^ ]+sbt-launch.jar' $(which sbt))
# Take leading integer as debug port and not sbt args
DEBUG_PORT=$1
SBT_ARGS=`echo "$@" | grep -oE "[^0-9].*"`

exec java -Xmx512M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${DEBUG_PORT} ${SBT_OPTS} -jar $SBT_LAUNCH $SBT_ARGS
