package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;
/* loaded from: classes3.dex */
abstract class MemberSignatureImpl extends SignatureImpl implements MemberSignature {
    public MemberSignatureImpl(int modifiers, String name, Class declaringType) {
        super(modifiers, name, declaringType);
    }

    public MemberSignatureImpl(String stringRep) {
        super(stringRep);
    }
}
