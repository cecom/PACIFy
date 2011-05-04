
SCRIPT_PATH=`dirname $0`

PFLIST_LIB_DIR=${SCRIPT_PATH}/../lib

for JAR in `ls $PFLIST_LIB_DIR/*.jar` ; do
  PFLIST_CLASSPATH="$PFLIST_CLASSPATH:$JAR"
done

java -classpath "$PFLIST_CLASSPATH" de.oppermann.maven.pflist.PFListPropertyReplacer $@
