package com.google.android.gms.vision.barcode;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes.dex */
public class Barcode extends AbstractSafeParcelable {
    @RecentlyNonNull
    public static final Parcelable.Creator<Barcode> CREATOR = new zzb();
    @RecentlyNonNull
    public CalendarEvent calendarEvent;
    @RecentlyNonNull
    public ContactInfo contactInfo;
    @RecentlyNonNull
    public Point[] cornerPoints;
    @RecentlyNonNull
    public String displayValue;
    @RecentlyNonNull
    public DriverLicense driverLicense;
    @RecentlyNonNull
    public Email email;
    public int format;
    @RecentlyNonNull
    public GeoPoint geoPoint;
    public boolean isRecognized;
    @RecentlyNonNull
    public Phone phone;
    @RecentlyNonNull
    public byte[] rawBytes;
    @RecentlyNonNull
    public String rawValue;
    @RecentlyNonNull
    public Sms sms;
    @RecentlyNonNull
    public UrlBookmark url;
    public int valueFormat;
    @RecentlyNonNull
    public WiFi wifi;

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class Address extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<Address> CREATOR = new zza();
        @RecentlyNonNull
        public String[] addressLines;
        public int type;

        public Address() {
        }

        public Address(int i, @RecentlyNonNull String[] strArr) {
            this.type = i;
            this.addressLines = strArr;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeInt(parcel, 2, this.type);
            SafeParcelWriter.writeStringArray(parcel, 3, this.addressLines, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class CalendarDateTime extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<CalendarDateTime> CREATOR = new zzd();
        public int day;
        public int hours;
        public boolean isUtc;
        public int minutes;
        public int month;
        @RecentlyNonNull
        public String rawValue;
        public int seconds;
        public int year;

        public CalendarDateTime() {
        }

        public CalendarDateTime(int i, int i2, int i3, int i4, int i5, int i6, boolean z, @RecentlyNonNull String str) {
            this.year = i;
            this.month = i2;
            this.day = i3;
            this.hours = i4;
            this.minutes = i5;
            this.seconds = i6;
            this.isUtc = z;
            this.rawValue = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeInt(parcel, 2, this.year);
            SafeParcelWriter.writeInt(parcel, 3, this.month);
            SafeParcelWriter.writeInt(parcel, 4, this.day);
            SafeParcelWriter.writeInt(parcel, 5, this.hours);
            SafeParcelWriter.writeInt(parcel, 6, this.minutes);
            SafeParcelWriter.writeInt(parcel, 7, this.seconds);
            SafeParcelWriter.writeBoolean(parcel, 8, this.isUtc);
            SafeParcelWriter.writeString(parcel, 9, this.rawValue, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class CalendarEvent extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<CalendarEvent> CREATOR = new zzf();
        @RecentlyNonNull
        public String description;
        @RecentlyNonNull
        public CalendarDateTime end;
        @RecentlyNonNull
        public String location;
        @RecentlyNonNull
        public String organizer;
        @RecentlyNonNull
        public CalendarDateTime start;
        @RecentlyNonNull
        public String status;
        @RecentlyNonNull
        public String summary;

        public CalendarEvent() {
        }

        public CalendarEvent(@RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull String str3, @RecentlyNonNull String str4, @RecentlyNonNull String str5, @RecentlyNonNull CalendarDateTime calendarDateTime, @RecentlyNonNull CalendarDateTime calendarDateTime2) {
            this.summary = str;
            this.description = str2;
            this.location = str3;
            this.organizer = str4;
            this.status = str5;
            this.start = calendarDateTime;
            this.end = calendarDateTime2;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.summary, false);
            SafeParcelWriter.writeString(parcel, 3, this.description, false);
            SafeParcelWriter.writeString(parcel, 4, this.location, false);
            SafeParcelWriter.writeString(parcel, 5, this.organizer, false);
            SafeParcelWriter.writeString(parcel, 6, this.status, false);
            SafeParcelWriter.writeParcelable(parcel, 7, this.start, i, false);
            SafeParcelWriter.writeParcelable(parcel, 8, this.end, i, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class ContactInfo extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<ContactInfo> CREATOR = new zze();
        @RecentlyNonNull
        public Address[] addresses;
        @RecentlyNonNull
        public Email[] emails;
        @RecentlyNonNull
        public PersonName name;
        @RecentlyNonNull
        public String organization;
        @RecentlyNonNull
        public Phone[] phones;
        @RecentlyNonNull
        public String title;
        @RecentlyNonNull
        public String[] urls;

        public ContactInfo() {
        }

        public ContactInfo(@RecentlyNonNull PersonName personName, @RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull Phone[] phoneArr, @RecentlyNonNull Email[] emailArr, @RecentlyNonNull String[] strArr, @RecentlyNonNull Address[] addressArr) {
            this.name = personName;
            this.organization = str;
            this.title = str2;
            this.phones = phoneArr;
            this.emails = emailArr;
            this.urls = strArr;
            this.addresses = addressArr;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeParcelable(parcel, 2, this.name, i, false);
            SafeParcelWriter.writeString(parcel, 3, this.organization, false);
            SafeParcelWriter.writeString(parcel, 4, this.title, false);
            SafeParcelWriter.writeTypedArray(parcel, 5, this.phones, i, false);
            SafeParcelWriter.writeTypedArray(parcel, 6, this.emails, i, false);
            SafeParcelWriter.writeStringArray(parcel, 7, this.urls, false);
            SafeParcelWriter.writeTypedArray(parcel, 8, this.addresses, i, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class DriverLicense extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<DriverLicense> CREATOR = new zzh();
        @RecentlyNonNull
        public String addressCity;
        @RecentlyNonNull
        public String addressState;
        @RecentlyNonNull
        public String addressStreet;
        @RecentlyNonNull
        public String addressZip;
        @RecentlyNonNull
        public String birthDate;
        @RecentlyNonNull
        public String documentType;
        @RecentlyNonNull
        public String expiryDate;
        @RecentlyNonNull
        public String firstName;
        @RecentlyNonNull
        public String gender;
        @RecentlyNonNull
        public String issueDate;
        @RecentlyNonNull
        public String issuingCountry;
        @RecentlyNonNull
        public String lastName;
        @RecentlyNonNull
        public String licenseNumber;
        @RecentlyNonNull
        public String middleName;

        public DriverLicense() {
        }

        public DriverLicense(@RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull String str3, @RecentlyNonNull String str4, @RecentlyNonNull String str5, @RecentlyNonNull String str6, @RecentlyNonNull String str7, @RecentlyNonNull String str8, @RecentlyNonNull String str9, @RecentlyNonNull String str10, @RecentlyNonNull String str11, @RecentlyNonNull String str12, @RecentlyNonNull String str13, @RecentlyNonNull String str14) {
            this.documentType = str;
            this.firstName = str2;
            this.middleName = str3;
            this.lastName = str4;
            this.gender = str5;
            this.addressStreet = str6;
            this.addressCity = str7;
            this.addressState = str8;
            this.addressZip = str9;
            this.licenseNumber = str10;
            this.issueDate = str11;
            this.expiryDate = str12;
            this.birthDate = str13;
            this.issuingCountry = str14;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.documentType, false);
            SafeParcelWriter.writeString(parcel, 3, this.firstName, false);
            SafeParcelWriter.writeString(parcel, 4, this.middleName, false);
            SafeParcelWriter.writeString(parcel, 5, this.lastName, false);
            SafeParcelWriter.writeString(parcel, 6, this.gender, false);
            SafeParcelWriter.writeString(parcel, 7, this.addressStreet, false);
            SafeParcelWriter.writeString(parcel, 8, this.addressCity, false);
            SafeParcelWriter.writeString(parcel, 9, this.addressState, false);
            SafeParcelWriter.writeString(parcel, 10, this.addressZip, false);
            SafeParcelWriter.writeString(parcel, 11, this.licenseNumber, false);
            SafeParcelWriter.writeString(parcel, 12, this.issueDate, false);
            SafeParcelWriter.writeString(parcel, 13, this.expiryDate, false);
            SafeParcelWriter.writeString(parcel, 14, this.birthDate, false);
            SafeParcelWriter.writeString(parcel, 15, this.issuingCountry, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class Email extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<Email> CREATOR = new zzg();
        @RecentlyNonNull
        public String address;
        @RecentlyNonNull
        public String body;
        @RecentlyNonNull
        public String subject;
        public int type;

        public Email() {
        }

        public Email(int i, @RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull String str3) {
            this.type = i;
            this.address = str;
            this.subject = str2;
            this.body = str3;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeInt(parcel, 2, this.type);
            SafeParcelWriter.writeString(parcel, 3, this.address, false);
            SafeParcelWriter.writeString(parcel, 4, this.subject, false);
            SafeParcelWriter.writeString(parcel, 5, this.body, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class GeoPoint extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<GeoPoint> CREATOR = new zzj();
        public double lat;
        public double lng;

        public GeoPoint() {
        }

        public GeoPoint(double d, double d2) {
            this.lat = d;
            this.lng = d2;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeDouble(parcel, 2, this.lat);
            SafeParcelWriter.writeDouble(parcel, 3, this.lng);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class PersonName extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<PersonName> CREATOR = new zzi();
        @RecentlyNonNull
        public String first;
        @RecentlyNonNull
        public String formattedName;
        @RecentlyNonNull
        public String last;
        @RecentlyNonNull
        public String middle;
        @RecentlyNonNull
        public String prefix;
        @RecentlyNonNull
        public String pronunciation;
        @RecentlyNonNull
        public String suffix;

        public PersonName() {
        }

        public PersonName(@RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull String str3, @RecentlyNonNull String str4, @RecentlyNonNull String str5, @RecentlyNonNull String str6, @RecentlyNonNull String str7) {
            this.formattedName = str;
            this.pronunciation = str2;
            this.prefix = str3;
            this.first = str4;
            this.middle = str5;
            this.last = str6;
            this.suffix = str7;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.formattedName, false);
            SafeParcelWriter.writeString(parcel, 3, this.pronunciation, false);
            SafeParcelWriter.writeString(parcel, 4, this.prefix, false);
            SafeParcelWriter.writeString(parcel, 5, this.first, false);
            SafeParcelWriter.writeString(parcel, 6, this.middle, false);
            SafeParcelWriter.writeString(parcel, 7, this.last, false);
            SafeParcelWriter.writeString(parcel, 8, this.suffix, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class Phone extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<Phone> CREATOR = new zzl();
        @RecentlyNonNull
        public String number;
        public int type;

        public Phone() {
        }

        public Phone(int i, @RecentlyNonNull String str) {
            this.type = i;
            this.number = str;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeInt(parcel, 2, this.type);
            SafeParcelWriter.writeString(parcel, 3, this.number, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class Sms extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<Sms> CREATOR = new zzk();
        @RecentlyNonNull
        public String message;
        @RecentlyNonNull
        public String phoneNumber;

        public Sms() {
        }

        public Sms(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
            this.message = str;
            this.phoneNumber = str2;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.message, false);
            SafeParcelWriter.writeString(parcel, 3, this.phoneNumber, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class UrlBookmark extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<UrlBookmark> CREATOR = new zzn();
        @RecentlyNonNull
        public String title;
        @RecentlyNonNull
        public String url;

        public UrlBookmark() {
        }

        public UrlBookmark(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
            this.title = str;
            this.url = str2;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.title, false);
            SafeParcelWriter.writeString(parcel, 3, this.url, false);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes.dex */
    public static class WiFi extends AbstractSafeParcelable {
        @RecentlyNonNull
        public static final Parcelable.Creator<WiFi> CREATOR = new zzm();
        public int encryptionType;
        @RecentlyNonNull
        public String password;
        @RecentlyNonNull
        public String ssid;

        public WiFi() {
        }

        public WiFi(@RecentlyNonNull String str, @RecentlyNonNull String str2, int i) {
            this.ssid = str;
            this.password = str2;
            this.encryptionType = i;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
            int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
            SafeParcelWriter.writeString(parcel, 2, this.ssid, false);
            SafeParcelWriter.writeString(parcel, 3, this.password, false);
            SafeParcelWriter.writeInt(parcel, 4, this.encryptionType);
            SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
        }
    }

    public Barcode() {
    }

    public Barcode(int i, @RecentlyNonNull String str, @RecentlyNonNull String str2, int i2, @RecentlyNonNull Point[] pointArr, @RecentlyNonNull Email email, @RecentlyNonNull Phone phone, @RecentlyNonNull Sms sms, @RecentlyNonNull WiFi wiFi, @RecentlyNonNull UrlBookmark urlBookmark, @RecentlyNonNull GeoPoint geoPoint, @RecentlyNonNull CalendarEvent calendarEvent, @RecentlyNonNull ContactInfo contactInfo, @RecentlyNonNull DriverLicense driverLicense, @RecentlyNonNull byte[] bArr, boolean z) {
        this.format = i;
        this.rawValue = str;
        this.rawBytes = bArr;
        this.displayValue = str2;
        this.valueFormat = i2;
        this.cornerPoints = pointArr;
        this.isRecognized = z;
        this.email = email;
        this.phone = phone;
        this.sms = sms;
        this.wifi = wiFi;
        this.url = urlBookmark;
        this.geoPoint = geoPoint;
        this.calendarEvent = calendarEvent;
        this.contactInfo = contactInfo;
        this.driverLicense = driverLicense;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(@RecentlyNonNull Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.format);
        SafeParcelWriter.writeString(parcel, 3, this.rawValue, false);
        SafeParcelWriter.writeString(parcel, 4, this.displayValue, false);
        SafeParcelWriter.writeInt(parcel, 5, this.valueFormat);
        SafeParcelWriter.writeTypedArray(parcel, 6, this.cornerPoints, i, false);
        SafeParcelWriter.writeParcelable(parcel, 7, this.email, i, false);
        SafeParcelWriter.writeParcelable(parcel, 8, this.phone, i, false);
        SafeParcelWriter.writeParcelable(parcel, 9, this.sms, i, false);
        SafeParcelWriter.writeParcelable(parcel, 10, this.wifi, i, false);
        SafeParcelWriter.writeParcelable(parcel, 11, this.url, i, false);
        SafeParcelWriter.writeParcelable(parcel, 12, this.geoPoint, i, false);
        SafeParcelWriter.writeParcelable(parcel, 13, this.calendarEvent, i, false);
        SafeParcelWriter.writeParcelable(parcel, 14, this.contactInfo, i, false);
        SafeParcelWriter.writeParcelable(parcel, 15, this.driverLicense, i, false);
        SafeParcelWriter.writeByteArray(parcel, 16, this.rawBytes, false);
        SafeParcelWriter.writeBoolean(parcel, 17, this.isRecognized);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
