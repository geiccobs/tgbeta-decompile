package j$.time.zone;

import j$.util.concurrent.ConcurrentHashMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
/* loaded from: classes2.dex */
final class TzdbZoneRulesProvider extends ZoneRulesProvider {
    private List<String> regionIds;
    private final Map<String, Object> regionToRules = new ConcurrentHashMap();
    private String versionId;

    public TzdbZoneRulesProvider() {
        try {
            URL datUrl = TzdbZoneRulesProvider.class.getClassLoader().getResource(System.getProperty("jre.tzdb.dat", "j$/time/zone/tzdb.dat"));
            DataInputStream dis = new DataInputStream(new BufferedInputStream(datUrl.openStream()));
            load(dis);
        } catch (Exception ex) {
            throw new ZoneRulesException("Unable to load TZDB time-zone rules", ex);
        }
    }

    @Override // j$.time.zone.ZoneRulesProvider
    protected Set<String> provideZoneIds() {
        return new HashSet(this.regionIds);
    }

    @Override // j$.time.zone.ZoneRulesProvider
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        Object obj = this.regionToRules.get(zoneId);
        if (obj == null) {
            throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
        }
        try {
            if (obj instanceof byte[]) {
                byte[] bytes = (byte[]) obj;
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                obj = Ser.read(dis);
                this.regionToRules.put(zoneId, obj);
            }
            return (ZoneRules) obj;
        } catch (Exception ex) {
            throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + zoneId + ", version: " + this.versionId, ex);
        }
    }

    @Override // j$.time.zone.ZoneRulesProvider
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        TreeMap<String, java.time.zone.ZoneRules> map = new TreeMap<>();
        ZoneRules rules = getRules(zoneId, false);
        if (rules != null) {
            map.put(this.versionId, rules);
        }
        return map;
    }

    private void load(DataInputStream dis) {
        if (dis.readByte() != 1) {
            throw new StreamCorruptedException("File format not recognised");
        }
        String groupId = dis.readUTF();
        if (!"TZDB".equals(groupId)) {
            throw new StreamCorruptedException("File format not recognised");
        }
        int versionCount = dis.readShort();
        for (int i = 0; i < versionCount; i++) {
            this.versionId = dis.readUTF();
        }
        int regionCount = dis.readShort();
        String[] regionArray = new String[regionCount];
        for (int i2 = 0; i2 < regionCount; i2++) {
            regionArray[i2] = dis.readUTF();
        }
        this.regionIds = Arrays.asList(regionArray);
        int ruleCount = dis.readShort();
        Object[] ruleArray = new Object[ruleCount];
        for (int i3 = 0; i3 < ruleCount; i3++) {
            byte[] bytes = new byte[dis.readShort()];
            dis.readFully(bytes);
            ruleArray[i3] = bytes;
        }
        for (int i4 = 0; i4 < versionCount; i4++) {
            int versionRegionCount = dis.readShort();
            this.regionToRules.clear();
            for (int j = 0; j < versionRegionCount; j++) {
                String region = regionArray[dis.readShort()];
                Object rule = ruleArray[dis.readShort() & 65535];
                this.regionToRules.put(region, rule);
            }
        }
    }

    public String toString() {
        return "TZDB[" + this.versionId + "]";
    }
}
