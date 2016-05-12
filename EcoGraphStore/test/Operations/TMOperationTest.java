/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import fr.inria.acacia.corese.exceptions.EngineException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMOperationTest {

    TMOperation onlyIns;
    TMOperation onlyDel;
    TMOperation delIns;
    ArrayList<SimplePenta> ins;
    ArrayList<SimplePenta> del;
    List<String> trace = new ArrayList<>();
    SimplePenta sp1 = new SimplePenta("http://example.org/graph1"
                ,"http://example.org/test",
                "http://example.org/resourceAsPredicate",
                "http://example.org/test",
                "1*ID1#1");
    SimplePenta sp2 = new SimplePenta("http://example.org/graph1"
                ,"http://example.org/test",
                "http://example.org/resourceAsPredicate",
                "LITERAL",
                "1*ID1#2");
    
    public TMOperationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        ins = new ArrayList<>();
        del = new ArrayList<>();
        ins.add(sp1);
        ins.add(sp2);
        del.addAll(ins);
        trace.add("AuthorID");
        onlyIns = new TMOperation(ins, new ArrayList<SimplePenta>(),trace);
        onlyDel = new TMOperation(new ArrayList<SimplePenta>(), del, trace);
        delIns = new TMOperation(ins,del,trace);
    }
    
    @After
    public void tearDown() {
    }

    @Test 
    public void testSetTrace() {
        List<String> newtrace = new ArrayList<>();
        newtrace.add("ID1");
        newtrace.add("ID2");
        TMOperation tm = new TMOperation(ins,del,trace);
        tm.setTrace(newtrace);
        assertEquals(newtrace,tm.getTrace());
    }

    @Test 
    public void testStamp() {
        List<String> newtrace = new ArrayList<>();
        newtrace.add("ID1");
        newtrace.add("ID2");
        TMOperation tm = new TMOperation(ins,del,newtrace);
        tm.stamp("ID3");
        List<String> expected = new ArrayList<>();
        expected.addAll(newtrace);
        expected.add("ID3");
        assertEquals(expected,tm.getTrace());
    }

    @Test 
    public void testGetTrace() {
        assertEquals(trace,onlyIns.getTrace());
        assertEquals(trace,onlyDel.getTrace());
        assertEquals(trace,delIns.getTrace());
    }

    @Test 
    public void testGetInsert() {
        assertEquals(ins,onlyIns.getInsert());
        assertEquals(ins,delIns.getInsert());
    }

    @Test 
    public void testGetDelete() {
        assertEquals(del,onlyDel.getDelete());
        assertEquals(del,delIns.getDelete());
    }

    @Test 
    public void testToString() {
        //Eye test
        System.out.println(onlyIns);
        System.out.println(onlyDel);
        System.out.println(delIns);
    }

    @Test 
    public void testConstructFromNPentaString(){
        String onlyInsStr = onlyIns.toString();
        TMOperation onlyIns2 = new TMOperation(onlyInsStr,SerialType.NPENTA);
        assertEquals(onlyIns,onlyIns2);

        String onlyDelStr = onlyDel.toString();
        TMOperation onlyDel2 = new TMOperation(onlyDelStr,SerialType.NPENTA);
        assertEquals(onlyDel,onlyDel2);

        String delInsStr = delIns.toString();
        TMOperation delIns2 = new TMOperation(delInsStr,SerialType.NPENTA);
        assertEquals(delIns,delIns2);
    }

    @Test 
    public void testConstructFromFile() throws IOException{
        Charset charset = Charset.forName("UTF-8");

        Path onlyInsPath = Paths.get("/tmp/onlyInsTest");
        try (BufferedWriter writer = Files.newBufferedWriter(onlyInsPath, charset)) {
            String str = onlyIns.toString();
            writer.write(str, 0, str.length());
        }

        File onlyInsFile = onlyInsPath.toFile();
        TMOperation fromFile = new TMOperation(onlyInsFile);
        assertEquals(onlyIns,fromFile);

        Path onlyDelPath = Paths.get("/tmp/onlyDelTest");
        try (BufferedWriter writer = Files.newBufferedWriter(onlyDelPath, charset)) {
            String str = onlyDel.toString();
            writer.write(str, 0, str.length());
        }

        File onlyDelFile = onlyDelPath.toFile();
        fromFile = new TMOperation(onlyDelFile);
        assertEquals(onlyDel,fromFile);

        Path delInsPath = Paths.get("/tmp/delInsTest");
        try (BufferedWriter writer = Files.newBufferedWriter(delInsPath, charset)) {
            String str = delIns.toString();
            writer.write(str, 0, str.length());
        }

        File delInsFile = delInsPath.toFile();
        fromFile = new TMOperation(delInsFile);
        assertEquals(delIns,fromFile);
        

        //Files.delete(onlyInsPath);
        //Files.delete(onlyDelPath);
        //Files.delete(delInsPath);
    }

    
    @Test 
    public void testToNPentaFile() throws FileNotFoundException, IOException{
        Path onlyInsPath = Paths.get("/tmp/onlyInsTest");
        File onlyInsFile = onlyInsPath.toFile();
        onlyIns.toNPentaFile(onlyInsFile);
        TMOperation fromFile = new TMOperation(onlyInsFile);
        assertEquals(onlyIns,fromFile);

        Path onlyDelPath = Paths.get("/tmp/onlyDelTest");
        File onlyDelFile = onlyDelPath.toFile();

        onlyDel.toNPentaFile(onlyDelFile);
        fromFile = new TMOperation(onlyDelFile);
        assertEquals(onlyDel,fromFile);

        Path delInsPath = Paths.get("/tmp/delInsTest");
        File delInsFile = delInsPath.toFile();
        delIns.toNPentaFile(delInsFile);
        fromFile = new TMOperation(delInsFile);
        assertEquals(delIns,fromFile);
        

        Files.delete(onlyInsPath);
        Files.delete(onlyDelPath);
        Files.delete(delInsPath);
    
    }

    @Test
    public void testToJSON(){
        System.out.println(onlyIns.toJSON());
        System.out.println(onlyDel.toJSON());
    
    }

    @Test 
    public void testSerialize() {
        fail("Not yet implemented");
    }

    @Test 
    public void testSubOperation() throws EngineException{
        TMOperation subOp = onlyIns.subOperation("?x ?y ?z");
        assertEquals(subOp,onlyIns);

    
        subOp = onlyIns.subOperation("?x ?y <http://example.org/test>");
        
        ArrayList<SimplePenta> insExpected = new ArrayList<>();
        insExpected.add(sp1);
        TMOperation expected = new TMOperation(insExpected, new ArrayList<SimplePenta>(),onlyIns.getTrace());
        assertEquals(expected,subOp);
    
        subOp = onlyDel.subOperation("?x ?y <http://example.org/test>");
        
        ArrayList<SimplePenta> delExpected = new ArrayList<>();
        delExpected.add(sp1);
        expected = new TMOperation(new ArrayList<SimplePenta>(), delExpected, onlyIns.getTrace());
        assertEquals(expected,subOp);
    
    }
}
