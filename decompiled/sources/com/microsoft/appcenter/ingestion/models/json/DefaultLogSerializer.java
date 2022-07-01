package com.microsoft.appcenter.ingestion.models.json;

import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class DefaultLogSerializer implements LogSerializer {
    private final Map<String, LogFactory> mLogFactories = new HashMap();

    private JSONStringer writeLog(JSONStringer jSONStringer, Log log) throws JSONException {
        jSONStringer.object();
        log.write(jSONStringer);
        jSONStringer.endObject();
        return jSONStringer;
    }

    private Log readLog(JSONObject jSONObject, String str) throws JSONException {
        if (str == null) {
            str = jSONObject.getString("type");
        }
        LogFactory logFactory = this.mLogFactories.get(str);
        if (logFactory == null) {
            throw new JSONException("Unknown log type: " + str);
        }
        Log create = logFactory.create();
        create.read(jSONObject);
        return create;
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public String serializeLog(Log log) throws JSONException {
        return writeLog(new JSONStringer(), log).toString();
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public Log deserializeLog(String str, String str2) throws JSONException {
        return readLog(new JSONObject(str), str2);
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public Collection<CommonSchemaLog> toCommonSchemaLog(Log log) {
        return this.mLogFactories.get(log.getType()).toCommonSchemaLogs(log);
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public String serializeContainer(LogContainer logContainer) throws JSONException {
        JSONStringer jSONStringer = new JSONStringer();
        jSONStringer.object();
        jSONStringer.key("logs").array();
        for (Log log : logContainer.getLogs()) {
            writeLog(jSONStringer, log);
        }
        jSONStringer.endArray();
        jSONStringer.endObject();
        return jSONStringer.toString();
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public void addLogFactory(String str, LogFactory logFactory) {
        this.mLogFactories.put(str, logFactory);
    }
}
