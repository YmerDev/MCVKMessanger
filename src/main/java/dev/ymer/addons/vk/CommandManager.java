package dev.ymer.addons.vk;

import dev.ymer.addons.vk.commands.Command;
//import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;

import java.util.*;

public class CommandManager {
    public static Set<Command> commands = new HashSet<>();

    public static boolean dispatchCommand (String str) {

        String[] data = str.split(" ");
        String alias = data[0];
        Command cmdFound = null;
        for (Command cmd : commands) {
            for (String s : cmd.getAliases()) {
                if (s.equalsIgnoreCase(alias)) {
                    cmdFound = cmd;
                    break;
                }
            }
        }
        if (cmdFound == null) return false;

        List<String> dataList = new ArrayList<>(Arrays.asList(data));
        dataList.remove(0); //Remove alias


        try {
            if (!cmdFound.execute(dataList.toArray(new String[0]))) {
                return false; //Skip the command
            }

        } catch (Exception e) {
            LabyMod.getInstance().displayMessageInChat(VKAddon.error + "Something went wrong.");
            e.printStackTrace();
        }
        return true;
    }

    public static void registerCommand(Command command) {
        commands.add(command);
    }
}
