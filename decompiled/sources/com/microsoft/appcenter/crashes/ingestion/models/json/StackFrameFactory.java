package com.microsoft.appcenter.crashes.ingestion.models.json;

import com.microsoft.appcenter.crashes.ingestion.models.StackFrame;
import com.microsoft.appcenter.ingestion.models.json.ModelFactory;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class StackFrameFactory implements ModelFactory<StackFrame> {
    private static final StackFrameFactory sInstance = new StackFrameFactory();

    private StackFrameFactory() {
    }

    public static StackFrameFactory getInstance() {
        return sInstance;
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.ModelFactory
    public StackFrame create() {
        return new StackFrame();
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.ModelFactory
    public List<StackFrame> createList(int capacity) {
        return new ArrayList(capacity);
    }
}
