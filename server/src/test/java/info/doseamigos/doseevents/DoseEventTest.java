package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.meds.Med;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Simple POJO test.  Tests that all setters work with both null and nonnull objects,
 * getters return what was set previously, and the equals, hashcode, and toString methods.
 */
public class DoseEventTest {

    private Date testTime = new Date();
    private DoseEvent doseEvent;

    @BeforeMethod
    public void setup() {
        doseEvent = new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        );
    }

    @Test
    public void testGetEventId() throws Exception {
        assertEquals(doseEvent.getEventId(), 1);
    }

    @Test
    public void testGetAmigoUser() throws Exception {
        assertEquals(doseEvent.getAmigoUser(), new AmigoUser(1, "test"));
    }

    @Test
    public void testGetMeds() throws Exception {
        assertEquals(doseEvent.getMeds(), Arrays.asList(new Med(
            1L,
            new AmigoUser(1, "test"),
            1,
            "Test Drug"
        )));
    }

    @Test
    public void testGetTimeTaken() throws Exception {
        assertEquals(doseEvent.getTimeTaken(), testTime);
    }

    @Test
    public void testGetEventType() throws Exception {
        assertEquals(doseEvent.getEventType(), EventType.MISSED);
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(doseEvent.equals(new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        )));

        assertFalse(doseEvent.equals(new DoseEvent(
            2,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        )));

        assertFalse(doseEvent.equals(new DoseEvent(
            1,
            new AmigoUser(2, "test2"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        )));

        assertFalse(doseEvent.equals(new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                2L,
                new AmigoUser(1, "test"),
                2,
                "Test Drug 2"
            )),
            testTime,
            EventType.MISSED
        )));

        assertFalse(doseEvent.equals(new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            new Date(),
            EventType.MISSED
        )));

        assertFalse(doseEvent.equals(new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.TAKEN
        )));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(doseEvent.hashCode(), new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        ).hashCode());

        assertNotEquals(doseEvent.hashCode(), new DoseEvent(
            2,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        ).hashCode());

        assertNotEquals(doseEvent.hashCode(), new DoseEvent(
            1,
            new AmigoUser(2, "test2"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.MISSED
        ).hashCode());

        assertNotEquals(doseEvent.hashCode(), new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                2L,
                new AmigoUser(1, "test"),
                2,
                "Test Drug 2"
            )),
            testTime,
            EventType.MISSED
        ).hashCode());

        assertNotEquals(doseEvent.hashCode(), new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            new Date(),
            EventType.MISSED
        ).hashCode());

        assertNotEquals(doseEvent.hashCode(), new DoseEvent(
            1,
            new AmigoUser(1, "test"),
            Arrays.asList(new Med(
                1L,
                new AmigoUser(1, "test"),
                1,
                "Test Drug"
            )),
            testTime,
            EventType.TAKEN
        ).hashCode());

    }

}