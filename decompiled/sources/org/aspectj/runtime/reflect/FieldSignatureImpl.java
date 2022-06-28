package org.aspectj.runtime.reflect;

import java.lang.reflect.Field;
import org.aspectj.lang.reflect.FieldSignature;
/* loaded from: classes3.dex */
public class FieldSignatureImpl extends MemberSignatureImpl implements FieldSignature {
    private Field field;
    Class fieldType;

    public FieldSignatureImpl(int modifiers, String name, Class declaringType, Class fieldType) {
        super(modifiers, name, declaringType);
        this.fieldType = fieldType;
    }

    public FieldSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override // org.aspectj.lang.reflect.FieldSignature
    public Class getFieldType() {
        if (this.fieldType == null) {
            this.fieldType = extractType(3);
        }
        return this.fieldType;
    }

    @Override // org.aspectj.runtime.reflect.SignatureImpl
    protected String createToString(StringMaker sm) {
        StringBuffer buf = new StringBuffer();
        buf.append(sm.makeModifiersString(getModifiers()));
        if (sm.includeArgs) {
            buf.append(sm.makeTypeName(getFieldType()));
        }
        if (sm.includeArgs) {
            buf.append(" ");
        }
        buf.append(sm.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
        buf.append(".");
        buf.append(getName());
        return buf.toString();
    }

    @Override // org.aspectj.lang.reflect.FieldSignature
    public Field getField() {
        if (this.field == null) {
            try {
                this.field = getDeclaringType().getDeclaredField(getName());
            } catch (Exception e) {
            }
        }
        return this.field;
    }
}
