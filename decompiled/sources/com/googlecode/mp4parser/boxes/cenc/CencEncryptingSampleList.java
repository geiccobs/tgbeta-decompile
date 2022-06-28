package com.googlecode.mp4parser.boxes.cenc;

import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
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
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes3.dex */
public class CencEncryptingSampleList extends AbstractList<Sample> {
    List<CencSampleAuxiliaryDataFormat> auxiliaryDataFormats;
    RangeStartMap<Integer, SecretKey> ceks;
    Cipher cipher;
    private final String encryptionAlgo;
    List<Sample> parent;

    public CencEncryptingSampleList(SecretKey defaultCek, List<Sample> parent, List<CencSampleAuxiliaryDataFormat> auxiliaryDataFormats) {
        this(new RangeStartMap(0, defaultCek), parent, auxiliaryDataFormats, C.CENC_TYPE_cenc);
    }

    public CencEncryptingSampleList(RangeStartMap<Integer, SecretKey> ceks, List<Sample> parent, List<CencSampleAuxiliaryDataFormat> auxiliaryDataFormats, String encryptionAlgo) {
        this.ceks = new RangeStartMap<>();
        this.auxiliaryDataFormats = auxiliaryDataFormats;
        this.ceks = ceks;
        this.encryptionAlgo = encryptionAlgo;
        this.parent = parent;
        try {
            if (C.CENC_TYPE_cenc.equals(encryptionAlgo)) {
                this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
            } else if (C.CENC_TYPE_cbc1.equals(encryptionAlgo)) {
                this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
            } else {
                throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e2) {
            throw new RuntimeException(e2);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public Sample get(int index) {
        Sample clearSample = this.parent.get(index);
        if (this.ceks.get(Integer.valueOf(index)) != null) {
            CencSampleAuxiliaryDataFormat entry = this.auxiliaryDataFormats.get(index);
            return new EncryptedSampleImpl(this, clearSample, entry, this.cipher, this.ceks.get(Integer.valueOf(index)), null);
        }
        return clearSample;
    }

    protected void initCipher(byte[] iv, SecretKey cek) {
        try {
            byte[] fullIv = new byte[16];
            System.arraycopy(iv, 0, fullIv, 0, iv.length);
            this.cipher.init(1, cek, new IvParameterSpec(fullIv));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e2) {
            throw new RuntimeException(e2);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.parent.size();
    }

    /* loaded from: classes3.dex */
    public class EncryptedSampleImpl implements Sample {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final SecretKey cek;
        private final CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat;
        private final Cipher cipher;
        private final Sample clearSample;

        /* synthetic */ EncryptedSampleImpl(CencEncryptingSampleList cencEncryptingSampleList, Sample sample, CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat, Cipher cipher, SecretKey secretKey, EncryptedSampleImpl encryptedSampleImpl) {
            this(sample, cencSampleAuxiliaryDataFormat, cipher, secretKey);
        }

        private EncryptedSampleImpl(Sample clearSample, CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat, Cipher cipher, SecretKey cek) {
            CencEncryptingSampleList.this = r1;
            this.clearSample = clearSample;
            this.cencSampleAuxiliaryDataFormat = cencSampleAuxiliaryDataFormat;
            this.cipher = cipher;
            this.cek = cek;
        }

        @Override // com.googlecode.mp4parser.authoring.Sample
        public void writeTo(WritableByteChannel channel) throws IOException {
            CencSampleAuxiliaryDataFormat.Pair[] pairArr;
            ByteBuffer sample = (ByteBuffer) this.clearSample.asByteBuffer().rewind();
            CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
            try {
                if (this.cencSampleAuxiliaryDataFormat.pairs != null && this.cencSampleAuxiliaryDataFormat.pairs.length > 0) {
                    byte[] fullSample = new byte[sample.limit()];
                    sample.get(fullSample);
                    int offset = 0;
                    for (CencSampleAuxiliaryDataFormat.Pair pair : this.cencSampleAuxiliaryDataFormat.pairs) {
                        int offset2 = offset + pair.clear();
                        if (pair.encrypted() <= 0) {
                            offset = offset2;
                        } else {
                            this.cipher.update(fullSample, offset2, CastUtils.l2i(pair.encrypted()), fullSample, offset2);
                            offset = (int) (offset2 + pair.encrypted());
                        }
                    }
                    channel.write(ByteBuffer.wrap(fullSample));
                } else {
                    byte[] fullyEncryptedSample = new byte[sample.limit()];
                    sample.get(fullyEncryptedSample);
                    if (!C.CENC_TYPE_cbc1.equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        if (C.CENC_TYPE_cenc.equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                            channel.write(ByteBuffer.wrap(this.cipher.doFinal(fullyEncryptedSample)));
                        }
                    } else {
                        int encryptedLength = (fullyEncryptedSample.length / 16) * 16;
                        channel.write(ByteBuffer.wrap(this.cipher.doFinal(fullyEncryptedSample, 0, encryptedLength)));
                        channel.write(ByteBuffer.wrap(fullyEncryptedSample, encryptedLength, fullyEncryptedSample.length - encryptedLength));
                    }
                }
                sample.rewind();
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e2) {
                throw new RuntimeException(e2);
            } catch (ShortBufferException e3) {
                throw new RuntimeException(e3);
            }
        }

        @Override // com.googlecode.mp4parser.authoring.Sample
        public long getSize() {
            return this.clearSample.getSize();
        }

        @Override // com.googlecode.mp4parser.authoring.Sample
        public ByteBuffer asByteBuffer() {
            CencSampleAuxiliaryDataFormat.Pair[] pairArr;
            ByteBuffer sample = (ByteBuffer) this.clearSample.asByteBuffer().rewind();
            ByteBuffer encSample = ByteBuffer.allocate(sample.limit());
            CencSampleAuxiliaryDataFormat entry = this.cencSampleAuxiliaryDataFormat;
            CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
            try {
                if (entry.pairs != null) {
                    for (CencSampleAuxiliaryDataFormat.Pair pair : entry.pairs) {
                        byte[] clears = new byte[pair.clear()];
                        sample.get(clears);
                        encSample.put(clears);
                        if (pair.encrypted() > 0) {
                            byte[] toBeEncrypted = new byte[CastUtils.l2i(pair.encrypted())];
                            sample.get(toBeEncrypted);
                            if (toBeEncrypted.length % 16 != 0) {
                                throw new AssertionError();
                            }
                            byte[] encrypted = this.cipher.update(toBeEncrypted);
                            if (encrypted.length != toBeEncrypted.length) {
                                throw new AssertionError();
                            }
                            encSample.put(encrypted);
                        }
                    }
                } else {
                    byte[] fullyEncryptedSample = new byte[sample.limit()];
                    sample.get(fullyEncryptedSample);
                    if (!C.CENC_TYPE_cbc1.equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        if (C.CENC_TYPE_cenc.equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                            encSample.put(this.cipher.doFinal(fullyEncryptedSample));
                        }
                    } else {
                        int encryptedLength = (fullyEncryptedSample.length / 16) * 16;
                        encSample.put(this.cipher.doFinal(fullyEncryptedSample, 0, encryptedLength));
                        encSample.put(fullyEncryptedSample, encryptedLength, fullyEncryptedSample.length - encryptedLength);
                    }
                }
                sample.rewind();
                encSample.rewind();
                return encSample;
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
}
