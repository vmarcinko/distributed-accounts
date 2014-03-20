package me.distributedaccounts.mgmt.service.cluster;

import org.apache.helix.NotificationContext;
import org.apache.helix.messaging.handling.MessageHandler;
import org.apache.helix.messaging.handling.MessageHandlerFactory;
import org.apache.helix.model.Message;

public class CustomMessageHandlerFactory implements MessageHandlerFactory {

    @Override
    public MessageHandler createHandler(Message message, NotificationContext context) {
        return new CustomMessageHandler(message, context);
    }

    @Override
    public String getMessageType() {
        return Message.MessageType.USER_DEFINE_MSG.toString();
    }

    @Override
    public void reset() {

    }
}
