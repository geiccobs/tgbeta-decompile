package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.properties.BooleanTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DateTimeTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DoubleTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class CommonSchemaDataUtils {
    static final int DATA_TYPE_DATETIME = 9;
    static final int DATA_TYPE_DOUBLE = 6;
    static final int DATA_TYPE_INT64 = 4;
    static final String METADATA_FIELDS = "f";

    public static void addCommonSchemaData(List<TypedProperty> properties, CommonSchemaLog dest) {
        Iterator<TypedProperty> it;
        CommonSchemaLog commonSchemaLog = dest;
        if (properties == null) {
            return;
        }
        try {
            Data data = new Data();
            commonSchemaLog.setData(data);
            MetadataExtension metadata = new MetadataExtension();
            Iterator<TypedProperty> it2 = properties.iterator();
            while (it2.hasNext()) {
                try {
                    TypedProperty property = it2.next();
                    try {
                        Object value = validateProperty(property);
                        Integer metadataType = getMetadataType(property);
                        String key = property.getName();
                        String[] keys = key.split("\\.", -1);
                        int lastIndex = keys.length - 1;
                        JSONObject destProperties = data.getProperties();
                        JSONObject destMetadata = metadata.getMetadata();
                        int i = 0;
                        while (true) {
                            it = it2;
                            TypedProperty property2 = property;
                            if (i >= lastIndex) {
                                break;
                            }
                            String subKey = keys[i];
                            String key2 = key;
                            JSONObject subDataObject = destProperties.optJSONObject(subKey);
                            if (subDataObject == null) {
                                if (destProperties.has(subKey)) {
                                    AppCenterLog.warn("AppCenter", "Property key '" + subKey + "' already has a value, the old value will be overridden.");
                                }
                                JSONObject subDataObject2 = new JSONObject();
                                destProperties.put(subKey, subDataObject2);
                                subDataObject = subDataObject2;
                            }
                            destProperties = subDataObject;
                            destMetadata = addIntermediateMetadata(destMetadata, subKey);
                            i++;
                            it2 = it;
                            property = property2;
                            key = key2;
                        }
                        String lastKey = keys[lastIndex];
                        if (destProperties.has(lastKey)) {
                            AppCenterLog.warn("AppCenter", "Property key '" + lastKey + "' already has a value, the old value will be overridden.");
                        }
                        destProperties.put(lastKey, value);
                        addLeafMetadata(metadataType, destMetadata, lastKey);
                        commonSchemaLog = dest;
                        it2 = it;
                    } catch (IllegalArgumentException e) {
                        AppCenterLog.warn("AppCenter", e.getMessage());
                        commonSchemaLog = dest;
                        it2 = it2;
                    }
                } catch (JSONException e2) {
                    return;
                }
            }
            JSONObject dataObject = data.getProperties();
            String baseType = dataObject.optString("baseType", null);
            JSONObject baseData = dataObject.optJSONObject("baseData");
            if (baseType == null && baseData != null) {
                AppCenterLog.warn("AppCenter", "baseData was set but baseType is missing.");
                dataObject.remove("baseData");
                JSONObject baseMetaData = metadata.getMetadata().optJSONObject(METADATA_FIELDS);
                baseMetaData.remove("baseData");
            }
            if (baseType != null && baseData == null) {
                AppCenterLog.warn("AppCenter", "baseType was set but baseData is missing.");
                dataObject.remove("baseType");
            }
            if (!cleanUpEmptyObjectsInMetadata(metadata.getMetadata())) {
                if (dest.getExt() == null) {
                    try {
                        dest.setExt(new Extensions());
                    } catch (JSONException e3) {
                        return;
                    }
                }
                dest.getExt().setMetadata(metadata);
            }
        } catch (JSONException e4) {
        }
    }

    private static Object validateProperty(TypedProperty property) throws IllegalArgumentException, JSONException {
        Object value;
        String key = property.getName();
        if (key == null) {
            throw new IllegalArgumentException("Property key cannot be null.");
        }
        if (key.equals("baseType") && !(property instanceof StringTypedProperty)) {
            throw new IllegalArgumentException("baseType must be a string.");
        }
        if (key.startsWith("baseType.")) {
            throw new IllegalArgumentException("baseType must be a string.");
        }
        if (key.equals("baseData")) {
            throw new IllegalArgumentException("baseData must be an object.");
        }
        if (property instanceof StringTypedProperty) {
            StringTypedProperty stringTypedProperty = (StringTypedProperty) property;
            value = stringTypedProperty.getValue();
        } else if (property instanceof LongTypedProperty) {
            LongTypedProperty longTypedProperty = (LongTypedProperty) property;
            value = Long.valueOf(longTypedProperty.getValue());
        } else if (property instanceof DoubleTypedProperty) {
            DoubleTypedProperty doubleTypedProperty = (DoubleTypedProperty) property;
            value = Double.valueOf(doubleTypedProperty.getValue());
        } else if (property instanceof DateTimeTypedProperty) {
            value = JSONDateUtils.toString(((DateTimeTypedProperty) property).getValue());
        } else if (property instanceof BooleanTypedProperty) {
            BooleanTypedProperty booleanTypedProperty = (BooleanTypedProperty) property;
            value = Boolean.valueOf(booleanTypedProperty.getValue());
        } else {
            throw new IllegalArgumentException("Unsupported property type: " + property.getType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value of property with key '" + key + "' cannot be null.");
        }
        return value;
    }

    private static Integer getMetadataType(TypedProperty property) {
        if (property instanceof LongTypedProperty) {
            return 4;
        }
        if (property instanceof DoubleTypedProperty) {
            return 6;
        }
        if (property instanceof DateTimeTypedProperty) {
            return 9;
        }
        return null;
    }

    private static void addLeafMetadata(Integer metadataType, JSONObject destMetadata, String lastKey) throws JSONException {
        JSONObject fields = destMetadata.optJSONObject(METADATA_FIELDS);
        if (metadataType != null) {
            if (fields == null) {
                fields = new JSONObject();
                destMetadata.put(METADATA_FIELDS, fields);
            }
            fields.put(lastKey, metadataType);
        } else if (fields != null) {
            fields.remove(lastKey);
        }
    }

    private static JSONObject addIntermediateMetadata(JSONObject destMetadata, String subKey) throws JSONException {
        JSONObject fields = destMetadata.optJSONObject(METADATA_FIELDS);
        if (fields == null) {
            fields = new JSONObject();
            destMetadata.put(METADATA_FIELDS, fields);
        }
        JSONObject subMetadataObject = fields.optJSONObject(subKey);
        if (subMetadataObject == null) {
            JSONObject subMetadataObject2 = new JSONObject();
            fields.put(subKey, subMetadataObject2);
            return subMetadataObject2;
        }
        return subMetadataObject;
    }

    private static boolean cleanUpEmptyObjectsInMetadata(JSONObject object) {
        Iterator<String> iterator = object.keys();
        while (iterator.hasNext()) {
            String childKey = iterator.next();
            JSONObject child = object.optJSONObject(childKey);
            if (child != null && cleanUpEmptyObjectsInMetadata(child)) {
                iterator.remove();
            }
        }
        return object.length() == 0;
    }
}
