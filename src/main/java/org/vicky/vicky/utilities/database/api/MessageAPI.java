package org.vicky.vicky.utilities.database.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.vicky.vicky.utilities.database.dao_s.FriendDAO;
import org.vicky.vicky.utilities.database.dao_s.MessageDAO;
import org.vicky.vicky.utilities.DBTemplates.FriendPlayer;
import org.vicky.vicky.utilities.DBTemplates.MessageEntity;

import java.util.List;

@Path("/chat")
public class MessageAPI {

    @Inject
    private FriendDAO friendDAO;

    @Inject
    private MessageDAO messageDAO;

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@QueryParam("sender") String senderId,
                                @QueryParam("receiver") String receiverId,
                                @QueryParam("content") String content) {
        if (senderId == null || receiverId == null || content == null || content.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sender ID, Receiver ID, and content must be provided.")
                    .build();
        }

        FriendPlayer sender = friendDAO.getFriendById(senderId);
        FriendPlayer receiver = friendDAO.getFriendById(receiverId);

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sender or receiver not found.")
                    .build();
        }

        sender.sendMessage(receiver, content);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatHistory(@QueryParam("user1") String user1Id,
                                   @QueryParam("user2") String user2Id) {
        if (user1Id == null || user2Id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User IDs must be provided.")
                    .build();
        }

        FriendPlayer user1 = friendDAO.getFriendById(user1Id);
        FriendPlayer user2 = friendDAO.getFriendById(user2Id);

        if (user1 == null || user2 == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("One or both users not found.")
                    .build();
        }

        List<MessageEntity> messages = messageDAO.getMessagesBetween(user1, user2);
        return Response.ok(messages).build();
    }
}
