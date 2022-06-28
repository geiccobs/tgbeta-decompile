package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public class AdaptationSet {
    public static final int ID_UNSET = -1;
    public final List<Descriptor> accessibilityDescriptors;
    public final List<Descriptor> essentialProperties;
    public final int id;
    public final List<Representation> representations;
    public final List<Descriptor> supplementalProperties;
    public final int type;

    public AdaptationSet(int id, int type, List<Representation> representations, List<Descriptor> accessibilityDescriptors, List<Descriptor> essentialProperties, List<Descriptor> supplementalProperties) {
        this.id = id;
        this.type = type;
        this.representations = Collections.unmodifiableList(representations);
        this.accessibilityDescriptors = Collections.unmodifiableList(accessibilityDescriptors);
        this.essentialProperties = Collections.unmodifiableList(essentialProperties);
        this.supplementalProperties = Collections.unmodifiableList(supplementalProperties);
    }
}
