package ie.gmit.ds.db;

import ie.gmit.ds.api.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* Cathal Butler | G00346889
 * Class that handles storing user accounts in a HashMap
 */

public class UserAccountDB {
    //Member Variables
    // HashMap to store all User accounts as a database is not being used
    private static HashMap<Integer, UserAccount> userAccountMap = new HashMap<>();

    static {
        userAccountMap.put(1, new UserAccount(100, "Butblob", "cb@outook.com", "mup"));
        userAccountMap.put(2, new UserAccount(200, "Morganic", "mr@outook.com", "naaaa"));
    }

    /**
     * @return new array list with map values
     */
    public static List<UserAccount> getUserAccounts() {
        return new ArrayList<UserAccount>(userAccountMap.values());
    }

    /**
     * @param id
     * @return account by its id
     */
    public static UserAccount getUserAccountById(Integer id) {
        return userAccountMap.get(id);
    }

    /**
     * @param id
     * @param userAccount
     */
    public static void updateUserAccount(Integer id, UserAccount userAccount) {
        userAccountMap.put(id, userAccount);
    }

    /**
     * @param userAccount
     */
    public static void addUserAccount(Integer id, UserAccount userAccount) {
        userAccountMap.put(id, userAccount);
    }

    /**
     * @param id
     */
    public static void removeUserAccounts(Integer id) {
        userAccountMap.remove(id);
    }
}// End class
