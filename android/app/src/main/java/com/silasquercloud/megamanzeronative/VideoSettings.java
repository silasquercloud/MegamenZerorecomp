package com.silasquercloud.megamanzeronative;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
/** UI requests are atomic; only the SDL thread touches renderer resources. */
public final class VideoSettings {
    public static native void apply(int cap,int scale,int aspect,boolean linear);
    public static native double[] stats();
    public static native void surface(android.view.Surface surface);
    public static native void backend(int api);
    public static native int activeBackend();
    public static native void effect(int effect);
    static SharedPreferences prefs(Context c){return c.getSharedPreferences("launcher",0);}
    static void applySaved(Context c){SharedPreferences p=prefs(c);apply(p.getInt("fps_cap",0),p.getInt("scale",0),p.getInt("aspect",0),p.getBoolean("linear",false));backend(p.getInt("graphics_api",0));effect(p.getInt("effect",0));}
    interface Changed {void run();}
    static LinearLayout view(Context c,Changed changed){
        LinearLayout box=new LinearLayout(c);box.setOrientation(1);box.setPadding(18,12,18,12);
        TextView note=new TextView(c);note.setText("Framebuffer original: 240 × 160. Escala = upscale real, sem novos detalhes; escalas altas podem reduzir FPS. Lógica/áudio: 59,73 Hz. 90/120/ilimitado ficam limitados aos frames originais (~60); 30 reduz apenas a apresentação. Razões alternativas distorcem a imagem; não expandem o mundo.");note.setTextColor(Color.LTGRAY);box.addView(note);
        choice(c,box,"API gráfica (efetiva no diagnóstico)","graphics_api",new String[]{"Automática","Vulkan","Anterior (SDL)"},new int[]{0,1,2},changed);
        choice(c,box,"Efeito de imagem • ao vivo","effect",new String[]{"Sem efeito","CRT • máscara RGB + scanlines","Scanlines","LCD • subpixels","Monocromático"},new int[]{0,1,2,3,4},changed);
        choice(c,box,"Limite de apresentação","fps_cap",new String[]{"Automático (~60)","30 FPS","60 FPS","90 FPS (efetivo ~60)","120 FPS (efetivo ~60)","Ilimitado (efetivo ~60)"},new int[]{0,30,60,90,120,-1},changed);
        choice(c,box,"Upscale","scale",new String[]{"Automático • econômico (1×)","1× • 240×160","2× • 480×320","3× • 720×480","4× • 960×640","5× • 1200×800","6× • 1440×960","7× • 1680×1120","8× • 1920×1280"},new int[]{0,1,2,3,4,5,6,7,8},changed);
        choice(c,box,"Proporção / tela","aspect",new String[]{"Automático • espaço para controles","Original 3:2 • fullscreen","4:3 • distorção","16:9 • distorção","18:9 • distorção","20:9 • distorção","Preencher tela • distorção"},new int[]{0,1,2,3,4,5,6},changed);
        Switch filter=new Switch(c);filter.setText("Bilinear (desligado: Nearest Neighbor)");filter.setTextColor(Color.WHITE);filter.setChecked(prefs(c).getBoolean("linear",false));box.addView(filter);filter.setOnCheckedChangeListener((b,v)->{prefs(c).edit().putBoolean("linear",v).apply();changed.run();});
        Switch overlay=new Switch(c);overlay.setText("Overlay FPS / frametime / simulação");overlay.setTextColor(Color.WHITE);overlay.setChecked(prefs(c).getBoolean("fps_overlay",true));box.addView(overlay);overlay.setOnCheckedChangeListener((b,v)->{prefs(c).edit().putBoolean("fps_overlay",v).apply();changed.run();});
        choice(c,box,"Posição do overlay","fps_position",new String[]{"Superior esquerdo","Superior direito","Inferior esquerdo","Inferior direito"},new int[]{0,1,2,3},changed);
        TouchOverlay.options(c,box,changed);
        return box;
    }
    static void choice(Context c,LinearLayout box,String label,String key,String[] names,int[] values,Changed changed){
        TextView title=new TextView(c);title.setText(label);title.setTextColor(Color.WHITE);title.setPadding(0,14,0,2);box.addView(title);
        Spinner spinner=new Spinner(c);spinner.setBackgroundColor(0xFFCCD7DF);spinner.setAdapter(new ArrayAdapter<String>(c,android.R.layout.simple_spinner_dropdown_item,names));int index=0;for(int i=0;i<values.length;i++)if(values[i]==prefs(c).getInt(key,key.equals("pad_size")?100:key.equals("pad_opacity")?70:0))index=i;spinner.setSelection(index);box.addView(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> v){}public void onItemSelected(AdapterView<?> parent,View v,int position,long id){prefs(c).edit().putInt(key,values[position]).apply();changed.run();}});
    }
}
