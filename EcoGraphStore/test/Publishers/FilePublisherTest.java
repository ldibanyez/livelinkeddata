/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Publishers;

import Operations.SimplePenta;
import Operations.TMOperation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */



public class FilePublisherTest {
    
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
        FilePublisher pub; 
    
    public FilePublisherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        pub = new FilePublisher("/tmp/testfillog");
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

    @Test
    public void testHandle() throws IOException {
        pub.handle(op1);
        pub.handle(op2);

        List<String> log = FileUtils.readLines(pub.getPath().toFile(), "UTF-8");
        List<String> expected = new ArrayList<>();
        expected.add(op1.toJSON());
        expected.add(op2.toJSON());

        assertEquals(expected,log);
    }

    @Test
    public void testGetFromJSON() throws IOException {
        pub.handle(op1);
        pub.handle(op1);
        pub.handle(op1);
        pub.handle(op1);
        pub.handle(op2);
        pub.handle(op2);
        pub.handle(op2);
        pub.handle(op2);

        List<String> sublog = pub.getFromJSON(4);
        List<String> expected = new ArrayList<>();
        expected.add(op2.toJSON());
        expected.add(op2.toJSON());
        expected.add(op2.toJSON());
        expected.add(op2.toJSON());
        assertEquals(expected.size(),sublog.size());
        assertEquals(expected,sublog);
    }

    public void testExistingFile() throws IOException{
    
        Files.createFile(Paths.get("/tmp/testExisting"));
        pub = new FilePublisher("/tmp/testExisting");
        pub.destroy();
    }
    
}
