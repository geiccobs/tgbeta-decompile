package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public interface zzld {
    int zza() throws IOException;

    <T> T zza(zzlc<T> zzlcVar, zzio zzioVar) throws IOException;

    <T> T zza(Class<T> cls, zzio zzioVar) throws IOException;

    void zza(List<Double> list) throws IOException;

    <T> void zza(List<T> list, zzlc<T> zzlcVar, zzio zzioVar) throws IOException;

    <K, V> void zza(Map<K, V> map, zzkf<K, V> zzkfVar, zzio zzioVar) throws IOException;

    int zzb();

    @Deprecated
    <T> T zzb(zzlc<T> zzlcVar, zzio zzioVar) throws IOException;

    @Deprecated
    <T> T zzb(Class<T> cls, zzio zzioVar) throws IOException;

    void zzb(List<Float> list) throws IOException;

    @Deprecated
    <T> void zzb(List<T> list, zzlc<T> zzlcVar, zzio zzioVar) throws IOException;

    void zzc(List<Long> list) throws IOException;

    boolean zzc() throws IOException;

    double zzd() throws IOException;

    void zzd(List<Long> list) throws IOException;

    float zze() throws IOException;

    void zze(List<Integer> list) throws IOException;

    long zzf() throws IOException;

    void zzf(List<Long> list) throws IOException;

    long zzg() throws IOException;

    void zzg(List<Integer> list) throws IOException;

    int zzh() throws IOException;

    void zzh(List<Boolean> list) throws IOException;

    long zzi() throws IOException;

    void zzi(List<String> list) throws IOException;

    int zzj() throws IOException;

    void zzj(List<String> list) throws IOException;

    void zzk(List<zzht> list) throws IOException;

    boolean zzk() throws IOException;

    String zzl() throws IOException;

    void zzl(List<Integer> list) throws IOException;

    String zzm() throws IOException;

    void zzm(List<Integer> list) throws IOException;

    zzht zzn() throws IOException;

    void zzn(List<Integer> list) throws IOException;

    int zzo() throws IOException;

    void zzo(List<Long> list) throws IOException;

    int zzp() throws IOException;

    void zzp(List<Integer> list) throws IOException;

    int zzq() throws IOException;

    void zzq(List<Long> list) throws IOException;

    long zzr() throws IOException;

    int zzs() throws IOException;

    long zzt() throws IOException;
}
