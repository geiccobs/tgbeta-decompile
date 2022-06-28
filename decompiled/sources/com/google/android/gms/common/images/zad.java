package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.internal.base.zaf;
import com.google.android.gms.internal.base.zal;
import java.lang.ref.WeakReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zad extends zab {
    private WeakReference<ImageView> zac;

    public zad(ImageView imageView, Uri uri) {
        super(uri, 0);
        Asserts.checkNotNull(imageView);
        this.zac = new WeakReference<>(imageView);
    }

    public zad(ImageView imageView, int i) {
        super(Uri.EMPTY, i);
        Asserts.checkNotNull(imageView);
        this.zac = new WeakReference<>(imageView);
    }

    public final int hashCode() {
        return 0;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zad)) {
            return false;
        }
        ImageView imageView = this.zac.get();
        ImageView imageView2 = ((zad) obj).zac.get();
        return (imageView2 == null || imageView == null || !Objects.equal(imageView2, imageView)) ? false : true;
    }

    @Override // com.google.android.gms.common.images.zab
    protected final void zaa(Drawable drawable, boolean z, boolean z2, boolean z3) {
        ImageView imageView = this.zac.get();
        if (imageView != null) {
            int i = 0;
            boolean z4 = !z2 && !z3;
            if (z4 && (imageView instanceof zal)) {
                zal zalVar = (zal) imageView;
                int zaa = zal.zaa();
                if (this.zab != 0 && zaa == this.zab) {
                    return;
                }
            }
            boolean zaa2 = zaa(z, z2);
            if (zaa2) {
                Drawable drawable2 = imageView.getDrawable();
                if (drawable2 == null) {
                    drawable2 = null;
                } else if (drawable2 instanceof zaf) {
                    drawable2 = ((zaf) drawable2).zaa();
                }
                drawable = new zaf(drawable2, drawable);
            }
            imageView.setImageDrawable(drawable);
            if (imageView instanceof zal) {
                zal zalVar2 = (zal) imageView;
                zal.zaa(z3 ? this.zaa.zaa : Uri.EMPTY);
                if (z4) {
                    i = this.zab;
                }
                zal.zaa(i);
            }
            if (drawable != null && zaa2) {
                ((zaf) drawable).zaa(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
        }
    }
}
