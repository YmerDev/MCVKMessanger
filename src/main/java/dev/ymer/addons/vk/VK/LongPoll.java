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

import dev.ymer.addons.vk.VK.Models.Message;
import main.java.stroum.HTTP.Requests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class LongPoll {

    public static boolean enabled = false;
    public static String key;
    public static String server;
    public static int ts;

    public static void getLongPollServer() {
        String url = VK.query("messages.getLongPollServer", "");
        try {
            JSONObject obj = new JSONObject(url);

            JSONObject response = obj.getJSONObject("response");

            key = response.getString("key");
            server = response.getString("server");
            ts = response.getInt("ts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getConnectUrl(int ts) {
        return "https://" + server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25&mode=2";
    }

    public static void cycle(String className) {
        getLongPollServer();

        while (LongPoll.enabled) { // so we can prevent getting new messages immediately.
            String lp = getConnectUrl(ts);

            String data = Requests.get(lp);

            try {
                JSONObject response = new JSONObject(data);
                int _ts = response.getInt("ts");
                JSONArray updates = response.getJSONArray("updates");

                if(updates.length() != 0) processEvent(updates, className);
                ts = _ts;
            } catch (JSONException ignored) {
            }
        }
    }


    private static void processEvent(JSONArray array, String className) {
        for (int i = 0; i <  array.length(); ++i) {
            try {
                JSONArray arrayItem = (JSONArray) array.get(i);
                int type = (Integer) arrayItem.get(0);
                int uid;

                /*
                0,$message_id,0 -- удаление сообщения с указанным local_id
                1,$message_id,$flags -- замена флагов сообщения (FLAGS:=$flags)
                2,$message_id,$mask[,$user_id] -- установка флагов сообщения (FLAGS|=$mask)
                3,$message_id,$mask[,$user_id] -- сброс флагов сообщения (FLAGS&=~$mask)
                4,$message_id,$flags,$from_id,$timestamp,$subject,$text,$attachments -- добавление нового сообщения
                8,-$user_id,0 -- друг $user_id стал онлайн
                9,-$user_id,$flags -- друг $user_id стал оффлайн ($flags равен 0, если пользователь покинул сайт (например, нажал выход) и 1, если оффлайн по таймауту (например, статус away))


                51,$chat_id,$self -- один из параметров (состав, тема) беседы $chat_id были изменены. $self - были ли изменения вызываны самим пользователем
                61,$user_id,$flags -- пользователь $user_id начал набирать текст в диалоге. событие должно приходить раз в ~5 секунд при постоянном наборе текста. $flags = 1
                62,$user_id,$chat_id -- пользователь $user_id начал набирать текст в беседе $chat_id.
                70,$user_id,$call_id -- пользователь $user_id совершил звонок имеющий идентификатор $call_id, дополнительную информацию о звонке можно получить используя метод voip.getCallInfo.
                */

                switch (type) {
                    case 8:
                        // uid = Math.abs(Integer.parseInt(arrayItem.get(1).toString()));
                        // System.out.println(VK.getUserName(uid) + " is online");
                        break;
                    case 9:
                        // uid = Math.abs(Integer.parseInt(arrayItem.get(1).toString()));
                        //System.out.println(VK.getUserName(uid) + " is offline");
                        break;
                    case 4:
                        Long message_id = Long.parseLong(arrayItem.get(1).toString());
                        int flags = Integer.parseInt(arrayItem.get(2).toString());
                        int from_id = Integer.parseInt(arrayItem.get(3).toString());
                        Long ts = Long.parseLong(arrayItem.get(4).toString());
                        String subject = arrayItem.get(5).toString();
                        String text = arrayItem.get(6).toString();
                        int from_uid = 0;
                        try {
                            JSONObject object = new JSONObject(arrayItem.get(7).toString());
                            from_uid = Integer.parseInt(object.getString("from"));
                        } catch (Exception ignored) {
                        }

                        try {
                            Class c = Class.forName("dev.ymer.addons.vk.VK.Handlers." + className); // Todo: Fix
                            Class[] paramTypes = new Class[] {
                                    Message.class
                            };
                            Object[] args = new Object[] {
                                    new Message(message_id, flags, from_id, ts, text, from_uid)
                            };
                            Method m = c.getMethod("handleMessage", paramTypes);
                            m.invoke(c, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            } catch (JSONException ignored) { }
        }
    }

    public static void setEnabled (String className) {
        LongPoll.enabled = true;
        cycle(className);
    }
}