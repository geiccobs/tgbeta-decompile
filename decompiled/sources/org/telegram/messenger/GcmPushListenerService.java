package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        final Map data = message.getData();
        final long time = message.getSentTime();
        long receiveTime = SystemClock.elapsedRealtime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.this.m281x1d2d684b(data, time);
            }
        });
        try {
            this.countDownLatch.await();
        } catch (Throwable th) {
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.elapsedRealtime() - receiveTime));
        }
    }

    /* renamed from: lambda$onMessageReceived$4$org-telegram-messenger-GcmPushListenerService */
    public /* synthetic */ void m281x1d2d684b(final Map data, final long time) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.this.m280xa7b3420a(data, time);
            }
        });
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:257:0x05f8 A[Catch: all -> 0x0354, TRY_ENTER, TryCatch #9 {all -> 0x0354, blocks: (B:142:0x0344, B:150:0x0374, B:155:0x038c, B:163:0x03a7, B:165:0x03af, B:172:0x03c5, B:174:0x03d4, B:178:0x03fd, B:179:0x040b, B:181:0x0416, B:182:0x0422, B:183:0x042d, B:184:0x0432, B:191:0x0464, B:192:0x0480, B:194:0x0485, B:195:0x0497, B:197:0x04b8, B:204:0x0506, B:208:0x0516, B:212:0x052a, B:214:0x053e, B:216:0x0561, B:223:0x0577, B:230:0x058d, B:243:0x05ca, B:250:0x05df, B:257:0x05f8, B:258:0x060c, B:260:0x060f, B:266:0x0640, B:268:0x0646, B:273:0x0679, B:290:0x06cd, B:295:0x06fe, B:298:0x070f, B:299:0x0713, B:301:0x0718, B:304:0x0724, B:307:0x0730, B:310:0x073c, B:313:0x0748, B:316:0x0754, B:319:0x0760, B:322:0x076c, B:325:0x0778, B:328:0x0784, B:331:0x0790, B:334:0x079c, B:337:0x07a8, B:340:0x07b4, B:343:0x07c0, B:346:0x07cc, B:349:0x07d8, B:352:0x07e4, B:355:0x07f0, B:358:0x07fc, B:361:0x0807, B:364:0x0813, B:367:0x081f, B:370:0x082b, B:373:0x0837, B:376:0x0843, B:379:0x084f, B:382:0x085b, B:385:0x0867, B:388:0x0873, B:391:0x087e, B:394:0x088a, B:397:0x0896, B:400:0x08a2, B:403:0x08ae, B:406:0x08ba, B:409:0x08c6, B:412:0x08d2, B:415:0x08de, B:418:0x08ea, B:421:0x08f6, B:424:0x0902, B:427:0x090e, B:430:0x091a, B:433:0x0926, B:436:0x0932, B:439:0x093e, B:442:0x094a, B:445:0x0956, B:448:0x0961, B:451:0x096d, B:454:0x0979, B:457:0x0985, B:460:0x0991, B:463:0x099d, B:466:0x09a9, B:469:0x09b5, B:472:0x09c1, B:475:0x09cd, B:478:0x09d9, B:481:0x09e5, B:484:0x09f1, B:487:0x09fd, B:490:0x0a09, B:493:0x0a15, B:496:0x0a21, B:499:0x0a2d, B:502:0x0a39, B:505:0x0a45, B:508:0x0a51, B:511:0x0a5d, B:514:0x0a69, B:517:0x0a75, B:520:0x0a81, B:523:0x0a8d, B:526:0x0a99, B:529:0x0aa5, B:532:0x0ab1, B:535:0x0abd, B:538:0x0ac9, B:541:0x0ad5, B:544:0x0ae1, B:547:0x0aed, B:550:0x0af9, B:553:0x0b05, B:556:0x0b11, B:559:0x0b1c, B:562:0x0b27, B:565:0x0b33, B:568:0x0b3f, B:571:0x0b4b, B:574:0x0b57, B:577:0x0b63, B:580:0x0b6f, B:583:0x0b7b, B:586:0x0b87, B:589:0x0b93, B:592:0x0b9e, B:595:0x0baa, B:598:0x0bb5, B:601:0x0bc1, B:604:0x0bcd, B:607:0x0bd8, B:610:0x0be4, B:613:0x0bf0, B:616:0x0bfc, B:619:0x0c08, B:622:0x0c13, B:625:0x0c1e, B:628:0x0c29, B:631:0x0c34, B:634:0x0c3f, B:637:0x0c4a, B:640:0x0c55, B:643:0x0c60, B:650:0x0c84, B:653:0x0c8e, B:656:0x0cb1, B:658:0x0cd3, B:659:0x0cf3, B:662:0x0d14, B:664:0x0d36, B:665:0x0d56, B:668:0x0d77, B:670:0x0d99, B:671:0x0db9, B:674:0x0dda, B:676:0x0dfc, B:677:0x0e1c, B:680:0x0e3d, B:682:0x0e5f, B:683:0x0e7f, B:686:0x0ea0, B:688:0x0ec2, B:689:0x0ee2, B:692:0x0f03, B:694:0x0f25, B:695:0x0f4a, B:698:0x0f70, B:700:0x0f92, B:701:0x0fb7, B:704:0x0fdd, B:706:0x0fff, B:707:0x1024, B:710:0x104a, B:712:0x106c, B:713:0x108c, B:716:0x10ad, B:718:0x10b1, B:720:0x10b9, B:721:0x10d9, B:723:0x10f6, B:725:0x10fa, B:727:0x1102, B:728:0x1127, B:729:0x1147, B:731:0x114b, B:733:0x1153, B:734:0x1173, B:737:0x1194, B:739:0x11b6, B:740:0x11d6, B:743:0x11f7, B:745:0x1219, B:746:0x1239, B:749:0x125a, B:751:0x127c, B:752:0x129c, B:755:0x12bd, B:757:0x12df, B:758:0x12ff, B:761:0x1320, B:763:0x1342, B:764:0x1362, B:767:0x1383, B:769:0x13a5, B:770:0x13ca, B:771:0x13ea, B:772:0x140c, B:773:0x1443, B:774:0x147a, B:775:0x14b1, B:776:0x14e6, B:777:0x151d, B:778:0x153d, B:779:0x155d, B:780:0x157d, B:781:0x159d, B:782:0x15bd, B:783:0x15dd, B:784:0x15fd, B:785:0x161d, B:786:0x1642, B:787:0x1662, B:788:0x1687, B:789:0x16a7, B:790:0x16c7, B:791:0x16e7, B:792:0x1717, B:793:0x1741, B:794:0x1771, B:795:0x179c, B:796:0x17c7, B:797:0x17f2, B:798:0x1822, B:799:0x1852, B:800:0x1882, B:801:0x18ad, B:803:0x18b1, B:805:0x18b9, B:806:0x18fc, B:807:0x193a, B:808:0x1965, B:809:0x1990, B:810:0x19bb, B:811:0x19e6, B:812:0x1a11, B:813:0x1a3a, B:814:0x1a57, B:815:0x1a89, B:816:0x1abb, B:817:0x1aed, B:818:0x1b1d, B:819:0x1b53, B:820:0x1b79, B:821:0x1b9f, B:822:0x1bc5, B:823:0x1beb, B:824:0x1c16, B:825:0x1c41, B:826:0x1c6c, B:827:0x1c92, B:829:0x1c96, B:831:0x1c9e, B:832:0x1cdc, B:833:0x1d00, B:834:0x1d26, B:835:0x1d4c, B:836:0x1d72, B:837:0x1d98, B:838:0x1dbe, B:839:0x1ddb, B:840:0x1e0d, B:841:0x1e3f, B:842:0x1e71, B:843:0x1ea1, B:844:0x1ed3, B:845:0x1efe, B:846:0x1f23, B:847:0x1f4e, B:848:0x1f74, B:849:0x1f9a, B:850:0x1fc0, B:851:0x1feb, B:852:0x2016, B:853:0x2041, B:854:0x2067, B:856:0x206b, B:858:0x2073, B:859:0x20b1, B:860:0x20d5, B:861:0x20fb, B:862:0x2121, B:863:0x213d, B:864:0x2163, B:865:0x2189, B:866:0x21af, B:867:0x21d5, B:868:0x21fb, B:869:0x221e, B:871:0x2249), top: B:977:0x0344 }] */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0619  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0640 A[Catch: all -> 0x0354, TRY_ENTER, TryCatch #9 {all -> 0x0354, blocks: (B:142:0x0344, B:150:0x0374, B:155:0x038c, B:163:0x03a7, B:165:0x03af, B:172:0x03c5, B:174:0x03d4, B:178:0x03fd, B:179:0x040b, B:181:0x0416, B:182:0x0422, B:183:0x042d, B:184:0x0432, B:191:0x0464, B:192:0x0480, B:194:0x0485, B:195:0x0497, B:197:0x04b8, B:204:0x0506, B:208:0x0516, B:212:0x052a, B:214:0x053e, B:216:0x0561, B:223:0x0577, B:230:0x058d, B:243:0x05ca, B:250:0x05df, B:257:0x05f8, B:258:0x060c, B:260:0x060f, B:266:0x0640, B:268:0x0646, B:273:0x0679, B:290:0x06cd, B:295:0x06fe, B:298:0x070f, B:299:0x0713, B:301:0x0718, B:304:0x0724, B:307:0x0730, B:310:0x073c, B:313:0x0748, B:316:0x0754, B:319:0x0760, B:322:0x076c, B:325:0x0778, B:328:0x0784, B:331:0x0790, B:334:0x079c, B:337:0x07a8, B:340:0x07b4, B:343:0x07c0, B:346:0x07cc, B:349:0x07d8, B:352:0x07e4, B:355:0x07f0, B:358:0x07fc, B:361:0x0807, B:364:0x0813, B:367:0x081f, B:370:0x082b, B:373:0x0837, B:376:0x0843, B:379:0x084f, B:382:0x085b, B:385:0x0867, B:388:0x0873, B:391:0x087e, B:394:0x088a, B:397:0x0896, B:400:0x08a2, B:403:0x08ae, B:406:0x08ba, B:409:0x08c6, B:412:0x08d2, B:415:0x08de, B:418:0x08ea, B:421:0x08f6, B:424:0x0902, B:427:0x090e, B:430:0x091a, B:433:0x0926, B:436:0x0932, B:439:0x093e, B:442:0x094a, B:445:0x0956, B:448:0x0961, B:451:0x096d, B:454:0x0979, B:457:0x0985, B:460:0x0991, B:463:0x099d, B:466:0x09a9, B:469:0x09b5, B:472:0x09c1, B:475:0x09cd, B:478:0x09d9, B:481:0x09e5, B:484:0x09f1, B:487:0x09fd, B:490:0x0a09, B:493:0x0a15, B:496:0x0a21, B:499:0x0a2d, B:502:0x0a39, B:505:0x0a45, B:508:0x0a51, B:511:0x0a5d, B:514:0x0a69, B:517:0x0a75, B:520:0x0a81, B:523:0x0a8d, B:526:0x0a99, B:529:0x0aa5, B:532:0x0ab1, B:535:0x0abd, B:538:0x0ac9, B:541:0x0ad5, B:544:0x0ae1, B:547:0x0aed, B:550:0x0af9, B:553:0x0b05, B:556:0x0b11, B:559:0x0b1c, B:562:0x0b27, B:565:0x0b33, B:568:0x0b3f, B:571:0x0b4b, B:574:0x0b57, B:577:0x0b63, B:580:0x0b6f, B:583:0x0b7b, B:586:0x0b87, B:589:0x0b93, B:592:0x0b9e, B:595:0x0baa, B:598:0x0bb5, B:601:0x0bc1, B:604:0x0bcd, B:607:0x0bd8, B:610:0x0be4, B:613:0x0bf0, B:616:0x0bfc, B:619:0x0c08, B:622:0x0c13, B:625:0x0c1e, B:628:0x0c29, B:631:0x0c34, B:634:0x0c3f, B:637:0x0c4a, B:640:0x0c55, B:643:0x0c60, B:650:0x0c84, B:653:0x0c8e, B:656:0x0cb1, B:658:0x0cd3, B:659:0x0cf3, B:662:0x0d14, B:664:0x0d36, B:665:0x0d56, B:668:0x0d77, B:670:0x0d99, B:671:0x0db9, B:674:0x0dda, B:676:0x0dfc, B:677:0x0e1c, B:680:0x0e3d, B:682:0x0e5f, B:683:0x0e7f, B:686:0x0ea0, B:688:0x0ec2, B:689:0x0ee2, B:692:0x0f03, B:694:0x0f25, B:695:0x0f4a, B:698:0x0f70, B:700:0x0f92, B:701:0x0fb7, B:704:0x0fdd, B:706:0x0fff, B:707:0x1024, B:710:0x104a, B:712:0x106c, B:713:0x108c, B:716:0x10ad, B:718:0x10b1, B:720:0x10b9, B:721:0x10d9, B:723:0x10f6, B:725:0x10fa, B:727:0x1102, B:728:0x1127, B:729:0x1147, B:731:0x114b, B:733:0x1153, B:734:0x1173, B:737:0x1194, B:739:0x11b6, B:740:0x11d6, B:743:0x11f7, B:745:0x1219, B:746:0x1239, B:749:0x125a, B:751:0x127c, B:752:0x129c, B:755:0x12bd, B:757:0x12df, B:758:0x12ff, B:761:0x1320, B:763:0x1342, B:764:0x1362, B:767:0x1383, B:769:0x13a5, B:770:0x13ca, B:771:0x13ea, B:772:0x140c, B:773:0x1443, B:774:0x147a, B:775:0x14b1, B:776:0x14e6, B:777:0x151d, B:778:0x153d, B:779:0x155d, B:780:0x157d, B:781:0x159d, B:782:0x15bd, B:783:0x15dd, B:784:0x15fd, B:785:0x161d, B:786:0x1642, B:787:0x1662, B:788:0x1687, B:789:0x16a7, B:790:0x16c7, B:791:0x16e7, B:792:0x1717, B:793:0x1741, B:794:0x1771, B:795:0x179c, B:796:0x17c7, B:797:0x17f2, B:798:0x1822, B:799:0x1852, B:800:0x1882, B:801:0x18ad, B:803:0x18b1, B:805:0x18b9, B:806:0x18fc, B:807:0x193a, B:808:0x1965, B:809:0x1990, B:810:0x19bb, B:811:0x19e6, B:812:0x1a11, B:813:0x1a3a, B:814:0x1a57, B:815:0x1a89, B:816:0x1abb, B:817:0x1aed, B:818:0x1b1d, B:819:0x1b53, B:820:0x1b79, B:821:0x1b9f, B:822:0x1bc5, B:823:0x1beb, B:824:0x1c16, B:825:0x1c41, B:826:0x1c6c, B:827:0x1c92, B:829:0x1c96, B:831:0x1c9e, B:832:0x1cdc, B:833:0x1d00, B:834:0x1d26, B:835:0x1d4c, B:836:0x1d72, B:837:0x1d98, B:838:0x1dbe, B:839:0x1ddb, B:840:0x1e0d, B:841:0x1e3f, B:842:0x1e71, B:843:0x1ea1, B:844:0x1ed3, B:845:0x1efe, B:846:0x1f23, B:847:0x1f4e, B:848:0x1f74, B:849:0x1f9a, B:850:0x1fc0, B:851:0x1feb, B:852:0x2016, B:853:0x2041, B:854:0x2067, B:856:0x206b, B:858:0x2073, B:859:0x20b1, B:860:0x20d5, B:861:0x20fb, B:862:0x2121, B:863:0x213d, B:864:0x2163, B:865:0x2189, B:866:0x21af, B:867:0x21d5, B:868:0x21fb, B:869:0x221e, B:871:0x2249), top: B:977:0x0344 }] */
    /* JADX WARN: Removed duplicated region for block: B:275:0x068a  */
    /* JADX WARN: Removed duplicated region for block: B:289:0x06cb  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x06f2  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x06fe A[Catch: all -> 0x0354, TRY_ENTER, TryCatch #9 {all -> 0x0354, blocks: (B:142:0x0344, B:150:0x0374, B:155:0x038c, B:163:0x03a7, B:165:0x03af, B:172:0x03c5, B:174:0x03d4, B:178:0x03fd, B:179:0x040b, B:181:0x0416, B:182:0x0422, B:183:0x042d, B:184:0x0432, B:191:0x0464, B:192:0x0480, B:194:0x0485, B:195:0x0497, B:197:0x04b8, B:204:0x0506, B:208:0x0516, B:212:0x052a, B:214:0x053e, B:216:0x0561, B:223:0x0577, B:230:0x058d, B:243:0x05ca, B:250:0x05df, B:257:0x05f8, B:258:0x060c, B:260:0x060f, B:266:0x0640, B:268:0x0646, B:273:0x0679, B:290:0x06cd, B:295:0x06fe, B:298:0x070f, B:299:0x0713, B:301:0x0718, B:304:0x0724, B:307:0x0730, B:310:0x073c, B:313:0x0748, B:316:0x0754, B:319:0x0760, B:322:0x076c, B:325:0x0778, B:328:0x0784, B:331:0x0790, B:334:0x079c, B:337:0x07a8, B:340:0x07b4, B:343:0x07c0, B:346:0x07cc, B:349:0x07d8, B:352:0x07e4, B:355:0x07f0, B:358:0x07fc, B:361:0x0807, B:364:0x0813, B:367:0x081f, B:370:0x082b, B:373:0x0837, B:376:0x0843, B:379:0x084f, B:382:0x085b, B:385:0x0867, B:388:0x0873, B:391:0x087e, B:394:0x088a, B:397:0x0896, B:400:0x08a2, B:403:0x08ae, B:406:0x08ba, B:409:0x08c6, B:412:0x08d2, B:415:0x08de, B:418:0x08ea, B:421:0x08f6, B:424:0x0902, B:427:0x090e, B:430:0x091a, B:433:0x0926, B:436:0x0932, B:439:0x093e, B:442:0x094a, B:445:0x0956, B:448:0x0961, B:451:0x096d, B:454:0x0979, B:457:0x0985, B:460:0x0991, B:463:0x099d, B:466:0x09a9, B:469:0x09b5, B:472:0x09c1, B:475:0x09cd, B:478:0x09d9, B:481:0x09e5, B:484:0x09f1, B:487:0x09fd, B:490:0x0a09, B:493:0x0a15, B:496:0x0a21, B:499:0x0a2d, B:502:0x0a39, B:505:0x0a45, B:508:0x0a51, B:511:0x0a5d, B:514:0x0a69, B:517:0x0a75, B:520:0x0a81, B:523:0x0a8d, B:526:0x0a99, B:529:0x0aa5, B:532:0x0ab1, B:535:0x0abd, B:538:0x0ac9, B:541:0x0ad5, B:544:0x0ae1, B:547:0x0aed, B:550:0x0af9, B:553:0x0b05, B:556:0x0b11, B:559:0x0b1c, B:562:0x0b27, B:565:0x0b33, B:568:0x0b3f, B:571:0x0b4b, B:574:0x0b57, B:577:0x0b63, B:580:0x0b6f, B:583:0x0b7b, B:586:0x0b87, B:589:0x0b93, B:592:0x0b9e, B:595:0x0baa, B:598:0x0bb5, B:601:0x0bc1, B:604:0x0bcd, B:607:0x0bd8, B:610:0x0be4, B:613:0x0bf0, B:616:0x0bfc, B:619:0x0c08, B:622:0x0c13, B:625:0x0c1e, B:628:0x0c29, B:631:0x0c34, B:634:0x0c3f, B:637:0x0c4a, B:640:0x0c55, B:643:0x0c60, B:650:0x0c84, B:653:0x0c8e, B:656:0x0cb1, B:658:0x0cd3, B:659:0x0cf3, B:662:0x0d14, B:664:0x0d36, B:665:0x0d56, B:668:0x0d77, B:670:0x0d99, B:671:0x0db9, B:674:0x0dda, B:676:0x0dfc, B:677:0x0e1c, B:680:0x0e3d, B:682:0x0e5f, B:683:0x0e7f, B:686:0x0ea0, B:688:0x0ec2, B:689:0x0ee2, B:692:0x0f03, B:694:0x0f25, B:695:0x0f4a, B:698:0x0f70, B:700:0x0f92, B:701:0x0fb7, B:704:0x0fdd, B:706:0x0fff, B:707:0x1024, B:710:0x104a, B:712:0x106c, B:713:0x108c, B:716:0x10ad, B:718:0x10b1, B:720:0x10b9, B:721:0x10d9, B:723:0x10f6, B:725:0x10fa, B:727:0x1102, B:728:0x1127, B:729:0x1147, B:731:0x114b, B:733:0x1153, B:734:0x1173, B:737:0x1194, B:739:0x11b6, B:740:0x11d6, B:743:0x11f7, B:745:0x1219, B:746:0x1239, B:749:0x125a, B:751:0x127c, B:752:0x129c, B:755:0x12bd, B:757:0x12df, B:758:0x12ff, B:761:0x1320, B:763:0x1342, B:764:0x1362, B:767:0x1383, B:769:0x13a5, B:770:0x13ca, B:771:0x13ea, B:772:0x140c, B:773:0x1443, B:774:0x147a, B:775:0x14b1, B:776:0x14e6, B:777:0x151d, B:778:0x153d, B:779:0x155d, B:780:0x157d, B:781:0x159d, B:782:0x15bd, B:783:0x15dd, B:784:0x15fd, B:785:0x161d, B:786:0x1642, B:787:0x1662, B:788:0x1687, B:789:0x16a7, B:790:0x16c7, B:791:0x16e7, B:792:0x1717, B:793:0x1741, B:794:0x1771, B:795:0x179c, B:796:0x17c7, B:797:0x17f2, B:798:0x1822, B:799:0x1852, B:800:0x1882, B:801:0x18ad, B:803:0x18b1, B:805:0x18b9, B:806:0x18fc, B:807:0x193a, B:808:0x1965, B:809:0x1990, B:810:0x19bb, B:811:0x19e6, B:812:0x1a11, B:813:0x1a3a, B:814:0x1a57, B:815:0x1a89, B:816:0x1abb, B:817:0x1aed, B:818:0x1b1d, B:819:0x1b53, B:820:0x1b79, B:821:0x1b9f, B:822:0x1bc5, B:823:0x1beb, B:824:0x1c16, B:825:0x1c41, B:826:0x1c6c, B:827:0x1c92, B:829:0x1c96, B:831:0x1c9e, B:832:0x1cdc, B:833:0x1d00, B:834:0x1d26, B:835:0x1d4c, B:836:0x1d72, B:837:0x1d98, B:838:0x1dbe, B:839:0x1ddb, B:840:0x1e0d, B:841:0x1e3f, B:842:0x1e71, B:843:0x1ea1, B:844:0x1ed3, B:845:0x1efe, B:846:0x1f23, B:847:0x1f4e, B:848:0x1f74, B:849:0x1f9a, B:850:0x1fc0, B:851:0x1feb, B:852:0x2016, B:853:0x2041, B:854:0x2067, B:856:0x206b, B:858:0x2073, B:859:0x20b1, B:860:0x20d5, B:861:0x20fb, B:862:0x2121, B:863:0x213d, B:864:0x2163, B:865:0x2189, B:866:0x21af, B:867:0x21d5, B:868:0x21fb, B:869:0x221e, B:871:0x2249), top: B:977:0x0344 }] */
    /* JADX WARN: Removed duplicated region for block: B:873:0x2266  */
    /* JADX WARN: Removed duplicated region for block: B:877:0x227b A[Catch: all -> 0x23c4, TryCatch #4 {all -> 0x23c4, blocks: (B:875:0x2271, B:877:0x227b, B:881:0x228d, B:888:0x22b4, B:897:0x22f2, B:898:0x2301, B:909:0x2351, B:915:0x235d, B:922:0x2394), top: B:968:0x2271 }] */
    /* JADX WARN: Removed duplicated region for block: B:917:0x2388  */
    /* JADX WARN: Removed duplicated region for block: B:925:0x23b4  */
    /* JADX WARN: Removed duplicated region for block: B:933:0x2409 A[Catch: all -> 0x2420, TryCatch #0 {all -> 0x2420, blocks: (B:924:0x23ad, B:933:0x2409, B:934:0x240e), top: B:961:0x23ad }] */
    /* JADX WARN: Removed duplicated region for block: B:954:0x2468  */
    /* JADX WARN: Removed duplicated region for block: B:955:0x2478  */
    /* JADX WARN: Removed duplicated region for block: B:958:0x247f  */
    /* renamed from: lambda$onMessageReceived$3$org-telegram-messenger-GcmPushListenerService */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m280xa7b3420a(java.util.Map r78, long r79) {
        /*
            Method dump skipped, instructions count: 10102
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.m280xa7b3420a(java.util.Map, long):void");
    }

    public static /* synthetic */ void lambda$onMessageReceived$1(int accountFinal) {
        if (UserConfig.getInstance(accountFinal).getClientUserId() != 0) {
            UserConfig.getInstance(accountFinal).clearConfig();
            MessagesController.getInstance(accountFinal).performLogout(0);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private String getReactedText(String loc_key, Object[] args) {
        char c;
        switch (loc_key.hashCode()) {
            case -2114646919:
                if (loc_key.equals("CHAT_REACT_CONTACT")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -1891797827:
                if (loc_key.equals("REACT_GEOLIVE")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1415696683:
                if (loc_key.equals("CHAT_REACT_NOTEXT")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -1375264434:
                if (loc_key.equals("REACT_NOTEXT")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1105974394:
                if (loc_key.equals("CHAT_REACT_INVOICE")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case -861247200:
                if (loc_key.equals("REACT_CONTACT")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -661458538:
                if (loc_key.equals("CHAT_REACT_STICKER")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 51977938:
                if (loc_key.equals("REACT_GAME")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 52259487:
                if (loc_key.equals("REACT_POLL")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 52294965:
                if (loc_key.equals("REACT_QUIZ")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 52369421:
                if (loc_key.equals("REACT_TEXT")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 147425325:
                if (loc_key.equals("REACT_INVOICE")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 192842257:
                if (loc_key.equals("CHAT_REACT_DOC")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 192844842:
                if (loc_key.equals("CHAT_REACT_GEO")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 192844957:
                if (loc_key.equals("CHAT_REACT_GIF")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 591941181:
                if (loc_key.equals("REACT_STICKER")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 635226735:
                if (loc_key.equals("CHAT_REACT_AUDIO")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 648703179:
                if (loc_key.equals("CHAT_REACT_PHOTO")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 650764327:
                if (loc_key.equals("CHAT_REACT_ROUND")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 654263060:
                if (loc_key.equals("CHAT_REACT_VIDEO")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1149769750:
                if (loc_key.equals("CHAT_REACT_GEOLIVE")) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case 1606362326:
                if (loc_key.equals("REACT_AUDIO")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1619838770:
                if (loc_key.equals("REACT_PHOTO")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1621899918:
                if (loc_key.equals("REACT_ROUND")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1625398651:
                if (loc_key.equals("REACT_VIDEO")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1664242232:
                if (loc_key.equals("REACT_DOC")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1664244817:
                if (loc_key.equals("REACT_GEO")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1664244932:
                if (loc_key.equals("REACT_GIF")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 1683218969:
                if (loc_key.equals("CHAT_REACT_GAME")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 1683500518:
                if (loc_key.equals("CHAT_REACT_POLL")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case 1683535996:
                if (loc_key.equals("CHAT_REACT_QUIZ")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 1683610452:
                if (loc_key.equals("CHAT_REACT_TEXT")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return LocaleController.formatString("PushReactText", org.telegram.messenger.beta.R.string.PushReactText, args);
            case 1:
                return LocaleController.formatString("PushReactNoText", org.telegram.messenger.beta.R.string.PushReactNoText, args);
            case 2:
                return LocaleController.formatString("PushReactPhoto", org.telegram.messenger.beta.R.string.PushReactPhoto, args);
            case 3:
                return LocaleController.formatString("PushReactVideo", org.telegram.messenger.beta.R.string.PushReactVideo, args);
            case 4:
                return LocaleController.formatString("PushReactRound", org.telegram.messenger.beta.R.string.PushReactRound, args);
            case 5:
                return LocaleController.formatString("PushReactDoc", org.telegram.messenger.beta.R.string.PushReactDoc, args);
            case 6:
                return LocaleController.formatString("PushReactSticker", org.telegram.messenger.beta.R.string.PushReactSticker, args);
            case 7:
                return LocaleController.formatString("PushReactAudio", org.telegram.messenger.beta.R.string.PushReactAudio, args);
            case '\b':
                return LocaleController.formatString("PushReactContect", org.telegram.messenger.beta.R.string.PushReactContect, args);
            case '\t':
                return LocaleController.formatString("PushReactGeo", org.telegram.messenger.beta.R.string.PushReactGeo, args);
            case '\n':
                return LocaleController.formatString("PushReactGeoLocation", org.telegram.messenger.beta.R.string.PushReactGeoLocation, args);
            case 11:
                return LocaleController.formatString("PushReactPoll", org.telegram.messenger.beta.R.string.PushReactPoll, args);
            case '\f':
                return LocaleController.formatString("PushReactQuiz", org.telegram.messenger.beta.R.string.PushReactQuiz, args);
            case '\r':
                return LocaleController.formatString("PushReactGame", org.telegram.messenger.beta.R.string.PushReactGame, args);
            case 14:
                return LocaleController.formatString("PushReactInvoice", org.telegram.messenger.beta.R.string.PushReactInvoice, args);
            case 15:
                return LocaleController.formatString("PushReactGif", org.telegram.messenger.beta.R.string.PushReactGif, args);
            case 16:
                return LocaleController.formatString("PushChatReactText", org.telegram.messenger.beta.R.string.PushChatReactText, args);
            case 17:
                return LocaleController.formatString("PushChatReactNotext", org.telegram.messenger.beta.R.string.PushChatReactNotext, args);
            case 18:
                return LocaleController.formatString("PushChatReactPhoto", org.telegram.messenger.beta.R.string.PushChatReactPhoto, args);
            case 19:
                return LocaleController.formatString("PushChatReactVideo", org.telegram.messenger.beta.R.string.PushChatReactVideo, args);
            case 20:
                return LocaleController.formatString("PushChatReactRound", org.telegram.messenger.beta.R.string.PushChatReactRound, args);
            case 21:
                return LocaleController.formatString("PushChatReactDoc", org.telegram.messenger.beta.R.string.PushChatReactDoc, args);
            case 22:
                return LocaleController.formatString("PushChatReactSticker", org.telegram.messenger.beta.R.string.PushChatReactSticker, args);
            case 23:
                return LocaleController.formatString("PushChatReactAudio", org.telegram.messenger.beta.R.string.PushChatReactAudio, args);
            case 24:
                return LocaleController.formatString("PushChatReactContact", org.telegram.messenger.beta.R.string.PushChatReactContact, args);
            case 25:
                return LocaleController.formatString("PushChatReactGeo", org.telegram.messenger.beta.R.string.PushChatReactGeo, args);
            case 26:
                return LocaleController.formatString("PushChatReactGeoLive", org.telegram.messenger.beta.R.string.PushChatReactGeoLive, args);
            case 27:
                return LocaleController.formatString("PushChatReactPoll", org.telegram.messenger.beta.R.string.PushChatReactPoll, args);
            case 28:
                return LocaleController.formatString("PushChatReactQuiz", org.telegram.messenger.beta.R.string.PushChatReactQuiz, args);
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                return LocaleController.formatString("PushChatReactGame", org.telegram.messenger.beta.R.string.PushChatReactGame, args);
            case 30:
                return LocaleController.formatString("PushChatReactInvoice", org.telegram.messenger.beta.R.string.PushChatReactInvoice, args);
            case 31:
                return LocaleController.formatString("PushChatReactGif", org.telegram.messenger.beta.R.string.PushChatReactGif, args);
            default:
                return null;
        }
    }

    private void onDecryptError() {
        for (int a = 0; a < 4; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(a);
                ConnectionsManager.getInstance(a).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onNewToken(final String token) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$onNewToken$5(token);
            }
        });
    }

    public static /* synthetic */ void lambda$onNewToken$5(String token) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + token);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(token);
    }

    public static void sendRegistrationToServer(final String token) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$sendRegistrationToServer$9(token);
            }
        });
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$9(final String token) {
        ConnectionsManager.setRegId(token, SharedConfig.pushStringStatus);
        if (token == null) {
            return;
        }
        boolean sendStat = false;
        if (SharedConfig.pushStringGetTimeStart != 0 && SharedConfig.pushStringGetTimeEnd != 0 && (!SharedConfig.pushStatSent || !TextUtils.equals(SharedConfig.pushString, token))) {
            sendStat = true;
            SharedConfig.pushStatSent = false;
        }
        SharedConfig.pushString = token;
        for (int a = 0; a < 4; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            userConfig.registeredForPush = false;
            userConfig.saveConfig(false);
            if (userConfig.getClientUserId() != 0) {
                final int currentAccount = a;
                if (sendStat) {
                    TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
                    TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
                    event.time = SharedConfig.pushStringGetTimeStart;
                    event.type = "fcm_token_request";
                    event.peer = 0L;
                    event.data = new TLRPC.TL_jsonNull();
                    req.events.add(event);
                    TLRPC.TL_inputAppEvent event2 = new TLRPC.TL_inputAppEvent();
                    event2.time = SharedConfig.pushStringGetTimeEnd;
                    event2.type = "fcm_token_response";
                    event2.peer = SharedConfig.pushStringGetTimeEnd - SharedConfig.pushStringGetTimeStart;
                    event2.data = new TLRPC.TL_jsonNull();
                    req.events.add(event2);
                    sendStat = false;
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req, GcmPushListenerService$$ExternalSyntheticLambda9.INSTANCE);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.getInstance(currentAccount).registerForPush(token);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$sendRegistrationToServer$6(TLRPC.TL_error error) {
        if (error != null) {
            SharedConfig.pushStatSent = true;
            SharedConfig.saveConfig();
        }
    }
}
