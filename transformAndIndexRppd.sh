#!/bin/bash
set -uo pipefail # See http://redsymbol.net/articles/unofficial-bash-strict-mode/

TIME=$(date "+%Y%m%d-%H%M")
INDEX="gnd-rppd-$TIME"
ALIAS="gnd-rppd-test"

# it is important to set the proper locale
. $HOME/.locale
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
JAVA_OPTS=$(echo "$JAVA_OPTS" |sed 's#,#\ #g')

cd ../rpb
bash transformRppd.sh
cd -
sbt clean
sbt --java-home $JAVA_HOME stage
JAVA_OPTS="$JAVA_OPTS -XX:+ExitOnOutOfMemoryError" ./target/universal/stage/bin/rppd -Dindex.prod.name=$INDEX "runMain apps.Index baseline"

COUNT=$(curl -X POST "indexcluster.lobid.org:9200/$INDEX/_count" | jq .count)
if (( $COUNT > 10000 )) ; then
    curl -X POST "indexcluster.lobid.org:9200/_aliases?pretty" -H 'Content-Type: application/json' -d'
        {
            "actions" : [
                { "remove" : { "index" : "*", "alias" : "'"$ALIAS"'" } },
                { "add" : { "index" : "'"$INDEX"'", "alias" : "'"$ALIAS"'" } }
            ]
        }
    '
    curl -S -s -o /dev/null https://rppd.lobid.org
else
    echo "Not switching alias, index count for $INDEX is too low: $COUNT"
fi
