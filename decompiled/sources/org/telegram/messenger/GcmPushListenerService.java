package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_saveAppLog;
import org.telegram.tgnet.TLRPC$TL_inputAppEvent;
import org.telegram.tgnet.TLRPC$TL_jsonNull;
import org.telegram.tgnet.TLRPC$TL_updates;
/* loaded from: classes.dex */
public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        final Map<String, String> data = remoteMessage.getData();
        final long sentTime = remoteMessage.getSentTime();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.this.lambda$onMessageReceived$4(data, sentTime);
            }
        });
        try {
            this.countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4(final Map map, final long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.this.lambda$onMessageReceived$3(map, j);
            }
        });
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x03ef, code lost:
        if (r11 > r1.intValue()) goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x03f1, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:188:0x040e, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r12).checkMessageByRandomId(r4) == false) goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x041d, code lost:
        if (r2.startsWith("CHAT_REACT_") != false) goto L194;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:215:0x046b A[Catch: all -> 0x045b, TRY_ENTER, TRY_LEAVE, TryCatch #19 {all -> 0x045b, blocks: (B:207:0x0451, B:215:0x046b), top: B:986:0x0451 }] */
    /* JADX WARN: Removed duplicated region for block: B:227:0x04ad  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x04c3 A[Catch: all -> 0x04a2, TRY_ENTER, TryCatch #2 {all -> 0x04a2, blocks: (B:221:0x0486, B:223:0x0499, B:231:0x04c3, B:233:0x04c9, B:238:0x04ea, B:256:0x0521, B:260:0x0554, B:263:0x0566, B:264:0x056a, B:266:0x056f, B:269:0x057b, B:272:0x0587, B:275:0x0593, B:278:0x059f, B:281:0x05ab, B:284:0x05b7, B:287:0x05c3, B:290:0x05cf, B:293:0x05db, B:296:0x05e7, B:299:0x05f3, B:302:0x05ff, B:305:0x060b, B:308:0x0617, B:311:0x0623, B:314:0x062f, B:317:0x063b, B:320:0x0647, B:323:0x0653, B:326:0x065e, B:329:0x066a, B:332:0x0676, B:335:0x0682, B:338:0x068e, B:341:0x069a, B:344:0x06a6, B:347:0x06b2, B:350:0x06be, B:353:0x06ca, B:356:0x06d5, B:359:0x06e1, B:362:0x06ed, B:365:0x06f9, B:368:0x0705, B:371:0x0711, B:374:0x071d, B:377:0x0729, B:380:0x0735, B:383:0x0741, B:386:0x074d, B:389:0x0759, B:392:0x0765, B:395:0x0771, B:398:0x077d, B:401:0x0789, B:404:0x0795, B:407:0x07a1, B:410:0x07ad, B:413:0x07b8, B:416:0x07c4, B:419:0x07d0, B:422:0x07dc, B:425:0x07e8, B:428:0x07f4, B:431:0x0800, B:434:0x080c, B:437:0x0818, B:440:0x0824, B:443:0x0830, B:446:0x083c, B:449:0x0848, B:452:0x0854, B:455:0x0860, B:458:0x086c, B:461:0x0878, B:464:0x0884, B:467:0x0890, B:470:0x089c, B:473:0x08a8, B:476:0x08b4, B:479:0x08c0, B:482:0x08cc, B:485:0x08d8, B:488:0x08e4, B:491:0x08f0, B:494:0x08fc, B:497:0x0908, B:500:0x0914, B:503:0x0920, B:506:0x092c, B:509:0x0938, B:512:0x0944, B:515:0x0950, B:518:0x095c, B:521:0x0968, B:524:0x0973, B:527:0x097e, B:530:0x098a, B:533:0x0996, B:536:0x09a2, B:539:0x09ae, B:542:0x09ba, B:545:0x09c6, B:548:0x09d2, B:551:0x09de, B:554:0x09ea, B:557:0x09f5, B:560:0x0a01, B:563:0x0a0c, B:566:0x0a18, B:569:0x0a24, B:572:0x0a2f, B:575:0x0a3b, B:578:0x0a47, B:581:0x0a53, B:584:0x0a5f, B:587:0x0a6a, B:590:0x0a75, B:593:0x0a80, B:596:0x0a8b, B:599:0x0a96, B:602:0x0aa1, B:605:0x0aac, B:608:0x0ab7, B:614:0x0ad8, B:615:0x0adc, B:619:0x0afc, B:621:0x0b16, B:622:0x0b2e, B:625:0x0b47, B:627:0x0b61, B:628:0x0b79, B:631:0x0b92, B:633:0x0bac, B:634:0x0bc4, B:637:0x0bdd, B:639:0x0bf7, B:640:0x0c0f, B:643:0x0c28, B:645:0x0c42, B:646:0x0c5a, B:649:0x0c73, B:651:0x0c8d, B:652:0x0ca5, B:655:0x0cbe, B:657:0x0cd8, B:658:0x0cf5, B:661:0x0d14, B:663:0x0d2e, B:664:0x0d4b, B:667:0x0d6a, B:669:0x0d84, B:670:0x0da1, B:673:0x0dc0, B:675:0x0dda, B:676:0x0df2, B:679:0x0e0c, B:681:0x0e10, B:683:0x0e18, B:684:0x0e30, B:686:0x0e45, B:688:0x0e49, B:690:0x0e51, B:691:0x0e6e, B:692:0x0e86, B:694:0x0e8a, B:696:0x0e92, B:697:0x0eaa, B:700:0x0ec4, B:702:0x0ede, B:703:0x0ef6, B:706:0x0f10, B:708:0x0f2a, B:709:0x0f42, B:712:0x0f5c, B:714:0x0f76, B:715:0x0f8e, B:718:0x0fa8, B:720:0x0fc2, B:721:0x0fda, B:724:0x0ff4, B:726:0x100e, B:727:0x1026, B:730:0x1040, B:732:0x105a, B:733:0x1077, B:734:0x108f, B:736:0x10ab, B:737:0x10d7, B:738:0x1103, B:739:0x1130, B:740:0x115d, B:741:0x118c, B:742:0x11a5, B:743:0x11be, B:744:0x11d7, B:745:0x11f0, B:746:0x1209, B:747:0x1222, B:748:0x123b, B:749:0x1254, B:750:0x1272, B:751:0x128a, B:752:0x12a7, B:753:0x12bf, B:754:0x12d7, B:756:0x12f2, B:757:0x1319, B:758:0x133b, B:759:0x1362, B:760:0x1384, B:761:0x13a6, B:762:0x13c8, B:763:0x13ee, B:764:0x1414, B:765:0x143a, B:767:0x145f, B:769:0x1463, B:771:0x146b, B:772:0x14a3, B:773:0x14d6, B:774:0x14f7, B:775:0x1518, B:776:0x1539, B:777:0x155a, B:778:0x157b, B:779:0x159a, B:780:0x15ad, B:781:0x15d3, B:782:0x15f9, B:783:0x161f, B:784:0x1645, B:785:0x1670, B:786:0x168c, B:787:0x16a8, B:788:0x16c4, B:789:0x16e0, B:790:0x1701, B:791:0x1722, B:792:0x1743, B:793:0x175f, B:795:0x1763, B:797:0x176b, B:798:0x179e, B:799:0x17b8, B:800:0x17d4, B:801:0x17f0, B:802:0x180c, B:803:0x1828, B:804:0x1844, B:806:0x1858, B:807:0x187d, B:808:0x18a2, B:809:0x18c7, B:810:0x18ed, B:811:0x1915, B:812:0x1936, B:813:0x1953, B:814:0x1974, B:815:0x1990, B:816:0x19ac, B:817:0x19c8, B:818:0x19e9, B:819:0x1a0a, B:820:0x1a2b, B:821:0x1a47, B:823:0x1a4b, B:825:0x1a53, B:826:0x1a86, B:827:0x1aa0, B:828:0x1abc, B:829:0x1ad8, B:832:0x1aef, B:833:0x1b0a, B:834:0x1b25, B:835:0x1b40, B:836:0x1b5b, B:839:0x1b7a, B:840:0x1b93, B:842:0x1bb5), top: B:955:0x0486 }] */
    /* JADX WARN: Removed duplicated region for block: B:240:0x04f3 A[Catch: all -> 0x1cd5, TRY_ENTER, TryCatch #14 {all -> 0x1cd5, blocks: (B:219:0x047a, B:229:0x04b3, B:240:0x04f3, B:248:0x050a, B:253:0x051b, B:258:0x054e), top: B:976:0x047a }] */
    /* JADX WARN: Removed duplicated region for block: B:255:0x051f  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x0548  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x0554 A[Catch: all -> 0x04a2, TRY_ENTER, TryCatch #2 {all -> 0x04a2, blocks: (B:221:0x0486, B:223:0x0499, B:231:0x04c3, B:233:0x04c9, B:238:0x04ea, B:256:0x0521, B:260:0x0554, B:263:0x0566, B:264:0x056a, B:266:0x056f, B:269:0x057b, B:272:0x0587, B:275:0x0593, B:278:0x059f, B:281:0x05ab, B:284:0x05b7, B:287:0x05c3, B:290:0x05cf, B:293:0x05db, B:296:0x05e7, B:299:0x05f3, B:302:0x05ff, B:305:0x060b, B:308:0x0617, B:311:0x0623, B:314:0x062f, B:317:0x063b, B:320:0x0647, B:323:0x0653, B:326:0x065e, B:329:0x066a, B:332:0x0676, B:335:0x0682, B:338:0x068e, B:341:0x069a, B:344:0x06a6, B:347:0x06b2, B:350:0x06be, B:353:0x06ca, B:356:0x06d5, B:359:0x06e1, B:362:0x06ed, B:365:0x06f9, B:368:0x0705, B:371:0x0711, B:374:0x071d, B:377:0x0729, B:380:0x0735, B:383:0x0741, B:386:0x074d, B:389:0x0759, B:392:0x0765, B:395:0x0771, B:398:0x077d, B:401:0x0789, B:404:0x0795, B:407:0x07a1, B:410:0x07ad, B:413:0x07b8, B:416:0x07c4, B:419:0x07d0, B:422:0x07dc, B:425:0x07e8, B:428:0x07f4, B:431:0x0800, B:434:0x080c, B:437:0x0818, B:440:0x0824, B:443:0x0830, B:446:0x083c, B:449:0x0848, B:452:0x0854, B:455:0x0860, B:458:0x086c, B:461:0x0878, B:464:0x0884, B:467:0x0890, B:470:0x089c, B:473:0x08a8, B:476:0x08b4, B:479:0x08c0, B:482:0x08cc, B:485:0x08d8, B:488:0x08e4, B:491:0x08f0, B:494:0x08fc, B:497:0x0908, B:500:0x0914, B:503:0x0920, B:506:0x092c, B:509:0x0938, B:512:0x0944, B:515:0x0950, B:518:0x095c, B:521:0x0968, B:524:0x0973, B:527:0x097e, B:530:0x098a, B:533:0x0996, B:536:0x09a2, B:539:0x09ae, B:542:0x09ba, B:545:0x09c6, B:548:0x09d2, B:551:0x09de, B:554:0x09ea, B:557:0x09f5, B:560:0x0a01, B:563:0x0a0c, B:566:0x0a18, B:569:0x0a24, B:572:0x0a2f, B:575:0x0a3b, B:578:0x0a47, B:581:0x0a53, B:584:0x0a5f, B:587:0x0a6a, B:590:0x0a75, B:593:0x0a80, B:596:0x0a8b, B:599:0x0a96, B:602:0x0aa1, B:605:0x0aac, B:608:0x0ab7, B:614:0x0ad8, B:615:0x0adc, B:619:0x0afc, B:621:0x0b16, B:622:0x0b2e, B:625:0x0b47, B:627:0x0b61, B:628:0x0b79, B:631:0x0b92, B:633:0x0bac, B:634:0x0bc4, B:637:0x0bdd, B:639:0x0bf7, B:640:0x0c0f, B:643:0x0c28, B:645:0x0c42, B:646:0x0c5a, B:649:0x0c73, B:651:0x0c8d, B:652:0x0ca5, B:655:0x0cbe, B:657:0x0cd8, B:658:0x0cf5, B:661:0x0d14, B:663:0x0d2e, B:664:0x0d4b, B:667:0x0d6a, B:669:0x0d84, B:670:0x0da1, B:673:0x0dc0, B:675:0x0dda, B:676:0x0df2, B:679:0x0e0c, B:681:0x0e10, B:683:0x0e18, B:684:0x0e30, B:686:0x0e45, B:688:0x0e49, B:690:0x0e51, B:691:0x0e6e, B:692:0x0e86, B:694:0x0e8a, B:696:0x0e92, B:697:0x0eaa, B:700:0x0ec4, B:702:0x0ede, B:703:0x0ef6, B:706:0x0f10, B:708:0x0f2a, B:709:0x0f42, B:712:0x0f5c, B:714:0x0f76, B:715:0x0f8e, B:718:0x0fa8, B:720:0x0fc2, B:721:0x0fda, B:724:0x0ff4, B:726:0x100e, B:727:0x1026, B:730:0x1040, B:732:0x105a, B:733:0x1077, B:734:0x108f, B:736:0x10ab, B:737:0x10d7, B:738:0x1103, B:739:0x1130, B:740:0x115d, B:741:0x118c, B:742:0x11a5, B:743:0x11be, B:744:0x11d7, B:745:0x11f0, B:746:0x1209, B:747:0x1222, B:748:0x123b, B:749:0x1254, B:750:0x1272, B:751:0x128a, B:752:0x12a7, B:753:0x12bf, B:754:0x12d7, B:756:0x12f2, B:757:0x1319, B:758:0x133b, B:759:0x1362, B:760:0x1384, B:761:0x13a6, B:762:0x13c8, B:763:0x13ee, B:764:0x1414, B:765:0x143a, B:767:0x145f, B:769:0x1463, B:771:0x146b, B:772:0x14a3, B:773:0x14d6, B:774:0x14f7, B:775:0x1518, B:776:0x1539, B:777:0x155a, B:778:0x157b, B:779:0x159a, B:780:0x15ad, B:781:0x15d3, B:782:0x15f9, B:783:0x161f, B:784:0x1645, B:785:0x1670, B:786:0x168c, B:787:0x16a8, B:788:0x16c4, B:789:0x16e0, B:790:0x1701, B:791:0x1722, B:792:0x1743, B:793:0x175f, B:795:0x1763, B:797:0x176b, B:798:0x179e, B:799:0x17b8, B:800:0x17d4, B:801:0x17f0, B:802:0x180c, B:803:0x1828, B:804:0x1844, B:806:0x1858, B:807:0x187d, B:808:0x18a2, B:809:0x18c7, B:810:0x18ed, B:811:0x1915, B:812:0x1936, B:813:0x1953, B:814:0x1974, B:815:0x1990, B:816:0x19ac, B:817:0x19c8, B:818:0x19e9, B:819:0x1a0a, B:820:0x1a2b, B:821:0x1a47, B:823:0x1a4b, B:825:0x1a53, B:826:0x1a86, B:827:0x1aa0, B:828:0x1abc, B:829:0x1ad8, B:832:0x1aef, B:833:0x1b0a, B:834:0x1b25, B:835:0x1b40, B:836:0x1b5b, B:839:0x1b7a, B:840:0x1b93, B:842:0x1bb5), top: B:955:0x0486 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0184 A[Catch: all -> 0x011b, TRY_ENTER, TryCatch #20 {all -> 0x011b, blocks: (B:35:0x0114, B:41:0x012e, B:44:0x0138, B:48:0x014d, B:51:0x0158, B:56:0x0168, B:63:0x0184, B:66:0x0193, B:69:0x0199, B:71:0x019d, B:72:0x01a2), top: B:988:0x0114 }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0199 A[Catch: all -> 0x011b, TryCatch #20 {all -> 0x011b, blocks: (B:35:0x0114, B:41:0x012e, B:44:0x0138, B:48:0x014d, B:51:0x0158, B:56:0x0168, B:63:0x0184, B:66:0x0193, B:69:0x0199, B:71:0x019d, B:72:0x01a2), top: B:988:0x0114 }] */
    /* JADX WARN: Removed duplicated region for block: B:847:0x1bd0  */
    /* JADX WARN: Removed duplicated region for block: B:850:0x1be3 A[Catch: all -> 0x1d03, TryCatch #4 {all -> 0x1d03, blocks: (B:848:0x1bda, B:850:0x1be3, B:854:0x1bf4, B:856:0x1bff, B:858:0x1c08, B:859:0x1c0f, B:861:0x1c17, B:864:0x1c2b, B:865:0x1c37, B:866:0x1c44, B:868:0x1c50, B:871:0x1c60, B:874:0x1c70, B:875:0x1c7c, B:881:0x1c88, B:883:0x1cb3, B:888:0x1cbf, B:902:0x1cf2, B:903:0x1cf7), top: B:958:0x1bda }] */
    /* JADX WARN: Removed duplicated region for block: B:902:0x1cf2 A[Catch: all -> 0x1d03, TryCatch #4 {all -> 0x1d03, blocks: (B:848:0x1bda, B:850:0x1be3, B:854:0x1bf4, B:856:0x1bff, B:858:0x1c08, B:859:0x1c0f, B:861:0x1c17, B:864:0x1c2b, B:865:0x1c37, B:866:0x1c44, B:868:0x1c50, B:871:0x1c60, B:874:0x1c70, B:875:0x1c7c, B:881:0x1c88, B:883:0x1cb3, B:888:0x1cbf, B:902:0x1cf2, B:903:0x1cf7), top: B:958:0x1bda }] */
    /* JADX WARN: Removed duplicated region for block: B:945:0x1df0  */
    /* JADX WARN: Removed duplicated region for block: B:946:0x1e00  */
    /* JADX WARN: Removed duplicated region for block: B:949:0x1e07  */
    /* JADX WARN: Removed duplicated region for block: B:955:0x0486 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:974:0x01a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:986:0x0451 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:990:0x0196 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v28 */
    /* JADX WARN: Type inference failed for: r2v31 */
    /* JADX WARN: Type inference failed for: r2v34 */
    /* JADX WARN: Type inference failed for: r2v37 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$onMessageReceived$3(java.util.Map r52, long r53) {
        /*
            Method dump skipped, instructions count: 8434
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$onMessageReceived$3(java.util.Map, long):void");
    }

    public static /* synthetic */ void lambda$onMessageReceived$0(int i, TLRPC$TL_updates tLRPC$TL_updates) {
        MessagesController.getInstance(i).processUpdates(tLRPC$TL_updates, false);
    }

    public static /* synthetic */ void lambda$onMessageReceived$1(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    public static /* synthetic */ void lambda$onMessageReceived$2(int i) {
        LocationController.getInstance(i).setNewLocationEndWatchTime();
    }

    private String getReactedText(String str, Object[] objArr) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2114646919:
                if (str.equals("CHAT_REACT_CONTACT")) {
                    c = 0;
                    break;
                }
                break;
            case -1891797827:
                if (str.equals("REACT_GEOLIVE")) {
                    c = 1;
                    break;
                }
                break;
            case -1415696683:
                if (str.equals("CHAT_REACT_NOTEXT")) {
                    c = 2;
                    break;
                }
                break;
            case -1375264434:
                if (str.equals("REACT_NOTEXT")) {
                    c = 3;
                    break;
                }
                break;
            case -1105974394:
                if (str.equals("CHAT_REACT_INVOICE")) {
                    c = 4;
                    break;
                }
                break;
            case -861247200:
                if (str.equals("REACT_CONTACT")) {
                    c = 5;
                    break;
                }
                break;
            case -661458538:
                if (str.equals("CHAT_REACT_STICKER")) {
                    c = 6;
                    break;
                }
                break;
            case 51977938:
                if (str.equals("REACT_GAME")) {
                    c = 7;
                    break;
                }
                break;
            case 52259487:
                if (str.equals("REACT_POLL")) {
                    c = '\b';
                    break;
                }
                break;
            case 52294965:
                if (str.equals("REACT_QUIZ")) {
                    c = '\t';
                    break;
                }
                break;
            case 52369421:
                if (str.equals("REACT_TEXT")) {
                    c = '\n';
                    break;
                }
                break;
            case 147425325:
                if (str.equals("REACT_INVOICE")) {
                    c = 11;
                    break;
                }
                break;
            case 192842257:
                if (str.equals("CHAT_REACT_DOC")) {
                    c = '\f';
                    break;
                }
                break;
            case 192844842:
                if (str.equals("CHAT_REACT_GEO")) {
                    c = '\r';
                    break;
                }
                break;
            case 192844957:
                if (str.equals("CHAT_REACT_GIF")) {
                    c = 14;
                    break;
                }
                break;
            case 591941181:
                if (str.equals("REACT_STICKER")) {
                    c = 15;
                    break;
                }
                break;
            case 635226735:
                if (str.equals("CHAT_REACT_AUDIO")) {
                    c = 16;
                    break;
                }
                break;
            case 648703179:
                if (str.equals("CHAT_REACT_PHOTO")) {
                    c = 17;
                    break;
                }
                break;
            case 650764327:
                if (str.equals("CHAT_REACT_ROUND")) {
                    c = 18;
                    break;
                }
                break;
            case 654263060:
                if (str.equals("CHAT_REACT_VIDEO")) {
                    c = 19;
                    break;
                }
                break;
            case 1149769750:
                if (str.equals("CHAT_REACT_GEOLIVE")) {
                    c = 20;
                    break;
                }
                break;
            case 1606362326:
                if (str.equals("REACT_AUDIO")) {
                    c = 21;
                    break;
                }
                break;
            case 1619838770:
                if (str.equals("REACT_PHOTO")) {
                    c = 22;
                    break;
                }
                break;
            case 1621899918:
                if (str.equals("REACT_ROUND")) {
                    c = 23;
                    break;
                }
                break;
            case 1625398651:
                if (str.equals("REACT_VIDEO")) {
                    c = 24;
                    break;
                }
                break;
            case 1664242232:
                if (str.equals("REACT_DOC")) {
                    c = 25;
                    break;
                }
                break;
            case 1664244817:
                if (str.equals("REACT_GEO")) {
                    c = 26;
                    break;
                }
                break;
            case 1664244932:
                if (str.equals("REACT_GIF")) {
                    c = 27;
                    break;
                }
                break;
            case 1683218969:
                if (str.equals("CHAT_REACT_GAME")) {
                    c = 28;
                    break;
                }
                break;
            case 1683500518:
                if (str.equals("CHAT_REACT_POLL")) {
                    c = 29;
                    break;
                }
                break;
            case 1683535996:
                if (str.equals("CHAT_REACT_QUIZ")) {
                    c = 30;
                    break;
                }
                break;
            case 1683610452:
                if (str.equals("CHAT_REACT_TEXT")) {
                    c = 31;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return LocaleController.formatString("PushChatReactContact", R.string.PushChatReactContact, objArr);
            case 1:
                return LocaleController.formatString("PushReactGeoLocation", R.string.PushReactGeoLocation, objArr);
            case 2:
                return LocaleController.formatString("PushChatReactNotext", R.string.PushChatReactNotext, objArr);
            case 3:
                return LocaleController.formatString("PushReactNoText", R.string.PushReactNoText, objArr);
            case 4:
                return LocaleController.formatString("PushChatReactInvoice", R.string.PushChatReactInvoice, objArr);
            case 5:
                return LocaleController.formatString("PushReactContect", R.string.PushReactContect, objArr);
            case 6:
                return LocaleController.formatString("PushChatReactSticker", R.string.PushChatReactSticker, objArr);
            case 7:
                return LocaleController.formatString("PushReactGame", R.string.PushReactGame, objArr);
            case '\b':
                return LocaleController.formatString("PushReactPoll", R.string.PushReactPoll, objArr);
            case '\t':
                return LocaleController.formatString("PushReactQuiz", R.string.PushReactQuiz, objArr);
            case '\n':
                return LocaleController.formatString("PushReactText", R.string.PushReactText, objArr);
            case 11:
                return LocaleController.formatString("PushReactInvoice", R.string.PushReactInvoice, objArr);
            case '\f':
                return LocaleController.formatString("PushChatReactDoc", R.string.PushChatReactDoc, objArr);
            case '\r':
                return LocaleController.formatString("PushChatReactGeo", R.string.PushChatReactGeo, objArr);
            case 14:
                return LocaleController.formatString("PushChatReactGif", R.string.PushChatReactGif, objArr);
            case 15:
                return LocaleController.formatString("PushReactSticker", R.string.PushReactSticker, objArr);
            case 16:
                return LocaleController.formatString("PushChatReactAudio", R.string.PushChatReactAudio, objArr);
            case 17:
                return LocaleController.formatString("PushChatReactPhoto", R.string.PushChatReactPhoto, objArr);
            case 18:
                return LocaleController.formatString("PushChatReactRound", R.string.PushChatReactRound, objArr);
            case R.styleable.MapAttrs_uiTiltGestures /* 19 */:
                return LocaleController.formatString("PushChatReactVideo", R.string.PushChatReactVideo, objArr);
            case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                return LocaleController.formatString("PushChatReactGeoLive", R.string.PushChatReactGeoLive, objArr);
            case R.styleable.MapAttrs_uiZoomGestures /* 21 */:
                return LocaleController.formatString("PushReactAudio", R.string.PushReactAudio, objArr);
            case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                return LocaleController.formatString("PushReactPhoto", R.string.PushReactPhoto, objArr);
            case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                return LocaleController.formatString("PushReactRound", R.string.PushReactRound, objArr);
            case 24:
                return LocaleController.formatString("PushReactVideo", R.string.PushReactVideo, objArr);
            case 25:
                return LocaleController.formatString("PushReactDoc", R.string.PushReactDoc, objArr);
            case 26:
                return LocaleController.formatString("PushReactGeo", R.string.PushReactGeo, objArr);
            case 27:
                return LocaleController.formatString("PushReactGif", R.string.PushReactGif, objArr);
            case 28:
                return LocaleController.formatString("PushChatReactGame", R.string.PushChatReactGame, objArr);
            case 29:
                return LocaleController.formatString("PushChatReactPoll", R.string.PushChatReactPoll, objArr);
            case 30:
                return LocaleController.formatString("PushChatReactQuiz", R.string.PushChatReactQuiz, objArr);
            case 31:
                return LocaleController.formatString("PushChatReactText", R.string.PushChatReactText, objArr);
            default:
                return null;
        }
    }

    private void onDecryptError() {
        for (int i = 0; i < 4; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onNewToken(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$onNewToken$5(str);
            }
        });
    }

    public static /* synthetic */ void lambda$onNewToken$5(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + str);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(str);
    }

    public static void sendRegistrationToServer(final String str) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$sendRegistrationToServer$9(str);
            }
        });
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$9(final String str) {
        boolean z;
        ConnectionsManager.setRegId(str, SharedConfig.pushStringStatus);
        if (str == null) {
            return;
        }
        if (SharedConfig.pushStringGetTimeStart == 0 || SharedConfig.pushStringGetTimeEnd == 0 || (SharedConfig.pushStatSent && TextUtils.equals(SharedConfig.pushString, str))) {
            z = false;
        } else {
            SharedConfig.pushStatSent = false;
            z = true;
        }
        SharedConfig.pushString = str;
        for (final int i = 0; i < 4; i++) {
            UserConfig userConfig = UserConfig.getInstance(i);
            userConfig.registeredForPush = false;
            userConfig.saveConfig(false);
            if (userConfig.getClientUserId() != 0) {
                if (z) {
                    TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
                    TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
                    tLRPC$TL_inputAppEvent.time = SharedConfig.pushStringGetTimeStart;
                    tLRPC$TL_inputAppEvent.type = "fcm_token_request";
                    tLRPC$TL_inputAppEvent.peer = 0L;
                    tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
                    tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
                    TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent2 = new TLRPC$TL_inputAppEvent();
                    long j = SharedConfig.pushStringGetTimeEnd;
                    tLRPC$TL_inputAppEvent2.time = j;
                    tLRPC$TL_inputAppEvent2.type = "fcm_token_response";
                    tLRPC$TL_inputAppEvent2.peer = j - SharedConfig.pushStringGetTimeStart;
                    tLRPC$TL_inputAppEvent2.data = new TLRPC$TL_jsonNull();
                    tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent2);
                    ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_saveAppLog, GcmPushListenerService$$ExternalSyntheticLambda9.INSTANCE);
                    z = false;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        GcmPushListenerService.lambda$sendRegistrationToServer$8(i, str);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$7(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$sendRegistrationToServer$6(TLRPC$TL_error.this);
            }
        });
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$6(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$8(int i, String str) {
        MessagesController.getInstance(i).registerForPush(str);
    }
}
