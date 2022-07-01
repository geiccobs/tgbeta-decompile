package com.stripe.android.model;

import com.stripe.android.util.DateUtils;
import com.stripe.android.util.StripeTextUtils;
/* loaded from: classes.dex */
public class Card {
    private String addressCity;
    private String addressCountry;
    private String addressLine1;
    private String addressLine2;
    private String addressState;
    private String addressZip;
    private String brand;
    private String currency;
    private String cvc;
    private Integer expMonth;
    private Integer expYear;
    private String last4;
    private String name;
    private String number;
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DISCOVER = {"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String[] PREFIXES_MASTERCARD = {"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55"};

    public Card(String str, Integer num, Integer num2, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15) {
        this.number = StripeTextUtils.nullIfBlank(normalizeCardNumber(str));
        this.expMonth = num;
        this.expYear = num2;
        this.cvc = StripeTextUtils.nullIfBlank(str2);
        this.name = StripeTextUtils.nullIfBlank(str3);
        this.addressLine1 = StripeTextUtils.nullIfBlank(str4);
        this.addressLine2 = StripeTextUtils.nullIfBlank(str5);
        this.addressCity = StripeTextUtils.nullIfBlank(str6);
        this.addressState = StripeTextUtils.nullIfBlank(str7);
        this.addressZip = StripeTextUtils.nullIfBlank(str8);
        this.addressCountry = StripeTextUtils.nullIfBlank(str9);
        this.brand = StripeTextUtils.asCardBrand(str10) == null ? getBrand() : str10;
        this.last4 = StripeTextUtils.nullIfBlank(str11) == null ? getLast4() : str11;
        StripeTextUtils.nullIfBlank(str12);
        StripeTextUtils.asFundingType(str13);
        StripeTextUtils.nullIfBlank(str14);
        this.currency = StripeTextUtils.nullIfBlank(str15);
    }

    public Card(String str, Integer num, Integer num2, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10) {
        this(str, num, num2, str2, str3, str4, str5, str6, str7, str8, str9, null, null, null, null, null, str10);
    }

    public boolean validateNumber() {
        if (StripeTextUtils.isBlank(this.number)) {
            return false;
        }
        String replaceAll = this.number.trim().replaceAll("\\s+|-", "");
        if (StripeTextUtils.isBlank(replaceAll) || !StripeTextUtils.isWholePositiveNumber(replaceAll) || !isValidLuhnNumber(replaceAll)) {
            return false;
        }
        String brand = getBrand();
        return "American Express".equals(brand) ? replaceAll.length() == 15 : "Diners Club".equals(brand) ? replaceAll.length() == 14 : replaceAll.length() == 16;
    }

    public boolean validateExpiryDate() {
        if (validateExpMonth() && validateExpYear()) {
            return !DateUtils.hasMonthPassed(this.expYear.intValue(), this.expMonth.intValue());
        }
        return false;
    }

    public boolean validateCVC() {
        if (StripeTextUtils.isBlank(this.cvc)) {
            return false;
        }
        String trim = this.cvc.trim();
        String brand = getBrand();
        return StripeTextUtils.isWholePositiveNumber(trim) && ((brand == null && trim.length() >= 3 && trim.length() <= 4) || (("American Express".equals(brand) && trim.length() == 4) || trim.length() == 3));
    }

    public boolean validateExpMonth() {
        Integer num = this.expMonth;
        return num != null && num.intValue() >= 1 && this.expMonth.intValue() <= 12;
    }

    public boolean validateExpYear() {
        Integer num = this.expYear;
        return num != null && !DateUtils.hasYearPassed(num.intValue());
    }

    public String getNumber() {
        return this.number;
    }

    public String getCVC() {
        return this.cvc;
    }

    public Integer getExpMonth() {
        return this.expMonth;
    }

    public Integer getExpYear() {
        return this.expYear;
    }

    public String getName() {
        return this.name;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public String getAddressCity() {
        return this.addressCity;
    }

    public String getAddressZip() {
        return this.addressZip;
    }

    public String getAddressState() {
        return this.addressState;
    }

    public String getAddressCountry() {
        return this.addressCountry;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getLast4() {
        if (!StripeTextUtils.isBlank(this.last4)) {
            return this.last4;
        }
        String str = this.number;
        if (str == null || str.length() <= 4) {
            return null;
        }
        String str2 = this.number;
        String substring = str2.substring(str2.length() - 4, this.number.length());
        this.last4 = substring;
        return substring;
    }

    @Deprecated
    public String getType() {
        return getBrand();
    }

    public String getBrand() {
        String str;
        if (StripeTextUtils.isBlank(this.brand) && !StripeTextUtils.isBlank(this.number)) {
            if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_AMERICAN_EXPRESS)) {
                str = "American Express";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DISCOVER)) {
                str = "Discover";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_JCB)) {
                str = "JCB";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DINERS_CLUB)) {
                str = "Diners Club";
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_VISA)) {
                str = "Visa";
            } else {
                str = StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_MASTERCARD) ? "MasterCard" : "Unknown";
            }
            this.brand = str;
        }
        return this.brand;
    }

    private boolean isValidLuhnNumber(String str) {
        int i = 0;
        boolean z = true;
        for (int length = str.length() - 1; length >= 0; length--) {
            char charAt = str.charAt(length);
            if (!Character.isDigit(charAt)) {
                return false;
            }
            int parseInt = Integer.parseInt("" + charAt);
            z ^= true;
            if (z) {
                parseInt *= 2;
            }
            if (parseInt > 9) {
                parseInt -= 9;
            }
            i += parseInt;
        }
        return i % 10 == 0;
    }

    private String normalizeCardNumber(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replaceAll("\\s+|-", "");
    }
}
