package me.distributedaccounts.mgmt.service.cluster;

import me.distributedaccounts.mgmt.service.ServiceStarter;
import org.apache.helix.NotificationContext;
import org.apache.helix.messaging.handling.HelixTaskResult;
import org.apache.helix.messaging.handling.MessageHandler;
import org.apache.helix.model.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class CustomMessageHandler extends MessageHandler {

    public CustomMessageHandler(Message message, NotificationContext context) {
        super(message, context);
    }

    @Override
    public HelixTaskResult handleMessage() throws InterruptedException {
        String hostName;
        HelixTaskResult result = new HelixTaskResult();
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        String port = "2134";
        String msgSubType = _message.getMsgSubType();
        if (msgSubType.equals(ServiceStarter.REQUEST_BOOTSTRAP_URL)) {
            result.getTaskResultMap().put(
                    "BOOTSTRAP_URL",
                    "http://" + hostName + ":" + port + "/getFile?path=/data/bootstrap/"
                            + _message.getResourceId().stringify() + "/"
                            + _message.getPartitionId().stringify() + ".tar");

            result.getTaskResultMap().put("BOOTSTRAP_TIME", "" + new Date().getTime());
        }

        result.setSuccess(true);
        return result;
    }

    @Override
    public void onError(Exception e, ErrorCode code, MessageHandler.ErrorType type) {
        e.printStackTrace();
    }
}
