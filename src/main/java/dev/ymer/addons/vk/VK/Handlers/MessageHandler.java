package dev.ymer.addons.vk.VK.Handlers;

import dev.ymer.addons.vk.VK.Cache;
import dev.ymer.addons.vk.VK.Models.Message;
import dev.ymer.addons.vk.VK.VK;
import dev.ymer.addons.vk.VKAddon;

public class MessageHandler {
    public static void handleMessage(Message msg) {
        /*
        +1	 UNREAD	message is unread
        +2	 OUTBOX	outgoing message
        +4	 REPLIED	a reply to the message has been created
        +8	 IMPORTANT	marked message
        +16	 CHAT	message sent from chat
        +32	 FRIENDS	message sent by a friend
        +64	 SPAM	message marked as "Spam"
        +128 DELЕTЕD	message deleted (in the recycle bin)
        +256 FIXED	message checked for spam by the user
        +512 MEDIA	message contains media content
        +8192 Dialog
         */

        boolean outbox = (msg.flags & 2) != 0;
        boolean chat = (msg.flags & 8192) != 0;
        boolean containsMedia = (msg.flags & 512) != 0;

        if (outbox) // we shouldnt handle outbox messages
            return;

        if (!Cache.easySend.contains(msg.from_id)) {
            Cache.easySend.add(msg.from_id);
        }

        int easySendId = Cache.easySend.indexOf(msg.from_id);

        if (VKAddon.isSilent || Cache.silentChats.contains(msg.from_id)) {
            Cache.silentCounter++;
            return;
        }

        if (chat) {
            VKAddon.sendLocalChat(VKAddon.info + "§d#"+ easySendId + " §8| §a[" + VK.getChatName(msg.from_id) + "] §3" + VK.getUserName(msg.from_uid) + "§f: " + msg.text);
        } else {
            VKAddon.sendLocalChat(VKAddon.info + "§d#" + easySendId + " §8| §3" + VK.getUserName(msg.from_id) + " §a» §3Вы§f: " + msg.text);
        }
    }
}
