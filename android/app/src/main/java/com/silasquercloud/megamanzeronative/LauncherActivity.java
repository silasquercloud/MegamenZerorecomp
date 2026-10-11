package com.silasquercloud.megamanzeronative;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.gbarecomp.GbaGameActivity;
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

    @Override public void onCreate(Bundle state) { super.onCreate(state); home(); }
    @Override public void onDestroy() { worker.shutdown(); super.onDestroy(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private void page(String title) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(12, 14, 18));
        body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(24), dp(24), dp(24), dp(24));
        scroll.addView(body);
        setContentView(scroll);
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
        Button button = new Button(this);
        button.setText(title); button.setAllCaps(false);
        button.setTextColor(Color.WHITE); button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(145, 25, 46)));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, dp(56));
        layout.topMargin = dp(8); body.addView(button, layout);
        button.setOnClickListener(v -> { if (!busy) action.run(); });
    }
    private File asset(boolean bios) { return new File(getFilesDir(), bios ? "bios/gba_bios.bin" : "roms/megaman_zero_usa.gba"); }
    private void home() {
        page("Jogar");
        text("ARM64 • 240 × 160 • runtime recompilado", 15, Color.LTGRAY);
        button("Jogar", this::play);
        button("Importar ROM", () -> importPage(false));
        button("Importar BIOS", () -> importPage(true));
        button("Guia de instalação", () -> info("Guia de instalação", "Instale o APK e permita a instalação por essa origem quando solicitado. Importe sua ROM Mega Man Zero USA revisão 0 e BIOS GBA. Os arquivos são verificados e ficam no armazenamento privado. Os salvamentos permanecem após fechar o aplicativo; desinstalar remove esses dados."));
        button("Configurações gráficas", () -> info("Configurações gráficas", "A configuração inicial mantém 240 × 160 e cores Raw. Durante o jogo, abra o menu do runtime para ajustar apresentação e cores. Nenhuma alteração de FPS é aplicada pelo launcher."));
        button("Controles", () -> info("Controles", "O runtime fornece D-Pad, A, B, L, R, Start e Select multitouch e suporte SDL a gamepads Bluetooth/USB. Use o editor de toque do runtime para ajustar o layout. A ação de cada botão depende dos controles definidos no jogo. Ícones dinâmicos de armas ainda não estão implementados."));
        button("Desempenho", () -> info("Desempenho", "A cadência é controlada pelo runtime GBA, sem aceleração de lógica. A compilação estática é o caminho principal; endereços não cobertos usam o fallback de interpretação do upstream e são registrados nos logs. Desempenho neste dispositivo requer teste de gameplay."));
        button("Logs e diagnóstico", this::logs);
        button("Créditos e versão", () -> info("Créditos e versão", "Mega Man Zero Native 0.1.0-dev\nMegaManZeroRecomp, gbarecomp e recomp-ui: Matthew Stanley e colaboradores. Licença PolyForm Noncommercial 1.0.0. SDL: licença zlib. Mega Man Zero e os arquivos originais pertencem aos respectivos titulares. Este APK não contém ROM ou BIOS."));
    }
    private void info(String title, String message) { page(title); text(message, 17, Color.LTGRAY); }
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
    private void play() { runWork("Verificando ROM e BIOS…", () -> { validate(asset(false), false); validate(asset(true), true); return "Iniciando runtime nativo…"; }, true); }
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
                    if (launch) startActivity(new Intent(this, GbaGameActivity.class));
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    busy = false;
                    if (isFinishing() || isDestroyed()) return;
                    status.setText("Operação não concluída.");
                    new AlertDialog.Builder(this).setTitle("Não foi possível continuar").setMessage(error.getMessage()).setPositiveButton("OK", null).show();
                });
            }
        });
    }
    private void logs() {
        page("Logs e diagnóstico");
        File log = new File(getFilesDir(), "android-runtime.log");
        if (!log.isFile()) { text("O runtime ainda não criou um log neste dispositivo.", 16, Color.LTGRAY); return; }
        try (RandomAccessFile input = new RandomAccessFile(log, "r")) {
            input.seek(Math.max(0, input.length() - 65536));
            byte[] bytes = new byte[(int)(input.length() - input.getFilePointer())]; input.readFully(bytes);
            TextView view = text(new String(bytes, java.nio.charset.StandardCharsets.UTF_8), 12, Color.LTGRAY);
            view.setTypeface(Typeface.MONOSPACE); view.setTextIsSelectable(true);
        } catch (IOException error) { text("Não foi possível ler os logs.", 16, Color.LTGRAY); }
    }
}
