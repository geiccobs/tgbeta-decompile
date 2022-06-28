package org.aspectj.internal.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjTypeSystem;
/* loaded from: classes3.dex */
public class StringToType {
    public static Type[] commaSeparatedListToTypeArray(String typeNames, Class classScope) throws ClassNotFoundException {
        StringTokenizer strTok = new StringTokenizer(typeNames, ",");
        Type[] ret = new Type[strTok.countTokens()];
        int index = 0;
        while (strTok.hasMoreTokens()) {
            String typeName = strTok.nextToken().trim();
            ret[index] = stringToType(typeName, classScope);
            index++;
        }
        return ret;
    }

    public static Type stringToType(String typeName, Class classScope) throws ClassNotFoundException {
        try {
            if (typeName.indexOf("<") == -1) {
                return AjTypeSystem.getAjType(Class.forName(typeName, false, classScope.getClassLoader()));
            }
            return makeParameterizedType(typeName, classScope);
        } catch (ClassNotFoundException e) {
            TypeVariable[] tVars = classScope.getTypeParameters();
            for (int i = 0; i < tVars.length; i++) {
                if (tVars[i].getName().equals(typeName)) {
                    return tVars[i];
                }
            }
            throw new ClassNotFoundException(typeName);
        }
    }

    private static Type makeParameterizedType(String typeName, Class classScope) throws ClassNotFoundException {
        int paramStart = typeName.indexOf(60);
        String baseName = typeName.substring(0, paramStart);
        final Class baseClass = Class.forName(baseName, false, classScope.getClassLoader());
        int paramEnd = typeName.lastIndexOf(62);
        String params = typeName.substring(paramStart + 1, paramEnd);
        final Type[] typeParams = commaSeparatedListToTypeArray(params, classScope);
        return new ParameterizedType() { // from class: org.aspectj.internal.lang.reflect.StringToType.1
            @Override // java.lang.reflect.ParameterizedType
            public Type[] getActualTypeArguments() {
                return typeParams;
            }

            @Override // java.lang.reflect.ParameterizedType
            public Type getRawType() {
                return baseClass;
            }

            @Override // java.lang.reflect.ParameterizedType
            public Type getOwnerType() {
                return baseClass.getEnclosingClass();
            }
        };
    }
}
