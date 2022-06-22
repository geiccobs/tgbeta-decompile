package com.google.firebase.platforminfo;

import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentFactory;
/* loaded from: classes.dex */
public final /* synthetic */ class DefaultUserAgentPublisher$$ExternalSyntheticLambda0 implements ComponentFactory {
    public static final /* synthetic */ DefaultUserAgentPublisher$$ExternalSyntheticLambda0 INSTANCE = new DefaultUserAgentPublisher$$ExternalSyntheticLambda0();

    private /* synthetic */ DefaultUserAgentPublisher$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.firebase.components.ComponentFactory
    public final Object create(ComponentContainer componentContainer) {
        UserAgentPublisher lambda$component$0;
        lambda$component$0 = DefaultUserAgentPublisher.lambda$component$0(componentContainer);
        return lambda$component$0;
    }
}
