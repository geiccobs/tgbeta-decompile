package com.microsoft.appcenter.ingestion.models.properties;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class TypedPropertyUtils {
    public static TypedProperty create(String type) throws JSONException {
        if (BooleanTypedProperty.TYPE.equals(type)) {
            return new BooleanTypedProperty();
        }
        if (DateTimeTypedProperty.TYPE.equals(type)) {
            return new DateTimeTypedProperty();
        }
        if (DoubleTypedProperty.TYPE.equals(type)) {
            return new DoubleTypedProperty();
        }
        if (LongTypedProperty.TYPE.equals(type)) {
            return new LongTypedProperty();
        }
        if (StringTypedProperty.TYPE.equals(type)) {
            return new StringTypedProperty();
        }
        throw new JSONException("Unsupported type: " + type);
    }

    public static List<TypedProperty> read(JSONObject object) throws JSONException {
        JSONArray jArray = object.optJSONArray(CommonProperties.TYPED_PROPERTIES);
        if (jArray != null) {
            List<TypedProperty> array = new ArrayList<>(jArray.length());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonObject = jArray.getJSONObject(i);
                TypedProperty typedProperty = create(jsonObject.getString(CommonProperties.TYPE));
                typedProperty.read(jsonObject);
                array.add(typedProperty);
            }
            return array;
        }
        return null;
    }
}
