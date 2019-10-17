package ie.gmit.ds;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Model class for Hashed Data. This is used to make objects from the hashed Password and the salt.
 */

public class HashedData {

    // Member Variables
    // Array list to hold hashed password and the salt
    private byte[] hashedPassword;
    private byte[] salt;

    // Default Constructor
    public HashedData() {
    }

    public HashedData(byte[] hashedPassword, byte[] salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}// End class
