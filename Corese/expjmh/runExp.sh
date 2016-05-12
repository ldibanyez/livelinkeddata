#!/bin/bash

#java -jar target/microbenchmarks.jar ".*GraphRecomputationRemote.*" -f 1 -rf CSV -rff GraphRecomputationRemote.res -wi 10 -i 10
java -Xmx1024m -jar target/microbenchmarks.jar ".*GraphRecomputationLocal.*" -f 10 -o GraphRecomputationLocal.out -rf CSV -rff GraphRecomputationLocal.res -wi 100 -i 100
java -Xmx1024m -jar target/microbenchmarks.jar ".*TMGraphIncrementalLocal.*" -f 10 -o TMGraphIncrementalLocal.out -rf CSV -rff TMGraphIncrementalLocal.res -wi 100 -i 100
