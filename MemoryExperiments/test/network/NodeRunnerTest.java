/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
public class NodeRunnerTest {
    
    public NodeRunnerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of printDocument method, of class NodeRunner.
     */
    @Test
    public void testPrintDocument() throws Exception {
    }

    /**
     * Test of checkOthersIdle method, of class NodeRunner.
     */
    @Test
    public void testCheckOthersIdle() throws Exception {
        Path tempdir = Files.createTempDirectory("testCheckOthersIdle");
        String idle = "1";
        String notidle = "0";
        Path f1 = Files.createTempFile(tempdir,"TemFile" ,"1");
        Path f2 = Files.createTempFile(tempdir,"TemFile" ,"2");
        Path f3 = Files.createTempFile(tempdir,"TemFile" ,"3");

        Files.write(f1,idle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f2,notidle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f3,notidle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);

        assertFalse(NodeRunner.checkOthersIdle(tempdir, f1));
        
        Files.write(f1,idle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f2,notidle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f3,idle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);

        assertFalse(NodeRunner.checkOthersIdle(tempdir, f1));

        Files.write(f1,notidle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f2,idle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(f3,idle.getBytes("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING);
        
        assertTrue(NodeRunner.checkOthersIdle(tempdir, f1));
    }
}