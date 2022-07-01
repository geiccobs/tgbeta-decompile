package j$.time.format;
/* loaded from: classes2.dex */
final class m implements g {
    private final String a;

    public m(String str) {
        this.a = str;
    }

    public String toString() {
        String replace = this.a.replace("'", "''");
        return "'" + replace + "'";
    }
}
