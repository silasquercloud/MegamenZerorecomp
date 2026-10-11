package com.silasquercloud.megamanzeronative;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import org.gbarecomp.GbaGameActivity;
import org.libsdl.app.SDLActivity;
public final class GameActivity extends GbaGameActivity {
    @Override public void loadLibraries() {
        try { super.loadLibraries(); VideoSettings.applySaved(this); BootLog.event(this,"MMZ_BOOT_04","SDL2 e main carregadas",null); }
        catch(Throwable e) { BootLog.event(this,"MMZ_BOOT_FAIL_04","Falha no carregamento nativo",e); throw e; }
    }
    @Override protected void onCreate(Bundle state) {
        try {
            super.onCreate(state);
            if (!SDLActivity.mBrokenLibraries) { BootLog.event(this,"MMZ_BOOT_05","SDL.setupJNI concluído",null); installOverlay(); }
        } catch(Throwable e) {
            BootLog.event(this,"MMZ_BOOT_FAIL_ACTIVITY","Activity de gameplay não iniciou",e);
            new AlertDialog.Builder(this).setTitle("Falha ao iniciar o jogo").setMessage(e.toString())
                .setPositiveButton("Ver logs",(d,w)->{startActivity(new Intent(this,LauncherActivity.class).putExtra("logs",true));finish();})
                .setNegativeButton("Voltar",(d,w)->finish()).show();
        }
    }
    private android.widget.TextView fps;
    private final android.os.Handler metrics = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable refresh = new Runnable(){public void run(){
        if(fps==null)return;
        double[] s=VideoSettings.stats();
        fps.setText(s[0]>0?String.format(java.util.Locale.US,"%.1f FPS • %.2f ms\nSim: %.1f Hz • pico %.1f ms\nCPU %.1f ms • vídeo %.1f ms\nUpscale %.0f×%.0f → %.0f×%.0f",s[0],s[1],s[2],s[3],s[4],s[5],s[8],s[9],s[10],s[11]):"Aguardando apresentação real…");
        metrics.postDelayed(this,500);
    }};
    private void overlayPosition(){
        android.content.SharedPreferences p=getSharedPreferences("launcher",0);
        fps.setVisibility(p.getBoolean("fps_overlay",true)?android.view.View.VISIBLE:android.view.View.GONE);
        int pos=p.getInt("fps_position",0);
        android.widget.RelativeLayout.LayoutParams lp=new android.widget.RelativeLayout.LayoutParams(-2,-2);
        lp.addRule(pos<2?android.widget.RelativeLayout.ALIGN_PARENT_TOP:android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(pos%2==0?android.widget.RelativeLayout.ALIGN_PARENT_LEFT:android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(12,12,12,12);fps.setLayoutParams(lp);
    }
    private void installOverlay(){
        fps=new android.widget.TextView(this);fps.setTextSize(11);fps.setTextColor(0xFF66F2E8);fps.setBackgroundColor(0xBA080B10);fps.setPadding(8,4,8,4);fps.setClickable(false);mLayout.addView(fps);overlayPosition();
        android.widget.TextView settings=new android.widget.TextView(this);settings.setText("VÍDEO");settings.setTextColor(0xFFFFFFFF);settings.setTextSize(12);settings.setPadding(14,12,14,12);settings.setBackgroundColor(0xBACC1538);
        android.widget.RelativeLayout.LayoutParams lp=new android.widget.RelativeLayout.LayoutParams(-2,-2);lp.addRule(android.widget.RelativeLayout.CENTER_HORIZONTAL);lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);mLayout.addView(settings,lp);
        settings.setOnClickListener(v->{android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.setBackgroundColor(0xFF10151B);scroll.addView(VideoSettings.view(this,()->{VideoSettings.applySaved(this);overlayPosition();}));new AlertDialog.Builder(this).setTitle("Vídeo e desempenho • ao vivo").setView(scroll).setPositiveButton("Continuar",(d,w)->{}).show();});
        metrics.removeCallbacks(refresh);metrics.post(refresh);
    }
    @Override protected void onResume(){super.onResume();if(fps!=null){VideoSettings.applySaved(this);metrics.removeCallbacks(refresh);metrics.post(refresh);}}
    @Override protected void onPause(){metrics.removeCallbacks(refresh);super.onPause();}
    @Override protected void onDestroy(){metrics.removeCallbacks(refresh);super.onDestroy();}
    @Override protected String[] getArguments() {
        android.content.SharedPreferences p=getSharedPreferences("launcher",0);
        return new String[]{"--linear-filter",p.getBoolean("linear",false)?"1":"0","--volume",Integer.toString(p.getInt("volume",100))};
    }
}
