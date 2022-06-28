package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CatchClauseSignature;
/* loaded from: classes3.dex */
class CatchClauseSignatureImpl extends SignatureImpl implements CatchClauseSignature {
    String parameterName;
    Class parameterType;

    public CatchClauseSignatureImpl(Class declaringType, Class parameterType, String parameterName) {
        super(0, "catch", declaringType);
        this.parameterType = parameterType;
        this.parameterName = parameterName;
    }

    public CatchClauseSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override // org.aspectj.lang.reflect.CatchClauseSignature
    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        return this.parameterType;
    }

    @Override // org.aspectj.lang.reflect.CatchClauseSignature
    public String getParameterName() {
        if (this.parameterName == null) {
            this.parameterName = extractString(4);
        }
        return this.parameterName;
    }

    @Override // org.aspectj.runtime.reflect.SignatureImpl
    protected String createToString(StringMaker sm) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("catch(");
        stringBuffer.append(sm.makeTypeName(getParameterType()));
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
