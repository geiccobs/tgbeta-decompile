package j$.util.stream;

import java.util.EnumMap;
import java.util.Map;
/* JADX WARN: Init of enum DISTINCT can be incorrect */
/* JADX WARN: Init of enum ORDERED can be incorrect */
/* JADX WARN: Init of enum SHORT_CIRCUIT can be incorrect */
/* JADX WARN: Init of enum SIZED can be incorrect */
/* JADX WARN: Init of enum SORTED can be incorrect */
/* renamed from: j$.util.stream.d4 */
/* loaded from: classes2.dex */
public enum EnumC0077d4 {
    DISTINCT(0, r2),
    SORTED(1, r5),
    ORDERED(2, r7),
    SIZED(3, r11),
    SHORT_CIRCUIT(12, r13);
    
    static final int f;
    static final int g;
    static final int h;
    private static final int i;
    private static final int j;
    private static final int k;
    static final int l;
    static final int m;
    static final int n;
    static final int o;
    static final int p;
    static final int q;
    static final int r;
    static final int s;
    static final int t;
    static final int u;
    private final Map a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    static {
        EnumC0071c4 enumC0071c4 = EnumC0071c4.SPLITERATOR;
        C0065b4 f2 = f(enumC0071c4);
        EnumC0071c4 enumC0071c42 = EnumC0071c4.STREAM;
        f2.b(enumC0071c42);
        EnumC0071c4 enumC0071c43 = EnumC0071c4.OP;
        f2.c(enumC0071c43);
        EnumC0077d4 enumC0077d4 = DISTINCT;
        C0065b4 f3 = f(enumC0071c4);
        f3.b(enumC0071c42);
        f3.c(enumC0071c43);
        EnumC0077d4 enumC0077d42 = SORTED;
        C0065b4 f4 = f(enumC0071c4);
        f4.b(enumC0071c42);
        f4.c(enumC0071c43);
        EnumC0071c4 enumC0071c44 = EnumC0071c4.TERMINAL_OP;
        f4.a(enumC0071c44);
        EnumC0071c4 enumC0071c45 = EnumC0071c4.UPSTREAM_TERMINAL_OP;
        f4.a(enumC0071c45);
        EnumC0077d4 enumC0077d43 = ORDERED;
        C0065b4 f5 = f(enumC0071c4);
        f5.b(enumC0071c42);
        f5.a(enumC0071c43);
        EnumC0077d4 enumC0077d44 = SIZED;
        f(enumC0071c43).b(enumC0071c44);
        EnumC0077d4 enumC0077d45 = SHORT_CIRCUIT;
        f = b(enumC0071c4);
        int b = b(enumC0071c42);
        g = b;
        h = b(enumC0071c43);
        b(enumC0071c44);
        b(enumC0071c45);
        int i2 = 0;
        for (EnumC0077d4 enumC0077d46 : values()) {
            i2 |= enumC0077d46.e;
        }
        i = i2;
        j = b;
        int i3 = b << 1;
        k = i3;
        l = b | i3;
        m = enumC0077d4.c;
        n = enumC0077d4.d;
        o = enumC0077d42.c;
        p = enumC0077d42.d;
        q = enumC0077d43.c;
        r = enumC0077d43.d;
        s = enumC0077d44.c;
        t = enumC0077d44.d;
        u = enumC0077d45.c;
    }

    EnumC0077d4(int i2, C0065b4 c0065b4) {
        EnumC0071c4[] values;
        for (EnumC0071c4 enumC0071c4 : EnumC0071c4.values()) {
            Map map = c0065b4.a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(enumC0071c4, 0);
            } else {
                map.get(enumC0071c4);
            }
        }
        this.a = c0065b4.a;
        int i3 = i2 * 2;
        this.b = i3;
        this.c = 1 << i3;
        this.d = 2 << i3;
        this.e = 3 << i3;
    }

    public static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? i : ((((j & i2) << 1) | i2) | ((k & i2) >> 1)) ^ (-1)));
    }

    private static int b(EnumC0071c4 enumC0071c4) {
        EnumC0077d4[] values;
        int i2 = 0;
        for (EnumC0077d4 enumC0077d4 : values()) {
            i2 |= ((Integer) enumC0077d4.a.get(enumC0071c4)).intValue() << enumC0077d4.b;
        }
        return i2;
    }

    public static int c(j$.util.u uVar) {
        int characteristics = uVar.characteristics();
        return ((characteristics & 4) == 0 || uVar.getComparator() == null) ? f & characteristics : f & characteristics & (-5);
    }

    private static C0065b4 f(EnumC0071c4 enumC0071c4) {
        EnumMap enumMap = new EnumMap(EnumC0071c4.class);
        C0065b4 c0065b4 = new C0065b4(enumMap);
        enumMap.put((EnumMap) enumC0071c4, (EnumC0071c4) 1);
        return c0065b4;
    }

    public static int g(int i2) {
        return i2 & ((i2 ^ (-1)) >> 1) & j;
    }

    public boolean d(int i2) {
        return (i2 & this.e) == this.c;
    }

    public boolean e(int i2) {
        int i3 = this.e;
        return (i2 & i3) == i3;
    }
}
