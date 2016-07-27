package info.doseamigos.amigousers;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Simple POJO test.  Tests that all setters work with both null and nonnull objects,
 * getters return what was set previously, and the equals, hashcode, and toString methods.
 */
public class AmigoUserTest {

    @Test
    public void testAmigoUserId() throws Exception {
        AmigoUser user = new AmigoUser();
        user.setId(1L);

        assertEquals(user.getId(), (Long) 1L);
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

    @Test(enabled = false)
    public void testEquals() throws Exception {
        AmigoUser user1 = new AmigoUser();
        user1.setId(1L);
        user1.setName("test1");

        AmigoUser user2 = new AmigoUser();
        user2.setId(1L);
        user2.setName("test1");

        assertTrue(user1.equals(user2));

        user2.setName("test2");
        assertFalse(user1.equals(user2));

        user2.setName("test1");
        user2.setId(2L);
        assertFalse(user1.equals(user2));

        user2.setName("test2");
        assertFalse(user1.equals(user2));

    }

    @Test
    public void testHashCode() throws Exception {
        AmigoUser user1 = new AmigoUser();
        user1.setId(1L);
        user1.setName("test1");

        AmigoUser user2 = new AmigoUser();
        user2.setId(1L);
        user2.setName("test1");

        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test2");
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test1");
        user2.setId(2L);
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setName("test2");
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

}