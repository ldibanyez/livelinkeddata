/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class TMOperationTest {
    
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
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetId() {
    }

    @Test
    public void testSetTrace() {
    }

    @Test
    public void testStamp() {
    }

    @Test
    public void testGetTrace() {
    }

    @Test
    public void testGetInsert() {
    }

    @Test
    public void testGetDelete() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testSerialize() {

        SimplePenta sp1 = new SimplePenta("graph","subject","predicate","object","tag");
        SimplePenta sp2 = new SimplePenta("graph","subject","predicate","object","tag");

        ArrayList<SimplePenta> toIns = new ArrayList<>();
        toIns.add(sp1);
        ArrayList<SimplePenta> toDel = new ArrayList<>();
        toDel.add(sp2);

        TMOperation op = new TMOperation("id",toIns,toDel);
        op.stamp("s1");
        op.stamp("s2");

        String ser = op.serialize();

        TMOperation op2 = new TMOperation(ser);

        assertEquals(op,op2);

    }
}
