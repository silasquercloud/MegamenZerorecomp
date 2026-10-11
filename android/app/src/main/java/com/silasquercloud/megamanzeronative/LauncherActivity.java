package com.silasquercloud.megamanzeronative;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.SeekBar;
import android.view.Gravity;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.*;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** ROM-free Android front end; Play launches the shared SDL runtime activity. */
public final class LauncherActivity extends Activity {
    private static final String ROM_SHA = "193b14120119162518a73c70876f0b8bffdbd96e";
    private static final String BIOS_SHA = "300c20df6731a33952ded8c436f7f186d25d3492";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private LinearLayout body;
    private TextView status;
    private boolean busy;

    @Override public void onCreate(Bundle state) { super.onCreate(state); home(); if(getIntent().getBooleanExtra("logs",false)) logs(); }
    @Override public void onDestroy() { worker.shutdown(); super.onDestroy(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private void page(String title) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(12, 14, 18));
        body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(12), dp(14), dp(12), dp(16));
        scroll.addView(body);
        setContentView(scroll);
        scroll.setOnApplyWindowInsetsListener((v,i)->{v.setPadding(i.getSystemWindowInsetLeft(),i.getSystemWindowInsetTop(),i.getSystemWindowInsetRight(),i.getSystemWindowInsetBottom());return i;});
        text("MEGA MAN ZERO NATIVE", 13, Color.rgb(56, 217, 230));
        text(title, 30, Color.WHITE);
        status = text("", 14, Color.LTGRAY);
        if (!title.equals("Jogar")) button("Voltar", this::home);
    }
    private TextView text(String message, int size, int color) {
        TextView label = new TextView(this);
        label.setText(message); label.setTextSize(size); label.setTextColor(color);
        label.setPadding(0, dp(8), 0, dp(8));
        if (size >= 24) label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        body.addView(label); return label;
    }
    private void button(String title, Runnable action) {
        TextView button=new TextView(this);button.setText(title);button.setTextSize(16);button.setTextColor(Color.WHITE);button.setGravity(Gravity.CENTER);button.setBackground(new TechDrawable(false));
        LinearLayout.LayoutParams layout=new LinearLayout.LayoutParams(-1,dp(52));layout.topMargin=dp(8);body.addView(button,layout);
        button.setOnClickListener(v->{if(!busy)action.run();});button.setFocusable(true);
    }
    private ImageView image(int res){ImageView i=new ImageView(this);i.setImageResource(res);i.setScaleType(ImageView.ScaleType.FIT_CENTER);return i;}
    private TextView label(String value,int size,int color){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);t.setMaxLines(2);t.setTypeface(Typeface.create("sans-serif-condensed",Typeface.NORMAL));return t;}
    private View card(String title,String subtitle,int art,int icon,boolean red,boolean compact,Runnable action){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),dp(8),dp(10),dp(8));row.setBackground(new TechDrawable(red));
        if(art!=0){ImageView illustration=image(art);row.addView(illustration,new LinearLayout.LayoutParams(dp(red?113:93),-1));}
        ImageView glyph=image(icon);LinearLayout.LayoutParams il=new LinearLayout.LayoutParams(dp(compact?27:35),dp(compact?27:35));il.setMargins(dp(8),0,dp(12),0);row.addView(glyph,il);
        LinearLayout words=new LinearLayout(this);words.setOrientation(LinearLayout.VERTICAL);row.addView(words,new LinearLayout.LayoutParams(0,-2,1));
        TextView main=label(title,red?24:compact?12:16,Color.WHITE);main.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));words.addView(main);
        TextView sub=label(subtitle,compact?10:12,0xFFAFBAC3);sub.setPadding(0,dp(5),0,0);words.addView(sub);
        TextView arrow=label("›",red?38:27,red?Color.WHITE:0xFF9DAFBA);row.addView(arrow,new LinearLayout.LayoutParams(dp(15),-2));
        row.setContentDescription(title+". "+subtitle);row.setFocusable(true);row.setClickable(true);row.setOnClickListener(v->{if(!busy){v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);action.run();}});
        return row;
    }
    private void panel(View v,int height){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(height));p.topMargin=dp(8);body.addView(v,p);}
    private void pair(View left,View right){LinearLayout row=new LinearLayout(this);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.rightMargin=dp(4);row.addView(left,p);LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(0,-1,1);q.leftMargin=dp(4);row.addView(right,q);panel(row,62);}
    private File asset(boolean bios) { return new File(getFilesDir(), bios ? "bios/gba_bios.bin" : "roms/megaman_zero_usa.gba"); }
    private void home() {
        page("Jogar");body.removeAllViews();
        FrameLayout hero=new FrameLayout(this);ImageView scene=image(com.silasquercloud.megamanzeronative.R.drawable.hero_scene);scene.setScaleType(ImageView.ScaleType.CENTER_CROP);android.graphics.ColorMatrix cm=new android.graphics.ColorMatrix();cm.setSaturation(0);scene.setColorFilter(new android.graphics.ColorMatrixColorFilter(cm));scene.setAlpha(.55f);hero.addView(scene,new FrameLayout.LayoutParams(-1,-1));
        ImageView character=image(R.drawable.zero_character);FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(-1,-1,Gravity.RIGHT|Gravity.TOP);hero.addView(character,cp);
        ImageView saber=image(R.drawable.saber_glow);FrameLayout.LayoutParams sp=new FrameLayout.LayoutParams(-1,dp(92),Gravity.BOTTOM);hero.addView(saber,sp);
        ImageView logo=image(R.drawable.mmz_logo);FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(240),dp(114),Gravity.LEFT|Gravity.BOTTOM);lp.bottomMargin=dp(55);hero.addView(logo,lp);
        TextView badge=label("v0.2.0-test.1",11,0xFFADB7C1);badge.setPadding(dp(8),dp(4),dp(8),dp(4));badge.setBackgroundColor(0xCD090B10);hero.addView(badge,new FrameLayout.LayoutParams(-2,dp(26),Gravity.LEFT|Gravity.TOP));
        ImageView gear=image(R.drawable.icon_gear);gear.setPadding(dp(9),dp(9),dp(9),dp(9));gear.setBackground(new TechDrawable(false));FrameLayout.LayoutParams gp=new FrameLayout.LayoutParams(dp(43),dp(43),Gravity.RIGHT|Gravity.TOP);hero.addView(gear,gp);gear.setContentDescription("Configurações");gear.setOnClickListener(v->graphics());
        TextView meta=label("RECOMPILED • ANDROID • ARM64",10,0xFFABBAC2);meta.setPadding(dp(8),dp(3),dp(8),dp(3));meta.setBackground(new TechDrawable(false));FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(dp(227),dp(24),Gravity.LEFT|Gravity.BOTTOM);mp.bottomMargin=dp(29);hero.addView(meta,mp);
        panel(hero,Math.round((getResources().getDisplayMetrics().widthPixels/getResources().getDisplayMetrics().density-24)*.75f));
        status=label("",12,0xFF25DCEA);body.addView(status,new LinearLayout.LayoutParams(-1,-2));
        panel(card("JOGAR","Iniciar o jogo",R.drawable.zero_character,R.drawable.icon_gamepad,true,false,this::play),92);
        panel(card("IMPORTAR ROM",asset(false).isFile()?"Arquivo importado • verificar ao jogar":"Selecione o arquivo da ROM",R.drawable.art_cartridge,R.drawable.icon_chip,false,false,()->importPage(false)),64);
        panel(card("IMPORTAR BIOS",asset(true).isFile()?"Arquivo importado • verificar ao jogar":"Selecione o arquivo da BIOS",R.drawable.art_bios,R.drawable.icon_chip,false,false,()->importPage(true)),64);
        panel(card("GUIA DE INSTALAÇÃO","Passo a passo em português",R.drawable.art_guide,R.drawable.icon_book,false,false,()->info("Guia de instalação","1. Instale o APK ARM64.\n2. Importe sua ROM USA revisão 0 e BIOS original. Os hashes são verificados.\n3. Toque JOGAR. Se o boot falhar, consulte ou exporte os logs.\n4. No gameplay use touch ou gamepad. O menu do runtime permite editar os controles.\nSalvamentos ficam privados; desinstalar remove os dados.")),64);
        pair(card("CONFIGURAÇÕES GRÁFICAS","Resolução, filtros e áudio",0,R.drawable.icon_gear,false,true,this::graphics),card("CONTROLES","Touch, gamepad e layout",0,R.drawable.icon_gamepad,false,true,this::controls));
        pair(card("DESEMPENHO","Métricas reais e dispositivo",0,R.drawable.icon_gauge,false,true,this::performance),card("LOGS E DIAGNÓSTICO","Ver registros e exportar",0,R.drawable.icon_logs,false,true,this::logs));
        panel(card("CRÉDITOS E VERSÃO","Informações, licenças e equipe",0,R.drawable.icon_trophy,false,false,this::credits),62);
        LinearLayout footer=new LinearLayout(this);footer.setPadding(dp(8),dp(12),dp(8),dp(12));footer.setBackground(new TechDrawable(false));
        String[] ft={"PLATAFORMA\nAndroid 9+\nARM64","RESOLUÇÃO\n240 × 160\n(nativo)","RUNTIME\ngbarecomp\nrecompilado","VERSÃO\n0.2.0-test.1\nexperimental"};
        for(String value:ft){TextView t=label(value,10,0xFFBAC8D0);t.setMaxLines(4);t.setGravity(Gravity.CENTER);footer.addView(t,new LinearLayout.LayoutParams(0,-1,1));}panel(footer,76);
    }
    private android.content.SharedPreferences prefs(){return getSharedPreferences("launcher",0);}
    private void toggle(String title,boolean checked,java.util.function.Consumer<Boolean> action){Switch sw=new Switch(this);sw.setText(title);sw.setTextColor(Color.WHITE);sw.setChecked(checked);sw.setPadding(0,dp(10),0,dp(10));body.addView(sw);sw.setOnCheckedChangeListener((b,v)->action.accept(v));}
    private void graphics(){page("Configurações gráficas");text("240 × 160 nativo • aplicado no próximo boot",16,Color.LTGRAY);toggle("Filtro linear (desligado: pixels nítidos)",prefs().getBoolean("linear",false),v->prefs().edit().putBoolean("linear",v).apply());text("Volume",17,Color.WHITE);SeekBar volume=new SeekBar(this);volume.setMax(100);volume.setProgress(prefs().getInt("volume",100));body.addView(volume);volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int v,boolean user){if(user)prefs().edit().putInt("volume",v).apply();}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});}
    private void controls(){page("Controles");text("D-Pad, A/B, L/R, Start e Select multitouch. Gamepads Bluetooth/USB são encaminhados pelo SDL. O editor de layout fica no menu do runtime durante o jogo.",16,Color.LTGRAY);toggle("Mostrar controles touch no próximo boot",prefs().getBoolean("touch",true),v->{try{writeTouch(v);prefs().edit().putBoolean("touch",v).apply();}catch(IOException e){failure(e);}});button("Restaurar controles touch",()->{try{writeTouch(true);prefs().edit().putBoolean("touch",true).apply();controls();}catch(IOException e){failure(e);}});}
    private void writeTouch(boolean visible)throws IOException {
        File f=new File(getFilesDir(),"config.ini");String old=f.isFile()?new String(java.nio.file.Files.readAllBytes(f.toPath()),java.nio.charset.StandardCharsets.UTF_8):"";
        StringBuilder out=new StringBuilder(),touch=new StringBuilder();boolean section=false;for(String line:old.split("\n")){if(line.trim().startsWith("["))section=line.trim().equalsIgnoreCase("[Touch]");if(!section)out.append(line).append('\n');else if(!line.trim().startsWith("[") && !line.trim().startsWith("pad_visible"))touch.append(line).append('\n');}
        out.append("\n[Touch]\npad_visible = ").append(visible?1:0).append("\n").append(touch);java.nio.file.Files.write(f.toPath(),out.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    private void performance(){page("Desempenho");android.app.ActivityManager.MemoryInfo m=new android.app.ActivityManager.MemoryInfo();((android.app.ActivityManager)getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(m);text("Android "+Build.VERSION.RELEASE+" • "+Build.MODEL+"\nABI: "+String.join(", ",Build.SUPPORTED_ABIS)+"\nRAM disponível: "+m.availMem/1048576+" MiB\nRuntime estático gbarecomp • lógica GBA 59,73 Hz.\nFPS e áudio precisam de sessão real; nenhuma medição é estimada.",16,Color.LTGRAY);text(readLogs(),12,Color.LTGRAY);}
    private void info(String title, String message) { page(title); text(message, 17, Color.LTGRAY); }
    private void credits() {
        String version;
        try { version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
        catch (android.content.pm.PackageManager.NameNotFoundException error) { version = "indisponível"; }
        info("Créditos e versão", "Mega Man Zero Native " + version + "\nMegaManZeroRecomp, gbarecomp e recomp-ui: Matthew Stanley e colaboradores. Licença PolyForm Noncommercial 1.0.0. SDL: licença zlib. Mega Man Zero e os arquivos originais pertencem aos respectivos titulares. Este APK não contém ROM ou BIOS.");
        for (String component : new String[]{"MegaManZeroRecomp", "gbarecomp", "recomp-ui", "SDL"}) {
            button("Licença: " + component, () -> {
                String filename = component.equals("SDL") ? "LICENSE.txt" : "LICENSE";
                try (InputStream input = getAssets().open("payload/licenses/" + component + "/" + filename)) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192]; int read;
                    while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
                    info("Licença: " + component, output.toString("UTF-8"));
                } catch (IOException error) { info("Licença: " + component, "Não foi possível ler o texto da licença."); }
            });
        }
    }
    private void importPage(boolean bios) {
        page(bios ? "Importar BIOS" : "Importar ROM");
        text(bios ? "BIOS GBA original: 16.384 bytes. SHA-1 " + BIOS_SHA : "Mega Man Zero USA revisão 0: 8.388.608 bytes. SHA-1 " + ROM_SHA, 16, Color.LTGRAY);
        button("Selecionar arquivo", () -> {
            Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            picker.addCategory(Intent.CATEGORY_OPENABLE); picker.setType("*/*");
            startActivityForResult(picker, bios ? 2 : 1);
        });
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if(request==3 && result==RESULT_OK && data!=null){try(OutputStream o=getContentResolver().openOutputStream(data.getData())){o.write(readLogs().getBytes(java.nio.charset.StandardCharsets.UTF_8));}catch(Exception e){failure(e);}return;}
        if(request==101){checkBootReturn();return;}
        if ((request != 1 && request != 2) || result != RESULT_OK || data == null || data.getData() == null) return;
        boolean bios = request == 2;
        Uri uri = data.getData();
        runWork("Verificando arquivo…", () -> {
            File target = asset(bios), parent = target.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Não foi possível criar o armazenamento privado.");
            File temporary = File.createTempFile("import-", ".tmp", parent);
            try {
                long limit = bios ? 16384 : 8388608, count = 0;
                try (InputStream input = getContentResolver().openInputStream(uri); OutputStream output = new FileOutputStream(temporary)) {
                    if (input == null) throw new IOException("Não foi possível abrir o arquivo selecionado.");
                    byte[] buffer = new byte[65536]; int read;
                    while ((read = input.read(buffer)) != -1) {
                        count += read;
                        if (count > limit) throw new IOException("Arquivo maior que o esperado para " + (bios ? "BIOS." : "ROM."));
                        output.write(buffer, 0, read);
                    }
                }
                validate(temporary, bios);
                java.nio.file.Files.move(temporary.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return "Arquivo verificado e importado com sucesso.";
            } finally { temporary.delete(); }
        }, false);
    }
    private void validate(File file, boolean bios) throws Exception {
        if (!file.isFile()) throw new IOException(bios ? "Importe a BIOS GBA original antes de jogar." : "Importe a ROM antes de jogar.");
        if (file.length() != (bios ? 16384 : 8388608)) throw new IOException("Tamanho incorreto para " + (bios ? "BIOS." : "ROM."));
        MessageDigest hash = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {
            byte[] bytes = new byte[65536]; int read;
            while ((read = input.read(bytes)) != -1) hash.update(bytes, 0, read);
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : hash.digest()) hex.append(String.format(Locale.ROOT, "%02x", b & 255));
        if (!hex.toString().equals(bios ? BIOS_SHA : ROM_SHA)) throw new IOException(bios ? "BIOS incompatível. Use o dump GBA original esperado." : "ROM incompatível. Use Mega Man Zero USA revisão 0, sem modificações.");
    }
    private void play() { BootLog.event(this,"MMZ_BOOT_01","Botão Jogar acionado",null);runWork("Verificando ROM e BIOS…", () -> { validate(asset(false), false);BootLog.event(this,"MMZ_BOOT_02",asset(false).getAbsolutePath()+" SHA-1 validado",null); validate(asset(true), true);BootLog.event(this,"MMZ_BOOT_03",asset(true).getAbsolutePath()+" SHA-1 validado",null); return "Iniciando runtime nativo…"; }, true); }
    private interface Task { String run() throws Exception; }
    private void runWork(String message, Task task, boolean launch) {
        busy = true; status.setText(message);
        worker.execute(() -> {
            try {
                String result = task.run();
                runOnUiThread(() -> {
                    busy = false;
                    if (isFinishing() || isDestroyed()) return;
                    status.setText(result);
                    if (launch) {try {new File(getFilesDir(),"first-frame.txt").delete();new File(getFilesDir(),"boot-state.txt").delete();prefs().edit().putBoolean("bootPending",true).commit();startActivityForResult(new Intent(this, GameActivity.class),101);}catch(Throwable e){prefs().edit().putBoolean("bootPending",false).apply();failure(e);}}
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    busy = false;
                    if (isFinishing() || isDestroyed()) return;
                    status.setText("Operação não concluída.");
                    failure(error);
                });
            }
        });
    }
    private void failure(Throwable e){BootLog.event(this,"MMZ_BOOT_FAIL",e.toString(),e);new AlertDialog.Builder(this).setTitle("Não foi possível iniciar ou concluir").setMessage(e.getMessage()==null?e.toString():e.getMessage()).setPositiveButton("Ver logs",(d,w)->logs()).setNegativeButton("Fechar",null).show();}
    private void checkBootReturn(){if(!prefs().getBoolean("bootPending",false))return;prefs().edit().putBoolean("bootPending",false).apply();String log=readLogs();if(!new File(getFilesDir(),"first-frame.txt").isFile() || log.contains("MMZ_BOOT_RETURN code=1") || log.contains("MMZ_BOOT_FAIL native signal")){failure(new IOException("O runtime encerrou antes de confirmar uma sessão. Consulte os logs de boot para identificar a última etapa."));}}
    @Override protected void onResume(){super.onResume();if(prefs().getBoolean("bootPending",false)){getWindow().getDecorView().post(()->checkBootReturn());}}
    private String readLogs(){StringBuilder all=new StringBuilder();for(String name:new String[]{"launcher-boot.log","android-runtime.log","boot-state.txt"}){File log=new File(getFilesDir(),name);all.append("\n— ").append(name).append(" —\n");if(!log.isFile()){all.append("Ainda sem registro.\n");continue;}try(RandomAccessFile in=new RandomAccessFile(log,"r")){in.seek(Math.max(0,in.length()-65536));byte[] bytes=new byte[(int)(in.length()-in.getFilePointer())];in.readFully(bytes);all.append(new String(bytes,java.nio.charset.StandardCharsets.UTF_8));}catch(IOException e){all.append(e.toString());}}return all.toString();}
    private void logs(){page("Logs e diagnóstico");button("Exportar logs",()->{Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/plain");i.putExtra(Intent.EXTRA_TITLE,"mmz-boot-0.2.0.txt");startActivityForResult(i,3);});TextView v=text(readLogs(),12,Color.LTGRAY);v.setTypeface(Typeface.MONOSPACE);v.setTextIsSelectable(true);}
}
