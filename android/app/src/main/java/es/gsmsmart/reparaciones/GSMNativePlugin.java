package es.gsmsmart.reparaciones;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.content.pm.PackageManager;
import androidx.core.content.FileProvider;
import android.webkit.WebView;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Funciones nativas que el WebView no ofrece: imprimir, guardar archivos y abrir enlaces externos. */
@CapacitorPlugin(name = "GSMNative")
public class GSMNativePlugin extends Plugin {

    @PluginMethod
    public void print(final PluginCall call) {
        final String title = call.getString("title", "GSMsmart");
        getActivity().runOnUiThread(() -> {
            try {
                WebView wv = getBridge().getWebView();
                PrintManager pm = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter adapter = wv.createPrintDocumentAdapter(title);
                PrintAttributes attrs = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .build();
                pm.print(title, adapter, attrs);
                call.resolve();
            } catch (Exception e) {
                call.reject(e.getMessage());
            }
        });
    }

    @PluginMethod
    public void saveFile(PluginCall call) {
        String name = call.getString("name", "gsmsmart.json");
        String content = call.getString("content", "");
        String mime = call.getString("mime", "application/json");
        byte[] data = call.getBoolean("base64", false)
                ? Base64.decode(content, Base64.DEFAULT)
                : content.getBytes(StandardCharsets.UTF_8);
        try {
            JSObject ret = new JSObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/GSMsmart");
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new Exception("No se ha podido crear el archivo");
                try (OutputStream os = resolver.openOutputStream(uri)) { os.write(data); }
                ret.put("path", "Descargas/GSMsmart/" + name);
            } else {
                File dir = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir != null && !dir.exists()) dir.mkdirs();
                File f = new File(dir, name);
                try (FileOutputStream os = new FileOutputStream(f)) { os.write(data); }
                ret.put("path", f.getAbsolutePath());
            }
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    /** Envía un PDF por WhatsApp (o WhatsApp Business) abriendo, si se puede, el chat del cliente. */
    @PluginMethod
    public void sharePdf(PluginCall call) {
        String name = call.getString("name", "ficha.pdf");
        String content = call.getString("content", "");
        String phone = call.getString("phone", "");
        String text = call.getString("text", "");
        try {
            File dir = new File(getContext().getCacheDir(), "compartir");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, name);
            try (FileOutputStream os = new FileOutputStream(f)) { os.write(Base64.decode(content, Base64.DEFAULT)); }
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);

            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("application/pdf");
            send.putExtra(Intent.EXTRA_STREAM, uri);
            if (!text.isEmpty()) send.putExtra(Intent.EXTRA_TEXT, text);
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            String pkg = installed("com.whatsapp.w4b") ? "com.whatsapp.w4b" : installed("com.whatsapp") ? "com.whatsapp" : null;
            Intent toStart;
            if (pkg != null) {
                send.setPackage(pkg);
                if (phone != null && phone.length() >= 9) send.putExtra("jid", phone + "@s.whatsapp.net");
                toStart = send;
            } else {
                toStart = Intent.createChooser(send, "Enviar ficha");
            }
            toStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(toStart);
            JSObject ret = new JSObject();
            ret.put("app", pkg == null ? "otra" : pkg);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    private boolean installed(String pkg) {
        try { getContext().getPackageManager().getPackageInfo(pkg, 0); return true; }
        catch (PackageManager.NameNotFoundException e) { return false; }
    }

    @PluginMethod
    public void openUrl(PluginCall call) {
        String url = call.getString("url");
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }
}
