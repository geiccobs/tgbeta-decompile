package com.googlecode.mp4parser.boxes.cenc;

import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes3.dex */
public class CencDecryptingSampleList extends AbstractList<Sample> {
    String encryptionAlgo;
    RangeStartMap<Integer, SecretKey> keys;
    List<Sample> parent;
    List<CencSampleAuxiliaryDataFormat> sencInfo;

    public CencDecryptingSampleList(SecretKey secretKey, List<Sample> parent, List<CencSampleAuxiliaryDataFormat> sencInfo) {
        this(new RangeStartMap(0, secretKey), parent, sencInfo, C.CENC_TYPE_cenc);
    }

    public CencDecryptingSampleList(RangeStartMap<Integer, SecretKey> keys, List<Sample> parent, List<CencSampleAuxiliaryDataFormat> sencInfo, String encryptionAlgo) {
        this.keys = new RangeStartMap<>();
        this.sencInfo = sencInfo;
        this.keys = keys;
        this.parent = parent;
        this.encryptionAlgo = encryptionAlgo;
    }

    Cipher getCipher(SecretKey sk, byte[] iv) {
        byte[] fullIv = new byte[16];
        System.arraycopy(iv, 0, fullIv, 0, iv.length);
        try {
            if (C.CENC_TYPE_cenc.equals(this.encryptionAlgo)) {
                Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
                c.init(2, sk, new IvParameterSpec(fullIv));
                return c;
            } else if (C.CENC_TYPE_cbc1.equals(this.encryptionAlgo)) {
                Cipher c2 = Cipher.getInstance("AES/CBC/NoPadding");
                c2.init(2, sk, new IvParameterSpec(fullIv));
                return c2;
            } else {
                throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
            }
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e2) {
            throw new RuntimeException(e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException(e3);
        } catch (NoSuchPaddingException e4) {
            throw new RuntimeException(e4);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public Sample get(int index) {
        CencSampleAuxiliaryDataFormat.Pair[] pairArr;
        if (this.keys.get(Integer.valueOf(index)) != null) {
            Sample encSample = this.parent.get(index);
            ByteBuffer encSampleBuffer = encSample.asByteBuffer();
            encSampleBuffer.rewind();
            ByteBuffer decSampleBuffer = ByteBuffer.allocate(encSampleBuffer.limit());
            CencSampleAuxiliaryDataFormat sencEntry = this.sencInfo.get(index);
            Cipher cipher = getCipher(this.keys.get(Integer.valueOf(index)), sencEntry.iv);
            try {
                if (sencEntry.pairs != null && sencEntry.pairs.length > 0) {
                    for (CencSampleAuxiliaryDataFormat.Pair pair : sencEntry.pairs) {
                        int clearBytes = pair.clear();
                        int encrypted = CastUtils.l2i(pair.encrypted());
                        byte[] clears = new byte[clearBytes];
                        encSampleBuffer.get(clears);
                        decSampleBuffer.put(clears);
                        if (encrypted > 0) {
                            byte[] encs = new byte[encrypted];
                            encSampleBuffer.get(encs);
                            byte[] decr = cipher.update(encs);
                            decSampleBuffer.put(decr);
                        }
                    }
                    if (encSampleBuffer.remaining() > 0) {
                        System.err.println("Decrypted sample but still data remaining: " + encSample.getSize());
                    }
                    decSampleBuffer.put(cipher.doFinal());
                } else {
                    byte[] fullyEncryptedSample = new byte[encSampleBuffer.limit()];
                    encSampleBuffer.get(fullyEncryptedSample);
                    if (C.CENC_TYPE_cbc1.equals(this.encryptionAlgo)) {
                        int encryptedLength = (fullyEncryptedSample.length / 16) * 16;
                        decSampleBuffer.put(cipher.doFinal(fullyEncryptedSample, 0, encryptedLength));
                        decSampleBuffer.put(fullyEncryptedSample, encryptedLength, fullyEncryptedSample.length - encryptedLength);
                    } else if (C.CENC_TYPE_cenc.equals(this.encryptionAlgo)) {
                        decSampleBuffer.put(cipher.doFinal(fullyEncryptedSample));
                    }
                }
                encSampleBuffer.rewind();
                decSampleBuffer.rewind();
                return new SampleImpl(decSampleBuffer);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e2) {
                throw new RuntimeException(e2);
            }
        }
        return this.parent.get(index);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.parent.size();
    }
}
