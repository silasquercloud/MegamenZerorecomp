package com.silasquercloud.megamanzeronative;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import org.gbarecomp.GbaGameActivity;
import org.libsdl.app.SDLActivity;
public final class GameActivity extends GbaGameActivity {
    @Override public void loadLibraries() {
        try { super.loadLibraries(); BootLog.event(this,"MMZ_BOOT_04","SDL2 e main carregadas",null); }
        catch(Throwable e) { BootLog.event(this,"MMZ_BOOT_FAIL_04","Falha no carregamento nativo",e); throw e; }
    }
    @Override protected void onCreate(Bundle state) {
        try {
            super.onCreate(state);
            if (!SDLActivity.mBrokenLibraries) BootLog.event(this,"MMZ_BOOT_05","SDL.setupJNI concluído",null);
        } catch(Throwable e) {
            BootLog.event(this,"MMZ_BOOT_FAIL_ACTIVITY","Activity de gameplay não iniciou",e);
            new AlertDialog.Builder(this).setTitle("Falha ao iniciar o jogo").setMessage(e.toString())
                .setPositiveButton("Ver logs",(d,w)->{startActivity(new Intent(this,LauncherActivity.class).putExtra("logs",true));finish();})
                .setNegativeButton("Voltar",(d,w)->finish()).show();
        }
    }
    @Override protected String[] getArguments() {
        android.content.SharedPreferences p=getSharedPreferences("launcher",0);
        return new String[]{"--linear-filter",p.getBoolean("linear",false)?"1":"0","--volume",Integer.toString(p.getInt("volume",100))};
    }
}
