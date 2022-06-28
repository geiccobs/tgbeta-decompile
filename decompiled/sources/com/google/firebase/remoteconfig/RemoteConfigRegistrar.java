package com.google.firebase.remoteconfig;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.abt.component.AbtComponent;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.firebase.installations.FirebaseInstallationsApi;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class RemoteConfigRegistrar implements ComponentRegistrar {
    @Override // com.google.firebase.components.ComponentRegistrar
    public List<Component<?>> getComponents() {
        return Arrays.asList(Component.builder(RemoteConfigComponent.class).add(Dependency.required(Context.class)).add(Dependency.required(FirebaseApp.class)).add(Dependency.required(FirebaseInstallationsApi.class)).add(Dependency.required(AbtComponent.class)).add(Dependency.optionalProvider(AnalyticsConnector.class)).factory(RemoteConfigRegistrar$$ExternalSyntheticLambda0.INSTANCE).eagerInDefaultApp().build(), LibraryVersionComponent.create("fire-rc", BuildConfig.VERSION_NAME));
    }

    public static /* synthetic */ RemoteConfigComponent lambda$getComponents$0(ComponentContainer container) {
        return new RemoteConfigComponent((Context) container.get(Context.class), (FirebaseApp) container.get(FirebaseApp.class), (FirebaseInstallationsApi) container.get(FirebaseInstallationsApi.class), ((AbtComponent) container.get(AbtComponent.class)).get(FirebaseABTesting.OriginService.REMOTE_CONFIG), container.getProvider(AnalyticsConnector.class));
    }
}
