package ie.gmit.ds;

import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Password Service Class to handle password.proto Services
 */

public class PasswordService extends PasswordServiceGrpc.PasswordServiceImplBase {

    //Member Variables
    private static final Logger logger =
            Logger.getLogger(Client.class.getName());
    private ArrayList<HashedData> hashedDataArrayList;

    // Constructor
    public PasswordService() {
        hashedDataArrayList = new ArrayList<HashedData>();
    }// End Constructor

    @Override
    public void hash(UserHashRequest request, StreamObserver<UserHashResponse> responseObserver) {
        //Variables
        // New instances of HashedData
        HashedData hd = new HashedData();
        int userId = request.getUserId();


        try {
            System.out.println("Service: Hash Request " + request);
            // Generate salt
            hd.setSalt(Passwords.getNextSalt());
            // Run Hash method is Passwords class passing it the password in a char array and the salt byte array
            hd.setHashedPassword(Passwords.hash(request.getPassword().toCharArray(), hd.getSalt()));
            // Adding data to array list
            hashedDataArrayList.add(hd);
            //Console log
            System.out.println("Added to array list: Salt= " + Arrays.toString(hd.getSalt()) + "\nHashed Password= "+ Arrays.toString(hd.getHashedPassword()));
            // Converting byte[] to Strings to return in the response:
            String hashedPassword = new String(hd.getHashedPassword());
            String salt = new String(hd.getSalt());
            System.out.println("Test data output: " + hashedPassword + salt);
            // Response
            responseObserver.onNext(UserHashResponse.newBuilder().setUserId(userId).setHashedPassword(hashedPassword).setSalt(salt).build());

        } catch (RuntimeException ex) {
            //responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
        }// End try catch

        responseObserver.onCompleted();
    }// End method

    @Override
    public void validate(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        super.validate(request, responseObserver);
    }// End method


}// End class
