package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
/* loaded from: classes3.dex */
public class InterTypeConstructorDeclarationImpl extends InterTypeDeclarationImpl implements InterTypeConstructorDeclaration {
    private Method baseMethod;

    public InterTypeConstructorDeclarationImpl(AjType<?> decType, String target, int mods, Method baseMethod) {
        super(decType, target, mods);
        this.baseMethod = baseMethod;
    }

    @Override // org.aspectj.lang.reflect.InterTypeConstructorDeclaration
    public AjType<?>[] getParameterTypes() {
        Class<?>[] baseTypes = this.baseMethod.getParameterTypes();
        AjType<?>[] ret = new AjType[baseTypes.length - 1];
        for (int i = 1; i < baseTypes.length; i++) {
            ret[i - 1] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.aspectj.lang.reflect.InterTypeConstructorDeclaration
    public Type[] getGenericParameterTypes() {
        Type[] baseTypes = this.baseMethod.getGenericParameterTypes();
        Type[] ret = new AjType[baseTypes.length - 1];
        for (int i = 1; i < baseTypes.length; i++) {
            if (baseTypes[i] instanceof Class) {
                ret[i - 1] = AjTypeSystem.getAjType((Class) baseTypes[i]);
            } else {
                ret[i - 1] = baseTypes[i];
            }
        }
        return ret;
    }

    @Override // org.aspectj.lang.reflect.InterTypeConstructorDeclaration
    public AjType<?>[] getExceptionTypes() {
        Class<?>[] baseTypes = this.baseMethod.getExceptionTypes();
        AjType<?>[] ret = new AjType[baseTypes.length];
        for (int i = 0; i < baseTypes.length; i++) {
            ret[i] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Modifier.toString(getModifiers()));
        sb.append(" ");
        sb.append(this.targetTypeName);
        sb.append(".new");
        sb.append("(");
        AjType<?>[] pTypes = getParameterTypes();
        for (int i = 0; i < pTypes.length - 1; i++) {
            sb.append(pTypes[i].toString());
            sb.append(", ");
        }
        int i2 = pTypes.length;
        if (i2 > 0) {
            sb.append(pTypes[pTypes.length - 1].toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
