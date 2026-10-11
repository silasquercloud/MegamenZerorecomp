package com.silasquercloud.megamanzeronative;
import android.content.Context;
import android.util.Log;
import java.io.*;
import java.nio.charset.StandardCharsets;
final class BootLog {
    static synchronized void event(Context c, String step, String detail, Throwable error) {
        String line = System.currentTimeMillis()+" "+step+" — "+detail+"\n"+(error == null ? "" : Log.getStackTraceString(error));
        Log.i("MMZ_BOOT", line);
        File file = new File(c.getFilesDir(), "launcher-boot.log");
        if (file.length()>262144) file.renameTo(new File(c.getFilesDir(), "launcher-boot.previous.log"));
        try(FileOutputStream out=new FileOutputStream(file,true)) { out.write(line.getBytes(StandardCharsets.UTF_8)); }
        catch(IOException e) { Log.e("MMZ_BOOT", "Cannot persist diagnostic", e); }
    }
}
