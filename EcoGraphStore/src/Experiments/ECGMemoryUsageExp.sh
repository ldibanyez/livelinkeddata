#/!bin/bash

ECOGRAPHPATH=/Users/luisdanielibanesgonzalez/NetBeansProjects/trunk/Corese/EcoGraphStore

CPATH=$ECOGRAPHPATH/lib/xstream-1.4.4.jar:$ECOGRAPHPATH/lib/kryonet-2.18-all.jar:$ECOGRAPHPATH/lib/corese-3.0.20.jar:$ECOGRAPHPATH/lib/guava-14.0.1.jar:$ECOGRAPHPATH/build/classes:$ECOGRAPHPATH/build/classes/Experiments

for NUMTRIPLES in 1000 10000 100000 
do
    echo $NUMTRIPLES
    for CONF in 1 2 3 4 5 6
    do
        #java -cp $CPATH -Xmx256M -javaagent:$ECOGRAPHPATH/lib/jamm-0.2.5.jar Experiments.ECGMemoryUsageExpJamm $CONF $NUMTRIPLES h
        java -cp $CPATH -Xmx256M Experiments.ECGMemoryUsageExpTOSC $CONF $NUMTRIPLES h
    done
    echo "END of $NUMTRIPLES" 
done