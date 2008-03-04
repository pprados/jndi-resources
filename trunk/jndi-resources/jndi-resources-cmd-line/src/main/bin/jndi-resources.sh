#!/bin/sh
. `dirname $0`/_set-env.sh

exec "$JAVA_HOME"/bin/java $JAVA_OPTS \
-classpath "$CLASSPATH" \
-Djndi.resources.home="$JNDI_RESOURCES_HOME" \
org.jndiresources.JNDIResources  -t "$PRGDIR/../templates" "$@"

