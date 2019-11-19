package ie.gmit.ds.resources;

import ie.gmit.ds.api.User;
import ie.gmit.ds.db.UserAccountDB;

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
    }// End Constructor

    /**
     * Method to get all user accounts in the database
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
            UserAccountDB.addUserAccount(user.getUserId(), user);
            //Return ok response to client
            return Response.created(new URI("/login/" + user.getUserId()))
                    .build();
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
        User ua = UserAccountDB.getUserAccountById(user.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<User> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        // If user account is not = to null do update
        if (ua != null) {
            System.out.println("GOT INSIDE METHOD");
            UserAccountDB.updateUserAccount(user.getUserId(), user);
            //Return ok response to client
            return Response.ok(user).build();
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).build();
    }// End updateUserAccountById method
}// End class
