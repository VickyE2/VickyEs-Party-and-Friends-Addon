package org.vicky.vicky.utilities.database.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.vicky.vicky.utilities.database.dao_s.FriendDAO;
import org.vicky.vicky.utilities.database.dao_s.FriendRequestDAO;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;
import org.vicky.vicky.utilities.DBTemplates.FriendRequest;
import org.vicky.vicky.utilities.enums.FriendRequestStatus;

@Path("/friend-requests")
public class FriendRequestAPI {

    @Inject
    private FriendRequestDAO friendRequestDAO;

    @Inject
    private FriendDAO friendDAO;

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendFriendRequest(@QueryParam("senderId") String senderId,
                                      @QueryParam("receiverId") String receiverId) {
        if (senderId == null || receiverId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sender ID and Receiver ID must be provided.")
                    .build();
        }

        FriendPlayer sender = friendDAO.getFriendById(senderId);
        FriendPlayer receiver = friendDAO.getFriendById(receiverId);

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sender or receiver not found.")
                    .build();
        }

        FriendRequest existingRequest = friendRequestDAO.getRequest(sender, receiver);
        if (existingRequest != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Friend request already exists.")
                    .build();
        }

        FriendRequest request = new FriendRequest(sender, receiver, FriendRequestStatus.PENDING);
        friendRequestDAO.saveRequest(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/accept")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acceptFriendRequest(@QueryParam("requestId") Long requestId) {
        if (requestId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request ID must be provided.")
                    .build();
        }

        friendRequestDAO.updateRequestStatus(requestId, FriendRequestStatus.ACCEPTED);
        FriendRequest request = friendRequestDAO.getRequest(requestId);
        friendDAO.addFriend(request.getReceiver().getId(), request.getSender().getId());
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/reject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rejectFriendRequest(@QueryParam("requestId") Long requestId) {
        if (requestId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request ID must be provided")
                    .build();
        }

        friendRequestDAO.updateRequestStatus(requestId, FriendRequestStatus.REJECTED);
        return Response.status(Response.Status.OK).build();
    }
}