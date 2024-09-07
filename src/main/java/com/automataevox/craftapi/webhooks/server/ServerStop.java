package com.automataevox.craftapi.webhooks.server;

import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.json.JSONObject;

public final class ServerStop implements WebHook {

    private final String eventName = WebHookEnum.SERVER_STOP.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", eventName);
            jsonObject.put("message", "Server has stopped");
            jsonObject.put("note", "This event is triggered when the plugin gets disabled, that means the server could still be running.");

            RegisterWebHooks.sendToAllUrls(jsonObject);
        }
    }
}
