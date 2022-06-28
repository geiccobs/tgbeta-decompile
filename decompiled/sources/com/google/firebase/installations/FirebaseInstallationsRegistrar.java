package com.google.firebase.installations;

import com.google.firebase.FirebaseApp;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.firebase.heartbeatinfo.HeartBeatInfo;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import com.google.firebase.platforminfo.UserAgentPublisher;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class FirebaseInstallationsRegistrar implements ComponentRegistrar {
    @Override // com.google.firebase.components.ComponentRegistrar
    public List<Component<?>> getComponents() {
        return Arrays.asList(Component.builder(FirebaseInstallationsApi.class).add(Dependency.required(FirebaseApp.class)).add(Dependency.optionalProvider(HeartBeatInfo.class)).add(Dependency.optionalProvider(UserAgentPublisher.class)).factory(FirebaseInstallationsRegistrar$$ExternalSyntheticLambda0.INSTANCE).build(), LibraryVersionComponent.create("fire-installations", "17.0.0"));
    }

    public static /* synthetic */ FirebaseInstallationsApi lambda$getComponents$0(ComponentContainer c) {
        return new FirebaseInstallations((FirebaseApp) c.get(FirebaseApp.class), c.getProvider(UserAgentPublisher.class), c.getProvider(HeartBeatInfo.class));
    }
}
