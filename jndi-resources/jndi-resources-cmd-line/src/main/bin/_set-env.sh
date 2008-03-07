#!/bin/sh
# -----------------------------------------------------------------------------
#
# Environment Variable Prequisites
#
#   JAVA_HOME        Must point at your Java Development Kit installation.
#                   Defaults to JAVA_HOME if empty.
# -----------------------------------------------------------------------------
# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`

# Get standard environment variables
JNDI_RESOURCES_HOME=`cd "$PRGDIR/.." ; pwd`

# Init JRE
if [ -z "$JAVA_HOME" ]; then
  echo "The JAVA_HOME environment variable is not defined"
  echo "This environment variable is needed to run this program"
  exit 1
fi

#JAVA_OPTS=-Djava.endorsed.dirs="$JNDI_RESOURCES_HOME"/lib
for f in "$JNDI_RESOURCES_HOME"/lib/* ; do CLASSPATH=$CLASSPATH:$f; done

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JNDI_RESOURCES_HOME" ] && JNDI_RESOURCES_HOME=`cygpath --unix "$JNDI_RESOURCES_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JNDI_RESOURCES_HOME=`cygpath --absolute --windows "$JNDI_RESOURCES_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

