package com.huawei.hms.push;

import com.huawei.hms.aaid.constant.ErrorEnum;
/* loaded from: classes.dex */
public class BaseException extends Exception {
    public final int a;
    public final ErrorEnum b;

    public BaseException(int i) {
        ErrorEnum fromCode = ErrorEnum.fromCode(i);
        this.b = fromCode;
        this.a = fromCode.getExternalCode();
    }

    public int getErrorCode() {
        return this.a;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.b.getMessage();
    }
}
