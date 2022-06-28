package com.microsoft.appcenter.ingestion.models.json;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class DefaultLogSerializer implements LogSerializer {
    private static final String LOGS = "logs";
    private final Map<String, LogFactory> mLogFactories = new HashMap();

    private JSONStringer writeLog(JSONStringer writer, Log log) throws JSONException {
        writer.object();
        log.write(writer);
        writer.endObject();
        return writer;
    }

    private Log readLog(JSONObject object, String type) throws JSONException {
        if (type == null) {
            type = object.getString(CommonProperties.TYPE);
        }
        LogFactory logFactory = this.mLogFactories.get(type);
        if (logFactory == null) {
            throw new JSONException("Unknown log type: " + type);
        }
        Log log = logFactory.create();
        log.read(object);
        return log;
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public String serializeLog(Log log) throws JSONException {
        return writeLog(new JSONStringer(), log).toString();
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public Log deserializeLog(String json, String type) throws JSONException {
        return readLog(new JSONObject(json), type);
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public Collection<CommonSchemaLog> toCommonSchemaLog(Log log) {
        return this.mLogFactories.get(log.getType()).toCommonSchemaLogs(log);
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public String serializeContainer(LogContainer logContainer) throws JSONException {
        JSONStringer writer = new JSONStringer();
        writer.object();
        writer.key(LOGS).array();
        for (Log log : logContainer.getLogs()) {
            writeLog(writer, log);
        }
        writer.endArray();
        writer.endObject();
        return writer.toString();
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public LogContainer deserializeContainer(String json, String type) throws JSONException {
        JSONObject jContainer = new JSONObject(json);
        LogContainer container = new LogContainer();
        JSONArray jLogs = jContainer.getJSONArray(LOGS);
        List<Log> logs = new ArrayList<>();
        for (int i = 0; i < jLogs.length(); i++) {
            JSONObject jLog = jLogs.getJSONObject(i);
            Log log = readLog(jLog, type);
            logs.add(log);
        }
        container.setLogs(logs);
        return container;
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogSerializer
    public void addLogFactory(String logType, LogFactory logFactory) {
        this.mLogFactories.put(logType, logFactory);
    }
}
