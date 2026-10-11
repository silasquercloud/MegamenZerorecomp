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
    private android.view.SurfaceView vulkanSurface;
    private TouchOverlay controls;
    private final android.os.Handler metrics = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable refresh = new Runnable(){public void run(){
        if(fps==null)return;
        double[] s=VideoSettings.stats();
        syncVideo();
        int api=VideoSettings.activeBackend();String name=api==1?"Vulkan":api==-2?"SDL fallback":api==-1?"Inicializando":"SDL";
        fps.setText(s[0]>0?String.format(java.util.Locale.US,"%s • %.1f FPS / sim %.1f Hz\n%.1f ms • p95 %.1f / p99 %.1f\nCPU %.1f • vídeo %.1f ms • tela %.0f Hz",name,s[0],s[2],s[1],s[12],s[13],s[4],s[5],getWindowManager().getDefaultDisplay().getRefreshRate()):name+" • aguardando frame");
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
    private void syncVideo(){if(vulkanSurface!=null){boolean show=getSharedPreferences("launcher",0).getInt("graphics_api",0)!=2 && VideoSettings.activeBackend()!=-2;vulkanSurface.setVisibility(show?android.view.View.VISIBLE:android.view.View.GONE);}}
    private void installOverlay(){
        vulkanSurface=new android.view.SurfaceView(this);vulkanSurface.setZOrderMediaOverlay(true);
        mLayout.addView(vulkanSurface,new android.widget.RelativeLayout.LayoutParams(-1,-1));
        vulkanSurface.getHolder().addCallback(new android.view.SurfaceHolder.Callback(){
            public void surfaceCreated(android.view.SurfaceHolder h){VideoSettings.surface(h.getSurface());}
            public void surfaceChanged(android.view.SurfaceHolder h,int format,int w,int height){VideoSettings.surface(h.getSurface());}
            public void surfaceDestroyed(android.view.SurfaceHolder h){VideoSettings.surface(null);}
        });
        controls=new TouchOverlay(this);mLayout.addView(controls,new android.widget.RelativeLayout.LayoutParams(-1,-1));syncVideo();

        fps=new android.widget.TextView(this);fps.setTextSize(11);fps.setTextColor(0xFF66F2E8);fps.setBackgroundColor(0xBA080B10);fps.setPadding(8,4,8,4);fps.setClickable(false);mLayout.addView(fps);overlayPosition();
        android.widget.TextView settings=new android.widget.TextView(this);settings.setText("AJUSTES");settings.setTextColor(0xFFFFFFFF);settings.setTextSize(12);settings.setPadding(14,12,14,12);settings.setBackgroundColor(0xBACC1538);
        android.widget.RelativeLayout.LayoutParams lp=new android.widget.RelativeLayout.LayoutParams(-2,-2);lp.addRule(android.widget.RelativeLayout.CENTER_HORIZONTAL);lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);mLayout.addView(settings,lp);
        settings.setOnClickListener(v->{
            if(controls.isEditing()){controls.edit();settings.setText("AJUSTES");return;}
            controls.releaseAll();
            android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.setBackgroundColor(0xFF10151B);
            scroll.addView(VideoSettings.view(this,()->{VideoSettings.applySaved(this);overlayPosition();controls.reload();syncVideo();}));
            new AlertDialog.Builder(this).setTitle("Vídeo e controles • ao vivo").setView(scroll).setPositiveButton("Continuar",(d,w)->{}).setNeutralButton("Editar controles",(d,w)->{controls.edit();settings.setText("SALVAR POSIÇÕES");}).show();
        });
        metrics.removeCallbacks(refresh);metrics.post(refresh);
    }
    @Override protected void onResume(){super.onResume();if(fps!=null){VideoSettings.applySaved(this);metrics.removeCallbacks(refresh);metrics.post(refresh);}}
    @Override protected void onPause(){metrics.removeCallbacks(refresh);if(controls!=null)controls.releaseAll();super.onPause();}
    @Override protected void onDestroy(){metrics.removeCallbacks(refresh);if(controls!=null){controls.releaseAll();VideoSettings.surface(null);}super.onDestroy();}
    @Override protected String[] getArguments() {
        android.content.SharedPreferences p=getSharedPreferences("launcher",0);
        return new String[]{"--linear-filter",p.getBoolean("linear",false)?"1":"0","--volume",Integer.toString(p.getInt("volume",100))};
    }
}
