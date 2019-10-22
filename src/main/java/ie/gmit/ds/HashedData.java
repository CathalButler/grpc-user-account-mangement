package ie.gmit.ds;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Model class for Hashed Data. This is used to make objects from the hashed Password and the salt.
 */

import com.google.protobuf.ByteString;

public class HashedData {

    // Member Variables
    // Array list to hold hashed password and the salt
    private ByteString hashedPassword;
    private ByteString salt;

    // Default Constructor
    public HashedData() {
    }

    public HashedData(ByteString hashedPassword, ByteString salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public ByteString getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(ByteString hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public ByteString getSalt() {
        return salt;
    }

    public void setSalt(ByteString salt) {
        this.salt = salt;
    }
}// End class
