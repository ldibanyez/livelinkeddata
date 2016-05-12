/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.SimplePenta;
import Operations.TMOperation;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class FileTriplePublisherTest {
    
        Gson gson = new Gson();
    
    SimplePenta p1 = new SimplePenta("http://www.example.org/G1", "<http://www.example.org/book1>", "<http://www.example.org/title>", "'A new book'","1.G1#1");
        SimplePenta p2 = new SimplePenta("http://www.example.org/G1", "<http://www.example.org/book2>", "<http://www.example.org/title>", "'A newer book'","1.G1#2");
        SimplePenta p3 = new SimplePenta("http://www.example.org/G3", "<http://www.example.org/book3>", "<http://www.example.org/title>", "'The newest book'","1.G3#1");
        SimplePenta p4 = new SimplePenta("http://www.example.org/G4", "<http://www.example.org/book4>", "<http://www.example.org/title>", "'An old book'","1.G4#1");
        ArrayList<SimplePenta> ins = new ArrayList<>();
        ArrayList<SimplePenta> del = new ArrayList<>();
        ArrayList<String> trace1 = new ArrayList<>();
        ArrayList<String> trace2 = new ArrayList<>();
        TMOperation op1;
        TMOperation op2;
        FileTriplePublisher pub; 

    public FileTriplePublisherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        pub = new FileTriplePublisher("/tmp/testfiletriplelog");
        ins.add(p1);
        ins.add(p2);
        del.add(p3);
        del.add(p4);
        trace1.add("p1");
        trace1.add("p2");
        trace2.add("p2");
        trace2.add("p1");
        op1 = new TMOperation(ins,del,trace1);
        op2 = new TMOperation(ins,del,trace2);
    }
    
    @After
    public void tearDown() throws IOException {
        pub.destroy();
    }

    /**
     * Test of handle method, of class FileTriplePublisher.
     */
    @Test
    public void testHandle() throws IOException {
        pub.handle(op1);
        pub.handle(op2);

        List<String> stringlog = 
                FileUtils.readLines(pub.getPath().toFile(), "UTF-8");

        List<SimplePenta> log = new ArrayList<>();
        for(String strpenta : stringlog){
            //log.add(new SimplePenta(strpenta.trim()));
            log.add(gson.fromJson(strpenta, SimplePenta.class));
        }
        
        List<SimplePenta> expected = new ArrayList<>();
        for(SimplePenta todel : op1.getDelete()){
            expected.add(todel);
        }
        for(SimplePenta toins : op1.getInsert()){
            expected.add(toins);
        }
        for(SimplePenta todel : op2.getDelete()){
            expected.add(todel);
        }
        for(SimplePenta toins : op2.getInsert()){
            expected.add(toins);
        }

        assertEquals(expected,log);
    }

    /**
     * Test of getFrom method, of class FileTriplePublisher.
     */
    @Test
    public void testGetFrom() throws Exception {

        pub.handle(op1);
        pub.handle(op1);
        pub.handle(op1);
        pub.handle(op2);
        pub.handle(op2);
        pub.handle(op2);
        pub.handle(op2);
        pub.handle(op1);

        List<SimplePenta> expected = new ArrayList<>();

        for(int i = 0 ; i< 3 ; i++){
            for(SimplePenta todel : op2.getDelete()){
                expected.add(todel);
            }
            for(SimplePenta toins : op2.getInsert()){
                expected.add(toins);
            }
        }
            for(SimplePenta todel : op1.getDelete()){
                expected.add(todel);
            }
            for(SimplePenta toins : op1.getInsert()){
                expected.add(toins);
            }

        List<String> sublog = pub.getFrom(4*4);
        List<SimplePenta> result = new ArrayList<>();
        for(String l : sublog){
            result.add(gson.fromJson(l, SimplePenta.class));
        }
        
        assertEquals(expected.size(),result.size());
        assertEquals(expected,result);
        
    }
}