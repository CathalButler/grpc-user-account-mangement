package ie.gmit.ds;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Password Service Class to handle password.proto Services
 */

public class PasswordService extends PasswordServiceGrpc.PasswordServiceImplBase {
    // Constructor
    public PasswordService() {
    }// End Constructor

    @Override
    public void hash(UserHashRequest request, StreamObserver<UserHashResponse> responseObserver) {
        //Variables
        int userId = request.getUserId();
        // Generate salt
        byte[] salt = Passwords.getNextSalt();
        // Run Hash method is Passwords class passing it the password in a char array and the salt byte array
        byte[] hashedPassword = Passwords.hash(request.getPassword().toCharArray(), salt);
        // Response
        UserHashResponse response = UserHashResponse.newBuilder().setUserId(userId)
                .setHashedPassword(ByteString.copyFrom(hashedPassword))
                .setSalt(ByteString.copyFrom(salt)).build();
        // Send response
        responseObserver.onNext(response);
        // Complete response
        responseObserver.onCompleted();
    }// End method

    @Override
    public void validate(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        // Variables
        char[] password = request.getPassword().toCharArray();
        byte[] salt = request.getSalt().toByteArray();
        byte[] expectedHash = request.getHashedPassword().toByteArray();
        // Run checker method in Passwords Class
        boolean validity = Passwords.isExpectedPassword(password, salt, expectedHash);
        //Response
        ValidateResponse response = ValidateResponse.newBuilder().setValidity(validity).build();
        // Send response
        responseObserver.onNext(response);
        // Complete response
        responseObserver.onCompleted();
    }// End method
}// End class
