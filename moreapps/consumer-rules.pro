# kotlinx.serialization keeps its own generated serializers through the rules bundled with the
# runtime; nothing here is read by reflection. WorkManager instantiates the worker by name.
-keep class io.github.raghavsatyadev.moreapps.worker.UpdateCheckWorker { <init>(...); }
