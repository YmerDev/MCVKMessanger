package dev.ymer.addons.vk.VK.Models;

public class Message {
    public long id;
    public long flags;
    public int from_id;
    public long ts;
    public String text;
    public int from_uid;

    public Message(long id, long flags, int from_id, long ts, String text, int from_uid) {
        this.id = id;
        this.flags = flags;
        this.from_id = from_id;
        this.ts = ts;
        this.text = text;
        this.from_uid = from_uid;
    }
}