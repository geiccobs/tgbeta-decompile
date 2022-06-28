package com.google.mlkit.common.sdkinternal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.common.model.RemoteModel;
import java.util.UUID;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class SharedPrefManager {
    public static final Component<?> COMPONENT = Component.builder(SharedPrefManager.class).add(Dependency.required(MlKitContext.class)).add(Dependency.required(Context.class)).factory(zzn.zza).build();
    public static final String PREF_FILE = "com.google.mlkit.internal";
    private final Context zza;

    private SharedPrefManager(Context context) {
        this.zza = context;
    }

    public static SharedPrefManager getInstance(MlKitContext mlKitContext) {
        return (SharedPrefManager) mlKitContext.get(SharedPrefManager.class);
    }

    public synchronized Long getDownloadingModelId(RemoteModel remoteModel) {
        long j = zza().getLong(String.format("downloading_model_id_%s", remoteModel.getUniqueModelNameForPersist()), -1L);
        if (j < 0) {
            return null;
        }
        return Long.valueOf(j);
    }

    public synchronized String getDownloadingModelHash(RemoteModel remoteModel) {
        return zza().getString(String.format("downloading_model_hash_%s", remoteModel.getUniqueModelNameForPersist()), null);
    }

    public synchronized String getLatestModelHash(RemoteModel remoteModel) {
        return zza().getString(String.format("current_model_hash_%s", remoteModel.getUniqueModelNameForPersist()), null);
    }

    public synchronized String getIncompatibleModelHash(RemoteModel remoteModel) {
        return zza().getString(String.format("bad_hash_%s", remoteModel.getUniqueModelNameForPersist()), null);
    }

    public synchronized String getPreviousAppVersion() {
        return zza().getString("app_version", null);
    }

    public synchronized long getModelDownloadBeginTimeMs(RemoteModel remoteModel) {
        return zza().getLong(String.format("downloading_begin_time_%s", remoteModel.getUniqueModelNameForPersist()), 0L);
    }

    public synchronized long getModelFirstUseTimeMs(RemoteModel remoteModel) {
        return zza().getLong(String.format("model_first_use_time_%s", remoteModel.getUniqueModelNameForPersist()), 0L);
    }

    public synchronized void setModelFirstUseTimeMs(RemoteModel remoteModel, long j) {
        zza().edit().putLong(String.format("model_first_use_time_%s", remoteModel.getUniqueModelNameForPersist()), j).apply();
    }

    public synchronized void clearDownloadingModelInfo(RemoteModel remoteModel) {
        zza().edit().remove(String.format("downloading_model_id_%s", remoteModel.getUniqueModelNameForPersist())).remove(String.format("downloading_model_hash_%s", remoteModel.getUniqueModelNameForPersist())).remove(String.format("downloading_model_type_%s", getDownloadingModelHash(remoteModel))).remove(String.format("downloading_begin_time_%s", remoteModel.getUniqueModelNameForPersist())).remove(String.format("model_first_use_time_%s", remoteModel.getUniqueModelNameForPersist())).apply();
    }

    public synchronized void clearLatestModelHash(RemoteModel remoteModel) {
        zza().edit().remove(String.format("current_model_hash_%s", remoteModel.getUniqueModelNameForPersist())).commit();
    }

    public synchronized void setLatestModelHash(RemoteModel remoteModel, String str) {
        zza().edit().putString(String.format("current_model_hash_%s", remoteModel.getUniqueModelNameForPersist()), str).apply();
    }

    public synchronized void setDownloadingModelInfo(long j, ModelInfo modelInfo) {
        String modelNameForPersist = modelInfo.getModelNameForPersist();
        zza().edit().putString(String.format("downloading_model_hash_%s", modelNameForPersist), modelInfo.getModelHash()).putLong(String.format("downloading_model_id_%s", modelNameForPersist), j).putLong(String.format("downloading_begin_time_%s", modelNameForPersist), SystemClock.elapsedRealtime()).apply();
    }

    public synchronized void setIncompatibleModelInfo(RemoteModel remoteModel, String str, String str2) {
        zza().edit().putString(String.format("bad_hash_%s", remoteModel.getUniqueModelNameForPersist()), str).putString("app_version", str2).apply();
    }

    public synchronized void clearIncompatibleModelInfo(RemoteModel remoteModel) {
        zza().edit().remove(String.format("bad_hash_%s", remoteModel.getUniqueModelNameForPersist())).remove("app_version").apply();
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

    public synchronized String getCachedLocalModelHash(LocalModel localModel, long j) {
        SharedPreferences zza;
        Object[] objArr;
        zza = zza();
        objArr = new Object[2];
        objArr[0] = localModel.getAbsoluteFilePath() != null ? localModel.getAbsoluteFilePath() : localModel.getAssetFilePath();
        objArr[1] = Long.valueOf(j);
        return zza.getString(String.format("cached_local_model_hash_%1s_%2s", objArr), null);
    }

    public synchronized void setCachedLocalModelHash(LocalModel localModel, long j, String str) {
        SharedPreferences.Editor edit = zza().edit();
        Object[] objArr = new Object[2];
        objArr[0] = localModel.getAbsoluteFilePath() != null ? localModel.getAbsoluteFilePath() : localModel.getAssetFilePath();
        objArr[1] = Long.valueOf(j);
        edit.putString(String.format("cached_local_model_hash_%1s_%2s", objArr), str).apply();
    }

    private final SharedPreferences zza() {
        return this.zza.getSharedPreferences(PREF_FILE, 0);
    }

    public static final /* synthetic */ SharedPrefManager zza(ComponentContainer componentContainer) {
        return new SharedPrefManager((Context) componentContainer.get(Context.class));
    }
}
