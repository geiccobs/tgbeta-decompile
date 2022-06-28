package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.TypePattern;
/* loaded from: classes3.dex */
public class DeclareParentsImpl implements DeclareParents {
    private AjType<?> declaringType;
    private String firstMissingTypeName;
    private boolean isExtends;
    private Type[] parents;
    private boolean parentsError;
    private String parentsString;
    private TypePattern targetTypesPattern;

    public DeclareParentsImpl(String targets, String parentsAsString, boolean isExtends, AjType<?> declaring) {
        this.parentsError = false;
        this.targetTypesPattern = new TypePatternImpl(targets);
        this.isExtends = isExtends;
        this.declaringType = declaring;
        this.parentsString = parentsAsString;
        try {
            this.parents = StringToType.commaSeparatedListToTypeArray(parentsAsString, declaring.getJavaClass());
        } catch (ClassNotFoundException cnfEx) {
            this.parentsError = true;
            this.firstMissingTypeName = cnfEx.getMessage();
        }
    }

    @Override // org.aspectj.lang.reflect.DeclareParents
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override // org.aspectj.lang.reflect.DeclareParents
    public TypePattern getTargetTypesPattern() {
        return this.targetTypesPattern;
    }

    @Override // org.aspectj.lang.reflect.DeclareParents
    public boolean isExtends() {
        return this.isExtends;
    }

    @Override // org.aspectj.lang.reflect.DeclareParents
    public boolean isImplements() {
        return !this.isExtends;
    }

    @Override // org.aspectj.lang.reflect.DeclareParents
    public Type[] getParentTypes() throws ClassNotFoundException {
        if (this.parentsError) {
            throw new ClassNotFoundException(this.firstMissingTypeName);
        }
        return this.parents;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare parents : ");
        sb.append(getTargetTypesPattern().asString());
        sb.append(isExtends() ? " extends " : " implements ");
        sb.append(this.parentsString);
        return sb.toString();
    }
}
