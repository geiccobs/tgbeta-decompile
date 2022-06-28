package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.SystemClock;
import android.util.Base64;
import com.google.android.datatransport.Encoding;
import com.google.android.datatransport.runtime.EncodedPayload;
import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.dagger.Lazy;
import com.google.android.datatransport.runtime.firebase.transport.ClientMetrics;
import com.google.android.datatransport.runtime.firebase.transport.GlobalMetrics;
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;
import com.google.android.datatransport.runtime.firebase.transport.LogSourceMetrics;
import com.google.android.datatransport.runtime.firebase.transport.StorageMetrics;
import com.google.android.datatransport.runtime.firebase.transport.TimeWindow;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.datatransport.runtime.synchronization.SynchronizationException;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import com.google.android.datatransport.runtime.time.Clock;
import com.google.android.datatransport.runtime.util.PriorityMapping;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes3.dex */
public class SQLiteEventStore implements EventStore, SynchronizationGuard, ClientHealthMetricsStore {
    private static final int LOCK_RETRY_BACK_OFF_MILLIS = 50;
    private static final String LOG_TAG = "SQLiteEventStore";
    static final int MAX_RETRIES = 16;
    private static final Encoding PROTOBUF_ENCODING = Encoding.of("proto");
    private final EventStoreConfig config;
    private final Clock monotonicClock;
    private final Lazy<String> packageName;
    private final SchemaManager schemaManager;
    private final Clock wallClock;

    /* loaded from: classes3.dex */
    public interface Function<T, U> {
        U apply(T t);
    }

    /* loaded from: classes3.dex */
    public interface Producer<T> {
        T produce();
    }

    @Inject
    public SQLiteEventStore(Clock wallClock, Clock clock, EventStoreConfig config, SchemaManager schemaManager, @Named("PACKAGE_NAME") Lazy<String> packageName) {
        this.schemaManager = schemaManager;
        this.wallClock = wallClock;
        this.monotonicClock = clock;
        this.config = config;
        this.packageName = packageName;
    }

