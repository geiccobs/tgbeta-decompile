package com.google.android.gms.internal.play_billing;

import com.google.android.exoplayer2.C;
import java.util.Arrays;
import javax.annotation.CheckForNull;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class zzaf extends zzx {
    static final zzx zza = new zzaf(null, new Object[0], 0);
    final transient Object[] zzb;
    @CheckForNull
    private final transient Object zzc;
    private final transient int zzd;

    private zzaf(@CheckForNull Object obj, Object[] objArr, int i) {
        this.zzc = obj;
        this.zzb = objArr;
        this.zzd = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static zzaf zzf(int i, Object[] objArr, zzw zzwVar) {
        int i2 = i;
        Object[] objArr2 = objArr;
        if (i2 == 0) {
            return (zzaf) zza;
        }
        Object[] objArr3 = null;
        if (i2 == 1) {
            Object obj = objArr2[0];
            obj.getClass();
            Object obj2 = objArr2[1];
            obj2.getClass();
            zzp.zza(obj, obj2);
            return new zzaf(null, objArr2, 1);
        }
        zzm.zzb(i2, objArr2.length >> 1, "index");
        char c = 2;
        int max = Math.max(i2, 2);
        int i3 = C.BUFFER_FLAG_ENCRYPTED;
        if (max < 751619276) {
            int highestOneBit = Integer.highestOneBit(max - 1);
            i3 = highestOneBit + highestOneBit;
            while (true) {
                double d = i3;
                Double.isNaN(d);
                if (d * 0.7d >= max) {
                    break;
                }
                i3 += i3;
            }
        } else if (max >= 1073741824) {
            throw new IllegalArgumentException("collection too large");
        }
        if (i2 == 1) {
            Object obj3 = objArr2[0];
            obj3.getClass();
            Object obj4 = objArr2[1];
            obj4.getClass();
            zzp.zza(obj3, obj4);
        } else {
            int i4 = i3 - 1;
            char c2 = 65535;
            if (i3 <= 128) {
                byte[] bArr = new byte[i3];
                Arrays.fill(bArr, (byte) -1);
                int i5 = 0;
                for (int i6 = 0; i6 < i2; i6++) {
                    int i7 = i6 + i6;
                    int i8 = i5 + i5;
                    Object obj5 = objArr2[i7];
                    obj5.getClass();
                    Object obj6 = objArr2[i7 ^ 1];
                    obj6.getClass();
                    zzp.zza(obj5, obj6);
                    int zza2 = zzq.zza(obj5.hashCode());
                    while (true) {
                        int i9 = zza2 & i4;
                        int i10 = bArr[i9] & 255;
                        if (i10 != 255) {
                            if (obj5.equals(objArr2[i10])) {
                                int i11 = i10 ^ 1;
                                Object obj7 = objArr2[i11];
                                obj7.getClass();
                                Object zzvVar = new zzv(obj5, obj6, obj7);
                                objArr2[i11] = obj6;
                                objArr3 = zzvVar;
                                break;
                            }
                            zza2 = i9 + 1;
                        } else {
                            bArr[i9] = (byte) i8;
                            if (i5 < i6) {
                                objArr2[i8] = obj5;
                                objArr2[i8 ^ 1] = obj6;
                            }
                            i5++;
                        }
                    }
                }
                if (i5 == i2) {
                    objArr3 = bArr;
                    c = 2;
                } else {
                    objArr3 = new Object[]{bArr, Integer.valueOf(i5), objArr3};
                    c = 2;
                }
            } else if (i3 <= 32768) {
                short[] sArr = new short[i3];
                Arrays.fill(sArr, (short) -1);
                int i12 = 0;
                for (int i13 = 0; i13 < i2; i13++) {
                    int i14 = i13 + i13;
                    int i15 = i12 + i12;
                    Object obj8 = objArr2[i14];
                    obj8.getClass();
                    Object obj9 = objArr2[i14 ^ 1];
                    obj9.getClass();
                    zzp.zza(obj8, obj9);
                    int zza3 = zzq.zza(obj8.hashCode());
                    while (true) {
                        int i16 = zza3 & i4;
                        char c3 = (char) sArr[i16];
                        if (c3 != 65535) {
                            if (obj8.equals(objArr2[c3])) {
                                int i17 = c3 ^ 1;
                                Object obj10 = objArr2[i17];
                                obj10.getClass();
                                Object zzvVar2 = new zzv(obj8, obj9, obj10);
                                objArr2[i17] = obj9;
                                objArr3 = zzvVar2;
                                break;
                            }
                            zza3 = i16 + 1;
                        } else {
                            sArr[i16] = (short) i15;
                            if (i12 < i13) {
                                objArr2[i15] = obj8;
                                objArr2[i15 ^ 1] = obj9;
                            }
                            i12++;
                        }
                    }
                }
                if (i12 == i2) {
                    objArr3 = sArr;
                    c = 2;
                } else {
                    c = 2;
                    objArr3 = new Object[]{sArr, Integer.valueOf(i12), objArr3};
                }
            } else {
                int[] iArr = new int[i3];
                Arrays.fill(iArr, -1);
                int i18 = 0;
                int i19 = 0;
                while (i18 < i2) {
                    int i20 = i18 + i18;
                    int i21 = i19 + i19;
                    Object obj11 = objArr2[i20];
                    obj11.getClass();
                    Object obj12 = objArr2[i20 ^ 1];
                    obj12.getClass();
                    zzp.zza(obj11, obj12);
                    int zza4 = zzq.zza(obj11.hashCode());
                    while (true) {
                        int i22 = zza4 & i4;
                        char c4 = iArr[i22];
                        if (c4 != c2) {
                            if (obj11.equals(objArr2[c4])) {
                                int i23 = c4 ^ 1;
                                Object obj13 = objArr2[i23];
                                obj13.getClass();
                                Object zzvVar3 = new zzv(obj11, obj12, obj13);
                                objArr2[i23] = obj12;
                                objArr3 = zzvVar3;
                                break;
                            }
                            zza4 = i22 + 1;
                            c2 = 65535;
                        } else {
                            iArr[i22] = i21;
                            if (i19 < i18) {
                                objArr2[i21] = obj11;
                                objArr2[i21 ^ 1] = obj12;
                            }
                            i19++;
                        }
                    }
                    i18++;
                    c2 = 65535;
                }
                if (i19 == i2) {
                    objArr3 = iArr;
                    c = 2;
                } else {
                    c = 2;
                    objArr3 = new Object[]{iArr, Integer.valueOf(i19), objArr3};
                }
            }
        }
        if (objArr3 instanceof Object[]) {
            Object[] objArr4 = objArr3;
            zzwVar.zzc = (zzv) objArr4[c];
            Object[] objArr5 = objArr4[0];
            int intValue = ((Integer) objArr4[1]).intValue();
            objArr2 = Arrays.copyOf(objArr2, intValue + intValue);
            objArr3 = objArr5;
            i2 = intValue;
        }
        return new zzaf(objArr3, objArr2, i2);
    }

    @Override // com.google.android.gms.internal.play_billing.zzx, java.util.Map, j$.util.Map
    @CheckForNull
    public final Object get(@CheckForNull Object obj) {
        Object obj2;
        Object obj3 = this.zzc;
        Object[] objArr = this.zzb;
        int i = this.zzd;
        if (obj == null) {
            obj2 = null;
        } else if (i == 1) {
            Object obj4 = objArr[0];
            obj4.getClass();
            if (obj4.equals(obj)) {
                obj2 = objArr[1];
                obj2.getClass();
            }
            obj2 = null;
        } else {
            if (obj3 != null) {
                if (obj3 instanceof byte[]) {
                    byte[] bArr = (byte[]) obj3;
                    int length = bArr.length - 1;
                    int zza2 = zzq.zza(obj.hashCode());
                    while (true) {
                        int i2 = zza2 & length;
                        int i3 = bArr[i2] & 255;
                        if (i3 == 255) {
                            obj2 = null;
                            break;
                        } else if (obj.equals(objArr[i3])) {
                            obj2 = objArr[i3 ^ 1];
                            break;
                        } else {
                            zza2 = i2 + 1;
                        }
                    }
                } else if (obj3 instanceof short[]) {
                    short[] sArr = (short[]) obj3;
                    int length2 = sArr.length - 1;
                    int zza3 = zzq.zza(obj.hashCode());
                    while (true) {
                        int i4 = zza3 & length2;
                        char c = (char) sArr[i4];
                        if (c == 65535) {
                            obj2 = null;
                            break;
                        } else if (obj.equals(objArr[c])) {
                            obj2 = objArr[c ^ 1];
                            break;
                        } else {
                            zza3 = i4 + 1;
                        }
                    }
                } else {
                    int[] iArr = (int[]) obj3;
                    int length3 = iArr.length - 1;
                    int zza4 = zzq.zza(obj.hashCode());
                    while (true) {
                        int i5 = zza4 & length3;
                        int i6 = iArr[i5];
                        if (i6 == -1) {
                            obj2 = null;
                            break;
                        } else if (obj.equals(objArr[i6])) {
                            obj2 = objArr[i6 ^ 1];
                            break;
                        } else {
                            zza4 = i5 + 1;
                        }
                    }
                }
            }
            obj2 = null;
        }
        if (obj2 == null) {
            return null;
        }
        return obj2;
    }

    @Override // java.util.Map, j$.util.Map
    public final int size() {
        return this.zzd;
    }

    @Override // com.google.android.gms.internal.play_billing.zzx
    final zzr zza() {
        return new zzae(this.zzb, 1, this.zzd);
    }

    @Override // com.google.android.gms.internal.play_billing.zzx
    final zzy zzc() {
        return new zzac(this, this.zzb, 0, this.zzd);
    }

    @Override // com.google.android.gms.internal.play_billing.zzx
    final zzy zzd() {
        return new zzad(this, new zzae(this.zzb, 0, this.zzd));
    }
}
