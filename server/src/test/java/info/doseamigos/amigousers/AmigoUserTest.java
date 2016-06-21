package info.doseamigos.amigousers;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Simple POJO test, make sure that getters and setters do as expected and equals method returns
 * what is expected.  I figure for small classes, this is easy enough, but larger classes might
 * not need 100 percent coverage.
 */
public class AmigoUserTest {

    @Test
    public void testAmigoUserId() throws Exception {
        AmigoUser user = new AmigoUser();
        user.setAmigoUserId(1);

        assertEquals(user.getAmigoUserId(), 1);
    }

    @Test
    public void testName() throws Exception {
        AmigoUser user = new AmigoUser();
        user.setName("test");

        assertEquals(user.getName(), "test");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSetName_Null_ThrowsException() throws Exception {
        AmigoUser user = new AmigoUser();
        user.setName(null);
    }

    @Test
    public void testEquals() throws Exception {
        AmigoUser user1 = new AmigoUser();
        user1.setAmigoUserId(1);
        user1.setName("test1");

        AmigoUser user2 = new AmigoUser();
        user2.setAmigoUserId(1);
        user2.setName("test1");

        assertTrue(user1.equals(user2));

        user2.setName("test2");
        assertFalse(user1.equals(user2));

        user2.setName("test1");
        user2.setAmigoUserId(2);
        assertFalse(user1.equals(user2));

        user2.setName("test2");
        assertFalse(user1.equals(user2));

    }

    @Test
    public void testHashCode() throws Exception {
        AmigoUser user1 = new AmigoUser();
        user1.setAmigoUserId(1);
        user1.setName("test1");

        AmigoUser user2 = new AmigoUser();
        user2.setAmigoUserId(1);
        user2.setName("test1");

        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test2");
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test1");
        user2.setAmigoUserId(2);
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test2");
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        AmigoUser user = new AmigoUser();
        user.setName("test");
        user.setAmigoUserId(1);

        assertEquals(user.toString(), "AmigoUser{amigoUserId=1, name='test'}");
    }
}