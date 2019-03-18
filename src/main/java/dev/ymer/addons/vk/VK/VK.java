package dev.ymer.addons.vk.VK;

/*
#
# Copyright (c) 2015 Alexander Rizaev
#
# Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
# "Software"), to deal in the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
# LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
# OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#
*/

import main.java.stroum.HTTP.Requests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Random;


public class VK {

    private static double version = 5.92;

    public static String query(String method) {
        String d = Requests.post("https://api.vk.com/method/" + method, "v=" + VK.version + "&access_token=" + Token.token);
        return d;
    }

    public static String query(String method, String params) {
        String d = Requests.post("https://api.vk.com/method/" + method, "v=" + VK.version + "&access_token=" + Token.token +
                "&" + params);
        System.out.println(d);
        return d;
    }

    public static String query(String method, String params, String customToken) {
        String d = Requests.post("https://api.vk.com/method/" + method, "v=" + VK.version + "&access_token=" + customToken +
                "&" + params);
        System.out.println(d);
        return d;
    }

    public static String sendMsg(int from_id, String text) {
        Integer randomVal = new Random().nextInt();
        return query("messages.send", getSender(from_id) + "&message=" + text + "&random_id=" + randomVal);
    }

    public static String sendMsg(int from_id, String text, String attaches) {
        Integer randomVal = new Random().nextInt();
        return query("messages.send", getSender(from_id) + "&message=" + text + "&attachment=" + attaches + "&random_id=" + randomVal);
    }

    public static void markAsRead(int from_id) {
        VK.query("messages.markAsRead", "peer_id=" + from_id);
    }

    public static String getChatName(int cid) {
        if (cid > 2000000000) {
            cid = cid - 2000000000;
        }

        if (Cache.chatNames.get(cid) != null) {
            return Cache.chatNames.get(cid);
        }

        String title;

        String data = query("messages.getChat", "chat_id=" + cid);
        try {
            JSONObject obj = new JSONObject(data);
            JSONObject response = obj.getJSONObject("response");
            title = response.getString("title");

            Cache.chatNames.put(cid, title);

        } catch (JSONException e) {
            e.printStackTrace();
            title = "";
        }

        return title;
    }

    /* Don't try to understand wtf is this */
    public static String uploadPhoto(File f) {
        String id = "";
        String upload_url = "";

        try {
            String server = VK.query("photos.getMessagesUploadServer");

            JSONObject s = new JSONObject(server);
            upload_url = s.getJSONObject("response").getString("upload_url");

            String res = Requests.post_upload(upload_url, f);


            JSONObject j = new JSONObject(res);

            int _server = j.getInt("server");
            String _photo = j.getString("photo");
            String _hash = j.getString("hash");

            String params = "server=" + _server + "&photo=" + _photo + "&hash=" + _hash;

            res = VK.query("photos.saveMessagesPhoto", params);

            j = new JSONObject(res);
            JSONArray arr = j.getJSONArray("response");
            JSONObject g = arr.getJSONObject(0);
            id = g.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public static String getUserName(int uid) {
        if (Cache.userNames.get(uid) != null) {
            return Cache.userNames.get(uid);
        }

        String data = query("users.get", "user_id=" + uid);
        String full_name;

        try {
            JSONObject obj = new JSONObject(data);
            JSONArray response = obj.getJSONArray("response");
            JSONObject _data = response.getJSONObject(0);
            String first_name = _data.getString("first_name");
            String last_name = _data.getString("last_name");

            full_name = first_name + " " + last_name;

            Cache.userNames.put(uid, full_name);

        } catch (JSONException e) {
            e.printStackTrace();
            full_name = "";
        }

        return full_name;
    }

    public static String getMe () {
        if (Cache.userNames.get(0) != null) {
            return Cache.userNames.get(0);
        }

        String data = query("users.get", "user_id=0");
        String full_name;

        try {
            JSONObject obj = new JSONObject(data);
            JSONArray response = obj.getJSONArray("response");
            full_name = setMe(response.getJSONObject(0));
        } catch (JSONException e) {
            full_name = "Undefined Undefined";
        }

        return full_name;
    }

    public static String setMe(JSONObject user) throws JSONException {
        String full_name = user.getString("first_name") + " " + user.getString("last_name");
        Cache.userNames.put(0, full_name);
        return full_name;
    }

    public static String getSender(int from_id) {
        String to;
        if (from_id > 2000000000) {
            from_id = from_id - 2000000000;
            to = "chat_id=" + from_id;
        } else {
            to = "user_id=" + from_id;
        }

        return to;
    }
}