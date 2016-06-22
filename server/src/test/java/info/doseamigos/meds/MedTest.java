package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Simple POJO test.  Tests that all setters work with both null and nonnull objects,
 * getters return what was set previously, and the equals, hashcode, and toString methods.
 */
public class MedTest {

    @Test
    public void testGetMedId() throws Exception {
        Med med = new Med();
        med.setMedId(1);

        assertEquals(med.getMedId(), 1);
    }

    @Test
    public void testGetUser() throws Exception {
        Med med = new Med();
        med.setUser(new AmigoUser(1, "test"));

        assertEquals(med.getUser(), new AmigoUser(1, "test"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSetUser() throws Exception {
        new Med().setUser(null);
    }

    @Test
    public void testGetRxcui() throws Exception {
        Med med = new Med();
        med.setRxcui(1L);

        assertEquals(med.getRxcui(), Long.valueOf(1));
    }

    @Test
    public void testSetRxcui() throws Exception {
        Med med = new Med();
        med.setRxcui(null);

        assertEquals(med.getRxcui(), null);
    }

    @Test
    public void testGetName() throws Exception {
        Med med = new Med();
        med.setName("Test Drug");

        assertEquals(med.getName(), "Test Drug");
    }

//    @Test(expectedExceptions = NullPointerException.class)
//    public void testSetName() throws Exception {
//        new Med().setName(null);
//    }

    @Test
    public void testEquals() throws Exception {
        Med med1 = new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            "Test Drug"
        );

        Med med2 = new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            "Test Drug"
        );

        assertTrue(med1.equals(med2));

        med2.setMedId(2);
        assertFalse(med1.equals(med2));

        med2.setMedId(1);
        med2.setUser(new AmigoUser(2, "test2"));
        assertFalse(med1.equals(med2));


        med2.setUser(new AmigoUser(1, "test1"));
        med2.setRxcui(2L);
        assertFalse(med1.equals(med2));

        med2.setRxcui(1L);
        med2.setName("Different Drug");
        assertFalse(med1.equals(med2));
    }

    @Test
    public void testHashCode() throws Exception {
        Med med1 = new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            "Test Drug"
        );

        Med med2 = new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            "Test Drug"
        );

        assertEquals(med1.hashCode(), med2.hashCode());

        med2.setMedId(2);
        assertNotEquals(med1.hashCode(), med2.hashCode());

        med2.setMedId(1);
        med2.setUser(new AmigoUser(2, "test2"));
        assertNotEquals(med1.hashCode(), med2.hashCode());


        med2.setUser(new AmigoUser(1, "test1"));
        med2.setRxcui(2L);
        assertNotEquals(med1.hashCode(), med2.hashCode());

        med2.setRxcui(1L);
        med2.setName("Different Drug");
        assertNotEquals(med1.hashCode(), med2.hashCode());
    }

}