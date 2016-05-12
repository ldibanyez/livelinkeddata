
import fr.inria.edelweiss.kgtool.load.LoadException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs.FileSystemException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class main {
    

    public static void main(String args[]) throws LoadException{
        try {
            server.Endpoint.main(args);
        } catch (URISyntaxException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileSystemException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
