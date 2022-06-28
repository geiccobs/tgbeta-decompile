package org.aspectj.lang.reflect;

import java.lang.reflect.Type;
/* loaded from: classes3.dex */
public interface InterTypeFieldDeclaration extends InterTypeDeclaration {
    Type getGenericType();

    String getName();

    AjType<?> getType();
}
