package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
/* loaded from: classes.dex */
public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private MediaController.VideoConvertorListener callback;
    private long endPresentationTime;
    private MediaExtractor extractor;
    private MP4Builder mediaMuxer;

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, boolean z4, MediaController.VideoConvertorListener videoConvertorListener) {
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, i7, i8, j, j2, j3, j4, z2, false, savedFilterState, str2, arrayList, z3, cropState, z4);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:(25:(2:392|393)(2:399|(47:401|402|1260|403|404|1308|(3:413|(1:418)(1:417)|419)(1:425)|426|(1:428)|429|430|(3:432|433|(39:435|(1:437)|1284|438|439|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(8:454|1314|455|456|1320|457|458|(23:460|1187|461|1235|479|480|1217|481|482|(2:1163|484)(1:488)|489|(7:1270|491|(3:1257|493|(4:495|499|(1:501)|(11:(9:1318|504|1231|505|(1:510)|511|512|(2:514|515)(2:1213|516)|517)(12:524|1250|525|526|(3:528|1240|529)(2:532|533)|534|1151|535|536|1282|537|538)|(1:558)(1:559)|1255|560|561|(2:(6:1262|566|(1:576)(4:569|1177|570|571)|(5:1298|578|579|(5:581|1258|582|(4:584|(1:586)(1:587)|588|(1:590)(1:591))(1:592)|593)(5:597|(2:599|(1:(16:1160|602|603|(4:1206|605|606|(3:608|1209|609))(1:614)|615|1149|616|617|(1:619)|620|(1:622)(2:623|624)|625|(3:632|633|(10:637|1288|638|639|(1:641)|642|1296|643|649|663))|648|649|663))(3:659|(1:662)|663))|660|(0)|663)|(2:667|668))(1:678)|679|(1:(11:1165|683|(1:685)|686|1169|687|688|(1:690)(2:692|(4:1328|694|(1:696)|697)(2:704|(3:706|(1:730)(7:709|710|1306|711|(3:713|714|(5:716|1300|717|718|723))(1:721)|722|723)|731)(3:732|1138|(4:734|735|(1:737)(1:738)|(12:740|741|(12:1276|743|744|(5:(1:753)(3:749|1191|750)|(3:757|(2:759|(2:760|(1:1365)(3:762|(2:773|1366)(2:768|(2:1364|772))|774)))(0)|775)|1219|776|(2:778|(5:780|1233|781|(1:783)|784)))(2:788|(14:790|(3:794|(2:800|(2:1371|802)(1:1375))|803)|804|805|(1:808)|809|810|1302|818|(1:820)(1:821)|822|823|(3:1360|825|1362)(5:1358|(7:827|1286|828|829|(1:831)(2:832|(2:834|(2:836|(1:838))(1:(20:845|(1:847)(1:848)|849|850|(1:857)(3:854|855|856)|858|(4:860|1145|861|(6:863|864|1143|865|866|(16:868|(3:1252|870|871)(4:872|873|874|875)|876|1330|877|878|892|(4:894|1158|895|(1:899))(1:902)|903|(1:905)(1:906)|907|(1:922)(2:911|(3:913|(1:915)(1:916)|917)(3:918|(1:920)|921))|(1:924)(4:925|(1:929)|1244|930)|(8:932|933|(1:935)(1:936)|1136|937|938|(4:943|944|1256|945)(1:946)|947)(1:948)|949|(3:951|(1:953)|954)(1:955))(13:883|891|892|(0)(0)|903|(0)(0)|907|(1:909)|922|(0)(0)|(0)(0)|949|(0)(0))))(1:889)|890|891|892|(0)(0)|903|(0)(0)|907|(0)|922|(0)(0)|(0)(0)|949|(0)(0))(1:1353))))|972|1363)(1:970)|971|972|1363)|1361))|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(1:816)|817|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(4:1355|977|978|979))(3:1354|980|981))))|691|(0)(0)|1361)))|682)|565|1237|1060|(1:1062)|1063)(1:546)))|498|499|(0)|(0)(0))(1:555)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(1:464))(1:477)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063))(1:444)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(6:407|1274|408|409|1264|410))|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)|1167|446|447|1147|448|1199|449|1175|450|451) */
    /* JADX WARN: Can't wrap try/catch for region: R(25:(2:392|393)(2:399|(47:401|402|1260|403|404|1308|(3:413|(1:418)(1:417)|419)(1:425)|426|(1:428)|429|430|(3:432|433|(39:435|(1:437)|1284|438|439|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(8:454|1314|455|456|1320|457|458|(23:460|1187|461|1235|479|480|1217|481|482|(2:1163|484)(1:488)|489|(7:1270|491|(3:1257|493|(4:495|499|(1:501)|(11:(9:1318|504|1231|505|(1:510)|511|512|(2:514|515)(2:1213|516)|517)(12:524|1250|525|526|(3:528|1240|529)(2:532|533)|534|1151|535|536|1282|537|538)|(1:558)(1:559)|1255|560|561|(2:(6:1262|566|(1:576)(4:569|1177|570|571)|(5:1298|578|579|(5:581|1258|582|(4:584|(1:586)(1:587)|588|(1:590)(1:591))(1:592)|593)(5:597|(2:599|(1:(16:1160|602|603|(4:1206|605|606|(3:608|1209|609))(1:614)|615|1149|616|617|(1:619)|620|(1:622)(2:623|624)|625|(3:632|633|(10:637|1288|638|639|(1:641)|642|1296|643|649|663))|648|649|663))(3:659|(1:662)|663))|660|(0)|663)|(2:667|668))(1:678)|679|(1:(11:1165|683|(1:685)|686|1169|687|688|(1:690)(2:692|(4:1328|694|(1:696)|697)(2:704|(3:706|(1:730)(7:709|710|1306|711|(3:713|714|(5:716|1300|717|718|723))(1:721)|722|723)|731)(3:732|1138|(4:734|735|(1:737)(1:738)|(12:740|741|(12:1276|743|744|(5:(1:753)(3:749|1191|750)|(3:757|(2:759|(2:760|(1:1365)(3:762|(2:773|1366)(2:768|(2:1364|772))|774)))(0)|775)|1219|776|(2:778|(5:780|1233|781|(1:783)|784)))(2:788|(14:790|(3:794|(2:800|(2:1371|802)(1:1375))|803)|804|805|(1:808)|809|810|1302|818|(1:820)(1:821)|822|823|(3:1360|825|1362)(5:1358|(7:827|1286|828|829|(1:831)(2:832|(2:834|(2:836|(1:838))(1:(20:845|(1:847)(1:848)|849|850|(1:857)(3:854|855|856)|858|(4:860|1145|861|(6:863|864|1143|865|866|(16:868|(3:1252|870|871)(4:872|873|874|875)|876|1330|877|878|892|(4:894|1158|895|(1:899))(1:902)|903|(1:905)(1:906)|907|(1:922)(2:911|(3:913|(1:915)(1:916)|917)(3:918|(1:920)|921))|(1:924)(4:925|(1:929)|1244|930)|(8:932|933|(1:935)(1:936)|1136|937|938|(4:943|944|1256|945)(1:946)|947)(1:948)|949|(3:951|(1:953)|954)(1:955))(13:883|891|892|(0)(0)|903|(0)(0)|907|(1:909)|922|(0)(0)|(0)(0)|949|(0)(0))))(1:889)|890|891|892|(0)(0)|903|(0)(0)|907|(0)|922|(0)(0)|(0)(0)|949|(0)(0))(1:1353))))|972|1363)(1:970)|971|972|1363)|1361))|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(1:816)|817|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(4:1355|977|978|979))(3:1354|980|981))))|691|(0)(0)|1361)))|682)|565|1237|1060|(1:1062)|1063)(1:546)))|498|499|(0)|(0)(0))(1:555)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(1:464))(1:477)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063))(1:444)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(6:407|1274|408|409|1264|410))|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063) */
    /* JADX WARN: Can't wrap try/catch for region: R(35:(2:392|393)(2:399|(47:401|402|1260|403|404|1308|(3:413|(1:418)(1:417)|419)(1:425)|426|(1:428)|429|430|(3:432|433|(39:435|(1:437)|1284|438|439|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(8:454|1314|455|456|1320|457|458|(23:460|1187|461|1235|479|480|1217|481|482|(2:1163|484)(1:488)|489|(7:1270|491|(3:1257|493|(4:495|499|(1:501)|(11:(9:1318|504|1231|505|(1:510)|511|512|(2:514|515)(2:1213|516)|517)(12:524|1250|525|526|(3:528|1240|529)(2:532|533)|534|1151|535|536|1282|537|538)|(1:558)(1:559)|1255|560|561|(2:(6:1262|566|(1:576)(4:569|1177|570|571)|(5:1298|578|579|(5:581|1258|582|(4:584|(1:586)(1:587)|588|(1:590)(1:591))(1:592)|593)(5:597|(2:599|(1:(16:1160|602|603|(4:1206|605|606|(3:608|1209|609))(1:614)|615|1149|616|617|(1:619)|620|(1:622)(2:623|624)|625|(3:632|633|(10:637|1288|638|639|(1:641)|642|1296|643|649|663))|648|649|663))(3:659|(1:662)|663))|660|(0)|663)|(2:667|668))(1:678)|679|(1:(11:1165|683|(1:685)|686|1169|687|688|(1:690)(2:692|(4:1328|694|(1:696)|697)(2:704|(3:706|(1:730)(7:709|710|1306|711|(3:713|714|(5:716|1300|717|718|723))(1:721)|722|723)|731)(3:732|1138|(4:734|735|(1:737)(1:738)|(12:740|741|(12:1276|743|744|(5:(1:753)(3:749|1191|750)|(3:757|(2:759|(2:760|(1:1365)(3:762|(2:773|1366)(2:768|(2:1364|772))|774)))(0)|775)|1219|776|(2:778|(5:780|1233|781|(1:783)|784)))(2:788|(14:790|(3:794|(2:800|(2:1371|802)(1:1375))|803)|804|805|(1:808)|809|810|1302|818|(1:820)(1:821)|822|823|(3:1360|825|1362)(5:1358|(7:827|1286|828|829|(1:831)(2:832|(2:834|(2:836|(1:838))(1:(20:845|(1:847)(1:848)|849|850|(1:857)(3:854|855|856)|858|(4:860|1145|861|(6:863|864|1143|865|866|(16:868|(3:1252|870|871)(4:872|873|874|875)|876|1330|877|878|892|(4:894|1158|895|(1:899))(1:902)|903|(1:905)(1:906)|907|(1:922)(2:911|(3:913|(1:915)(1:916)|917)(3:918|(1:920)|921))|(1:924)(4:925|(1:929)|1244|930)|(8:932|933|(1:935)(1:936)|1136|937|938|(4:943|944|1256|945)(1:946)|947)(1:948)|949|(3:951|(1:953)|954)(1:955))(13:883|891|892|(0)(0)|903|(0)(0)|907|(1:909)|922|(0)(0)|(0)(0)|949|(0)(0))))(1:889)|890|891|892|(0)(0)|903|(0)(0)|907|(0)|922|(0)(0)|(0)(0)|949|(0)(0))(1:1353))))|972|1363)(1:970)|971|972|1363)|1361))|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(1:816)|817|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(4:1355|977|978|979))(3:1354|980|981))))|691|(0)(0)|1361)))|682)|565|1237|1060|(1:1062)|1063)(1:546)))|498|499|(0)|(0)(0))(1:555)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(1:464))(1:477)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063))(1:444)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(6:407|1274|408|409|1264|410))|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063) */
    /* JADX WARN: Can't wrap try/catch for region: R(46:350|(14:1290|351|352|(3:354|1294|355)(2:361|362)|356|363|364|(3:366|(1:368)(2:369|(1:371)(1:372))|373)(1:(1:375)(1:376))|377|(2:1272|379)|386|(1:388)(1:389)|390|1162)|(2:392|393)(2:399|(47:401|402|1260|403|404|1308|(3:413|(1:418)(1:417)|419)(1:425)|426|(1:428)|429|430|(3:432|433|(39:435|(1:437)|1284|438|439|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(8:454|1314|455|456|1320|457|458|(23:460|1187|461|1235|479|480|1217|481|482|(2:1163|484)(1:488)|489|(7:1270|491|(3:1257|493|(4:495|499|(1:501)|(11:(9:1318|504|1231|505|(1:510)|511|512|(2:514|515)(2:1213|516)|517)(12:524|1250|525|526|(3:528|1240|529)(2:532|533)|534|1151|535|536|1282|537|538)|(1:558)(1:559)|1255|560|561|(2:(6:1262|566|(1:576)(4:569|1177|570|571)|(5:1298|578|579|(5:581|1258|582|(4:584|(1:586)(1:587)|588|(1:590)(1:591))(1:592)|593)(5:597|(2:599|(1:(16:1160|602|603|(4:1206|605|606|(3:608|1209|609))(1:614)|615|1149|616|617|(1:619)|620|(1:622)(2:623|624)|625|(3:632|633|(10:637|1288|638|639|(1:641)|642|1296|643|649|663))|648|649|663))(3:659|(1:662)|663))|660|(0)|663)|(2:667|668))(1:678)|679|(1:(11:1165|683|(1:685)|686|1169|687|688|(1:690)(2:692|(4:1328|694|(1:696)|697)(2:704|(3:706|(1:730)(7:709|710|1306|711|(3:713|714|(5:716|1300|717|718|723))(1:721)|722|723)|731)(3:732|1138|(4:734|735|(1:737)(1:738)|(12:740|741|(12:1276|743|744|(5:(1:753)(3:749|1191|750)|(3:757|(2:759|(2:760|(1:1365)(3:762|(2:773|1366)(2:768|(2:1364|772))|774)))(0)|775)|1219|776|(2:778|(5:780|1233|781|(1:783)|784)))(2:788|(14:790|(3:794|(2:800|(2:1371|802)(1:1375))|803)|804|805|(1:808)|809|810|1302|818|(1:820)(1:821)|822|823|(3:1360|825|1362)(5:1358|(7:827|1286|828|829|(1:831)(2:832|(2:834|(2:836|(1:838))(1:(20:845|(1:847)(1:848)|849|850|(1:857)(3:854|855|856)|858|(4:860|1145|861|(6:863|864|1143|865|866|(16:868|(3:1252|870|871)(4:872|873|874|875)|876|1330|877|878|892|(4:894|1158|895|(1:899))(1:902)|903|(1:905)(1:906)|907|(1:922)(2:911|(3:913|(1:915)(1:916)|917)(3:918|(1:920)|921))|(1:924)(4:925|(1:929)|1244|930)|(8:932|933|(1:935)(1:936)|1136|937|938|(4:943|944|1256|945)(1:946)|947)(1:948)|949|(3:951|(1:953)|954)(1:955))(13:883|891|892|(0)(0)|903|(0)(0)|907|(1:909)|922|(0)(0)|(0)(0)|949|(0)(0))))(1:889)|890|891|892|(0)(0)|903|(0)(0)|907|(0)|922|(0)(0)|(0)(0)|949|(0)(0))(1:1353))))|972|1363)(1:970)|971|972|1363)|1361))|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(1:816)|817|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(4:1355|977|978|979))(3:1354|980|981))))|691|(0)(0)|1361)))|682)|565|1237|1060|(1:1062)|1063)(1:546)))|498|499|(0)|(0)(0))(1:555)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(1:464))(1:477)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063))(1:444)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(6:407|1274|408|409|1264|410))|411|1308|(0)(0)|426|(0)|429|430|(0)(0)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063) */
    /* JADX WARN: Can't wrap try/catch for region: R(59:350|1290|351|352|(3:354|1294|355)(2:361|362)|356|363|364|(3:366|(1:368)(2:369|(1:371)(1:372))|373)(1:(1:375)(1:376))|377|(2:1272|379)|386|(1:388)(1:389)|390|1162|(2:392|393)(2:399|(47:401|402|1260|403|404|1308|(3:413|(1:418)(1:417)|419)(1:425)|426|(1:428)|429|430|(3:432|433|(39:435|(1:437)|1284|438|439|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(8:454|1314|455|456|1320|457|458|(23:460|1187|461|1235|479|480|1217|481|482|(2:1163|484)(1:488)|489|(7:1270|491|(3:1257|493|(4:495|499|(1:501)|(11:(9:1318|504|1231|505|(1:510)|511|512|(2:514|515)(2:1213|516)|517)(12:524|1250|525|526|(3:528|1240|529)(2:532|533)|534|1151|535|536|1282|537|538)|(1:558)(1:559)|1255|560|561|(2:(6:1262|566|(1:576)(4:569|1177|570|571)|(5:1298|578|579|(5:581|1258|582|(4:584|(1:586)(1:587)|588|(1:590)(1:591))(1:592)|593)(5:597|(2:599|(1:(16:1160|602|603|(4:1206|605|606|(3:608|1209|609))(1:614)|615|1149|616|617|(1:619)|620|(1:622)(2:623|624)|625|(3:632|633|(10:637|1288|638|639|(1:641)|642|1296|643|649|663))|648|649|663))(3:659|(1:662)|663))|660|(0)|663)|(2:667|668))(1:678)|679|(1:(11:1165|683|(1:685)|686|1169|687|688|(1:690)(2:692|(4:1328|694|(1:696)|697)(2:704|(3:706|(1:730)(7:709|710|1306|711|(3:713|714|(5:716|1300|717|718|723))(1:721)|722|723)|731)(3:732|1138|(4:734|735|(1:737)(1:738)|(12:740|741|(12:1276|743|744|(5:(1:753)(3:749|1191|750)|(3:757|(2:759|(2:760|(1:1365)(3:762|(2:773|1366)(2:768|(2:1364|772))|774)))(0)|775)|1219|776|(2:778|(5:780|1233|781|(1:783)|784)))(2:788|(14:790|(3:794|(2:800|(2:1371|802)(1:1375))|803)|804|805|(1:808)|809|810|1302|818|(1:820)(1:821)|822|823|(3:1360|825|1362)(5:1358|(7:827|1286|828|829|(1:831)(2:832|(2:834|(2:836|(1:838))(1:(20:845|(1:847)(1:848)|849|850|(1:857)(3:854|855|856)|858|(4:860|1145|861|(6:863|864|1143|865|866|(16:868|(3:1252|870|871)(4:872|873|874|875)|876|1330|877|878|892|(4:894|1158|895|(1:899))(1:902)|903|(1:905)(1:906)|907|(1:922)(2:911|(3:913|(1:915)(1:916)|917)(3:918|(1:920)|921))|(1:924)(4:925|(1:929)|1244|930)|(8:932|933|(1:935)(1:936)|1136|937|938|(4:943|944|1256|945)(1:946)|947)(1:948)|949|(3:951|(1:953)|954)(1:955))(13:883|891|892|(0)(0)|903|(0)(0)|907|(1:909)|922|(0)(0)|(0)(0)|949|(0)(0))))(1:889)|890|891|892|(0)(0)|903|(0)(0)|907|(0)|922|(0)(0)|(0)(0)|949|(0)(0))(1:1353))))|972|1363)(1:970)|971|972|1363)|1361))|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(1:816)|817|785|1302|818|(0)(0)|822|823|(0)(0)|1361)(4:1355|977|978|979))(3:1354|980|981))))|691|(0)(0)|1361)))|682)|565|1237|1060|(1:1062)|1063)(1:546)))|498|499|(0)|(0)(0))(1:555)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(1:464))(1:477)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063))(1:444)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063)(6:407|1274|408|409|1264|410))|411|1308|(0)(0)|426|(0)|429|430|(0)(0)|445|1167|446|447|1147|448|1199|449|1175|450|451|1193|452|(0)(0)|478|1235|479|480|1217|481|482|(0)(0)|489|(0)(0)|556|(0)(0)|1255|560|561|(9:(0)|1262|566|(0)|576|(0)(0)|679|(12:(0)|1165|683|(0)|686|1169|687|688|(0)(0)|691|(0)(0)|1361)|682)|565|1237|1060|(0)|1063) */
    /* JADX WARN: Code restructure failed: missing block: B:1005:0x134e, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1006:0x134f, code lost:
        r10 = r87;
        r5 = r88;
        r69 = r9;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1007:0x1368, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1008:0x1369, code lost:
        r10 = r87;
        r5 = r88;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1009:0x137c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1010:0x137d, code lost:
        r10 = r87;
        r5 = r88;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r8 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1011:0x1391, code lost:
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1014:0x139c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1015:0x139d, code lost:
        r10 = r87;
        r5 = r88;
        r14 = r2;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1016:0x13af, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1017:0x13b0, code lost:
        r10 = r87;
        r94 = r14;
        r71 = r30;
        r15 = r78;
        r14 = r2;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r54 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1018:0x13c1, code lost:
        r8 = r14;
        r3 = r21;
        r13 = -5;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1019:0x13c7, code lost:
        r69 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1021:0x13cc, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1022:0x13cd, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r54 = r4;
        r14 = r5;
        r3 = r21;
        r8 = null;
        r13 = -5;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1023:0x13e6, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1024:0x13e7, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r14 = r5;
        r3 = r21;
        r8 = null;
        r13 = -5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1030:0x1411, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1031:0x1412, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r3 = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:420:0x087b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x087c, code lost:
        r2 = r85;
        r72 = r92;
        r1 = r0;
        r7 = r3;
        r10 = r87;
        r44 = r14;
        r6 = false;
        r13 = -5;
        r15 = r78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:422:0x088b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x088c, code lost:
        r72 = r92;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01fb, code lost:
        r6 = r7;
        r13 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:958:0x122b, code lost:
        r10 = r87;
        r93 = r11;
        r4 = r54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:959:0x1247, code lost:
        throw new java.lang.RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + r1);
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1051:0x1480 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:1062:0x14cc A[Catch: all -> 0x14dc, TRY_LEAVE, TryCatch #108 {all -> 0x14dc, blocks: (B:1060:0x14c3, B:1062:0x14cc), top: B:1237:0x14c3 }] */
    /* JADX WARN: Removed duplicated region for block: B:1075:0x1502  */
    /* JADX WARN: Removed duplicated region for block: B:1082:0x152d A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1077:0x151d, B:1082:0x152d, B:1084:0x1532, B:1086:0x153a, B:1087:0x153d), top: B:1132:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1084:0x1532 A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1077:0x151d, B:1082:0x152d, B:1084:0x1532, B:1086:0x153a, B:1087:0x153d), top: B:1132:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1086:0x153a A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1077:0x151d, B:1082:0x152d, B:1084:0x1532, B:1086:0x153a, B:1087:0x153d), top: B:1132:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1091:0x1548  */
    /* JADX WARN: Removed duplicated region for block: B:1108:0x15b5  */
    /* JADX WARN: Removed duplicated region for block: B:1116:0x15d3  */
    /* JADX WARN: Removed duplicated region for block: B:1118:0x1602  */
    /* JADX WARN: Removed duplicated region for block: B:1132:0x151d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1156:0x0643 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1163:0x0a0d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1171:0x154f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1225:0x15bc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1270:0x0a2e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1298:0x0bd0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1335:0x045b A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1337:0x044c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1358:0x1028 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1360:0x1009 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:199:0x043a  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x043c  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x05fc A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:302:0x0658 A[Catch: all -> 0x0647, TryCatch #24 {all -> 0x0647, blocks: (B:298:0x0643, B:302:0x0658, B:304:0x065d, B:305:0x0663), top: B:1156:0x0643 }] */
    /* JADX WARN: Removed duplicated region for block: B:304:0x065d A[Catch: all -> 0x0647, TryCatch #24 {all -> 0x0647, blocks: (B:298:0x0643, B:302:0x0658, B:304:0x065d, B:305:0x0663), top: B:1156:0x0643 }] */
    /* JADX WARN: Removed duplicated region for block: B:350:0x0744  */
    /* JADX WARN: Removed duplicated region for block: B:413:0x0861  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x08a1  */
    /* JADX WARN: Removed duplicated region for block: B:428:0x08ab A[Catch: all -> 0x087b, Exception -> 0x088b, TRY_ENTER, TRY_LEAVE, TryCatch #129 {Exception -> 0x088b, all -> 0x087b, blocks: (B:417:0x086c, B:418:0x0871, B:428:0x08ab, B:432:0x08e9), top: B:1308:0x085f }] */
    /* JADX WARN: Removed duplicated region for block: B:432:0x08e9 A[Catch: all -> 0x087b, Exception -> 0x088b, TRY_ENTER, TRY_LEAVE, TryCatch #129 {Exception -> 0x088b, all -> 0x087b, blocks: (B:417:0x086c, B:418:0x0871, B:428:0x08ab, B:432:0x08e9), top: B:1308:0x085f }] */
    /* JADX WARN: Removed duplicated region for block: B:444:0x091b  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x0989  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x09ee  */
    /* JADX WARN: Removed duplicated region for block: B:488:0x0a28  */
    /* JADX WARN: Removed duplicated region for block: B:501:0x0a68  */
    /* JADX WARN: Removed duplicated region for block: B:503:0x0a6b  */
    /* JADX WARN: Removed duplicated region for block: B:546:0x0b21  */
    /* JADX WARN: Removed duplicated region for block: B:555:0x0b4e  */
    /* JADX WARN: Removed duplicated region for block: B:558:0x0b5c  */
    /* JADX WARN: Removed duplicated region for block: B:559:0x0b5e  */
    /* JADX WARN: Removed duplicated region for block: B:563:0x0b7f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:568:0x0b9f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:662:0x0d44  */
    /* JADX WARN: Removed duplicated region for block: B:678:0x0d84  */
    /* JADX WARN: Removed duplicated region for block: B:681:0x0da5 A[ADDED_TO_REGION, EDGE_INSN: B:681:0x0da5->B:1359:0x0da8 ?: BREAK  ] */
    /* JADX WARN: Removed duplicated region for block: B:685:0x0dc8  */
    /* JADX WARN: Removed duplicated region for block: B:690:0x0dd9  */
    /* JADX WARN: Removed duplicated region for block: B:692:0x0df1  */
    /* JADX WARN: Removed duplicated region for block: B:820:0x0ffa  */
    /* JADX WARN: Removed duplicated region for block: B:821:0x0ffc  */
    /* JADX WARN: Removed duplicated region for block: B:894:0x1156  */
    /* JADX WARN: Removed duplicated region for block: B:902:0x116b  */
    /* JADX WARN: Removed duplicated region for block: B:905:0x1173  */
    /* JADX WARN: Removed duplicated region for block: B:906:0x1177  */
    /* JADX WARN: Removed duplicated region for block: B:909:0x117e  */
    /* JADX WARN: Removed duplicated region for block: B:924:0x11bf  */
    /* JADX WARN: Removed duplicated region for block: B:925:0x11c2  */
    /* JADX WARN: Removed duplicated region for block: B:932:0x11d7 A[Catch: Exception -> 0x1229, all -> 0x1248, TRY_LEAVE, TryCatch #25 {all -> 0x1248, blocks: (B:895:0x1158, B:897:0x1160, B:913:0x1186, B:915:0x118a, B:918:0x11ad, B:920:0x11b7, B:929:0x11cc, B:930:0x11d2, B:932:0x11d7, B:935:0x11df, B:937:0x11e6, B:941:0x11ed, B:943:0x11f3, B:945:0x11fe, B:949:0x1210, B:951:0x1216, B:953:0x121a, B:954:0x121f, B:958:0x122b, B:959:0x1247), top: B:1158:0x1158 }] */
    /* JADX WARN: Removed duplicated region for block: B:948:0x120a  */
    /* JADX WARN: Removed duplicated region for block: B:951:0x1216 A[Catch: all -> 0x1248, Exception -> 0x124a, TryCatch #25 {all -> 0x1248, blocks: (B:895:0x1158, B:897:0x1160, B:913:0x1186, B:915:0x118a, B:918:0x11ad, B:920:0x11b7, B:929:0x11cc, B:930:0x11d2, B:932:0x11d7, B:935:0x11df, B:937:0x11e6, B:941:0x11ed, B:943:0x11f3, B:945:0x11fe, B:949:0x1210, B:951:0x1216, B:953:0x121a, B:954:0x121f, B:958:0x122b, B:959:0x1247), top: B:1158:0x1158 }] */
    /* JADX WARN: Removed duplicated region for block: B:955:0x1225  */
    /* JADX WARN: Type inference failed for: r44v185 */
    /* JADX WARN: Type inference failed for: r44v186 */
    /* JADX WARN: Type inference failed for: r44v187 */
    /* JADX WARN: Type inference failed for: r44v188 */
    /* JADX WARN: Type inference failed for: r44v72 */
    /* JADX WARN: Type inference failed for: r44v73 */
    /* JADX WARN: Type inference failed for: r4v123 */
    /* JADX WARN: Type inference failed for: r4v185 */
    /* JADX WARN: Type inference failed for: r4v188 */
    /* JADX WARN: Type inference failed for: r4v189 */
    /* JADX WARN: Type inference failed for: r4v190 */
    /* JADX WARN: Type inference failed for: r4v191 */
    /* JADX WARN: Type inference failed for: r4v192 */
    /* JADX WARN: Type inference failed for: r4v193 */
    /* JADX WARN: Type inference failed for: r4v194 */
    /* JADX WARN: Type inference failed for: r4v38 */
    /* JADX WARN: Type inference failed for: r4v40 */
    /* JADX WARN: Type inference failed for: r4v41 */
    /* JADX WARN: Type inference failed for: r4v46, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r4v47 */
    /* JADX WARN: Type inference failed for: r4v48 */
    /* JADX WARN: Type inference failed for: r4v53 */
    /* JADX WARN: Type inference failed for: r4v54 */
    /* JADX WARN: Type inference failed for: r4v56 */
    /* JADX WARN: Type inference failed for: r4v57 */
    /* JADX WARN: Type inference failed for: r5v42, types: [android.media.MediaExtractor] */
    /* JADX WARN: Type inference failed for: r5v48, types: [org.telegram.messenger.video.MP4Builder] */
    /* JADX WARN: Type inference failed for: r9v40 */
    /* JADX WARN: Type inference failed for: r9v41, types: [org.telegram.messenger.video.InputSurface] */
    /* JADX WARN: Type inference failed for: r9v53, types: [org.telegram.messenger.video.InputSurface] */
    /* JADX WARN: Type inference failed for: r9v55 */
    /* JADX WARN: Type inference failed for: r9v60, types: [org.telegram.messenger.video.InputSurface] */
    @android.annotation.TargetApi(18)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean convertVideoInternal(java.lang.String r79, java.io.File r80, int r81, boolean r82, int r83, int r84, int r85, int r86, int r87, int r88, int r89, long r90, long r92, long r94, long r96, boolean r98, boolean r99, org.telegram.messenger.MediaController.SavedFilterState r100, java.lang.String r101, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r102, boolean r103, org.telegram.messenger.MediaController.CropState r104, boolean r105) {
        /*
            Method dump skipped, instructions count: 5734
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState, boolean):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:70:0x0123, code lost:
        if (r9[r6 + 3] != 1) goto L72;
     */
    /* JADX WARN: Removed duplicated region for block: B:113:0x01d0  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x01d5  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x01ec  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01ee  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00df  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private long readAndWriteTracks(android.media.MediaExtractor r29, org.telegram.messenger.video.MP4Builder r30, android.media.MediaCodec.BufferInfo r31, long r32, long r34, long r36, java.io.File r38, boolean r39) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 524
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener == null || !videoConvertorListener.checkConversionCanceled()) {
            return;
        }
        throw new ConversionCanceledException();
    }

    private static String createFragmentShader(int i, int i2, int i3, int i4, boolean z) {
        int clamp = (int) Utilities.clamp((Math.max(i, i2) / Math.max(i4, i3)) * 0.8f, 2.0f, 1.0f);
        FileLog.d("source size " + i + "x" + i2 + "    dest size " + i3 + i4 + "   kernelRadius " + clamp);
        if (z) {
            return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i + ".0;\nconst float pixelSizeY = 1.0 / " + i2 + ".0;\nuniform samplerExternalOES sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
        }
        return "precision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i2 + ".0;\nconst float pixelSizeY = 1.0 / " + i + ".0;\nuniform sampler2D sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
    }

    /* loaded from: classes.dex */
    public class ConversionCanceledException extends RuntimeException {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ConversionCanceledException() {
            super("canceled conversion");
            MediaCodecVideoConvertor.this = r1;
        }
    }
}
