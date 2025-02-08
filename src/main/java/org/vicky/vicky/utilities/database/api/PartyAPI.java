package org.vicky.vicky.utilities.database.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.vicky.vicky.utilities.database.dao_s.PartyDAO;
import org.vicky.vicky.utilities.database.dao_s.FriendDAO;
import org.vicky.vicky.utilities.DBTemplates.Party;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;

@Path("/party")
public class PartyAPI {

    private final PartyDAO partyDAO = new PartyDAO();
    private final FriendDAO friendDAO = new FriendDAO();

    // Create a new party
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createParty(@QueryParam("leader") String leaderId,
                                @QueryParam("name") String partyName) {
        FriendPlayer leader = friendDAO.getFriendById(leaderId);
        if (leader == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Leader not found")
                    .build();
        }
        Party party = partyDAO.createParty(partyName, leader);
        return Response.status(Response.Status.CREATED).entity(party).build();
    }
}
