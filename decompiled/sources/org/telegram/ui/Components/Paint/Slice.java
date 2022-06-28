package org.telegram.ui.Components.Paint;

import android.graphics.RectF;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
/* loaded from: classes5.dex */
public class Slice {
    private RectF bounds;
    private File file;

    public Slice(ByteBuffer data, RectF rect, DispatchQueue queue) {
        this.bounds = rect;
        try {
            File outputDir = ApplicationLoader.applicationContext.getCacheDir();
            this.file = File.createTempFile("paint", ".bin", outputDir);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (this.file == null) {
            return;
        }
        storeData(data);
    }

    public void cleanResources() {
        File file = this.file;
        if (file != null) {
            file.delete();
            this.file = null;
        }
    }

    private void storeData(ByteBuffer data) {
        try {
            byte[] input = data.array();
            FileOutputStream fos = new FileOutputStream(this.file);
            Deflater deflater = new Deflater(1, true);
            deflater.setInput(input, data.arrayOffset(), data.remaining());
            deflater.finish();
            byte[] buf = new byte[1024];
            while (!deflater.finished()) {
                int byteCount = deflater.deflate(buf);
                fos.write(buf, 0, byteCount);
            }
            deflater.end();
            fos.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ByteBuffer getData() {
        try {
            byte[] input = new byte[1024];
            byte[] output = new byte[1024];
            FileInputStream fin = new FileInputStream(this.file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Inflater inflater = new Inflater(true);
            while (true) {
                int numRead = fin.read(input);
                if (numRead != -1) {
                    inflater.setInput(input, 0, numRead);
                }
                while (true) {
                    int numDecompressed = inflater.inflate(output, 0, output.length);
                    if (numDecompressed == 0) {
                        break;
                    }
                    bos.write(output, 0, numDecompressed);
                }
                if (!inflater.finished()) {
                    inflater.needsInput();
                } else {
                    inflater.end();
                    ByteBuffer result = ByteBuffer.wrap(bos.toByteArray(), 0, bos.size());
                    bos.close();
                    fin.close();
                    return result;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public int getX() {
        return (int) this.bounds.left;
    }

    public int getY() {
        return (int) this.bounds.top;
    }

    public int getWidth() {
        return (int) this.bounds.width();
    }

    public int getHeight() {
        return (int) this.bounds.height();
    }

    public RectF getBounds() {
        return new RectF(this.bounds);
    }
}
