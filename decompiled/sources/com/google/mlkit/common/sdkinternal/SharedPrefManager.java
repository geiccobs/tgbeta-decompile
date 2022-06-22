package com.google.mlkit.common.sdkinternal;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import java.util.UUID;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public class SharedPrefManager {
    public static final Component<?> COMPONENT = Component.builder(SharedPrefManager.class).add(Dependency.required(MlKitContext.class)).add(Dependency.required(Context.class)).factory(zzn.zza).build();
    private final Context zza;

    private SharedPrefManager(Context context) {
        this.zza = context;
    }

    public synchronized String getMlSdkInstanceId() {
        String string = zza().getString("ml_sdk_instance_id", null);
        if (string != null) {
            return string;
        }
        String uuid = UUID.randomUUID().toString();
        zza().edit().putString("ml_sdk_instance_id", uuid).apply();
        return uuid;
    }

    private final SharedPreferences zza() {
        return this.zza.getSharedPreferences("com.google.mlkit.internal", 0);
    }

    public static final /* synthetic */ SharedPrefManager zza(ComponentContainer componentContainer) {
        return new SharedPrefManager((Context) componentContainer.get(Context.class));
    }
}
