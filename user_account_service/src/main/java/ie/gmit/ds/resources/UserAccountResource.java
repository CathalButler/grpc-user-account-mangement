package ie.gmit.ds.resources;

import ie.gmit.ds.api.User;
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
     * Method to get a user account by id
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
            System.out.println(validationMessages);
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
     * @param id
     * @return
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

}// End class
