
SCRIPT_PATH=`dirname $0`

PACIFY_LIB_DIR=${SCRIPT_PATH}/../lib

java -classpath "$PACIFY_LIB_DIR/*" com.geewhiz.pacify.commandline.PacifyViaCommandline $@