    SQLiteDatabase getDb() {
        final SchemaManager schemaManager = this.schemaManager;
        schemaManager.getClass();
        return (SQLiteDatabase) retryIfDbLocked(new Producer() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda19
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Producer
            public final Object produce() {
                return SchemaManager.this.getWritableDatabase();
            }
        }, SQLiteEventStore$$ExternalSyntheticLambda17.INSTANCE);
    }

    public static /* synthetic */ SQLiteDatabase lambda$getDb$0(Throwable ex) {
        throw new SynchronizationException("Timed out while trying to open db.", ex);
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public PersistedEvent persist(final TransportContext transportContext, final EventInternal event) {
        Logging.d(LOG_TAG, "Storing event with priority=%s, name=%s for destination %s", transportContext.getPriority(), event.getTransportName(), transportContext.getBackendName());
        long newRowId = ((Long) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda25
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m26x42ac2bf1(event, transportContext, (SQLiteDatabase) obj);
            }
        })).longValue();
        if (newRowId < 1) {
            return null;
        }
        return PersistedEvent.create(newRowId, transportContext, event);
    }

    /* renamed from: lambda$persist$1$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Long m26x42ac2bf1(EventInternal event, TransportContext transportContext, SQLiteDatabase db) {
        long newEventId;
        if (!isStorageAtLimit()) {
            long contextId = ensureTransportContext(db, transportContext);
            int maxBlobSizePerRow = this.config.getMaxBlobByteSizePerRow();
            byte[] payloadBytes = event.getEncodedPayload().getBytes();
            boolean inline = payloadBytes.length <= maxBlobSizePerRow;
            ContentValues values = new ContentValues();
            values.put("context_id", Long.valueOf(contextId));
            values.put("transport_name", event.getTransportName());
            values.put("timestamp_ms", Long.valueOf(event.getEventMillis()));
            values.put("uptime_ms", Long.valueOf(event.getUptimeMillis()));
            values.put("payload_encoding", event.getEncodedPayload().getEncoding().getName());
            values.put("code", event.getCode());
            values.put("num_attempts", (Integer) 0);
            values.put("inline", Boolean.valueOf(inline));
            values.put("payload", inline ? payloadBytes : new byte[0]);
            long newEventId2 = db.insert("events", null, values);
            if (inline) {
                newEventId = newEventId2;
            } else {
                double length = payloadBytes.length;
                newEventId = newEventId2;
                double d = maxBlobSizePerRow;
                Double.isNaN(length);
                Double.isNaN(d);
                int numChunks = (int) Math.ceil(length / d);
                for (int chunk = 1; chunk <= numChunks; chunk++) {
                    byte[] chunkBytes = Arrays.copyOfRange(payloadBytes, (chunk - 1) * maxBlobSizePerRow, Math.min(chunk * maxBlobSizePerRow, payloadBytes.length));
                    ContentValues payloadValues = new ContentValues();
                    payloadValues.put("event_id", Long.valueOf(newEventId));
                    payloadValues.put("sequence_num", Integer.valueOf(chunk));
                    payloadValues.put("bytes", chunkBytes);
                    db.insert("event_payloads", null, payloadValues);
                }
            }
            for (Map.Entry<String, String> entry : event.getMetadata().entrySet()) {
                ContentValues metadata = new ContentValues();
                metadata.put("event_id", Long.valueOf(newEventId));
                metadata.put(CommonProperties.NAME, entry.getKey());
                metadata.put(CommonProperties.VALUE, entry.getValue());
                db.insert("event_metadata", null, metadata);
            }
            return Long.valueOf(newEventId);
        }
        recordLogEventDropped(1L, LogEventDropped.Reason.CACHE_FULL, event.getTransportName());
        return -1L;
    }

    private long ensureTransportContext(SQLiteDatabase db, TransportContext transportContext) {
        Long existingId = getTransportContextId(db, transportContext);
        if (existingId != null) {
            return existingId.longValue();
        }
        ContentValues record = new ContentValues();
        record.put("backend_name", transportContext.getBackendName());
        record.put("priority", Integer.valueOf(PriorityMapping.toInt(transportContext.getPriority())));
        record.put("next_request_ms", (Integer) 0);
        if (transportContext.getExtras() != null) {
            record.put("extras", Base64.encodeToString(transportContext.getExtras(), 0));
        }
        return db.insert("transport_contexts", null, record);
    }

    private Long getTransportContextId(SQLiteDatabase db, TransportContext transportContext) {
        StringBuilder selection = new StringBuilder("backend_name = ? and priority = ?");
        ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))));
        if (transportContext.getExtras() != null) {
            selection.append(" and extras = ?");
            selectionArgs.add(Base64.encodeToString(transportContext.getExtras(), 0));
        } else {
            selection.append(" and extras is null");
        }
        return (Long) tryWithCursor(db.query("transport_contexts", new String[]{"_id"}, selection.toString(), (String[]) selectionArgs.toArray(new String[0]), null, null, null), SQLiteEventStore$$ExternalSyntheticLambda8.INSTANCE);
    }

    public static /* synthetic */ Long lambda$getTransportContextId$2(Cursor cursor) {
        if (!cursor.moveToNext()) {
            return null;
        }
        return Long.valueOf(cursor.getLong(0));
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public void recordFailure(Iterable<PersistedEvent> events) {
        if (!events.iterator().hasNext()) {
            return;
        }
        final String incrementAttemptNumQuery = "UPDATE events SET num_attempts = num_attempts + 1 WHERE _id in " + toIdList(events);
        inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda1
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m28x9f560649(incrementAttemptNumQuery, r3, (SQLiteDatabase) obj);
            }
        });
    }

    /* renamed from: lambda$recordFailure$4$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Object m28x9f560649(String incrementAttemptNumQuery, String countMaxAttemptsEventsQuery, SQLiteDatabase db) {
        db.compileStatement(incrementAttemptNumQuery).execute();
        tryWithCursor(db.rawQuery(countMaxAttemptsEventsQuery, null), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda22
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m27x70a49c2a((Cursor) obj);
            }
        });
        db.compileStatement("DELETE FROM events WHERE num_attempts >= 16").execute();
        return null;
    }

    /* renamed from: lambda$recordFailure$3$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Object m27x70a49c2a(Cursor cursor) {
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            String transportName = cursor.getString(1);
            recordLogEventDropped(count, LogEventDropped.Reason.MAX_RETRIES_REACHED, transportName);
        }
        return null;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public void recordSuccess(Iterable<PersistedEvent> events) {
        if (!events.iterator().hasNext()) {
            return;
        }
        String query = "DELETE FROM events WHERE _id in " + toIdList(events);
        getDb().compileStatement(query).execute();
    }

    private static String toIdList(Iterable<PersistedEvent> events) {
        StringBuilder idList = new StringBuilder("(");
        Iterator<PersistedEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            idList.append(iterator.next().getId());
            if (iterator.hasNext()) {
                idList.append(',');
            }
        }
        idList.append(')');
        return idList.toString();
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public long getNextCallTime(TransportContext transportContext) {
        return ((Long) tryWithCursor(getDb().rawQuery("SELECT next_request_ms FROM transport_contexts WHERE backend_name = ? and priority = ?", new String[]{transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))}), SQLiteEventStore$$ExternalSyntheticLambda7.INSTANCE)).longValue();
    }

    public static /* synthetic */ Long lambda$getNextCallTime$5(Cursor cursor) {
        if (cursor.moveToNext()) {
            return Long.valueOf(cursor.getLong(0));
        }
        return 0L;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public boolean hasPendingEventsFor(final TransportContext transportContext) {
        return ((Boolean) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda26
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m21xca7e02ad(transportContext, (SQLiteDatabase) obj);
            }
        })).booleanValue();
    }

    /* renamed from: lambda$hasPendingEventsFor$6$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Boolean m21xca7e02ad(TransportContext transportContext, SQLiteDatabase db) {
        Long contextId = getTransportContextId(db, transportContext);
        if (contextId == null) {
            return false;
        }
        return (Boolean) tryWithCursor(getDb().rawQuery("SELECT 1 FROM events WHERE context_id = ? LIMIT 1", new String[]{contextId.toString()}), SQLiteEventStore$$ExternalSyntheticLambda13.INSTANCE);
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public void recordNextCallTime(final TransportContext transportContext, final long timestampMs) {
        inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda20
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.lambda$recordNextCallTime$7(timestampMs, transportContext, (SQLiteDatabase) obj);
            }
        });
    }

    public static /* synthetic */ Object lambda$recordNextCallTime$7(long timestampMs, TransportContext transportContext, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("next_request_ms", Long.valueOf(timestampMs));
        int rowsUpdated = db.update("transport_contexts", values, "backend_name = ? and priority = ?", new String[]{transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))});
        if (rowsUpdated < 1) {
            values.put("backend_name", transportContext.getBackendName());
            values.put("priority", Integer.valueOf(PriorityMapping.toInt(transportContext.getPriority())));
            db.insert("transport_contexts", null, values);
        }
        return null;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public Iterable<PersistedEvent> loadBatch(final TransportContext transportContext) {
        return (Iterable) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda27
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m22x21bf8b6a(transportContext, (SQLiteDatabase) obj);
            }
        });
    }

    /* renamed from: lambda$loadBatch$8$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ List m22x21bf8b6a(TransportContext transportContext, SQLiteDatabase db) {
        List<PersistedEvent> events = loadEvents(db, transportContext);
        return join(events, loadMetadata(db, events));
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public Iterable<TransportContext> loadActiveContexts() {
        return (Iterable) inTransaction(SQLiteEventStore$$ExternalSyntheticLambda15.INSTANCE);
    }

    public static /* synthetic */ List lambda$loadActiveContexts$10(SQLiteDatabase db) {
        return (List) tryWithCursor(db.rawQuery("SELECT distinct t._id, t.backend_name, t.priority, t.extras FROM transport_contexts AS t, events AS e WHERE e.context_id = t._id", new String[0]), SQLiteEventStore$$ExternalSyntheticLambda9.INSTANCE);
    }

    public static /* synthetic */ List lambda$loadActiveContexts$9(Cursor cursor) {
        List<TransportContext> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(TransportContext.builder().setBackendName(cursor.getString(1)).setPriority(PriorityMapping.valueOf(cursor.getInt(2))).setExtras(maybeBase64Decode(cursor.getString(3))).build());
        }
        return results;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.EventStore
    public int cleanUp() {
        final long oneWeekAgo = this.wallClock.getTime() - this.config.getEventCleanUpAge();
        return ((Integer) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda24
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m20xf6f3aef7(oneWeekAgo, (SQLiteDatabase) obj);
            }
        })).intValue();
    }

    /* renamed from: lambda$cleanUp$12$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Integer m20xf6f3aef7(long oneWeekAgo, SQLiteDatabase db) {
        String[] selectionArgs = {String.valueOf(oneWeekAgo)};
        tryWithCursor(db.rawQuery("SELECT COUNT(*), transport_name FROM events WHERE timestamp_ms < ? GROUP BY transport_name", selectionArgs), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda21
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m19xc84244d8((Cursor) obj);
            }
        });
        return Integer.valueOf(db.delete("events", "timestamp_ms < ?", selectionArgs));
    }

    /* renamed from: lambda$cleanUp$11$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Object m19xc84244d8(Cursor cursor) {
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            String transportName = cursor.getString(1);
            recordLogEventDropped(count, LogEventDropped.Reason.MESSAGE_TOO_OLD, transportName);
        }
        return null;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.schemaManager.close();
    }

    public void clearDb() {
        inTransaction(SQLiteEventStore$$ExternalSyntheticLambda14.INSTANCE);
    }

    public static /* synthetic */ Object lambda$clearDb$13(SQLiteDatabase db) {
        db.delete("events", null, new String[0]);
        db.delete("transport_contexts", null, new String[0]);
        return null;
    }

    private static byte[] maybeBase64Decode(String value) {
        if (value == null) {
            return null;
        }
        return Base64.decode(value, 0);
    }

    private List<PersistedEvent> loadEvents(SQLiteDatabase db, final TransportContext transportContext) {
        final List<PersistedEvent> events = new ArrayList<>();
        Long contextId = getTransportContextId(db, transportContext);
        if (contextId == null) {
            return events;
        }
        tryWithCursor(db.query("events", new String[]{"_id", "transport_name", "timestamp_ms", "uptime_ms", "payload_encoding", "payload", "code", "inline"}, "context_id = ?", new String[]{contextId.toString()}, null, null, null, String.valueOf(this.config.getLoadBatchSize())), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda3
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m25x1b337a6a(events, transportContext, (Cursor) obj);
            }
        });
        return events;
    }

    /* renamed from: lambda$loadEvents$14$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Object m25x1b337a6a(List events, TransportContext transportContext, Cursor cursor) {
        while (cursor.moveToNext()) {
            boolean inline = false;
            long id = cursor.getLong(0);
            if (cursor.getInt(7) != 0) {
                inline = true;
            }
            EventInternal.Builder event = EventInternal.builder().setTransportName(cursor.getString(1)).setEventMillis(cursor.getLong(2)).setUptimeMillis(cursor.getLong(3));
            if (inline) {
                event.setEncodedPayload(new EncodedPayload(toEncoding(cursor.getString(4)), cursor.getBlob(5)));
            } else {
                event.setEncodedPayload(new EncodedPayload(toEncoding(cursor.getString(4)), readPayload(id)));
            }
            if (!cursor.isNull(6)) {
                event.setCode(Integer.valueOf(cursor.getInt(6)));
            }
            events.add(PersistedEvent.create(id, transportContext, event.build()));
        }
        return null;
    }

    private byte[] readPayload(long eventId) {
        return (byte[]) tryWithCursor(getDb().query("event_payloads", new String[]{"bytes"}, "event_id = ?", new String[]{String.valueOf(eventId)}, null, null, "sequence_num"), SQLiteEventStore$$ExternalSyntheticLambda10.INSTANCE);
    }

    public static /* synthetic */ byte[] lambda$readPayload$15(Cursor cursor) {
        List<byte[]> chunks = new ArrayList<>();
        int totalLength = 0;
        while (cursor.moveToNext()) {
            byte[] chunk = cursor.getBlob(0);
            chunks.add(chunk);
            totalLength += chunk.length;
        }
        byte[] payloadBytes = new byte[totalLength];
        int offset = 0;
        for (int i = 0; i < chunks.size(); i++) {
            byte[] chunk2 = chunks.get(i);
            System.arraycopy(chunk2, 0, payloadBytes, offset, chunk2.length);
            offset += chunk2.length;
        }
        return payloadBytes;
    }

    private static Encoding toEncoding(String value) {
        if (value == null) {
            return PROTOBUF_ENCODING;
        }
        return Encoding.of(value);
    }

    private Map<Long, Set<Metadata>> loadMetadata(SQLiteDatabase db, List<PersistedEvent> events) {
        final Map<Long, Set<Metadata>> metadataIndex = new HashMap<>();
        StringBuilder whereClause = new StringBuilder("event_id IN (");
        for (int i = 0; i < events.size(); i++) {
            whereClause.append(events.get(i).getId());
            if (i < events.size() - 1) {
                whereClause.append(',');
            }
        }
        whereClause.append(')');
        tryWithCursor(db.query("event_metadata", new String[]{"event_id", CommonProperties.NAME, CommonProperties.VALUE}, whereClause.toString(), null, null, null, null), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda6
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.lambda$loadMetadata$16(metadataIndex, (Cursor) obj);
            }
        });
        return metadataIndex;
    }

    public static /* synthetic */ Object lambda$loadMetadata$16(Map metadataIndex, Cursor cursor) {
        while (cursor.moveToNext()) {
            long eventId = cursor.getLong(0);
            Set<Metadata> currentSet = (Set) metadataIndex.get(Long.valueOf(eventId));
            if (currentSet == null) {
                currentSet = new HashSet<>();
                metadataIndex.put(Long.valueOf(eventId), currentSet);
            }
            currentSet.add(new Metadata(cursor.getString(1), cursor.getString(2)));
        }
        return null;
    }

    private List<PersistedEvent> join(List<PersistedEvent> events, Map<Long, Set<Metadata>> metadataIndex) {
        ListIterator<PersistedEvent> iterator = events.listIterator();
        while (iterator.hasNext()) {
            PersistedEvent current = iterator.next();
            if (metadataIndex.containsKey(Long.valueOf(current.getId()))) {
                EventInternal.Builder newEvent = current.getEvent().toBuilder();
                for (Metadata metadata : metadataIndex.get(Long.valueOf(current.getId()))) {
                    newEvent.addMetadata(metadata.key, metadata.value);
                }
                iterator.set(PersistedEvent.create(current.getId(), current.getTransportContext(), newEvent.build()));
            }
        }
        return events;
    }

    private <T> T retryIfDbLocked(Producer<T> retriable, Function<Throwable, T> failureHandler) {
        long startTime = this.monotonicClock.getTime();
        while (true) {
            try {
                return retriable.produce();
            } catch (SQLiteDatabaseLockedException ex) {
                if (this.monotonicClock.getTime() >= this.config.getCriticalSectionEnterTimeoutMs() + startTime) {
                    return failureHandler.apply(ex);
                }
                SystemClock.sleep(50L);
            }
        }
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.ClientHealthMetricsStore
    public void recordLogEventDropped(final long eventsDroppedCount, final LogEventDropped.Reason reason, final String logSource) {
        inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda5
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.lambda$recordLogEventDropped$18(logSource, reason, eventsDroppedCount, (SQLiteDatabase) obj);
            }
        });
    }

    public static /* synthetic */ Object lambda$recordLogEventDropped$18(String logSource, LogEventDropped.Reason reason, long eventsDroppedCount, SQLiteDatabase db) {
        String[] selectionArgs = {logSource, Integer.toString(reason.getNumber())};
        boolean isRowExist = ((Boolean) tryWithCursor(db.rawQuery("SELECT 1 FROM log_event_dropped WHERE log_source = ? AND reason = ?", selectionArgs), SQLiteEventStore$$ExternalSyntheticLambda12.INSTANCE)).booleanValue();
        if (!isRowExist) {
            ContentValues metrics = new ContentValues();
            metrics.put("log_source", logSource);
            metrics.put("reason", Integer.valueOf(reason.getNumber()));
            metrics.put("events_dropped_count", Long.valueOf(eventsDroppedCount));
            db.insert("log_event_dropped", null, metrics);
        } else {
            String updateSql = "UPDATE log_event_dropped SET events_dropped_count = events_dropped_count + " + eventsDroppedCount + " WHERE log_source = ? AND reason = ?";
            db.execSQL(updateSql, new String[]{logSource, Integer.toString(reason.getNumber())});
        }
        return null;
    }

    private LogEventDropped.Reason convertToReason(int number) {
        if (number == LogEventDropped.Reason.REASON_UNKNOWN.getNumber()) {
            return LogEventDropped.Reason.REASON_UNKNOWN;
        }
        if (number == LogEventDropped.Reason.MESSAGE_TOO_OLD.getNumber()) {
            return LogEventDropped.Reason.MESSAGE_TOO_OLD;
        }
        if (number == LogEventDropped.Reason.CACHE_FULL.getNumber()) {
            return LogEventDropped.Reason.CACHE_FULL;
        }
        if (number == LogEventDropped.Reason.PAYLOAD_TOO_BIG.getNumber()) {
            return LogEventDropped.Reason.PAYLOAD_TOO_BIG;
        }
        if (number == LogEventDropped.Reason.MAX_RETRIES_REACHED.getNumber()) {
            return LogEventDropped.Reason.MAX_RETRIES_REACHED;
        }
        if (number == LogEventDropped.Reason.INVALID_PAYLOD.getNumber()) {
            return LogEventDropped.Reason.INVALID_PAYLOD;
        }
        if (number == LogEventDropped.Reason.SERVER_ERROR.getNumber()) {
            return LogEventDropped.Reason.SERVER_ERROR;
        }
        Logging.d(LOG_TAG, "%n is not valid. No matched LogEventDropped-Reason found. Treated it as REASON_UNKNOWN", Integer.valueOf(number));
        return LogEventDropped.Reason.REASON_UNKNOWN;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.ClientHealthMetricsStore
    public ClientMetrics loadClientMetrics() {
        final ClientMetrics.Builder clientMetricsBuilder = ClientMetrics.newBuilder();
        final Map<String, List<LogEventDropped>> metricsMap = new HashMap<>();
        return (ClientMetrics) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda2
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m24xdd9aea28(r2, metricsMap, clientMetricsBuilder, (SQLiteDatabase) obj);
            }
        });
    }

    /* renamed from: lambda$loadClientMetrics$20$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ ClientMetrics m24xdd9aea28(String query, final Map metricsMap, final ClientMetrics.Builder clientMetricsBuilder, SQLiteDatabase db) {
        return (ClientMetrics) tryWithCursor(db.rawQuery(query, new String[0]), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda4
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m23xda5bcb7e(metricsMap, clientMetricsBuilder, (Cursor) obj);
            }
        });
    }

    /* renamed from: lambda$loadClientMetrics$19$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ ClientMetrics m23xda5bcb7e(Map metricsMap, ClientMetrics.Builder clientMetricsBuilder, Cursor cursor) {
        while (cursor.moveToNext()) {
            String logSource = cursor.getString(0);
            LogEventDropped.Reason reason = convertToReason(cursor.getInt(1));
            long eventsDroppedCount = cursor.getLong(2);
            if (!metricsMap.containsKey(logSource)) {
                metricsMap.put(logSource, new ArrayList());
            }
            ((List) metricsMap.get(logSource)).add(LogEventDropped.newBuilder().setReason(reason).setEventsDroppedCount(eventsDroppedCount).build());
        }
        populateLogSourcesMetrics(clientMetricsBuilder, metricsMap);
        clientMetricsBuilder.setWindow(getTimeWindow());
        clientMetricsBuilder.setGlobalMetrics(getGlobalMetrics());
        clientMetricsBuilder.setAppNamespace(this.packageName.get());
        return clientMetricsBuilder.build();
    }

    private void populateLogSourcesMetrics(ClientMetrics.Builder clientMetricsBuilder, Map<String, List<LogEventDropped>> metricsMap) {
        for (Map.Entry<String, List<LogEventDropped>> entry : metricsMap.entrySet()) {
            clientMetricsBuilder.addLogSourceMetrics(LogSourceMetrics.newBuilder().setLogSource(entry.getKey()).setLogEventDroppedList(entry.getValue()).build());
        }
    }

    private TimeWindow getTimeWindow() {
        final long currentTime = this.wallClock.getTime();
        return (TimeWindow) inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda11
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.lambda$getTimeWindow$22(currentTime, (SQLiteDatabase) obj);
            }
        });
    }

    public static /* synthetic */ TimeWindow lambda$getTimeWindow$22(final long currentTime, SQLiteDatabase db) {
        return (TimeWindow) tryWithCursor(db.rawQuery("SELECT last_metrics_upload_ms FROM global_log_event_state LIMIT 1", new String[0]), new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda0
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.lambda$getTimeWindow$21(currentTime, (Cursor) obj);
            }
        });
    }

    public static /* synthetic */ TimeWindow lambda$getTimeWindow$21(long currentTime, Cursor cursor) {
        cursor.moveToNext();
        long start_ms = cursor.getLong(0);
        return TimeWindow.newBuilder().setStartMs(start_ms).setEndMs(currentTime).build();
    }

    private GlobalMetrics getGlobalMetrics() {
        return GlobalMetrics.newBuilder().setStorageMetrics(StorageMetrics.newBuilder().setCurrentCacheSizeBytes(getByteSize()).setMaxCacheSizeBytes(EventStoreConfig.DEFAULT.getMaxStorageSizeInBytes()).build()).build();
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.ClientHealthMetricsStore
    public void resetClientMetrics() {
        inTransaction(new Function() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda23
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Function
            public final Object apply(Object obj) {
                return SQLiteEventStore.this.m29x18ea3bd6((SQLiteDatabase) obj);
            }
        });
    }

    /* renamed from: lambda$resetClientMetrics$23$com-google-android-datatransport-runtime-scheduling-persistence-SQLiteEventStore */
    public /* synthetic */ Object m29x18ea3bd6(SQLiteDatabase db) {
        db.compileStatement("DELETE FROM log_event_dropped").execute();
        db.compileStatement("UPDATE global_log_event_state SET last_metrics_upload_ms=" + this.wallClock.getTime()).execute();
        return null;
    }

    private void ensureBeginTransaction(final SQLiteDatabase db) {
        retryIfDbLocked(new Producer() { // from class: com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore$$ExternalSyntheticLambda18
            @Override // com.google.android.datatransport.runtime.scheduling.persistence.SQLiteEventStore.Producer
            public final Object produce() {
                return db.beginTransaction();
            }
        }, SQLiteEventStore$$ExternalSyntheticLambda16.INSTANCE);
    }

    public static /* synthetic */ Object lambda$ensureBeginTransaction$25(Throwable ex) {
        throw new SynchronizationException("Timed out while trying to acquire the lock.", ex);
    }

    @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard
    public <T> T runCriticalSection(SynchronizationGuard.CriticalSection<T> criticalSection) {
        SQLiteDatabase db = getDb();
        ensureBeginTransaction(db);
        try {
            T result = criticalSection.execute();
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    <T> T inTransaction(Function<SQLiteDatabase, T> function) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        try {
            T result = function.apply(db);
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    /* loaded from: classes3.dex */
    public static class Metadata {
        final String key;
        final String value;

        private Metadata(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private boolean isStorageAtLimit() {
        long byteSize = getPageCount() * getPageSize();
        return byteSize >= this.config.getMaxStorageSizeInBytes();
    }

    long getByteSize() {
        return getPageCount() * getPageSize();
    }

    private long getPageSize() {
        return getDb().compileStatement("PRAGMA page_size").simpleQueryForLong();
    }

    private long getPageCount() {
        return getDb().compileStatement("PRAGMA page_count").simpleQueryForLong();
    }

    static <T> T tryWithCursor(Cursor c, Function<Cursor, T> function) {
        try {
            return function.apply(c);
        } finally {
            c.close();
        }
    }
}
