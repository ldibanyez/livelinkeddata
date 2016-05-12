/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.Operation;
import Operations.SimplePenta;
import Operations.TMOperation;
import Provenance.TrioMonoid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 *
 * Publishes a log file locally in disk
 * 
 * @author luisdanielibanesgonzalez
 * 
 */
public class FileTripleNPentaPublisher extends FilePublisher{


    public FileTripleNPentaPublisher(String logpath){
        super(logpath);
    }
/*
 * Prints the JSON one line representation in the file 
 */
    @Override
    public void handle(Operation op) {
        if (!(op instanceof TMOperation)){
            throw new Error(op.toString() +" is not a TMOperation");
        }else{
            TMOperation tmop = (TMOperation) op;
            for(SimplePenta toDel : tmop.getDelete()){
                TrioMonoid poly = new TrioMonoid(toDel.getTag());
                poly.invert();
                toDel.setTag(poly.toString());
                writer.println(toDel.toString());
                lastline++;
            }
            for(SimplePenta toIns : tmop.getInsert()){
                writer.println(toIns.toString());
                lastline++;
            }
            writer.flush();
        }
    }

    public List<String> getFrom(final int from) throws IOException{
            if(from > lastline){
                throw new IllegalArgumentException
                        ("From value larger that last line of the log");
            }
            List<String> chunk = new ArrayList<>(lastline-from);
            try(ReversedLinesFileReader reader =
                new ReversedLinesFileReader(path.toFile()))
            {
                String line = reader.readLine();
                int i = lastline;
                while(line != null && i > from ){
                    chunk.add(0, line);
                    line = reader.readLine();
                    i--;
                }
            }
            return chunk;
    }
}
