package ie.gmit.ds.resources;

import ie.gmit.ds.api.Responder;
import ie.gmit.ds.api.User;
import ie.gmit.ds.api.UserLogin;
import ie.gmit.ds.client.Client;
import ie.gmit.ds.db.UserAccountDB;
import io.grpc.StatusRuntimeException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountResource {
    //Member Variable
    private final Validator validator;

    //Constructor
    public UserAccountResource(Validator validator) {
        this.validator = validator;
        //Create connection to server
    }// End Constructor

    /**
     * Method to get all user accounts in the databases
     *
     * @return response status to client
     */
    @GET
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
    public Response getUserAccountById(@PathParam("userId") int id) {
        User user = UserAccountDB.getUserAccountById(id);
        if (user != null)
            return Response.ok(user).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }//End get user by id method

    /**
     * Method that handles POST requests made to the endpoint '/users'. A new user will be added to the database with
     * the information passed in the request. The users password is not stored in the database but is sent to the
     * password server which will return a salt and hash for that password which will then also be saved with personal
     * details.
     *
     * @param user
     * @return response status to client
     * @throws URISyntaxException
     */
    @POST
    public Response createUserAccount(User user) throws URISyntaxException {
        // Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User ua = UserAccountDB.getUserAccountById(user.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<User> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        if (ua == null) {
            //Password hashing
            Client client = new Client("localhost", 50551);

            try {
                client.hashRequest(user);
                return Response.created(new URI("/login/" + user.getUserId()))
                        .build();

            } catch (StatusRuntimeException ex) {
                //Return bas request with runtime exception
                return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
            }//End try catch
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).build();
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
    public Response updateUserAccountById(@PathParam("userId") int id, User user) {
        // Validation
        System.out.println(user.toString());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        User ua = UserAccountDB.getUserAccountById(id);
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
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
            UserAccountDB.removeUserAccount(id);
            //Add new updated entry details to the db:
            Client client = new Client("localhost", 50551);
            client.hashRequest(user);
            //Return ok response to client
            return Response.ok(user).build();
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).build();
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
    public Response removeUserById(@PathParam("userId") int id) {
        User user = UserAccountDB.getUserAccountById(id);
        if (user != null) {
            UserAccountDB.removeUserAccount(id);
            return Response.ok().build();
        } else
            return Response.status(Response.Status.NOT_FOUND).build();
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
    public Response login(UserLogin userLogin) {
        Set<ConstraintViolation<UserLogin>> violations = validator.validate(userLogin);
        User ua = UserAccountDB.getUserAccountById(userLogin.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<UserLogin> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if

        if (ua != null) {
            Client client = new Client("localhost", 50551);

            //Make a request to validate info
            if (client.validateRequest(userLogin.getPassword(), ua.getHashedPassword(), ua.getSalt())) {
                return Response.status(Response.Status.OK).entity(new Responder("Login Stressful")).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(new Responder("Login details incorrect")).build();
            }
        } else
            return Response.status(Response.Status.NOT_FOUND).build();
    }//End removeUserAccount method
}// End class
