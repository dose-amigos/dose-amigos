package info.doseamigos.authusers;

import info.doseamigos.amigousers.AmigoUser;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Simple POJO test.  Tests that all setters work with both null and nonnull objects,
 * getters return what was set previously, and the equals, hashcode, and toString methods.
 */
public class AuthUserTest {

    @Test
    public void testGetAuthUserId() throws Exception {
        AuthUser user = new AuthUser();
        user.setAuthUserId(1L);

        assertEquals(user.getAuthUserId(), (Long) 1L);
    }

    @Test
    public void testGetAmigoUser() throws Exception {
        AuthUser user = new AuthUser();
        user.setAmigoUser(new AmigoUser(1, "test"));

        assertEquals(user.getAmigoUser(), new AmigoUser(1, "test"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSetAmigoUser() throws Exception {
        new AuthUser().setAmigoUser(null);
    }

    @Test
    public void testGetEmail() throws Exception {
        AuthUser user = new AuthUser();
        user.setEmail("test@biz.quick");

        assertEquals(user.getEmail(), "test@biz.quick");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSetEmail() throws Exception {
        new AuthUser().setEmail(null);
    }

    @Test
    public void testEquals() throws Exception {
        AuthUser user1 = new AuthUser();
        user1.setAuthUserId(1L);
        user1.setAmigoUser(new AmigoUser(1, "test"));
        user1.setEmail("test@biz.quick");

        AuthUser user2 = new AuthUser();
        user2.setAuthUserId(1L);
        user2.setAmigoUser(new AmigoUser(1, "test"));
        user2.setEmail("test@biz.quick");

        assertTrue(user1.equals(user2));

        user2.setAuthUserId(2L);
        assertFalse(user1.equals(user2));

        user2.setAuthUserId(1L);
        user2.setAmigoUser(new AmigoUser(2, "test2"));
        assertFalse(user1.equals(user2));

        user2.setAmigoUser(new AmigoUser(1, "test"));
        user2.setEmail("test2@biz.quick");
        assertFalse(user1.equals(user2));

    }

    @Test
    public void testHashCode() throws Exception {
        AuthUser user1 = new AuthUser();
        user1.setAuthUserId(1L);
        user1.setAmigoUser(new AmigoUser(1, "test"));
        user1.setEmail("test@biz.quick");

        AuthUser user2 = new AuthUser();
        user2.setAuthUserId(1L);
        user2.setAmigoUser(new AmigoUser(1, "test"));
        user2.setEmail("test@biz.quick");

        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setAuthUserId(2L);
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setAuthUserId(1L);
        user2.setAmigoUser(new AmigoUser(2, "test2"));
        assertNotEquals(user1.hashCode(), user2.hashCode());

        user2.setAmigoUser(new AmigoUser(1, "test"));
        user2.setEmail("test2@biz.quick");
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

}