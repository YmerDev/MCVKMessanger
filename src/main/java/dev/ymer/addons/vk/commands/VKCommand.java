package dev.ymer.addons.vk.commands;

import dev.ymer.addons.vk.VK.Cache;
import dev.ymer.addons.vk.VK.LongPoll;
import dev.ymer.addons.vk.VK.Token;
import dev.ymer.addons.vk.VK.VK;
import dev.ymer.addons.vk.VKAddon;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import org.json.JSONException;
import org.json.JSONObject;

public class VKCommand implements Command {

    private VKAddon addon;

    public VKCommand(VKAddon addon) {
        this.addon = addon;
    }

    @Override
    public String getName() {
        return "vk";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"/vk", "/vk"};
    }

    @Override
    public boolean execute(String[] args) {

        if (Token.token == null && args.length < 2) {
            VKAddon.sendLocalChat(VKAddon.error + "Вы не авторизованы. Используйте /vk auth [token]");
            return true;
        }

        switch (args[0]) {
            case "auth": {
                if (Token.token != null) {
                    VKAddon.sendLocalChat(VKAddon.error + "Вы уже авторизованы.");
                    return true;
                }

                if (args.length > 2) {
                    VKAddon.sendLocalChat(VKAddon.error + "Эта команда требует 1 аргумент.");
                    VKAddon.sendLocalChat(VKAddon.info + "/vk auth [token]");
                    return true;
                }

                new Thread(() -> {
                    String data = VK.query("users.get", "fields=sex", args[1]);

                    try {
                        JSONObject json = new JSONObject(data);
                        JSONObject user = json.getJSONArray("response").getJSONObject(0);
                        VK.setMe(user);
                        Token.token = args[1];

                        this.addon.getConfig().addProperty("token", args[1]);
                        this.addon.saveConfig();

                        VKAddon.sendLocalChat(VKAddon.info + "Вы успешно вошли!");

                        VKAddon.longPoll = new Thread(() -> LongPoll.setEnabled("MessageHandler"));

                        VKAddon.longPoll.start();
                    } catch (JSONException e) {
                        VKAddon.sendLocalChat(VKAddon.error + "Введённый токен не является верным.");
                    }
                }).start();
            }
            break;
            case "friends": {
                VKAddon.sendLocalChat("Todo...");
            }
            break;
            case "silent": {

                if (args.length > 1) {

                    new Thread(() -> {
                        try {
                            final int silentId = Integer.parseInt(args[1]);

                            if (Cache.silentChats.contains(silentId)) {
                                Cache.silentChats.remove(Cache.silentChats.indexOf(silentId));
                                VKAddon.sendLocalChat(VKAddon.info + VK.getUserName(silentId) + " успешно удален(-а) из списка тишины.");
                            } else {
                                Cache.silentChats.add(silentId);
                                VKAddon.sendLocalChat(VKAddon.info + VK.getUserName(silentId) + " успешно добавлен(-а) в список тишины.");
                            }

                        } catch (NumberFormatException e) {
                            VKAddon.sendLocalChat(VKAddon.error + "Чат должен являться целым числом");
                        }
                    }).start();

                    return true;
                }

                VKAddon.isSilent ^= true;

                VKAddon.sendLocalChat(VKAddon.info + "Режим тишины переключён.");
                if (!VKAddon.isSilent) {
                    VKAddon.sendLocalChat(VKAddon.info + "Вы получили " + Cache.silentCounter + " сообщений пока были в режиме тишины.");
                    Cache.silentCounter = 0;
                }
            }
            break;
            default: {
                if (Token.token == null)
                    return true;

                String me = VK.getMe();

                VKAddon.sendLocalChat(VKAddon.bar);
                VKAddon.sendLocalChat(VKAddon.info + "Добро пожаловать, " + me + "!");
                VKAddon.sendLocalChat(VKAddon.info + "Используйте /vk friends, чтобы получить список друзей;");
                VKAddon.sendLocalChat(VKAddon.info + "Используйте /vk dialogs, чтобы получить список диалогов;");
                VKAddon.sendLocalChat(VKAddon.info + "Используйте /m, чтобы отправить сообщение;");
                VKAddon.sendLocalChat(VKAddon.info + "Используйте /vk silent, чтобы отключить получение новых сообщений;");
            }
            break;
        }
        return true;
    }
}
