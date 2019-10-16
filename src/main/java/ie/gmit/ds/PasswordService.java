package ie.gmit.ds;

import com.google.protobuf.BoolValue;
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
    private ArrayList<UserHashResponse> usersHashedData;

    // Default Constructor
    public PasswordService() {

    }// End Constructor

    @Override
    public void hash(UserHashRequest request, StreamObserver<BoolValue> responseObserver) {
        //Variables
        byte[] salt;
        //UserHashResponse uhr = new

        try {
            System.out.println("Service: Hash Request " + request);
            // Generate salt
            salt = Passwords.getNextSalt();

            System.out.println("\nUser ID: " + request.getUserId() + "\nBit of salt :" + Arrays.toString(salt));
            // Run Hash method is Passwords class passing it the password in a char array and the salt byte array
            System.out.println("\nHashed Password " + Arrays.toString(Passwords.hash(request.getPassword().toCharArray(), salt)));
            //Console log

            responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
        } catch (RuntimeException ex) {
            responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
        }// End try catch

        responseObserver.onCompleted();
    }// End method

    @Override
    public void validate(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        super.validate(request, responseObserver);
    }// End method


}// End class
