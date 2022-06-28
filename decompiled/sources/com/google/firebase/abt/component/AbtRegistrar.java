package com.google.firebase.abt.component;

import android.content.Context;
import com.google.firebase.abt.BuildConfig;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class AbtRegistrar implements ComponentRegistrar {
    @Override // com.google.firebase.components.ComponentRegistrar
    public List<Component<?>> getComponents() {
        return Arrays.asList(Component.builder(AbtComponent.class).add(Dependency.required(Context.class)).add(Dependency.optionalProvider(AnalyticsConnector.class)).factory(AbtRegistrar$$ExternalSyntheticLambda0.INSTANCE).build(), LibraryVersionComponent.create("fire-abt", BuildConfig.VERSION_NAME));
    }

    public static /* synthetic */ AbtComponent lambda$getComponents$0(ComponentContainer container) {
        return new AbtComponent((Context) container.get(Context.class), container.getProvider(AnalyticsConnector.class));
    }
}
