//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package main.java.stroum.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    public MultipartUtility(String var1, String var2) throws IOException {
        this.charset = var2;
        this.boundary = "===" + System.currentTimeMillis() + "===";
        URL var3 = new URL(var1);
        this.httpConn = (HttpURLConnection)var3.openConnection();
        this.httpConn.setUseCaches(false);
        this.httpConn.setDoOutput(true);
        this.httpConn.setDoInput(true);
        this.httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
        this.httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        this.httpConn.setRequestProperty("Test", "Bonjour");
        this.outputStream = this.httpConn.getOutputStream();
        this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, var2), true);
    }

    public void addFormField(String var1, String var2) {
        this.writer.append("--" + this.boundary).append("\r\n");
        this.writer.append("Content-Disposition: form-data; name=\"" + var1 + "\"").append("\r\n");
        this.writer.append("Content-Type: text/plain; charset=" + this.charset).append("\r\n");
        this.writer.append("\r\n");
        this.writer.append(var2).append("\r\n");
        this.writer.flush();
    }

    public void addFilePart(String var1, File var2) throws IOException {
        String var3 = var2.getName();
        this.writer.append("--" + this.boundary).append("\r\n");
        this.writer.append("Content-Disposition: form-data; name=\"" + var1 + "\"; filename=\"" + var3 + "\"").append("\r\n");
        this.writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(var3)).append("\r\n");
        this.writer.append("Content-Transfer-Encoding: binary").append("\r\n");
        this.writer.append("\r\n");
        this.writer.flush();
        FileInputStream var4 = new FileInputStream(var2);
        byte[] var5 = new byte[4096];
        boolean var6 = true;

        int var7;
        while((var7 = var4.read(var5)) != -1) {
            this.outputStream.write(var5, 0, var7);
        }

        this.outputStream.flush();
        var4.close();
        this.writer.append("\r\n");
        this.writer.flush();
    }

    public void addHeaderField(String var1, String var2) {
        this.writer.append(var1 + ": " + var2).append("\r\n");
        this.writer.flush();
    }

    public List<String> finish() throws IOException {
        ArrayList var1 = new ArrayList();
        this.writer.append("\r\n").flush();
        this.writer.append("--" + this.boundary + "--").append("\r\n");
        this.writer.close();
        int var2 = this.httpConn.getResponseCode();
        if (var2 != 200) {
            throw new IOException("Server returned non-OK status: " + var2);
        } else {
            BufferedReader var3 = new BufferedReader(new InputStreamReader(this.httpConn.getInputStream()));
            String var4 = null;

            while((var4 = var3.readLine()) != null) {
                var1.add(var4);
            }

            var3.close();
            this.httpConn.disconnect();
            return var1;
        }
    }
}
