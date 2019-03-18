package dev.ymer.addons.vk.VK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    public static Map<Integer, String> userNames = new HashMap<>();
    public static Map<Integer, String> chatNames = new HashMap<>();
    public static List<Integer> easySend = new ArrayList<>(); // use for easy answering.

    public static List<Integer> silentChats = new ArrayList<>();
    public static int silentCounter = 0; // we can (or should?) count new message which user got when he was in silent mode.

}
