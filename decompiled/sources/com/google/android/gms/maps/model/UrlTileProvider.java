package com.google.android.gms.maps.model;

import com.google.android.gms.common.internal.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public abstract class UrlTileProvider implements TileProvider {
    private final int zza;
    private final int zzb;

    public UrlTileProvider(int i, int i2) {
        this.zza = i;
        this.zzb = i2;
    }

    @Override // com.google.android.gms.maps.model.TileProvider
    public final Tile getTile(int x, int y, int zoom) {
        URL tileUrl = getTileUrl(x, y, zoom);
        if (tileUrl != null) {
            try {
                com.google.android.gms.internal.maps.zzf.zza(4352);
                int i = this.zza;
                int i2 = this.zzb;
                InputStream inputStream = tileUrl.openConnection().getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Preconditions.checkNotNull(inputStream, "from must not be null.");
                Preconditions.checkNotNull(byteArrayOutputStream, "to must not be null.");
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read != -1) {
                        byteArrayOutputStream.write(bArr, 0, read);
                    } else {
                        Tile tile = new Tile(i, i2, byteArrayOutputStream.toByteArray());
                        com.google.android.gms.internal.maps.zzf.zzb();
                        return tile;
                    }
                }
            } catch (IOException e) {
                com.google.android.gms.internal.maps.zzf.zzb();
                return null;
            } catch (Throwable th) {
                com.google.android.gms.internal.maps.zzf.zzb();
                throw th;
            }
        } else {
            return NO_TILE;
        }
    }

    public abstract URL getTileUrl(int i, int i2, int i3);
}
