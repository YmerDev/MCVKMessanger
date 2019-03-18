package dev.ymer.addons.vk;

//import dev.ymer.addons.vk.commands.Command;
import dev.ymer.addons.vk.VK.LongPoll;
import dev.ymer.addons.vk.VK.Token;
import dev.ymer.addons.vk.commands.VKCommand;
import dev.ymer.addons.vk.commands.VKMessage;
import net.labymod.api.LabyModAddon;
//import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

import java.util.List;
import java.util.UUID;

public class VKAddon extends LabyModAddon {

    public static final String error = "§7[§cVK§7] §c";
    public static final String info = "§7[§9VK§7] §3";
    public static final String normal = "§7[§1VK§7] §f";
    public static boolean isSilent = false;

    public static Thread longPoll;

    public static final String bar = "§7§m                                                                              ";

    @Override
    public void onEnable() {

        CommandManager.registerCommand(new VKCommand(this));
        CommandManager.registerCommand(new VKMessage());

        getApi().getEventManager().register(new MessageSendEvent() {
            @Override
            public boolean onSend(final String message) {

                if (message.startsWith("/") && !message.startsWith("/ ")) {

                    if (CommandManager.dispatchCommand(message)) {
                        return true;
                    }
                }
                return false;
            }
        });

        getApi().getEventManager().registerOnJoin(serverData -> {
            if (Token.token != null) {
                if (!longPoll.isAlive()) {
                    longPoll = new Thread(() -> LongPoll.setEnabled("MessageHandler"));
                    longPoll.start();
                }
            }
        });

        getApi().getEventManager().registerOnQuit(serverData -> {
            if (longPoll.isAlive())
                longPoll.interrupt();
        });
    }

    @Override
    public void loadConfig() {
        if (this.getConfig().has("token")) {
            Token.token = this.getConfig().get("token").getAsString();
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

    }

    public static void sendLocalChat(String message) {
        LabyMod.getInstance().displayMessageInChat(message);
    }
}
