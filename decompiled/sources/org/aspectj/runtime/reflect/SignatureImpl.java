package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;
/* loaded from: classes3.dex */
abstract class SignatureImpl implements Signature {
    static final String INNER_SEP = ":";
    static final char SEP = '-';
    Class declaringType;
    String declaringTypeName;
    ClassLoader lookupClassLoader;
    int modifiers;
    String name;
    Cache stringCache;
    private String stringRep;
    private static boolean useCache = true;
    static String[] EMPTY_STRING_ARRAY = new String[0];
    static Class[] EMPTY_CLASS_ARRAY = new Class[0];

    /* loaded from: classes3.dex */
    public interface Cache {
        String get(int i);

        void set(int i, String str);
    }

    protected abstract String createToString(StringMaker stringMaker);

    public SignatureImpl(int modifiers, String name, Class declaringType) {
        this.modifiers = -1;
        this.lookupClassLoader = null;
        this.modifiers = modifiers;
        this.name = name;
        this.declaringType = declaringType;
    }

    public String toString(StringMaker sm) {
        String result = null;
        if (useCache) {
            Cache cache = this.stringCache;
            if (cache == null) {
                try {
                    this.stringCache = new CacheImpl();
                } catch (Throwable th) {
                    useCache = false;
                }
            } else {
                result = cache.get(sm.cacheOffset);
            }
        }
        if (result == null) {
            result = createToString(sm);
        }
        if (useCache) {
            this.stringCache.set(sm.cacheOffset, result);
        }
        return result;
    }

    @Override // org.aspectj.lang.Signature
    public final String toString() {
        return toString(StringMaker.middleStringMaker);
    }

    @Override // org.aspectj.lang.Signature
    public final String toShortString() {
        return toString(StringMaker.shortStringMaker);
    }

    @Override // org.aspectj.lang.Signature
    public final String toLongString() {
        return toString(StringMaker.longStringMaker);
    }

    @Override // org.aspectj.lang.Signature
    public int getModifiers() {
        if (this.modifiers == -1) {
            this.modifiers = extractInt(0);
        }
        return this.modifiers;
    }

    @Override // org.aspectj.lang.Signature
    public String getName() {
        if (this.name == null) {
            this.name = extractString(1);
        }
        return this.name;
    }

    @Override // org.aspectj.lang.Signature
    public Class getDeclaringType() {
        if (this.declaringType == null) {
            this.declaringType = extractType(2);
        }
        return this.declaringType;
    }

    @Override // org.aspectj.lang.Signature
    public String getDeclaringTypeName() {
        if (this.declaringTypeName == null) {
            this.declaringTypeName = getDeclaringType().getName();
        }
        return this.declaringTypeName;
    }

    String fullTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (!type.isArray()) {
            return type.getName().replace('$', '.');
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fullTypeName(type.getComponentType()));
        stringBuffer.append("[]");
        return stringBuffer.toString();
    }

    String stripPackageName(String name) {
        int dot = name.lastIndexOf(46);
        return dot == -1 ? name : name.substring(dot + 1);
    }

    String shortTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (!type.isArray()) {
            return stripPackageName(type.getName()).replace('$', '.');
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(shortTypeName(type.getComponentType()));
        stringBuffer.append("[]");
        return stringBuffer.toString();
    }

    void addFullTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(fullTypeName(types[i]));
        }
    }

    void addShortTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(shortTypeName(types[i]));
        }
    }

    void addTypeArray(StringBuffer buf, Class[] types) {
        addFullTypeNames(buf, types);
    }

    public void setLookupClassLoader(ClassLoader loader) {
        this.lookupClassLoader = loader;
    }

    private ClassLoader getLookupClassLoader() {
        if (this.lookupClassLoader == null) {
            this.lookupClassLoader = getClass().getClassLoader();
        }
        return this.lookupClassLoader;
    }

    public SignatureImpl(String stringRep) {
        this.modifiers = -1;
        this.lookupClassLoader = null;
        this.stringRep = stringRep;
    }

    public String extractString(int n) {
        int startIndex = 0;
        int endIndex = this.stringRep.indexOf(45);
        while (true) {
            int n2 = n - 1;
            if (n <= 0) {
                break;
            }
            startIndex = endIndex + 1;
            endIndex = this.stringRep.indexOf(45, startIndex);
            n = n2;
        }
        if (endIndex == -1) {
            endIndex = this.stringRep.length();
        }
        return this.stringRep.substring(startIndex, endIndex);
    }

    int extractInt(int n) {
        String s = extractString(n);
        return Integer.parseInt(s, 16);
    }

    public Class extractType(int n) {
        String s = extractString(n);
        return Factory.makeClass(s, getLookupClassLoader());
    }

    public String[] extractStrings(int n) {
        String s = extractString(n);
        StringTokenizer st = new StringTokenizer(s, ":");
        int N = st.countTokens();
        String[] ret = new String[N];
        for (int i = 0; i < N; i++) {
            ret[i] = st.nextToken();
        }
        return ret;
    }

    public Class[] extractTypes(int n) {
        String s = extractString(n);
        StringTokenizer st = new StringTokenizer(s, ":");
        int N = st.countTokens();
        Class[] ret = new Class[N];
        for (int i = 0; i < N; i++) {
            ret[i] = Factory.makeClass(st.nextToken(), getLookupClassLoader());
        }
        return ret;
    }

    static void setUseCache(boolean b) {
        useCache = b;
    }

    static boolean getUseCache() {
        return useCache;
    }

    /* loaded from: classes3.dex */
    public static final class CacheImpl implements Cache {
        private SoftReference toStringCacheRef;

        public CacheImpl() {
            makeCache();
        }

        @Override // org.aspectj.runtime.reflect.SignatureImpl.Cache
        public String get(int cacheOffset) {
            String[] cachedArray = array();
            if (cachedArray == null) {
                return null;
            }
            return cachedArray[cacheOffset];
        }

        @Override // org.aspectj.runtime.reflect.SignatureImpl.Cache
        public void set(int cacheOffset, String result) {
            String[] cachedArray = array();
            if (cachedArray == null) {
                cachedArray = makeCache();
            }
            cachedArray[cacheOffset] = result;
        }

        private String[] array() {
            return (String[]) this.toStringCacheRef.get();
        }

        private String[] makeCache() {
            String[] array = new String[3];
            this.toStringCacheRef = new SoftReference(array);
            return array;
        }
    }
}
