package j$.time;

import j$.time.zone.ZoneRules;
import j$.time.zone.ZoneRulesException;
import j$.time.zone.ZoneRulesProvider;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class ZoneRegion extends ZoneId implements Serializable {
    private static final long serialVersionUID = 8386373296231747096L;
    private final String id;
    private final transient ZoneRules rules;

    public static ZoneRegion ofId(String zoneId, boolean checkAvailable) {
        Objects.requireNonNull(zoneId, "zoneId");
        checkName(zoneId);
        ZoneRules rules = null;
        try {
            rules = ZoneRulesProvider.getRules(zoneId, true);
        } catch (ZoneRulesException ex) {
            if (checkAvailable) {
                throw ex;
            }
        }
        return new ZoneRegion(zoneId, rules);
    }

    private static void checkName(String zoneId) {
        int n = zoneId.length();
        if (n < 2) {
            throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + zoneId);
        }
        for (int i = 0; i < n; i++) {
            char c = zoneId.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && ((c != '/' || i == 0) && ((c < '0' || c > '9' || i == 0) && ((c != '~' || i == 0) && ((c != '.' || i == 0) && ((c != '_' || i == 0) && ((c != '+' || i == 0) && (c != '-' || i == 0))))))))) {
                throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + zoneId);
            }
        }
    }

    public ZoneRegion(String id, ZoneRules rules) {
        this.id = id;
        this.rules = rules;
    }

    @Override // j$.time.ZoneId
    public String getId() {
        return this.id;
    }

    @Override // j$.time.ZoneId
    public ZoneRules getRules() {
        ZoneRules zoneRules = this.rules;
        return zoneRules != null ? zoneRules : ZoneRulesProvider.getRules(this.id, false);
    }

    private Object writeReplace() {
        return new Ser((byte) 7, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    @Override // j$.time.ZoneId
    public void write(DataOutput out) {
        out.writeByte(7);
        writeExternal(out);
    }

    public void writeExternal(DataOutput out) {
        out.writeUTF(this.id);
    }

    public static ZoneId readExternal(DataInput in) {
        String id = in.readUTF();
        return ZoneId.of(id, false);
    }
}
