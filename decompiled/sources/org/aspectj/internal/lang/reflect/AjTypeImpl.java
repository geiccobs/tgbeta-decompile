package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;
import org.aspectj.internal.lang.annotation.ajcDeclareParents;
import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.aspectj.internal.lang.annotation.ajcITD;
import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.Pointcut;
/* loaded from: classes3.dex */
public class AjTypeImpl<T> implements AjType<T> {
    private static final String ajcMagic = "ajc$";
    private Class<T> clazz;
    private Pointcut[] declaredPointcuts = null;
    private Pointcut[] pointcuts = null;
    private Advice[] declaredAdvice = null;
    private Advice[] advice = null;
    private InterTypeMethodDeclaration[] declaredITDMethods = null;
    private InterTypeMethodDeclaration[] itdMethods = null;
    private InterTypeFieldDeclaration[] declaredITDFields = null;
    private InterTypeFieldDeclaration[] itdFields = null;
    private InterTypeConstructorDeclaration[] itdCons = null;
    private InterTypeConstructorDeclaration[] declaredITDCons = null;

    public AjTypeImpl(Class<T> fromClass) {
        this.clazz = fromClass;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public String getName() {
        return this.clazz.getName();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Package getPackage() {
        return this.clazz.getPackage();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<?>[] getInterfaces() {
        Class<?>[] baseInterfaces = this.clazz.getInterfaces();
        return toAjTypeArray(baseInterfaces);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public int getModifiers() {
        return this.clazz.getModifiers();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Class<T> getJavaClass() {
        return this.clazz;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<? super T> getSupertype() {
        Class<? super T> superclass = this.clazz.getSuperclass();
        if (superclass == null) {
            return null;
        }
        return new AjTypeImpl(superclass);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Type getGenericSupertype() {
        return this.clazz.getGenericSuperclass();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Method getEnclosingMethod() {
        return this.clazz.getEnclosingMethod();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Constructor getEnclosingConstructor() {
        return this.clazz.getEnclosingConstructor();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<?> getEnclosingType() {
        Class<?> enc = this.clazz.getEnclosingClass();
        if (enc != null) {
            return new AjTypeImpl(enc);
        }
        return null;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<?> getDeclaringType() {
        Class dec = this.clazz.getDeclaringClass();
        if (dec != null) {
            return new AjTypeImpl(dec);
        }
        return null;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public PerClause getPerClause() {
        if (isAspect()) {
            Aspect aspectAnn = (Aspect) this.clazz.getAnnotation(Aspect.class);
            String perClause = aspectAnn.value();
            if (perClause.equals("")) {
                if (getSupertype().isAspect()) {
                    return getSupertype().getPerClause();
                }
                return new PerClauseImpl(PerClauseKind.SINGLETON);
            } else if (perClause.startsWith("perthis(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTHIS, perClause.substring("perthis(".length(), perClause.length() - 1));
            } else {
                if (perClause.startsWith("pertarget(")) {
                    return new PointcutBasedPerClauseImpl(PerClauseKind.PERTARGET, perClause.substring("pertarget(".length(), perClause.length() - 1));
                }
                if (perClause.startsWith("percflow(")) {
                    return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOW, perClause.substring("percflow(".length(), perClause.length() - 1));
                }
                if (perClause.startsWith("percflowbelow(")) {
                    return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOWBELOW, perClause.substring("percflowbelow(".length(), perClause.length() - 1));
                }
                if (perClause.startsWith("pertypewithin")) {
                    return new TypePatternBasedPerClauseImpl(PerClauseKind.PERTYPEWITHIN, perClause.substring("pertypewithin(".length(), perClause.length() - 1));
                }
                throw new IllegalStateException("Per-clause not recognized: " + perClause);
            }
        }
        return null;
    }

    @Override // java.lang.reflect.AnnotatedElement
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.clazz.isAnnotationPresent(annotationType);
    }

    @Override // java.lang.reflect.AnnotatedElement
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (A) this.clazz.getAnnotation(annotationType);
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getAnnotations() {
        return this.clazz.getAnnotations();
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getDeclaredAnnotations() {
        return this.clazz.getDeclaredAnnotations();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<?>[] getAjTypes() {
        Class[] classes = this.clazz.getClasses();
        return toAjTypeArray(classes);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public AjType<?>[] getDeclaredAjTypes() {
        Class[] classes = this.clazz.getDeclaredClasses();
        return toAjTypeArray(classes);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Constructor getConstructor(AjType<?>... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getConstructor(toClassArray(parameterTypes));
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Constructor[] getConstructors() {
        return this.clazz.getConstructors();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Constructor getDeclaredConstructor(AjType<?>... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getDeclaredConstructor(toClassArray(parameterTypes));
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Constructor[] getDeclaredConstructors() {
        return this.clazz.getDeclaredConstructors();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Field getDeclaredField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getDeclaredField(name);
        if (f.getName().startsWith(ajcMagic)) {
            throw new NoSuchFieldException(name);
        }
        return f;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Field[] getDeclaredFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        List<Field> filteredFields = new ArrayList<>();
        for (Field field : fields) {
            if (!field.getName().startsWith(ajcMagic) && !field.isAnnotationPresent(DeclareWarning.class) && !field.isAnnotationPresent(DeclareError.class)) {
                filteredFields.add(field);
            }
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Field getField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getField(name);
        if (f.getName().startsWith(ajcMagic)) {
            throw new NoSuchFieldException(name);
        }
        return f;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Field[] getFields() {
        Field[] fields = this.clazz.getFields();
        List<Field> filteredFields = new ArrayList<>();
        for (Field field : fields) {
            if (!field.getName().startsWith(ajcMagic) && !field.isAnnotationPresent(DeclareWarning.class) && !field.isAnnotationPresent(DeclareError.class)) {
                filteredFields.add(field);
            }
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Method getDeclaredMethod(String name, AjType<?>... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getDeclaredMethod(name, toClassArray(parameterTypes));
        if (!isReallyAMethod(m)) {
            throw new NoSuchMethodException(name);
        }
        return m;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Method getMethod(String name, AjType<?>... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getMethod(name, toClassArray(parameterTypes));
        if (!isReallyAMethod(m)) {
            throw new NoSuchMethodException(name);
        }
        return m;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Method[] getDeclaredMethods() {
        Method[] methods = this.clazz.getDeclaredMethods();
        List<Method> filteredMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isReallyAMethod(method)) {
                filteredMethods.add(method);
            }
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Method[] getMethods() {
        Method[] methods = this.clazz.getMethods();
        List<Method> filteredMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isReallyAMethod(method)) {
                filteredMethods.add(method);
            }
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    private boolean isReallyAMethod(Method method) {
        if (method.getName().startsWith(ajcMagic)) {
            return false;
        }
        if (method.getAnnotations().length == 0) {
            return true;
        }
        return !method.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class) && !method.isAnnotationPresent(Before.class) && !method.isAnnotationPresent(After.class) && !method.isAnnotationPresent(AfterReturning.class) && !method.isAnnotationPresent(AfterThrowing.class) && !method.isAnnotationPresent(Around.class);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Pointcut getDeclaredPointcut(String name) throws NoSuchPointcutException {
        Pointcut[] pcs = getDeclaredPointcuts();
        for (Pointcut pc : pcs) {
            if (pc.getName().equals(name)) {
                return pc;
            }
        }
        throw new NoSuchPointcutException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Pointcut getPointcut(String name) throws NoSuchPointcutException {
        Pointcut[] pcs = getPointcuts();
        for (Pointcut pc : pcs) {
            if (pc.getName().equals(name)) {
                return pc;
            }
        }
        throw new NoSuchPointcutException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Pointcut[] getDeclaredPointcuts() {
        Pointcut[] pointcutArr = this.declaredPointcuts;
        if (pointcutArr != null) {
            return pointcutArr;
        }
        List<Pointcut> pointcuts = new ArrayList<>();
        Method[] methods = this.clazz.getDeclaredMethods();
        for (Method method : methods) {
            Pointcut pc = asPointcut(method);
            if (pc != null) {
                pointcuts.add(pc);
            }
        }
        Pointcut[] ret = new Pointcut[pointcuts.size()];
        pointcuts.toArray(ret);
        this.declaredPointcuts = ret;
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Pointcut[] getPointcuts() {
        Pointcut[] pointcutArr = this.pointcuts;
        if (pointcutArr != null) {
            return pointcutArr;
        }
        List<Pointcut> pcuts = new ArrayList<>();
        Method[] methods = this.clazz.getMethods();
        for (Method method : methods) {
            Pointcut pc = asPointcut(method);
            if (pc != null) {
                pcuts.add(pc);
            }
        }
        Pointcut[] ret = new Pointcut[pcuts.size()];
        pcuts.toArray(ret);
        this.pointcuts = ret;
        return ret;
    }

    private Pointcut asPointcut(Method method) {
        org.aspectj.lang.annotation.Pointcut pcAnn = (org.aspectj.lang.annotation.Pointcut) method.getAnnotation(org.aspectj.lang.annotation.Pointcut.class);
        if (pcAnn != null) {
            String name = method.getName();
            if (name.startsWith(ajcMagic)) {
                int nameStart = name.indexOf("$$");
                name = name.substring(nameStart + 2, name.length());
                int nextDollar = name.indexOf("$");
                if (nextDollar != -1) {
                    name = name.substring(0, nextDollar);
                }
            }
            return new PointcutImpl(name, pcAnn.value(), method, AjTypeSystem.getAjType(method.getDeclaringClass()), pcAnn.argNames());
        }
        return null;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Advice[] getDeclaredAdvice(AdviceKind... ofType) {
        Set<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return getDeclaredAdvice(types);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Advice[] getAdvice(AdviceKind... ofType) {
        Set<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return getAdvice(types);
    }

    private Advice[] getDeclaredAdvice(Set ofAdviceTypes) {
        if (this.declaredAdvice == null) {
            initDeclaredAdvice();
        }
        List<Advice> adviceList = new ArrayList<>();
        Advice[] arr$ = this.declaredAdvice;
        for (Advice a : arr$) {
            if (ofAdviceTypes.contains(a.getKind())) {
                adviceList.add(a);
            }
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initDeclaredAdvice() {
        Method[] methods = this.clazz.getDeclaredMethods();
        List<Advice> adviceList = new ArrayList<>();
        for (Method method : methods) {
            Advice advice = asAdvice(method);
            if (advice != null) {
                adviceList.add(advice);
            }
        }
        Advice[] adviceArr = new Advice[adviceList.size()];
        this.declaredAdvice = adviceArr;
        adviceList.toArray(adviceArr);
    }

    private Advice[] getAdvice(Set ofAdviceTypes) {
        if (this.advice == null) {
            initAdvice();
        }
        List<Advice> adviceList = new ArrayList<>();
        Advice[] arr$ = this.advice;
        for (Advice a : arr$) {
            if (ofAdviceTypes.contains(a.getKind())) {
                adviceList.add(a);
            }
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initAdvice() {
        Method[] methods = this.clazz.getMethods();
        List<Advice> adviceList = new ArrayList<>();
        for (Method method : methods) {
            Advice advice = asAdvice(method);
            if (advice != null) {
                adviceList.add(advice);
            }
        }
        Advice[] adviceArr = new Advice[adviceList.size()];
        this.advice = adviceArr;
        adviceList.toArray(adviceArr);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Advice getAdvice(String name) throws NoSuchAdviceException {
        if (name.equals("")) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.advice == null) {
            initAdvice();
        }
        Advice[] arr$ = this.advice;
        for (Advice a : arr$) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        throw new NoSuchAdviceException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public Advice getDeclaredAdvice(String name) throws NoSuchAdviceException {
        if (name.equals("")) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.declaredAdvice == null) {
            initDeclaredAdvice();
        }
        Advice[] arr$ = this.declaredAdvice;
        for (Advice a : arr$) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        throw new NoSuchAdviceException(name);
    }

    private Advice asAdvice(Method method) {
        if (method.getAnnotations().length == 0) {
            return null;
        }
        Before beforeAnn = (Before) method.getAnnotation(Before.class);
        if (beforeAnn != null) {
            return new AdviceImpl(method, beforeAnn.value(), AdviceKind.BEFORE);
        }
        After afterAnn = (After) method.getAnnotation(After.class);
        if (afterAnn != null) {
            return new AdviceImpl(method, afterAnn.value(), AdviceKind.AFTER);
        }
        AfterReturning afterReturningAnn = (AfterReturning) method.getAnnotation(AfterReturning.class);
        if (afterReturningAnn != null) {
            String pcExpr = afterReturningAnn.pointcut();
            if (pcExpr.equals("")) {
                pcExpr = afterReturningAnn.value();
            }
            return new AdviceImpl(method, pcExpr, AdviceKind.AFTER_RETURNING, afterReturningAnn.returning());
        }
        AfterThrowing afterThrowingAnn = (AfterThrowing) method.getAnnotation(AfterThrowing.class);
        if (afterThrowingAnn != null) {
            String pcExpr2 = afterThrowingAnn.pointcut();
            if (pcExpr2 == null) {
                pcExpr2 = afterThrowingAnn.value();
            }
            return new AdviceImpl(method, pcExpr2, AdviceKind.AFTER_THROWING, afterThrowingAnn.throwing());
        }
        Around aroundAnn = (Around) method.getAnnotation(Around.class);
        if (aroundAnn == null) {
            return null;
        }
        return new AdviceImpl(method, aroundAnn.value(), AdviceKind.AROUND);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeMethodDeclaration getDeclaredITDMethod(String name, AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        InterTypeMethodDeclaration[] itdms = getDeclaredITDMethods();
        for (InterTypeMethodDeclaration itdm : itdms) {
            try {
                if (itdm.getName().equals(name)) {
                    AjType<?> itdTarget = itdm.getTargetType();
                    if (itdTarget.equals(target)) {
                        AjType<?>[] ptypes = itdm.getParameterTypes();
                        if (ptypes.length == parameterTypes.length) {
                            for (int i = 0; i < ptypes.length; i++) {
                                if (!ptypes[i].equals(parameterTypes[i])) {
                                    break;
                                }
                            }
                            return itdm;
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeMethodDeclaration[] getDeclaredITDMethods() {
        if (this.declaredITDMethods == null) {
            List<InterTypeMethodDeclaration> itdms = new ArrayList<>();
            Method[] baseMethods = this.clazz.getDeclaredMethods();
            for (Method m : baseMethods) {
                if (m.getName().contains("ajc$interMethodDispatch1$") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    InterTypeMethodDeclaration itdm = new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m);
                    itdms.add(itdm);
                }
            }
            addAnnotationStyleITDMethods(itdms, false);
            InterTypeMethodDeclaration[] interTypeMethodDeclarationArr = new InterTypeMethodDeclaration[itdms.size()];
            this.declaredITDMethods = interTypeMethodDeclarationArr;
            itdms.toArray(interTypeMethodDeclarationArr);
        }
        return this.declaredITDMethods;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeMethodDeclaration getITDMethod(String name, AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        InterTypeMethodDeclaration[] itdms = getITDMethods();
        for (InterTypeMethodDeclaration itdm : itdms) {
            try {
                if (itdm.getName().equals(name)) {
                    AjType<?> itdTarget = itdm.getTargetType();
                    if (itdTarget.equals(target)) {
                        AjType<?>[] ptypes = itdm.getParameterTypes();
                        if (ptypes.length == parameterTypes.length) {
                            for (int i = 0; i < ptypes.length; i++) {
                                if (!ptypes[i].equals(parameterTypes[i])) {
                                    break;
                                }
                            }
                            return itdm;
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeMethodDeclaration[] getITDMethods() {
        if (this.itdMethods == null) {
            List<InterTypeMethodDeclaration> itdms = new ArrayList<>();
            Method[] baseMethods = this.clazz.getDeclaredMethods();
            for (Method m : baseMethods) {
                if (m.getName().contains("ajc$interMethod$") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(ann.modifiers())) {
                        InterTypeMethodDeclaration itdm = new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m);
                        itdms.add(itdm);
                    }
                }
            }
            addAnnotationStyleITDMethods(itdms, true);
            InterTypeMethodDeclaration[] interTypeMethodDeclarationArr = new InterTypeMethodDeclaration[itdms.size()];
            this.itdMethods = interTypeMethodDeclarationArr;
            itdms.toArray(interTypeMethodDeclarationArr);
        }
        return this.itdMethods;
    }

    private void addAnnotationStyleITDMethods(List<InterTypeMethodDeclaration> toList, boolean publicOnly) {
        if (isAspect()) {
            Field[] arr$ = this.clazz.getDeclaredFields();
            for (Field f : arr$) {
                if (f.getType().isInterface() && f.isAnnotationPresent(DeclareParents.class)) {
                    DeclareParents decPAnn = (DeclareParents) f.getAnnotation(DeclareParents.class);
                    if (decPAnn.defaultImpl() != DeclareParents.class) {
                        Method[] arr$2 = f.getType().getDeclaredMethods();
                        for (Method itdM : arr$2) {
                            if (Modifier.isPublic(itdM.getModifiers()) || !publicOnly) {
                                InterTypeMethodDeclaration itdm = new InterTypeMethodDeclarationImpl(this, AjTypeSystem.getAjType(f.getType()), itdM, 1);
                                toList.add(itdm);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addAnnotationStyleITDFields(List<InterTypeFieldDeclaration> toList, boolean publicOnly) {
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        InterTypeConstructorDeclaration[] itdcs = getDeclaredITDConstructors();
        for (InterTypeConstructorDeclaration itdc : itdcs) {
            try {
                AjType<?> itdTarget = itdc.getTargetType();
                if (itdTarget.equals(target)) {
                    AjType<?>[] ptypes = itdc.getParameterTypes();
                    if (ptypes.length == parameterTypes.length) {
                        for (int i = 0; i < ptypes.length; i++) {
                            if (!ptypes[i].equals(parameterTypes[i])) {
                                break;
                            }
                        }
                        return itdc;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeConstructorDeclaration[] getDeclaredITDConstructors() {
        if (this.declaredITDCons == null) {
            List<InterTypeConstructorDeclaration> itdcs = new ArrayList<>();
            Method[] baseMethods = this.clazz.getDeclaredMethods();
            for (Method m : baseMethods) {
                if (m.getName().contains("ajc$postInterConstructor") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    InterTypeConstructorDeclaration itdc = new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m);
                    itdcs.add(itdc);
                }
            }
            InterTypeConstructorDeclaration[] interTypeConstructorDeclarationArr = new InterTypeConstructorDeclaration[itdcs.size()];
            this.declaredITDCons = interTypeConstructorDeclarationArr;
            itdcs.toArray(interTypeConstructorDeclarationArr);
        }
        return this.declaredITDCons;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeConstructorDeclaration getITDConstructor(AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        InterTypeConstructorDeclaration[] itdcs = getITDConstructors();
        for (InterTypeConstructorDeclaration itdc : itdcs) {
            try {
                AjType<?> itdTarget = itdc.getTargetType();
                if (itdTarget.equals(target)) {
                    AjType<?>[] ptypes = itdc.getParameterTypes();
                    if (ptypes.length == parameterTypes.length) {
                        for (int i = 0; i < ptypes.length; i++) {
                            if (!ptypes[i].equals(parameterTypes[i])) {
                                break;
                            }
                        }
                        return itdc;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeConstructorDeclaration[] getITDConstructors() {
        if (this.itdCons == null) {
            List<InterTypeConstructorDeclaration> itdcs = new ArrayList<>();
            Method[] baseMethods = this.clazz.getMethods();
            for (Method m : baseMethods) {
                if (m.getName().contains("ajc$postInterConstructor") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(ann.modifiers())) {
                        InterTypeConstructorDeclaration itdc = new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m);
                        itdcs.add(itdc);
                    }
                }
            }
            InterTypeConstructorDeclaration[] interTypeConstructorDeclarationArr = new InterTypeConstructorDeclaration[itdcs.size()];
            this.itdCons = interTypeConstructorDeclarationArr;
            itdcs.toArray(interTypeConstructorDeclarationArr);
        }
        return this.itdCons;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeFieldDeclaration getDeclaredITDField(String name, AjType<?> target) throws NoSuchFieldException {
        InterTypeFieldDeclaration[] itdfs = getDeclaredITDFields();
        for (InterTypeFieldDeclaration itdf : itdfs) {
            if (itdf.getName().equals(name)) {
                try {
                    AjType<?> itdTarget = itdf.getTargetType();
                    if (itdTarget.equals(target)) {
                        return itdf;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeFieldDeclaration[] getDeclaredITDFields() {
        List<InterTypeFieldDeclaration> itdfs = new ArrayList<>();
        if (this.declaredITDFields == null) {
            Method[] baseMethods = this.clazz.getDeclaredMethods();
            for (Method m : baseMethods) {
                if (m.isAnnotationPresent(ajcITD.class) && m.getName().contains("ajc$interFieldInit")) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    String interFieldInitMethodName = m.getName();
                    String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                    try {
                        Method dispatch = this.clazz.getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                        try {
                            InterTypeFieldDeclaration itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                            itdfs.add(itdf);
                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                        }
                    } catch (NoSuchMethodException e2) {
                    }
                }
            }
            addAnnotationStyleITDFields(itdfs, false);
            InterTypeFieldDeclaration[] interTypeFieldDeclarationArr = new InterTypeFieldDeclaration[itdfs.size()];
            this.declaredITDFields = interTypeFieldDeclarationArr;
            itdfs.toArray(interTypeFieldDeclarationArr);
        }
        return this.declaredITDFields;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeFieldDeclaration getITDField(String name, AjType<?> target) throws NoSuchFieldException {
        InterTypeFieldDeclaration[] itdfs = getITDFields();
        for (InterTypeFieldDeclaration itdf : itdfs) {
            if (itdf.getName().equals(name)) {
                try {
                    AjType<?> itdTarget = itdf.getTargetType();
                    if (itdTarget.equals(target)) {
                        return itdf;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public InterTypeFieldDeclaration[] getITDFields() {
        List<InterTypeFieldDeclaration> itdfs = new ArrayList<>();
        if (this.itdFields == null) {
            Method[] baseMethods = this.clazz.getMethods();
            for (Method m : baseMethods) {
                if (m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (m.getName().contains("ajc$interFieldInit") && Modifier.isPublic(ann.modifiers())) {
                        String interFieldInitMethodName = m.getName();
                        String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                        try {
                            Method dispatch = m.getDeclaringClass().getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                            try {
                                InterTypeFieldDeclaration itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                                itdfs.add(itdf);
                            } catch (NoSuchMethodException e) {
                                throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                            }
                        } catch (NoSuchMethodException e2) {
                        }
                    }
                }
            }
            addAnnotationStyleITDFields(itdfs, true);
            InterTypeFieldDeclaration[] interTypeFieldDeclarationArr = new InterTypeFieldDeclaration[itdfs.size()];
            this.itdFields = interTypeFieldDeclarationArr;
            itdfs.toArray(interTypeFieldDeclarationArr);
        }
        return this.itdFields;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public DeclareErrorOrWarning[] getDeclareErrorOrWarnings() {
        List<DeclareErrorOrWarning> deows = new ArrayList<>();
        Field[] arr$ = this.clazz.getDeclaredFields();
        for (Field field : arr$) {
            try {
                if (field.isAnnotationPresent(DeclareWarning.class)) {
                    DeclareWarning dw = (DeclareWarning) field.getAnnotation(DeclareWarning.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        String message = (String) field.get(null);
                        DeclareErrorOrWarningImpl deow = new DeclareErrorOrWarningImpl(dw.value(), message, false, this);
                        deows.add(deow);
                    }
                } else if (field.isAnnotationPresent(DeclareError.class)) {
                    DeclareError de = (DeclareError) field.getAnnotation(DeclareError.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        String message2 = (String) field.get(null);
                        DeclareErrorOrWarningImpl deow2 = new DeclareErrorOrWarningImpl(de.value(), message2, true, this);
                        deows.add(deow2);
                    }
                }
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e2) {
            }
        }
        Method[] arr$2 = this.clazz.getDeclaredMethods();
        for (Method method : arr$2) {
            if (method.isAnnotationPresent(ajcDeclareEoW.class)) {
                ajcDeclareEoW deowAnn = (ajcDeclareEoW) method.getAnnotation(ajcDeclareEoW.class);
                DeclareErrorOrWarning deow3 = new DeclareErrorOrWarningImpl(deowAnn.pointcut(), deowAnn.message(), deowAnn.isError(), this);
                deows.add(deow3);
            }
        }
        DeclareErrorOrWarning[] ret = new DeclareErrorOrWarning[deows.size()];
        deows.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public org.aspectj.lang.reflect.DeclareParents[] getDeclareParents() {
        List<org.aspectj.lang.reflect.DeclareParents> decps = new ArrayList<>();
        Method[] arr$ = this.clazz.getDeclaredMethods();
        for (Method method : arr$) {
            if (method.isAnnotationPresent(ajcDeclareParents.class)) {
                ajcDeclareParents decPAnn = (ajcDeclareParents) method.getAnnotation(ajcDeclareParents.class);
                DeclareParentsImpl decp = new DeclareParentsImpl(decPAnn.targetTypePattern(), decPAnn.parentTypes(), decPAnn.isExtends(), this);
                decps.add(decp);
            }
        }
        addAnnotationStyleDeclareParents(decps);
        if (getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(getSupertype().getDeclareParents()));
        }
        org.aspectj.lang.reflect.DeclareParents[] ret = new org.aspectj.lang.reflect.DeclareParents[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    private void addAnnotationStyleDeclareParents(List<org.aspectj.lang.reflect.DeclareParents> toList) {
        Field[] arr$ = this.clazz.getDeclaredFields();
        for (Field f : arr$) {
            if (f.isAnnotationPresent(DeclareParents.class) && f.getType().isInterface()) {
                DeclareParents ann = (DeclareParents) f.getAnnotation(DeclareParents.class);
                String parentType = f.getType().getName();
                DeclareParentsImpl decp = new DeclareParentsImpl(ann.value(), parentType, false, this);
                toList.add(decp);
            }
        }
    }

    @Override // org.aspectj.lang.reflect.AjType
    public DeclareSoft[] getDeclareSofts() {
        List<DeclareSoft> decs = new ArrayList<>();
        Method[] arr$ = this.clazz.getDeclaredMethods();
        for (Method method : arr$) {
            if (method.isAnnotationPresent(ajcDeclareSoft.class)) {
                ajcDeclareSoft decSAnn = (ajcDeclareSoft) method.getAnnotation(ajcDeclareSoft.class);
                DeclareSoftImpl ds = new DeclareSoftImpl(this, decSAnn.pointcut(), decSAnn.exceptionType());
                decs.add(ds);
            }
        }
        if (getSupertype().isAspect()) {
            decs.addAll(Arrays.asList(getSupertype().getDeclareSofts()));
        }
        DeclareSoft[] ret = new DeclareSoft[decs.size()];
        decs.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public DeclareAnnotation[] getDeclareAnnotations() {
        Annotation targetAnnotation;
        List<DeclareAnnotation> decAs = new ArrayList<>();
        Method[] arr$ = this.clazz.getDeclaredMethods();
        for (Method method : arr$) {
            if (method.isAnnotationPresent(ajcDeclareAnnotation.class)) {
                ajcDeclareAnnotation decAnn = (ajcDeclareAnnotation) method.getAnnotation(ajcDeclareAnnotation.class);
                Annotation[] anns = method.getAnnotations();
                int len$ = anns.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        targetAnnotation = null;
                        break;
                    }
                    Annotation ann = anns[i$];
                    if (ann.annotationType() == ajcDeclareAnnotation.class) {
                        i$++;
                    } else {
                        targetAnnotation = ann;
                        break;
                    }
                }
                DeclareAnnotationImpl da = new DeclareAnnotationImpl(this, decAnn.kind(), decAnn.pattern(), targetAnnotation, decAnn.annotation());
                decAs.add(da);
            }
        }
        if (getSupertype().isAspect()) {
            decAs.addAll(Arrays.asList(getSupertype().getDeclareAnnotations()));
        }
        DeclareAnnotation[] ret = new DeclareAnnotation[decAs.size()];
        decAs.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public DeclarePrecedence[] getDeclarePrecedence() {
        List<DeclarePrecedence> decps = new ArrayList<>();
        if (this.clazz.isAnnotationPresent(org.aspectj.lang.annotation.DeclarePrecedence.class)) {
            org.aspectj.lang.annotation.DeclarePrecedence ann = (org.aspectj.lang.annotation.DeclarePrecedence) this.clazz.getAnnotation(org.aspectj.lang.annotation.DeclarePrecedence.class);
            DeclarePrecedenceImpl decp = new DeclarePrecedenceImpl(ann.value(), this);
            decps.add(decp);
        }
        Method[] arr$ = this.clazz.getDeclaredMethods();
        for (Method method : arr$) {
            if (method.isAnnotationPresent(ajcDeclarePrecedence.class)) {
                ajcDeclarePrecedence decPAnn = (ajcDeclarePrecedence) method.getAnnotation(ajcDeclarePrecedence.class);
                DeclarePrecedenceImpl decp2 = new DeclarePrecedenceImpl(decPAnn.value(), this);
                decps.add(decp2);
            }
        }
        if (getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(getSupertype().getDeclarePrecedence()));
        }
        DeclarePrecedence[] ret = new DeclarePrecedence[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public T[] getEnumConstants() {
        return this.clazz.getEnumConstants();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public TypeVariable<Class<T>>[] getTypeParameters() {
        return this.clazz.getTypeParameters();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isEnum() {
        return this.clazz.isEnum();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isInstance(Object o) {
        return this.clazz.isInstance(o);
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isLocalClass() {
        return this.clazz.isLocalClass() && !isAspect();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isMemberClass() {
        return this.clazz.isMemberClass() && !isAspect();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isArray() {
        return this.clazz.isArray();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isPrimitive() {
        return this.clazz.isPrimitive();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isAspect() {
        return this.clazz.getAnnotation(Aspect.class) != null;
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isMemberAspect() {
        return this.clazz.isMemberClass() && isAspect();
    }

    @Override // org.aspectj.lang.reflect.AjType
    public boolean isPrivileged() {
        return isAspect() && this.clazz.isAnnotationPresent(ajcPrivileged.class);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AjTypeImpl)) {
            return false;
        }
        AjTypeImpl other = (AjTypeImpl) obj;
        return other.clazz.equals(this.clazz);
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    private AjType<?>[] toAjTypeArray(Class<?>[] classes) {
        AjType<?>[] ajtypes = new AjType[classes.length];
        for (int i = 0; i < ajtypes.length; i++) {
            ajtypes[i] = AjTypeSystem.getAjType(classes[i]);
        }
        return ajtypes;
    }

    private Class<?>[] toClassArray(AjType<?>[] ajTypes) {
        Class<?>[] classes = new Class[ajTypes.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = ajTypes[i].getJavaClass();
        }
        return classes;
    }

    public String toString() {
        return getName();
    }
}
