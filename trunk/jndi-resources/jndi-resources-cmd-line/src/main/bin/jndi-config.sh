#!/bin/sh

. `dirname $0`/_set-env.sh

#echo "---------"
#echo JNDI_RESOURCES_HOME=$JNDI_RESOURCES_HOME
#echo CLASSPATH=$CLASSPATH
#echo JAVA_OPTS=$JAVA_OPTS
#echo "---------"

exec "$JAVA_HOME"/bin/java $JAVA_OPTS \
-classpath "$CLASSPATH" \
-Djndi.resources.home="$JNDI_RESOURCES_HOME" \
org.jndiresources.config.JNDIConfig  -t "$JNDI_RESOURCES_HOME/templates" "$@"
