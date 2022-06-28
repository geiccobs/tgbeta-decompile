package org.telegram.PhoneFormat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
/* loaded from: classes3.dex */
public class PhoneFormat {
    private static volatile PhoneFormat Instance = null;
    public ByteBuffer buffer;
    public HashMap<String, ArrayList<String>> callingCodeCountries;
    public HashMap<String, CallingCodeInfo> callingCodeData;
    public HashMap<String, Integer> callingCodeOffsets;
    public HashMap<String, String> countryCallingCode;
    public byte[] data;
    public String defaultCallingCode;
    public String defaultCountry;
    private boolean initialzed = false;

    public static PhoneFormat getInstance() {
        PhoneFormat localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhoneFormat.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    PhoneFormat phoneFormat = new PhoneFormat();
                    localInstance = phoneFormat;
                    Instance = phoneFormat;
                }
            }
        }
        return localInstance;
    }

    public static String strip(String str) {
        StringBuilder res = new StringBuilder(str);
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!"0123456789+*#".contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str, boolean includePlus) {
        if (str == null) {
            return null;
        }
        StringBuilder res = new StringBuilder(str);
        String phoneChars = "0123456789";
        if (includePlus) {
            phoneChars = phoneChars + "+";
        }
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public PhoneFormat() {
        init(null);
    }

    public PhoneFormat(String countryCode) {
        init(countryCode);
    }

    public void init(String countryCode) {
        InputStream stream = null;
        ByteArrayOutputStream bos = null;
        try {
            try {
                stream = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
                bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                while (true) {
                    int len = stream.read(buf, 0, 1024);
                    if (len == -1) {
                        break;
                    }
                    bos.write(buf, 0, len);
                }
                byte[] byteArray = bos.toByteArray();
                this.data = byteArray;
                ByteBuffer wrap = ByteBuffer.wrap(byteArray);
                this.buffer = wrap;
                wrap.order(ByteOrder.LITTLE_ENDIAN);
                try {
                    bos.close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (countryCode != null && countryCode.length() != 0) {
                    this.defaultCountry = countryCode;
                } else {
                    Locale loc = Locale.getDefault();
                    this.defaultCountry = loc.getCountry().toLowerCase();
                }
                this.callingCodeOffsets = new HashMap<>(255);
                this.callingCodeCountries = new HashMap<>(255);
                this.callingCodeData = new HashMap<>(10);
                this.countryCallingCode = new HashMap<>(255);
                parseDataHeader();
                this.initialzed = true;
            } catch (Exception e3) {
                e3.printStackTrace();
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (Exception e4) {
                        FileLog.e(e4);
                    }
                }
                if (stream == null) {
                    return;
                }
                try {
                    stream.close();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
        } catch (Throwable th) {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e6) {
                    FileLog.e(e6);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e7) {
                    FileLog.e(e7);
                }
            }
            throw th;
        }
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public String callingCodeForCountryCode(String countryCode) {
        return this.countryCallingCode.get(countryCode.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String callingCode) {
        if (callingCode.startsWith("+")) {
            callingCode = callingCode.substring(1);
        }
        return this.callingCodeCountries.get(callingCode);
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo res = null;
        for (int i = 0; i < 3 && i < str.length() && (res = callingCodeInfo(str.substring(0, i + 1))) == null; i++) {
        }
        return res;
    }

    public String format(String orig) {
        if (!this.initialzed) {
            return orig;
        }
        try {
            String str = strip(orig);
            if (str.startsWith("+")) {
                String rest = str.substring(1);
                CallingCodeInfo info = findCallingCodeInfo(rest);
                if (info != null) {
                    String phone = info.format(rest);
                    return "+" + phone;
                }
                return orig;
            }
            CallingCodeInfo info2 = callingCodeInfo(this.defaultCallingCode);
            if (info2 == null) {
                return orig;
            }
            String accessCode = info2.matchingAccessCode(str);
            if (accessCode != null) {
                String rest2 = str.substring(accessCode.length());
                String phone2 = rest2;
                CallingCodeInfo info22 = findCallingCodeInfo(rest2);
                if (info22 != null) {
                    phone2 = info22.format(rest2);
                }
                if (phone2.length() == 0) {
                    return accessCode;
                }
                return String.format("%s %s", accessCode, phone2);
            }
            return info2.format(str);
        } catch (Exception e) {
            FileLog.e(e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        CallingCodeInfo info2;
        if (!this.initialzed) {
            return true;
        }
        String str = strip(phoneNumber);
        if (str.startsWith("+")) {
            String rest = str.substring(1);
            CallingCodeInfo info = findCallingCodeInfo(rest);
            return info != null && info.isValidPhoneNumber(rest);
        }
        CallingCodeInfo info3 = callingCodeInfo(this.defaultCallingCode);
        if (info3 == null) {
            return false;
        }
        String accessCode = info3.matchingAccessCode(str);
        if (accessCode != null) {
            String rest2 = str.substring(accessCode.length());
            return (rest2.length() == 0 || (info2 = findCallingCodeInfo(rest2)) == null || !info2.isValidPhoneNumber(rest2)) ? false : true;
        }
        return info3.isValidPhoneNumber(str);
    }

    int value32(int offset) {
        if (offset + 4 <= this.data.length) {
            this.buffer.position(offset);
            return this.buffer.getInt();
        }
        return 0;
    }

    short value16(int offset) {
        if (offset + 2 <= this.data.length) {
            this.buffer.position(offset);
            return this.buffer.getShort();
        }
        return (short) 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0018, code lost:
        return new java.lang.String(r2, r6, r1 - r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x000e, code lost:
        if (r6 != (r1 - r6)) goto L10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0010, code lost:
        return "";
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String valueString(int r6) {
        /*
            r5 = this;
            java.lang.String r0 = ""
            r1 = r6
        L3:
            byte[] r2 = r5.data     // Catch: java.lang.Exception -> L1d
            int r3 = r2.length     // Catch: java.lang.Exception -> L1d
            if (r1 >= r3) goto L1c
            r3 = r2[r1]     // Catch: java.lang.Exception -> L1d
            if (r3 != 0) goto L19
            int r3 = r1 - r6
            if (r6 != r3) goto L11
            return r0
        L11:
            java.lang.String r3 = new java.lang.String     // Catch: java.lang.Exception -> L1d
            int r4 = r1 - r6
            r3.<init>(r2, r6, r4)     // Catch: java.lang.Exception -> L1d
            return r3
        L19:
            int r1 = r1 + 1
            goto L3
        L1c:
            return r0
        L1d:
            r1 = move-exception
            r1.printStackTrace()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.PhoneFormat.PhoneFormat.valueString(int):java.lang.String");
    }

    public CallingCodeInfo callingCodeInfo(String callingCode) {
        Integer num;
        int block1Len;
        int start;
        int offset;
        boolean z;
        PhoneFormat phoneFormat = this;
        CallingCodeInfo res = phoneFormat.callingCodeData.get(callingCode);
        if (res == null && (num = phoneFormat.callingCodeOffsets.get(callingCode)) != null) {
            byte[] bytes = phoneFormat.data;
            int start2 = num.intValue();
            res = new CallingCodeInfo();
            res.callingCode = callingCode;
            res.countries = phoneFormat.callingCodeCountries.get(callingCode);
            phoneFormat.callingCodeData.put(callingCode, res);
            int block1Len2 = phoneFormat.value16(start2);
            int offset2 = start2 + 2 + 2;
            int block2Len = phoneFormat.value16(offset2);
            int offset3 = offset2 + 2 + 2;
            int setCnt = phoneFormat.value16(offset3);
            int offset4 = offset3 + 2 + 2;
            ArrayList<String> strs = new ArrayList<>(5);
            while (true) {
                String str = phoneFormat.valueString(offset4);
                if (str.length() == 0) {
                    break;
                }
                strs.add(str);
                offset4 += str.length() + 1;
            }
            res.trunkPrefixes = strs;
            int offset5 = offset4 + 1;
            ArrayList<String> strs2 = new ArrayList<>(5);
            while (true) {
                String str2 = phoneFormat.valueString(offset5);
                if (str2.length() == 0) {
                    break;
                }
                strs2.add(str2);
                offset5 += str2.length() + 1;
            }
            res.intlPrefixes = strs2;
            ArrayList<RuleSet> ruleSets = new ArrayList<>(setCnt);
            int offset6 = start2 + block1Len2;
            int s = 0;
            while (s < setCnt) {
                RuleSet ruleSet = new RuleSet();
                ruleSet.matchLen = phoneFormat.value16(offset6);
                int offset7 = offset6 + 2;
                int ruleCnt = phoneFormat.value16(offset7);
                offset6 = offset7 + 2;
                ArrayList<PhoneRule> rules = new ArrayList<>(ruleCnt);
                Integer num2 = num;
                int r = 0;
                while (r < ruleCnt) {
                    PhoneRule rule = new PhoneRule();
                    int setCnt2 = setCnt;
                    ArrayList<String> strs3 = strs2;
                    rule.minVal = phoneFormat.value32(offset6);
                    int offset8 = offset6 + 4;
                    rule.maxVal = phoneFormat.value32(offset8);
                    int offset9 = offset8 + 4;
                    int offset10 = offset9 + 1;
                    rule.byte8 = bytes[offset9];
                    int offset11 = offset10 + 1;
                    rule.maxLen = bytes[offset10];
                    int offset12 = offset11 + 1;
                    rule.otherFlag = bytes[offset11];
                    int offset13 = offset12 + 1;
                    rule.prefixLen = bytes[offset12];
                    int offset14 = offset13 + 1;
                    rule.flag12 = bytes[offset13];
                    int offset15 = offset14 + 1;
                    rule.flag13 = bytes[offset14];
                    int strOffset = phoneFormat.value16(offset15);
                    int offset16 = offset15 + 2;
                    byte[] bytes2 = bytes;
                    rule.format = phoneFormat.valueString(start2 + block1Len2 + block2Len + strOffset);
                    int openPos = rule.format.indexOf("[[");
                    if (openPos == -1) {
                        start = start2;
                        offset = offset16;
                        block1Len = block1Len2;
                    } else {
                        start = start2;
                        int closePos = rule.format.indexOf("]]");
                        offset = offset16;
                        block1Len = block1Len2;
                        rule.format = String.format("%s%s", rule.format.substring(0, openPos), rule.format.substring(closePos + 2));
                    }
                    rules.add(rule);
                    if (!rule.hasIntlPrefix) {
                        z = true;
                    } else {
                        z = true;
                        ruleSet.hasRuleWithIntlPrefix = true;
                    }
                    if (rule.hasTrunkPrefix) {
                        ruleSet.hasRuleWithTrunkPrefix = z;
                    }
                    r++;
                    phoneFormat = this;
                    offset6 = offset;
                    setCnt = setCnt2;
                    start2 = start;
                    strs2 = strs3;
                    bytes = bytes2;
                    block1Len2 = block1Len;
                }
                ruleSet.rules = rules;
                ruleSets.add(ruleSet);
                s++;
                phoneFormat = this;
                num = num2;
                bytes = bytes;
            }
            res.ruleSets = ruleSets;
        }
        return res;
    }

    public void parseDataHeader() {
        int count = value32(0);
        int base = (count * 12) + 4;
        int spot = 4;
        for (int i = 0; i < count; i++) {
            String callingCode = valueString(spot);
            int spot2 = spot + 4;
            String country = valueString(spot2);
            int spot3 = spot2 + 4;
            int offset = value32(spot3) + base;
            spot = spot3 + 4;
            if (country.equals(this.defaultCountry)) {
                this.defaultCallingCode = callingCode;
            }
            this.countryCallingCode.put(country, callingCode);
            this.callingCodeOffsets.put(callingCode, Integer.valueOf(offset));
            ArrayList<String> countries = this.callingCodeCountries.get(callingCode);
            if (countries == null) {
                countries = new ArrayList<>();
                this.callingCodeCountries.put(callingCode, countries);
            }
            countries.add(country);
        }
        String str = this.defaultCallingCode;
        if (str != null) {
            callingCodeInfo(str);
        }
    }
}
