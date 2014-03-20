package me.distributedaccounts.mgmt.service.cluster;

import org.apache.helix.messaging.AsyncCallback;
import org.apache.helix.model.Message;

public class BootstrapReplyHandler extends AsyncCallback {

    private String bootstrapUrl;
    private String bootstrapTime;

    public BootstrapReplyHandler() {
    }

    public String getBootstrapUrl() {
        return bootstrapUrl;
    }

    public String getBootstrapTime() {
        return bootstrapTime;
    }

    @Override
    public void onTimeOut() {
        System.out.println("Timed out");
    }

    @Override
    public void onReplyMessage(Message message) {
        String time = message.getResultMap().get("BOOTSTRAP_TIME");
        if (bootstrapTime == null || time.compareTo(bootstrapTime) > -1) {
            bootstrapTime = message.getResultMap().get("BOOTSTRAP_TIME");
            bootstrapUrl = message.getResultMap().get("BOOTSTRAP_URL");
        }
    }
}
