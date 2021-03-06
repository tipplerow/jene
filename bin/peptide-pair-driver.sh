#!/bin/sh
########################################################################
# Usage: peptide-pair-driver.sh [JVM OPTIONS] PROP_FILE1 [PROP_FILE2 ...]
########################################################################

if [ $# -lt 1 ]
then
    echo "Usage:" `basename $0` "[JVM OPTIONS] PROP_FILE1 [PROP_FILE2 ...]"
    exit 1
fi

if [ -z "${JAM_HOME}" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

if [ -z "${JENE_HOME}" ]
then
    echo "Environment variable JENE_HOME is not set; exiting."
    exit 1
fi

${JAM_HOME}/bin/jam-run.sh $JENE_HOME jene.neo.PeptidePairDriver "$@"
