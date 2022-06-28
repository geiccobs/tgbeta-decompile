package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;
/* loaded from: classes3.dex */
public class PerClauseImpl implements PerClause {
    private final PerClauseKind kind;

    public PerClauseImpl(PerClauseKind kind) {
        this.kind = kind;
    }

    @Override // org.aspectj.lang.reflect.PerClause
    public PerClauseKind getKind() {
        return this.kind;
    }

    public String toString() {
        return "issingleton()";
    }
}
