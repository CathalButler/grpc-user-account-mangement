package ie.gmit.ds.db;

import ie.gmit.ds.api.User;

import javax.ws.rs.core.GenericEntity;
import java.util.ArrayList;
import java.util.HashMap;

/* Cathal Butler | G00346889
 * Class that handles storing user accounts in a HashMap
 */

public class UserAccountDB {
    //Member Variables
    // HashMap to store all User accounts as a database is not being used
    private static HashMap<Integer, User> userAccountMap = new HashMap<>();

    public UserAccountDB() {
    }

    static {
        User user = new User(100, "Butblob", "cb@outook.com");
        userAccountMap.put(user.getUserId(), user);
        User user1 = new User(200, "Morganic", "mr@outook.com");
        userAccountMap.put(user1.getUserId(), user1);
    }

    /**
     * @return new array list with map values
     */
    public static GenericEntity<ArrayList<User>> getUserAccounts() {
        ArrayList<User> userDb = new ArrayList<User>(userAccountMap.values());
        return new GenericEntity<ArrayList<User>>(userDb) {
        };
    }

    /**
     * @param id
     * @return account by its id
     */
    public static User getUserAccountById(Integer id) {
        return userAccountMap.get(id);
    }

    /**
     * @param user
     */
    public static void addUserAccount(Integer id, User user) {
        userAccountMap.put(id, user);
    }

    /**
     * @param id
     */
    public static void removeUserAccount(Integer id) {
        userAccountMap.remove(id);
    }
}// End class
