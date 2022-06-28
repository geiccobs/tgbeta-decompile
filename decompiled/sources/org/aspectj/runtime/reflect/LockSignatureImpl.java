package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.LockSignature;
/* loaded from: classes3.dex */
class LockSignatureImpl extends SignatureImpl implements LockSignature {
    private Class parameterType;

    public LockSignatureImpl(Class c) {
        super(8, JoinPoint.SYNCHRONIZATION_LOCK, c);
        this.parameterType = c;
    }

    public LockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override // org.aspectj.runtime.reflect.SignatureImpl
    protected String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("lock(");
        stringBuffer.append(sm.makeTypeName(this.parameterType));
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        return this.parameterType;
    }
}
