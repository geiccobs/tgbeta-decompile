package com.huawei.hmf.tasks;
/* loaded from: classes.dex */
public abstract class Task<TResult> {
    public abstract Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> onCompleteListener);

    public abstract Task<TResult> addOnFailureListener(OnFailureListener onFailureListener);

    public abstract Task<TResult> addOnSuccessListener(OnSuccessListener<TResult> onSuccessListener);

    public abstract Exception getException();

    public abstract TResult getResult();

    public abstract boolean isCanceled();

    public abstract boolean isComplete();

    public abstract boolean isSuccessful();
}
