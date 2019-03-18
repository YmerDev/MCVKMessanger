//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package main.java.stroum.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

public class Requests {
    public Requests() {
    }

    public static void main(String[] var0) {
    }

    public static String post_upload(String var0, File var1) {
        String var2 = "";

        try {
            MultipartUtility var3 = new MultipartUtility(var0, "utf-8");
            var3.addFilePart("file", var1);
            List var4 = var3.finish();

            String var6;
            for(Iterator var5 = var4.iterator(); var5.hasNext(); var2 = var2 + var6) {
                var6 = (String)var5.next();
            }
        } catch (IOException var7) {
            var2 = "upload error";
            var7.printStackTrace();
        }

        return var2;
    }

    public static String post(String var0, String var1) {
        try {
            URL var2 = new URL(var0);
            URLConnection var3 = var2.openConnection();
            var3.setDoOutput(true);
            OutputStreamWriter var4 = new OutputStreamWriter(var3.getOutputStream());
            var4.write(var1);
            var4.flush();
            String var6 = "";

            String var5;
            BufferedReader var7;
            for(var7 = new BufferedReader(new InputStreamReader(var3.getInputStream())); (var5 = var7.readLine()) != null; var6 = var6 + var5) {
            }

            var4.close();
            var7.close();
            return var6;
        } catch (Exception var8) {
            var8.printStackTrace();
            return "fail";
        }
    }

    public static String get(String var0) {
        try {
            URL var1 = new URL(var0);
            HttpURLConnection var2 = (HttpURLConnection)var1.openConnection();
            var2.setDoOutput(false);
            var2.setConnectTimeout(60000);
            var2.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            var2.setRequestProperty("Accept", "*/*");
            int var3 = var2.getResponseCode();
            InputStream var4;
            if (var3 >= 400) {
                var4 = var2.getErrorStream();
            } else {
                var4 = var2.getInputStream();
            }

            String var6 = "";

            String var5;
            BufferedReader var7;
            for(var7 = new BufferedReader(new InputStreamReader(var4)); (var5 = var7.readLine()) != null; var6 = var6 + var5) {
            }

            var7.close();
            return var6;
        } catch (Exception var8) {
            var8.printStackTrace();
            return "fail";
        }
    }
}
