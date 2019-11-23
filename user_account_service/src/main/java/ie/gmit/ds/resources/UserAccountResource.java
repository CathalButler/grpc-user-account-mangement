package ie.gmit.ds.resources;

import com.google.protobuf.ByteString;
import ie.gmit.ds.*;
import ie.gmit.ds.api.DataIntegrityValidation;
import ie.gmit.ds.api.User;
import ie.gmit.ds.api.UserLogin;
import ie.gmit.ds.db.UserAccountDB;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/users")
public class UserAccountResource {
    //Member Variable
    private final Validator validator;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub blockingStub;
    private final PasswordServiceGrpc.PasswordServiceStub asyncStub;
    private static final Logger logger = Logger.getLogger(UserAccountResource.class.getName());

    //Constructor
    public UserAccountResource(Validator validator, ManagedChannel externalServiceChannel) {
        this.validator = validator;
        blockingStub = PasswordServiceGrpc.newBlockingStub(externalServiceChannel);
        asyncStub = PasswordServiceGrpc.newStub(externalServiceChannel);

    }// End Constructor

    /**
     * Method to get all user accounts in the databases
     *
     * @return response status to client`
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUserAccounts() {
        return Response.ok(UserAccountDB.getUserAccounts()).build();
    }

    /**
     * Method that handles a GET request made to the endpoint '/users' with an id. A user if exists in the database
     * will be returned.
     *
     * @param id
     * @return response status to client
     */
    @GET
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUserAccountById(@PathParam("userId") int id) {
        User user = UserAccountDB.getUserAccountById(id);
        if (user != null)
            return Response.ok(user).build();
        else
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist").build();
    }//End get user by id method

    /**
     * Method that handles POST requests made to the endpoint '/users'. A new user will be added to the database with
     * the information passed in the request. The users password is not stored in the database but is sent to the
     * password server which will return a salt and hash for that password which will then also be saved with personal
     * details.
     *
     * @param user
     * @return response status to client
     */
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createUserAccount(User user) {
        // Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User ua = UserAccountDB.getUserAccountById(user.getUserId());
        if (violations.size() > 0) {
            DataIntegrityValidation validationMessages = new DataIntegrityValidation();
            for (ConstraintViolation<User> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //ref link above
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        if (ua == null) {
            //Hash users password and the account will be added to the database after.
            hash(user);
            return Response.status(Response.Status.CREATED).entity("User account has been created").build();
        } else
            //Return status problem to user
            return Response.status(Response.Status.CONFLICT).entity("User ID already exists in the database").build();
    }// End createUserAccount method

    /**
     * Method that handles PUT requests made to the endpoint '/users/' with an id. The user account matching the user id
     * will be updated in the database with the data passed in the request.
     *
     * @param id
     * @param user
     * @return response status to client
     */
    @PUT
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateUserAccountById(@PathParam("userId") int id, User user) {
        // Validation
        System.out.println(user.toString());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User ua = UserAccountDB.getUserAccountById(id);
        if (violations.size() > 0) {
            DataIntegrityValidation validationMessages = new DataIntegrityValidation();
            for (ConstraintViolation<User> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        // If user account is not = to null process result
        if (ua != null) {
            // Due to design choice the primary key is being set as the the User ID so a replace cannot be done
            // on an existing entry in the map. To over come this problem the original account will be remove and then
            // the new account will be added to the map. a.k.a the UserAccountsDB
            //Add new updated entry details to the db:
            try {
                UserAccountDB.removeUserAccount(id);
                hash(user);
                //Return ok response to client
                return Response.ok(user).entity("User account has been updated").build();
            } catch (StatusRuntimeException ex) {
                //Return bas request with runtime exception
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
            }//End try catch
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exists").build();
    }// End updateUserAccountById method

    /**
     * Method that handles DELETE requests made to the endpoint '/users/' with an id. The User account matching the user id
     * will be removed from the database.
     *
     * @param id
     * @return Response status to client
     */
    @DELETE
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response removeUserById(@PathParam("userId") int id) {
        User user = UserAccountDB.getUserAccountById(id);
        if (user != null) {
            UserAccountDB.removeUserAccount(id);
            return Response.ok().entity("User with id: " + id + " has been removed from the database").build();
        } else
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist with user id: "
                    + id).build();
    }//End removeUserAccount method


    /**
     * Method that handles POST login requests made to the endpoint '/users/login'. Passwords passed in the body are validated
     * agents there salt and hashed password to see if it is correct.
     *
     * @param userLogin
     * @return Response status to client
     */
    @POST
    @Path("/login")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response login(UserLogin userLogin) {
        Set<ConstraintViolation<UserLogin>> violations = validator.validate(userLogin);
        User ua = UserAccountDB.getUserAccountById(userLogin.getUserId());
        if (violations.size() > 0) {
            DataIntegrityValidation validationMessages = new DataIntegrityValidation();
            for (ConstraintViolation<UserLogin> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if

        if (ua != null) {
            //Make a request to validate info on the password server
            return validateRequest(userLogin.getPassword(), ua.getHashedPassword(), ua.getSalt());
        } else
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exists").build();
    }//End removeUserAccount method


    /**
     * Method that makes a hash request to the server with the params below
     *
     * @param user
     */
    public void hash(User user) {
        // Creating a request to the Server:
        UserHashRequest request = UserHashRequest.newBuilder().setUserId(user.getUserId())
                .setPassword(user.getPassword()).build();
        // Make a request to the server hash method
        asyncStub.hash(request, new StreamObserver<>() {
            @Override
            public void onNext(UserHashResponse userHashResponse) {
                //Log incoming request
                logger.info("Received request items");
                //Creating a new User object with the now hashed password and salt
                User newUser = new User(user.getUserId(), user.getUserName(), user.getEmail(),
                        userHashResponse.getSalt(), userHashResponse.getHashedPassword());
                //Added the new user account to the database
                UserAccountDB.addUserAccount(newUser.getUserId(), newUser);
            }//End override method

            @Override
            public void onError(Throwable throwable) {
                //Log errors if any:
                Status status = Status.fromThrowable(throwable);
                logger.log(Level.WARNING, "RPC Error: {0}", status);
            }//End onError

            @Override
            public void onCompleted() {
                logger.info("Finished Request");
            }//End onCompleted
        });
    }//End hash method

    /**
     * Mehtod that makes a validate request to the server with the parameters below
     *
     * @param password
     * @param hashedPassword
     * @param salt
     */
    public Response validateRequest(String password, String hashedPassword, String salt) {
        //Creating a request to the server
        ValidateRequest request = ValidateRequest.newBuilder()
                .setPassword(password)
                .setHashedPassword(ByteString.copyFrom(Base64.getDecoder().decode(hashedPassword)))
                .setSalt(ByteString.copyFrom(Base64.getDecoder().decode(salt)))
                .build();
        // Instance of Validate response
        ValidateResponse response;
        try {
            // Create the response to the
            response = blockingStub.validate(request);
            if (response.getValidity()) {
                return Response.status(Response.Status.OK).entity("Login Successful").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Login details incorrect").build();
            }//end if else
        } catch (StatusRuntimeException ex) {
            // Log exception if any
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        }// End try catch
    }// End validate request method
}// End class
