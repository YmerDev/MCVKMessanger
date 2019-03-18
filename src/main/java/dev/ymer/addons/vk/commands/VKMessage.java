package dev.ymer.addons.vk.commands;

import dev.ymer.addons.vk.VK.Cache;
import dev.ymer.addons.vk.VK.Token;
import dev.ymer.addons.vk.VK.VK;
import dev.ymer.addons.vk.VKAddon;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class VKMessage implements Command {
    @Override
    public String getName() {
        return "vkmessage";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"/m", "/vkmsg", "/vkm", "/vm"};
    }

    @Override
    public boolean execute(String[] args) {

        if (args.length < 2) {
            VKAddon.sendLocalChat(VKAddon.info + "/m [peer_id] [message]");
            return true;
        }

        if (Token.token == null) {
            VKAddon.sendLocalChat(VKAddon.error + "Используйте /vk auth [token] для авторизации.");
            return true;
        }

        final String message = String.join(" ", ArrayUtils.remove(args, 0));
        int peerId;
        try {
            peerId = Integer.parseInt(args[0]);

            if (Cache.easySend.size() >= peerId) { // .get isnt good solution?
                peerId = Cache.easySend.get(peerId);
                System.out.println(peerId);
            }

        } catch (NumberFormatException e) {
            VKAddon.sendLocalChat(VKAddon.error + "Получать должен быть целым числом.");
            return true;
        }

        final int pId = peerId;

        Thread sendMessage = new Thread(() -> {
            String jsonResult = VK.sendMsg(pId, message);

            try {
                JSONObject json = new JSONObject(jsonResult);

                if (json.has("error")) { // probably message wasn't send.
                    JSONObject errorObject = json.getJSONObject("error");
                    VKAddon.sendLocalChat(VKAddon.error + errorObject.getString("error_msg"));
                } else {
                    String peerName = VK.getUserName(pId);
                    VKAddon.sendLocalChat(VKAddon.info + "§3Вы §a» §3" + peerName + "§f: " + message);
                }

            } catch (JSONException ignored) {}

            Thread.currentThread().interrupt();
        });

        sendMessage.start();

        return true;
    }
}
