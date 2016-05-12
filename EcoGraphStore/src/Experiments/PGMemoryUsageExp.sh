#/!bin/bash

ECOGRAPHPATH=/Users/luisdanielibanesgonzalez/NetBeansProjects/trunk/Corese/EcoGraphStore

CPATH=$ECOGRAPHPATH/lib/xstream-1.4.4.jar:$ECOGRAPHPATH/lib/kryonet-2.18-all.jar:$ECOGRAPHPATH/lib/corese-3.0.20.jar:$ECOGRAPHPATH/lib/guava-14.0.1.jar:$ECOGRAPHPATH/build/classes:$ECOGRAPHPATH/build/classes/Experiments


for NUMTRIPLES in 1000 10000 100000 
do
    echo $NUMTRIPLES
    #java -cp $CPATH -Xmx256m -javaagent:$ECOGRAPHPATH/lib/jamm-0.2.5.jar Experiments.PGMemoryUsageExpJamm $NUMTRIPLES h
    java -cp $CPATH -Xmx256m Experiments.PGMemoryUsageExpTOSC $NUMTRIPLES h
    echo "END of $NUMTRIPLES" 
done