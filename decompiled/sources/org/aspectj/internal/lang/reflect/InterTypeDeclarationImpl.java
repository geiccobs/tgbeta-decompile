package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;
/* loaded from: classes3.dex */
public class InterTypeDeclarationImpl implements InterTypeDeclaration {
    private AjType<?> declaringType;
    private int modifiers;
    private AjType<?> targetType;
    protected String targetTypeName;

    public InterTypeDeclarationImpl(AjType<?> decType, String target, int mods) {
        this.declaringType = decType;
        this.targetTypeName = target;
        this.modifiers = mods;
        try {
            this.targetType = (AjType) StringToType.stringToType(target, decType.getJavaClass());
        } catch (ClassNotFoundException e) {
        }
    }

    public InterTypeDeclarationImpl(AjType<?> decType, AjType<?> targetType, int mods) {
        this.declaringType = decType;
        this.targetType = targetType;
        this.targetTypeName = targetType.getName();
        this.modifiers = mods;
    }

    @Override // org.aspectj.lang.reflect.InterTypeDeclaration
    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    @Override // org.aspectj.lang.reflect.InterTypeDeclaration
    public AjType<?> getTargetType() throws ClassNotFoundException {
        AjType<?> ajType = this.targetType;
        if (ajType == null) {
            throw new ClassNotFoundException(this.targetTypeName);
        }
        return ajType;
    }

    @Override // org.aspectj.lang.reflect.InterTypeDeclaration
    public int getModifiers() {
        return this.modifiers;
    }
}
