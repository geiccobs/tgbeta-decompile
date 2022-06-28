package org.telegram.ui.Components.voip;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.core.text.HtmlCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.gms.location.LocationRequest;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BasePermissionsActivity;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes5.dex */
public class DarkTheme {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getColor(String key) {
        char c;
        switch (key.hashCode()) {
            case -2147269658:
                if (key.equals(Theme.key_chat_outMenu)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case -2139469579:
                if (key.equals(Theme.key_chat_emojiPanelEmptyText)) {
                    c = 'c';
                    break;
                }
                c = 65535;
                break;
            case -2132427577:
                if (key.equals(Theme.key_chat_outViews)) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case -2103805301:
                if (key.equals(Theme.key_actionBarActionModeDefault)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -2102232027:
                if (key.equals(Theme.key_profile_actionIcon)) {
                    c = 150;
                    break;
                }
                c = 65535;
                break;
            case -2019587427:
                if (key.equals("listSelector")) {
                    c = 'e';
                    break;
                }
                c = 65535;
                break;
            case -1992864503:
                if (key.equals(Theme.key_actionBarDefaultSubmenuBackground)) {
                    c = 'X';
                    break;
                }
                c = 65535;
                break;
            case -1992639563:
                if (key.equals("avatar_actionBarSelectorViolet")) {
                    c = 195;
                    break;
                }
                c = 65535;
                break;
            case -1975862704:
                if (key.equals(Theme.key_player_button)) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case -1974166005:
                if (key.equals(Theme.key_chat_outFileProgressSelected)) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case -1961633574:
                if (key.equals(Theme.key_chat_outLoader)) {
                    c = 179;
                    break;
                }
                c = 65535;
                break;
            case -1942198229:
                if (key.equals(Theme.key_chats_menuPhone)) {
                    c = 196;
                    break;
                }
                c = 65535;
                break;
            case -1927175348:
                if (key.equals(Theme.key_chat_outFileBackgroundSelected)) {
                    c = 208;
                    break;
                }
                c = 65535;
                break;
            case -1926854985:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText2)) {
                    c = 148;
                    break;
                }
                c = 65535;
                break;
            case -1926854984:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText3)) {
                    c = 'W';
                    break;
                }
                c = 65535;
                break;
            case -1926854983:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText4)) {
                    c = 159;
                    break;
                }
                c = 65535;
                break;
            case -1924841028:
                if (key.equals(Theme.key_actionBarDefaultSubtitle)) {
                    c = 'i';
                    break;
                }
                c = 65535;
                break;
            case -1891930735:
                if (key.equals(Theme.key_chat_outFileBackground)) {
                    c = 162;
                    break;
                }
                c = 65535;
                break;
            case -1878988531:
                if (key.equals("avatar_actionBarSelectorGreen")) {
                    c = 'l';
                    break;
                }
                c = 65535;
                break;
            case -1853661732:
                if (key.equals(Theme.key_chat_outTimeSelectedText)) {
                    c = 'Z';
                    break;
                }
                c = 65535;
                break;
            case -1850167367:
                if (key.equals(Theme.key_chat_emojiPanelShadowLine)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -1849805674:
                if (key.equals(Theme.key_dialogBackground)) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case -1787129273:
                if (key.equals(Theme.key_chat_outContactBackground)) {
                    c = 211;
                    break;
                }
                c = 65535;
                break;
            case -1779173263:
                if (key.equals(Theme.key_chat_topPanelMessage)) {
                    c = 132;
                    break;
                }
                c = 65535;
                break;
            case -1777297962:
                if (key.equals(Theme.key_chats_muteIcon)) {
                    c = 244;
                    break;
                }
                c = 65535;
                break;
            case -1767675171:
                if (key.equals(Theme.key_chat_inViaBotNameText)) {
                    c = 156;
                    break;
                }
                c = 65535;
                break;
            case -1758608141:
                if (key.equals(Theme.key_windowBackgroundWhiteValueText)) {
                    c = 232;
                    break;
                }
                c = 65535;
                break;
            case -1733632792:
                if (key.equals(Theme.key_emptyListPlaceholder)) {
                    c = 241;
                    break;
                }
                c = 65535;
                break;
            case -1724033454:
                if (key.equals(Theme.key_chat_inPreviewInstantText)) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1719903102:
                if (key.equals(Theme.key_chat_outViewsSelected)) {
                    c = 145;
                    break;
                }
                c = 65535;
                break;
            case -1719839798:
                if (key.equals(Theme.key_avatar_backgroundInProfileBlue)) {
                    c = 189;
                    break;
                }
                c = 65535;
                break;
            case -1683744660:
                if (key.equals(Theme.key_profile_verifiedBackground)) {
                    c = 161;
                    break;
                }
                c = 65535;
                break;
            case -1654302575:
                if (key.equals(Theme.key_chats_menuBackground)) {
                    c = 173;
                    break;
                }
                c = 65535;
                break;
            case -1633591792:
                if (key.equals(Theme.key_chat_emojiPanelStickerPackSelector)) {
                    c = 225;
                    break;
                }
                c = 65535;
                break;
            case -1625862693:
                if (key.equals(Theme.key_chat_wallpaper)) {
                    c = 224;
                    break;
                }
                c = 65535;
                break;
            case -1623818608:
                if (key.equals(Theme.key_chat_inForwardedNameText)) {
                    c = 165;
                    break;
                }
                c = 65535;
                break;
            case -1604008580:
                if (key.equals(Theme.key_chat_outAudioProgress)) {
                    c = '6';
                    break;
                }
                c = 65535;
                break;
            case -1589702002:
                if (key.equals(Theme.key_chat_inLoaderPhotoSelected)) {
                    c = '<';
                    break;
                }
                c = 65535;
                break;
            case -1565843249:
                if (key.equals(Theme.key_files_folderIcon)) {
                    c = 139;
                    break;
                }
                c = 65535;
                break;
            case -1543133775:
                if (key.equals(Theme.key_chat_outContactNameText)) {
                    c = 222;
                    break;
                }
                c = 65535;
                break;
            case -1542353776:
                if (key.equals(Theme.key_chat_outVoiceSeekbar)) {
                    c = 133;
                    break;
                }
                c = 65535;
                break;
            case -1533503664:
                if (key.equals(Theme.key_chat_outFileProgress)) {
                    c = '|';
                    break;
                }
                c = 65535;
                break;
            case -1530345450:
                if (key.equals(Theme.key_chat_inReplyMessageText)) {
                    c = 'k';
                    break;
                }
                c = 65535;
                break;
            case -1496224782:
                if (key.equals(Theme.key_chat_inReplyLine)) {
                    c = 'h';
                    break;
                }
                c = 65535;
                break;
            case -1415980195:
                if (key.equals(Theme.key_files_folderIconBackground)) {
                    c = 160;
                    break;
                }
                c = 65535;
                break;
            case -1407570354:
                if (key.equals(Theme.key_chat_inReplyMediaMessageText)) {
                    c = 176;
                    break;
                }
                c = 65535;
                break;
            case -1397026623:
                if (key.equals(Theme.key_windowBackgroundGray)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1385379359:
                if (key.equals(Theme.key_dialogIcon)) {
                    c = ']';
                    break;
                }
                c = 65535;
                break;
            case -1316415606:
                if (key.equals(Theme.key_actionBarActionModeDefaultSelector)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1310183623:
                if (key.equals(Theme.key_chat_muteIcon)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1262649070:
                if (key.equals(Theme.key_avatar_nameInMessageGreen)) {
                    c = 'B';
                    break;
                }
                c = 65535;
                break;
            case -1240647597:
                if (key.equals(Theme.key_chat_outBubbleShadow)) {
                    c = '7';
                    break;
                }
                c = 65535;
                break;
            case -1229478359:
                if (key.equals(Theme.key_chats_unreadCounter)) {
                    c = 'U';
                    break;
                }
                c = 65535;
                break;
            case -1213387098:
                if (key.equals(Theme.key_chat_inMenuSelected)) {
                    c = '8';
                    break;
                }
                c = 65535;
                break;
            case -1147596450:
                if (key.equals(Theme.key_chat_inFileInfoSelectedText)) {
                    c = 'O';
                    break;
                }
                c = 65535;
                break;
            case -1106471792:
                if (key.equals(Theme.key_chat_outAudioPerformerText)) {
                    c = '^';
                    break;
                }
                c = 65535;
                break;
            case -1078554766:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueHeader)) {
                    c = 'a';
                    break;
                }
                c = 65535;
                break;
            case -1074293766:
                if (key.equals("avatar_backgroundActionBarGreen")) {
                    c = 143;
                    break;
                }
                c = 65535;
                break;
            case -1063762099:
                if (key.equals(Theme.key_windowBackgroundWhiteGreenText2)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1062379852:
                if (key.equals(Theme.key_chat_messageLinkOut)) {
                    c = 204;
                    break;
                }
                c = 65535;
                break;
            case -1046600742:
                if (key.equals(Theme.key_profile_actionBackground)) {
                    c = '@';
                    break;
                }
                c = 65535;
                break;
            case -1019316079:
                if (key.equals(Theme.key_chat_outReplyMessageText)) {
                    c = 210;
                    break;
                }
                c = 65535;
                break;
            case -1012016554:
                if (key.equals(Theme.key_chat_inFileBackground)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1006953508:
                if (key.equals(Theme.key_chat_secretTimerBackground)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1005376655:
                if (key.equals(Theme.key_chat_inAudioSeekbar)) {
                    c = 187;
                    break;
                }
                c = 65535;
                break;
            case -1005120019:
                if (key.equals(Theme.key_chats_secretIcon)) {
                    c = '\\';
                    break;
                }
                c = 65535;
                break;
            case -1004973057:
                if (key.equals(Theme.key_chats_secretName)) {
                    c = 'g';
                    break;
                }
                c = 65535;
                break;
            case -960321732:
                if (key.equals(Theme.key_chat_mediaMenu)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case -955211830:
                if (key.equals(Theme.key_chat_topPanelLine)) {
                    c = '1';
                    break;
                }
                c = 65535;
                break;
            case -938826921:
                if (key.equals(Theme.key_player_actionBarSubtitle)) {
                    c = 223;
                    break;
                }
                c = 65535;
                break;
            case -901363160:
                if (key.equals(Theme.key_chats_menuPhoneCats)) {
                    c = 226;
                    break;
                }
                c = 65535;
                break;
            case -834035478:
                if (key.equals(Theme.key_chat_outSentClockSelected)) {
                    c = 'A';
                    break;
                }
                c = 65535;
                break;
            case -810517465:
                if (key.equals(Theme.key_chat_outAudioSeekbarSelected)) {
                    c = 129;
                    break;
                }
                c = 65535;
                break;
            case -805096120:
                if (key.equals(Theme.key_chats_nameIcon)) {
                    c = 'H';
                    break;
                }
                c = 65535;
                break;
            case -792942846:
                if (key.equals(Theme.key_graySection)) {
                    c = 'G';
                    break;
                }
                c = 65535;
                break;
            case -779362418:
                if (key.equals(Theme.key_chat_emojiPanelTrendingTitle)) {
                    c = 'M';
                    break;
                }
                c = 65535;
                break;
            case -763385518:
                if (key.equals(Theme.key_chats_date)) {
                    c = '{';
                    break;
                }
                c = 65535;
                break;
            case -763087825:
                if (key.equals(Theme.key_chats_name)) {
                    c = 168;
                    break;
                }
                c = 65535;
                break;
            case -756337980:
                if (key.equals(Theme.key_profile_actionPressedBackground)) {
                    c = 't';
                    break;
                }
                c = 65535;
                break;
            case -712338357:
                if (key.equals(Theme.key_chat_inSiteNameText)) {
                    c = 130;
                    break;
                }
                c = 65535;
                break;
            case -687452692:
                if (key.equals(Theme.key_chat_inLoaderPhotoIcon)) {
                    c = 193;
                    break;
                }
                c = 65535;
                break;
            case -654429213:
                if (key.equals(Theme.key_chats_message)) {
                    c = 238;
                    break;
                }
                c = 65535;
                break;
            case -652337344:
                if (key.equals("chat_outVenueNameText")) {
                    c = 240;
                    break;
                }
                c = 65535;
                break;
            case -629209323:
                if (key.equals(Theme.key_chats_pinnedIcon)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -608456434:
                if (key.equals(Theme.key_chat_outBubbleSelected)) {
                    c = '}';
                    break;
                }
                c = 65535;
                break;
            case -603597494:
                if (key.equals(Theme.key_chat_inSentClock)) {
                    c = 166;
                    break;
                }
                c = 65535;
                break;
            case -570274322:
                if (key.equals(Theme.key_chat_outReplyMediaMessageSelectedText)) {
                    c = 248;
                    break;
                }
                c = 65535;
                break;
            case -564899147:
                if (key.equals(Theme.key_chat_outInstantSelected)) {
                    c = 190;
                    break;
                }
                c = 65535;
                break;
            case -560721948:
                if (key.equals(Theme.key_chat_outSentCheckSelected)) {
                    c = 'Y';
                    break;
                }
                c = 65535;
                break;
            case -552118908:
                if (key.equals(Theme.key_actionBarDefault)) {
                    c = 'q';
                    break;
                }
                c = 65535;
                break;
            case -493564645:
                if (key.equals("avatar_actionBarSelectorRed")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -450514995:
                if (key.equals(Theme.key_chats_actionMessage)) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case -427186938:
                if (key.equals(Theme.key_chat_inAudioDurationSelectedText)) {
                    c = 'o';
                    break;
                }
                c = 65535;
                break;
            case -391617936:
                if (key.equals(Theme.key_chat_selectedBackground)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -354489314:
                if (key.equals(Theme.key_chat_outFileInfoText)) {
                    c = '3';
                    break;
                }
                c = 65535;
                break;
            case -343666293:
                if (key.equals(Theme.key_windowBackgroundWhite)) {
                    c = 171;
                    break;
                }
                c = 65535;
                break;
            case -294026410:
                if (key.equals(Theme.key_chat_inReplyNameText)) {
                    c = 'E';
                    break;
                }
                c = 65535;
                break;
            case -264184037:
                if (key.equals(Theme.key_inappPlayerClose)) {
                    c = 206;
                    break;
                }
                c = 65535;
                break;
            case -260428237:
                if (key.equals(Theme.key_chat_outVoiceSeekbarFill)) {
                    c = 197;
                    break;
                }
                c = 65535;
                break;
            case -258492929:
                if (key.equals(Theme.key_avatar_nameInMessageOrange)) {
                    c = 158;
                    break;
                }
                c = 65535;
                break;
            case -251079667:
                if (key.equals(Theme.key_chat_outPreviewInstantText)) {
                    c = 'L';
                    break;
                }
                c = 65535;
                break;
            case -249481380:
                if (key.equals(Theme.key_listSelector)) {
                    c = 213;
                    break;
                }
                c = 65535;
                break;
            case -248568965:
                if (key.equals(Theme.key_inappPlayerTitle)) {
                    c = 229;
                    break;
                }
                c = 65535;
                break;
            case -212237793:
                if (key.equals(Theme.key_player_actionBar)) {
                    c = '2';
                    break;
                }
                c = 65535;
                break;
            case -185786131:
                if (key.equals(Theme.key_chat_unreadMessagesStartText)) {
                    c = 205;
                    break;
                }
                c = 65535;
                break;
            case -176488427:
                if (key.equals(Theme.key_chat_replyPanelLine)) {
                    c = 175;
                    break;
                }
                c = 65535;
                break;
            case -143547632:
                if (key.equals(Theme.key_chat_inFileProgressSelected)) {
                    c = 131;
                    break;
                }
                c = 65535;
                break;
            case -127673038:
                if (key.equals("key_chats_menuTopShadow")) {
                    c = 170;
                    break;
                }
                c = 65535;
                break;
            case -108292334:
                if (key.equals(Theme.key_chats_menuTopShadow)) {
                    c = 136;
                    break;
                }
                c = 65535;
                break;
            case -71280336:
                if (key.equals(Theme.key_switchTrackChecked)) {
                    c = 146;
                    break;
                }
                c = 65535;
                break;
            case -65607089:
                if (key.equals(Theme.key_chats_menuItemIcon)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -65277181:
                if (key.equals(Theme.key_chats_menuItemText)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -35597940:
                if (key.equals(Theme.key_chat_inContactNameText)) {
                    c = 218;
                    break;
                }
                c = 65535;
                break;
            case -18073397:
                if (key.equals(Theme.key_chats_tabletSelectedOverlay)) {
                    c = '$';
                    break;
                }
                c = 65535;
                break;
            case -12871922:
                if (key.equals(Theme.key_chat_secretChatStatusText)) {
                    c = 151;
                    break;
                }
                c = 65535;
                break;
            case 6289575:
                if (key.equals(Theme.key_chat_inLoaderPhotoIconSelected)) {
                    c = 216;
                    break;
                }
                c = 65535;
                break;
            case 27337780:
                if (key.equals(Theme.key_chats_pinnedOverlay)) {
                    c = '_';
                    break;
                }
                c = 65535;
                break;
            case 49148112:
                if (key.equals(Theme.key_chat_inPreviewLine)) {
                    c = 153;
                    break;
                }
                c = 65535;
                break;
            case 51359814:
                if (key.equals(Theme.key_chat_replyPanelMessage)) {
                    c = 'K';
                    break;
                }
                c = 65535;
                break;
            case 57332012:
                if (key.equals(Theme.key_chats_sentCheck)) {
                    c = 200;
                    break;
                }
                c = 65535;
                break;
            case 57460786:
                if (key.equals(Theme.key_chats_sentClock)) {
                    c = 203;
                    break;
                }
                c = 65535;
                break;
            case 89466127:
                if (key.equals(Theme.key_chat_outAudioSeekbarFill)) {
                    c = 'C';
                    break;
                }
                c = 65535;
                break;
            case 117743477:
                if (key.equals(Theme.key_chat_outPreviewLine)) {
                    c = 199;
                    break;
                }
                c = 65535;
                break;
            case 141076636:
                if (key.equals(Theme.key_groupcreate_spanBackground)) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case 141894978:
                if (key.equals(Theme.key_windowBackgroundWhiteRedText5)) {
                    c = 194;
                    break;
                }
                c = 65535;
                break;
            case 185438775:
                if (key.equals(Theme.key_chat_outAudioSelectedProgress)) {
                    c = 182;
                    break;
                }
                c = 65535;
                break;
            case 216441603:
                if (key.equals(Theme.key_chat_goDownButton)) {
                    c = 'r';
                    break;
                }
                c = 65535;
                break;
            case 231486891:
                if (key.equals(Theme.key_chat_inAudioPerformerText)) {
                    c = 'R';
                    break;
                }
                c = 65535;
                break;
            case 243668262:
                if (key.equals(Theme.key_chat_inTimeText)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 257089712:
                if (key.equals(Theme.key_chat_outAudioDurationText)) {
                    c = 'w';
                    break;
                }
                c = 65535;
                break;
            case 271457747:
                if (key.equals(Theme.key_chat_inBubbleSelected)) {
                    c = 'z';
                    break;
                }
                c = 65535;
                break;
            case 303350244:
                if (key.equals(Theme.key_chat_reportSpam)) {
                    c = 227;
                    break;
                }
                c = 65535;
                break;
            case 316847509:
                if (key.equals(Theme.key_chat_outLoaderSelected)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 339397761:
                if (key.equals(Theme.key_windowBackgroundWhiteBlackText)) {
                    c = 246;
                    break;
                }
                c = 65535;
                break;
            case 371859081:
                if (key.equals(Theme.key_chat_inReplyMediaMessageSelectedText)) {
                    c = 140;
                    break;
                }
                c = 65535;
                break;
            case 415452907:
                if (key.equals(Theme.key_chat_outAudioDurationSelectedText)) {
                    c = 142;
                    break;
                }
                c = 65535;
                break;
            case 421601145:
                if (key.equals(Theme.key_chat_emojiPanelIconSelected)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 421601469:
                if (key.equals("chat_emojiPanelIconSelector")) {
                    c = 'J';
                    break;
                }
                c = 65535;
                break;
            case 426061980:
                if (key.equals(Theme.key_chat_serviceBackground)) {
                    c = 147;
                    break;
                }
                c = 65535;
                break;
            case 429680544:
                if (key.equals(Theme.key_avatar_subtitleInProfileBlue)) {
                    c = 239;
                    break;
                }
                c = 65535;
                break;
            case 429722217:
                if (key.equals("avatar_subtitleInProfileCyan")) {
                    c = '?';
                    break;
                }
                c = 65535;
                break;
            case 430094524:
                if (key.equals("avatar_subtitleInProfilePink")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 435303214:
                if (key.equals(Theme.key_actionBarDefaultSubmenuItem)) {
                    c = 198;
                    break;
                }
                c = 65535;
                break;
            case 439976061:
                if (key.equals("avatar_subtitleInProfileGreen")) {
                    c = 228;
                    break;
                }
                c = 65535;
                break;
            case 444983522:
                if (key.equals(Theme.key_chat_topPanelClose)) {
                    c = 250;
                    break;
                }
                c = 65535;
                break;
            case 446162770:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueText)) {
                    c = 247;
                    break;
                }
                c = 65535;
                break;
            case 460598594:
                if (key.equals(Theme.key_chat_topPanelTitle)) {
                    c = 219;
                    break;
                }
                c = 65535;
                break;
            case 484353662:
                if (key.equals(Theme.key_chat_inVenueInfoText)) {
                    c = 'v';
                    break;
                }
                c = 65535;
                break;
            case 503923205:
                if (key.equals(Theme.key_chat_inSentClockSelected)) {
                    c = 183;
                    break;
                }
                c = 65535;
                break;
            case 527405547:
                if (key.equals(Theme.key_inappPlayerBackground)) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case 556028747:
                if (key.equals(Theme.key_chat_outVoiceSeekbarSelected)) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 589961756:
                if (key.equals(Theme.key_chat_goDownButtonIcon)) {
                    c = 214;
                    break;
                }
                c = 65535;
                break;
            case 613458991:
                if (key.equals(Theme.key_dialogTextLink)) {
                    c = 164;
                    break;
                }
                c = 65535;
                break;
            case 626157205:
                if (key.equals(Theme.key_chat_inVoiceSeekbar)) {
                    c = '\"';
                    break;
                }
                c = 65535;
                break;
            case 634019162:
                if (key.equals(Theme.key_chat_emojiPanelBackspace)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 635007317:
                if (key.equals(Theme.key_chat_inFileProgress)) {
                    c = 242;
                    break;
                }
                c = 65535;
                break;
            case 648238646:
                if (key.equals(Theme.key_chat_outAudioTitleText)) {
                    c = ';';
                    break;
                }
                c = 65535;
                break;
            case 655457041:
                if (key.equals(Theme.key_chat_inFileBackgroundSelected)) {
                    c = 234;
                    break;
                }
                c = 65535;
                break;
            case 676996437:
                if (key.equals(Theme.key_chat_outLocationIcon)) {
                    c = 'Q';
                    break;
                }
                c = 65535;
                break;
            case 716656587:
                if (key.equals("avatar_backgroundGroupCreateSpanBlue")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 732262561:
                if (key.equals(Theme.key_chat_outTimeText)) {
                    c = 258;
                    break;
                }
                c = 65535;
                break;
            case 759679774:
                if (key.equals(Theme.key_chat_outVenueInfoSelectedText)) {
                    c = 135;
                    break;
                }
                c = 65535;
                break;
            case 765296599:
                if (key.equals(Theme.key_chat_outReplyLine)) {
                    c = 254;
                    break;
                }
                c = 65535;
                break;
            case 803672502:
                if (key.equals(Theme.key_chat_messagePanelIcons)) {
                    c = 'F';
                    break;
                }
                c = 65535;
                break;
            case 826015922:
                if (key.equals(Theme.key_chat_emojiPanelTrendingDescription)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 850854541:
                if (key.equals(Theme.key_chat_inPreviewInstantSelectedText)) {
                    c = 'N';
                    break;
                }
                c = 65535;
                break;
            case 890367586:
                if (key.equals(Theme.key_chat_inViews)) {
                    c = 'd';
                    break;
                }
                c = 65535;
                break;
            case 911091978:
                if (key.equals(Theme.key_chat_outLocationBackground)) {
                    c = 243;
                    break;
                }
                c = 65535;
                break;
            case 913069217:
                if (key.equals(Theme.key_chat_outMenuSelected)) {
                    c = 252;
                    break;
                }
                c = 65535;
                break;
            case 927863384:
                if (key.equals(Theme.key_chat_inBubbleShadow)) {
                    c = 184;
                    break;
                }
                c = 65535;
                break;
            case 939137799:
                if (key.equals(Theme.key_chat_inContactPhoneText)) {
                    c = 188;
                    break;
                }
                c = 65535;
                break;
            case 939824634:
                if (key.equals(Theme.key_chat_outInstant)) {
                    c = 209;
                    break;
                }
                c = 65535;
                break;
            case 946144034:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueText4)) {
                    c = 217;
                    break;
                }
                c = 65535;
                break;
            case 962085693:
                if (key.equals(Theme.key_chats_menuCloudBackgroundCats)) {
                    c = 215;
                    break;
                }
                c = 65535;
                break;
            case 983278580:
                if (key.equals("avatar_subtitleInProfileOrange")) {
                    c = 261;
                    break;
                }
                c = 65535;
                break;
            case 993048796:
                if (key.equals(Theme.key_chat_inFileSelectedIcon)) {
                    c = 149;
                    break;
                }
                c = 65535;
                break;
            case 1008947016:
                if (key.equals("avatar_backgroundActionBarRed")) {
                    c = 231;
                    break;
                }
                c = 65535;
                break;
            case 1020100908:
                if (key.equals(Theme.key_chat_inAudioSeekbarSelected)) {
                    c = 167;
                    break;
                }
                c = 65535;
                break;
            case 1045892135:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayIcon)) {
                    c = 186;
                    break;
                }
                c = 65535;
                break;
            case 1046222043:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText)) {
                    c = 'V';
                    break;
                }
                c = 65535;
                break;
            case 1079427869:
                if (key.equals(Theme.key_chat_inViewsSelected)) {
                    c = 141;
                    break;
                }
                c = 65535;
                break;
            case 1100033490:
                if (key.equals(Theme.key_chat_inAudioSelectedProgress)) {
                    c = 's';
                    break;
                }
                c = 65535;
                break;
            case 1106068251:
                if (key.equals(Theme.key_groupcreate_spanText)) {
                    c = 245;
                    break;
                }
                c = 65535;
                break;
            case 1121079660:
                if (key.equals(Theme.key_chat_outAudioSeekbar)) {
                    c = 192;
                    break;
                }
                c = 65535;
                break;
            case 1122192435:
                if (key.equals(Theme.key_chat_outLoaderPhotoSelected)) {
                    c = 220;
                    break;
                }
                c = 65535;
                break;
            case 1175786053:
                if (key.equals("avatar_subtitleInProfileViolet")) {
                    c = 181;
                    break;
                }
                c = 65535;
                break;
            case 1195322391:
                if (key.equals(Theme.key_chat_inAudioProgress)) {
                    c = 207;
                    break;
                }
                c = 65535;
                break;
            case 1199344772:
                if (key.equals(Theme.key_chat_topPanelBackground)) {
                    c = 134;
                    break;
                }
                c = 65535;
                break;
            case 1201609915:
                if (key.equals(Theme.key_chat_outReplyNameText)) {
                    c = 180;
                    break;
                }
                c = 65535;
                break;
            case 1202885960:
                if (key.equals(Theme.key_chat_outPreviewInstantSelectedText)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1212117123:
                if (key.equals(Theme.key_avatar_backgroundActionBarBlue)) {
                    c = 155;
                    break;
                }
                c = 65535;
                break;
            case 1212158796:
                if (key.equals("avatar_backgroundActionBarCyan")) {
                    c = 249;
                    break;
                }
                c = 65535;
                break;
            case 1212531103:
                if (key.equals("avatar_backgroundActionBarPink")) {
                    c = 178;
                    break;
                }
                c = 65535;
                break;
            case 1231763334:
                if (key.equals(Theme.key_chat_addContact)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 1239758101:
                if (key.equals("player_placeholder")) {
                    c = 'D';
                    break;
                }
                c = 65535;
                break;
            case 1265168609:
                if (key.equals(Theme.key_player_actionBarItems)) {
                    c = 138;
                    break;
                }
                c = 65535;
                break;
            case 1269980952:
                if (key.equals(Theme.key_chat_inBubble)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1275014009:
                if (key.equals(Theme.key_player_actionBarTitle)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1285554199:
                if (key.equals("avatar_backgroundActionBarOrange")) {
                    c = 233;
                    break;
                }
                c = 65535;
                break;
            case 1288729698:
                if (key.equals(Theme.key_chat_unreadMessagesStartArrowIcon)) {
                    c = '5';
                    break;
                }
                c = 65535;
                break;
            case 1308150651:
                if (key.equals(Theme.key_chat_outFileNameText)) {
                    c = '&';
                    break;
                }
                c = 65535;
                break;
            case 1316752473:
                if (key.equals(Theme.key_chat_outFileInfoSelectedText)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1327229315:
                if (key.equals(Theme.key_actionBarDefaultSelector)) {
                    c = 'b';
                    break;
                }
                c = 65535;
                break;
            case 1333190005:
                if (key.equals(Theme.key_chat_outForwardedNameText)) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 1372411761:
                if (key.equals(Theme.key_inappPlayerPerformer)) {
                    c = '=';
                    break;
                }
                c = 65535;
                break;
            case 1381159341:
                if (key.equals(Theme.key_chat_inContactIcon)) {
                    c = '9';
                    break;
                }
                c = 65535;
                break;
            case 1411374187:
                if (key.equals(Theme.key_chat_messagePanelHint)) {
                    c = 174;
                    break;
                }
                c = 65535;
                break;
            case 1411728145:
                if (key.equals(Theme.key_chat_messagePanelText)) {
                    c = 253;
                    break;
                }
                c = 65535;
                break;
            case 1414117958:
                if (key.equals(Theme.key_chat_outSiteNameText)) {
                    c = 'y';
                    break;
                }
                c = 65535;
                break;
            case 1449754706:
                if (key.equals(Theme.key_chat_outContactIcon)) {
                    c = '`';
                    break;
                }
                c = 65535;
                break;
            case 1450167170:
                if (key.equals(Theme.key_chat_outContactPhoneText)) {
                    c = 'u';
                    break;
                }
                c = 65535;
                break;
            case 1456911705:
                if (key.equals(Theme.key_player_progressBackground)) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 1478061672:
                if (key.equals("avatar_backgroundActionBarViolet")) {
                    c = 'I';
                    break;
                }
                c = 65535;
                break;
            case 1491567659:
                if (key.equals("player_seekBarBackground")) {
                    c = 202;
                    break;
                }
                c = 65535;
                break;
            case 1504078167:
                if (key.equals(Theme.key_chat_outFileSelectedIcon)) {
                    c = '[';
                    break;
                }
                c = 65535;
                break;
            case 1528152827:
                if (key.equals(Theme.key_chat_inAudioTitleText)) {
                    c = 'n';
                    break;
                }
                c = 65535;
                break;
            case 1549064140:
                if (key.equals(Theme.key_chat_outLoaderPhotoIconSelected)) {
                    c = 191;
                    break;
                }
                c = 65535;
                break;
            case 1573464919:
                if (key.equals(Theme.key_chat_serviceBackgroundSelected)) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case 1585168289:
                if (key.equals(Theme.key_chat_inFileIcon)) {
                    c = 'm';
                    break;
                }
                c = 65535;
                break;
            case 1595048395:
                if (key.equals(Theme.key_chat_inAudioDurationText)) {
                    c = 212;
                    break;
                }
                c = 65535;
                break;
            case 1628297471:
                if (key.equals(Theme.key_chat_messageLinkIn)) {
                    c = 'T';
                    break;
                }
                c = 65535;
                break;
            case 1635685130:
                if (key.equals(Theme.key_profile_verifiedCheck)) {
                    c = 144;
                    break;
                }
                c = 65535;
                break;
            case 1637669025:
                if (key.equals(Theme.key_chat_messageTextOut)) {
                    c = ':';
                    break;
                }
                c = 65535;
                break;
            case 1647377944:
                if (key.equals(Theme.key_chat_outViaBotNameText)) {
                    c = 230;
                    break;
                }
                c = 65535;
                break;
            case 1657795113:
                if (key.equals(Theme.key_chat_outSentCheck)) {
                    c = 251;
                    break;
                }
                c = 65535;
                break;
            case 1657923887:
                if (key.equals(Theme.key_chat_outSentClock)) {
                    c = 'p';
                    break;
                }
                c = 65535;
                break;
            case 1663688926:
                if (key.equals(Theme.key_chats_attachMessage)) {
                    c = 'S';
                    break;
                }
                c = 65535;
                break;
            case 1674274489:
                if (key.equals(Theme.key_chat_inVenueInfoSelectedText)) {
                    c = 236;
                    break;
                }
                c = 65535;
                break;
            case 1674318617:
                if (key.equals(Theme.key_divider)) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case 1676443787:
                if (key.equals("avatar_subtitleInProfileRed")) {
                    c = 'P';
                    break;
                }
                c = 65535;
                break;
            case 1682961989:
                if (key.equals("switchThumbChecked")) {
                    c = 'j';
                    break;
                }
                c = 65535;
                break;
            case 1687612836:
                if (key.equals(Theme.key_actionBarActionModeDefaultIcon)) {
                    c = 237;
                    break;
                }
                c = 65535;
                break;
            case 1714118894:
                if (key.equals(Theme.key_chat_unreadMessagesStartBackground)) {
                    c = 154;
                    break;
                }
                c = 65535;
                break;
            case 1743255577:
                if (key.equals(Theme.key_dialogBackgroundGray)) {
                    c = 255;
                    break;
                }
                c = 65535;
                break;
            case 1809914009:
                if (key.equals(Theme.key_dialogButtonSelector)) {
                    c = 256;
                    break;
                }
                c = 65535;
                break;
            case 1814021667:
                if (key.equals(Theme.key_chat_inFileInfoText)) {
                    c = 185;
                    break;
                }
                c = 65535;
                break;
            case 1828201066:
                if (key.equals(Theme.key_dialogTextBlack)) {
                    c = 137;
                    break;
                }
                c = 65535;
                break;
            case 1829565163:
                if (key.equals(Theme.key_chat_inMenu)) {
                    c = 201;
                    break;
                }
                c = 65535;
                break;
            case 1853943154:
                if (key.equals(Theme.key_chat_messageTextIn)) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 1878895888:
                if (key.equals(Theme.key_avatar_actionBarSelectorBlue)) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case 1878937561:
                if (key.equals("avatar_actionBarSelectorCyan")) {
                    c = 157;
                    break;
                }
                c = 65535;
                break;
            case 1879309868:
                if (key.equals("avatar_actionBarSelectorPink")) {
                    c = 221;
                    break;
                }
                c = 65535;
                break;
            case 1921699010:
                if (key.equals(Theme.key_chats_unreadCounterMuted)) {
                    c = 127;
                    break;
                }
                c = 65535;
                break;
            case 1929729373:
                if (key.equals(Theme.key_progressCircle)) {
                    c = '~';
                    break;
                }
                c = 65535;
                break;
            case 1930276193:
                if (key.equals(Theme.key_chat_inTimeSelectedText)) {
                    c = 259;
                    break;
                }
                c = 65535;
                break;
            case 1947549395:
                if (key.equals(Theme.key_chat_inLoaderPhoto)) {
                    c = 163;
                    break;
                }
                c = 65535;
                break;
            case 1972802227:
                if (key.equals(Theme.key_chat_outReplyMediaMessageText)) {
                    c = 177;
                    break;
                }
                c = 65535;
                break;
            case 1979989987:
                if (key.equals(Theme.key_chat_outVenueInfoText)) {
                    c = 257;
                    break;
                }
                c = 65535;
                break;
            case 1994112714:
                if (key.equals(Theme.key_actionBarActionModeDefaultTop)) {
                    c = '>';
                    break;
                }
                c = 65535;
                break;
            case 2016144760:
                if (key.equals(Theme.key_chat_outLoaderPhoto)) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case 2016511272:
                if (key.equals(Theme.key_stickers_menu)) {
                    c = 128;
                    break;
                }
                c = 65535;
                break;
            case 2052611411:
                if (key.equals(Theme.key_chat_outBubble)) {
                    c = 172;
                    break;
                }
                c = 65535;
                break;
            case 2067556030:
                if (key.equals(Theme.key_chat_emojiPanelIcon)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 2073762588:
                if (key.equals(Theme.key_chat_outFileIcon)) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case 2090082520:
                if (key.equals(Theme.key_chats_nameMessage)) {
                    c = 169;
                    break;
                }
                c = 65535;
                break;
            case 2099978769:
                if (key.equals(Theme.key_chat_outLoaderPhotoIcon)) {
                    c = '4';
                    break;
                }
                c = 65535;
                break;
            case 2109820260:
                if (key.equals("avatar_actionBarSelectorOrange")) {
                    c = 235;
                    break;
                }
                c = 65535;
                break;
            case 2118871810:
                if (key.equals("switchThumb")) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case 2119150199:
                if (key.equals(Theme.key_switchTrack)) {
                    c = 260;
                    break;
                }
                c = 65535;
                break;
            case 2131990258:
                if (key.equals(Theme.key_windowBackgroundWhiteLinkText)) {
                    c = 'x';
                    break;
                }
                c = 65535;
                break;
            case 2133456819:
                if (key.equals(Theme.key_chat_emojiPanelBackground)) {
                    c = 152;
                    break;
                }
                c = 65535;
                break;
            case 2141345810:
                if (key.equals(Theme.key_chat_messagePanelBackground)) {
                    c = 'f';
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
                return -7697782;
            case 1:
                return -1239540194;
            case 2:
                return -9342607;
            case 3:
                return -10653824;
            case 4:
                return -11167525;
            case 5:
                return 2047809827;
            case 6:
                return -8224126;
            case 7:
                return -645885536;
            case '\b':
                return -13803892;
            case '\t':
                return -15921907;
            case '\n':
                return -12401818;
            case 11:
                return -9276814;
            case '\f':
                return -1;
            case '\r':
                return -14339006;
            case 14:
                return -1;
            case 15:
                return -1;
            case 16:
                return -9342607;
            case 17:
                return 1276090861;
            case 18:
                return -8882056;
            case 19:
                return -1579033;
            case 20:
                return -8487298;
            case 21:
                return -1;
            case 22:
                return -11164709;
            case 23:
                return -9594162;
            case 24:
                return -14339006;
            case 25:
                return 251658239;
            case 26:
                return -14605274;
            case 27:
                return -11164965;
            case 28:
                return -1313793;
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                return -3019777;
            case 30:
                return -1;
            case 31:
                return -1979711488;
            case ' ':
                return -11972268;
            case '!':
                return -7960954;
            case '\"':
                return -10653824;
            case '#':
                return -12829636;
            case '$':
                return 268435455;
            case '%':
                return -986896;
            case '&':
                return -2954241;
            case '\'':
                return 402653183;
            case '(':
                return -8211748;
            case ')':
                return -11972524;
            case '*':
                return -11234874;
            case '+':
                return -14143949;
            case ',':
                return -328966;
            case '-':
                return -13077852;
            case '.':
                return -13143396;
            case '/':
                return 1615417684;
            case '0':
                return -668259541;
            case '1':
                return -11108183;
            case '2':
                return -14935012;
            case '3':
                return -5582866;
            case '4':
                return -9263664;
            case '5':
                return -10851462;
            case '6':
                return -13077596;
            case '7':
                return -16777216;
            case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                return -2102800402;
            case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                return -14338750;
            case UndoView.ACTION_TEXT_COPIED /* 58 */:
                return -328966;
            case ';':
                return -3019777;
            case UndoView.ACTION_PHONE_COPIED /* 60 */:
                return -14925725;
            case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                return -328966;
            case '>':
                return -1543503872;
            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                return -7697782;
            case '@':
                return -13091262;
            case VoIPService.CALL_MIN_LAYER /* 65 */:
                return -1;
            case 'B':
                return -9652901;
            case 'C':
                return -3874313;
            case 'D':
                return -13948117;
            case 'E':
                return -11164965;
            case UndoView.ACTION_AUTO_DELETE_ON /* 70 */:
                return -9868951;
            case 'G':
                return -14540254;
            case 'H':
                return -2236963;
            case 'I':
                return -14605274;
            case UndoView.ACTION_REPORT_SENT /* 74 */:
                return -11167525;
            case UndoView.ACTION_GIGAGROUP_CANCEL /* 75 */:
                return -7105645;
            case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
                return -3019777;
            case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
                return -723724;
            case UndoView.ACTION_PIN_DIALOGS /* 78 */:
                return -11099429;
            case UndoView.ACTION_UNPIN_DIALOGS /* 79 */:
                return -5648402;
            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                return -7697782;
            case UndoView.ACTION_CLEAR_DATES /* 81 */:
                return -10052929;
            case UndoView.ACTION_PREVIEW_MEDIA_DESELECTED /* 82 */:
                return -8812393;
            case 'S':
                return -11234874;
            case 'T':
                return -11099173;
            case 'U':
                return -14183202;
            case 'V':
                return -10132123;
            case 'W':
                return -9408400;
            case 'X':
                return -81911774;
            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /* 89 */:
                return -1;
            case 'Z':
                return -1;
            case '[':
                return -13925429;
            case '\\':
                return -9316522;
            case ']':
                return -8747891;
            case '^':
                return -7028510;
            case '_':
                return 167772159;
            case '`':
                return -5452289;
            case 'a':
                return -9851917;
            case 'b':
                return -11972268;
            case 'c':
                return -10658467;
            case 'd':
                return -8812137;
            case 'e':
                return 771751936;
            case 'f':
                return -14803426;
            case 'g':
                return -9316522;
            case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                return -11230501;
            case LocationRequest.PRIORITY_NO_POWER /* 105 */:
                return -7368817;
            case 'j':
                return -13600600;
            case 'k':
                return -1;
            case 'l':
                return -11972268;
            case 'm':
                return -14470078;
            case 'n':
                return -11099173;
            case 'o':
                return -5648402;
            case 'p':
                return -8211748;
            case 'q':
                return -14407896;
            case 'r':
                return -11711155;
            case 's':
                return -14925469;
            case 't':
                return -11972524;
            case 'u':
                return -4792321;
            case 'v':
                return -10653824;
            case 'w':
                return -3019777;
            case 'x':
                return -12741934;
            case 'y':
                return -3019777;
            case 'z':
                return -14925725;
            case '{':
                return -10592674;
            case '|':
                return -9263664;
            case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                return -13859893;
            case '~':
                return -13221820;
            case 127:
                return -12303292;
            case 128:
                return -11710381;
            case TsExtractor.TS_STREAM_TYPE_AC3 /* 129 */:
                return -1;
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /* 130 */:
                return -11164965;
            case 131:
                return -5845010;
            case 132:
                return -9803158;
            case 133:
                return -9263664;
            case TsExtractor.TS_STREAM_TYPE_SPLICE_INFO /* 134 */:
                return -98821092;
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /* 135 */:
                return -1;
            case 136:
                return -15724528;
            case 137:
                return -394759;
            case TsExtractor.TS_STREAM_TYPE_DTS /* 138 */:
                return -1;
            case 139:
                return -5855578;
            case 140:
                return -9590561;
            case 141:
                return -5648402;
            case 142:
                return -1;
            case TLRPC.LAYER /* 143 */:
                return -14605274;
            case 144:
                return -1;
            case 145:
                return -1;
            case 146:
                return -15316366;
            case 147:
                return 1713910333;
            case 148:
                return -8816263;
            case 149:
                return -15056797;
            case 150:
                return -1;
            case BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR /* 151 */:
                return -9934744;
            case 152:
                return -14474461;
            case 153:
                return -11230501;
            case 154:
                return -14339006;
            case 155:
                return -14605274;
            case 156:
                return -11164965;
            case 157:
                return -11972268;
            case 158:
                return -2324391;
            case 159:
                return -9539986;
            case 160:
                return -13619152;
            case 161:
                return -11416584;
            case 162:
                return -9263664;
            case 163:
                return -14404542;
            case 164:
                return -13007663;
            case GroupCallGridCell.CELL_HEIGHT /* 165 */:
                return -11164965;
            case 166:
                return -10653824;
            case 167:
                return -5648402;
            case 168:
                return -1644826;
            case 169:
                return -11696202;
            case 170:
                return 789516;
            case 171:
                return -15263719;
            case TsExtractor.TS_STREAM_TYPE_AC4 /* 172 */:
                return -13077852;
            case 173:
                return -14868445;
            case 174:
                return -11776948;
            case 175:
                return -14869219;
            case 176:
                return -8812393;
            case 177:
                return -3019777;
            case 178:
                return -14605274;
            case 179:
                return -7421976;
            case 180:
                return -3019777;
            case 181:
                return -7697782;
            case 182:
                return -14187829;
            case 183:
                return -5648146;
            case 184:
                return -16777216;
            case 185:
                return -8812137;
            case 186:
                return -8224126;
            case 187:
                return -11443856;
            case TsExtractor.TS_PACKET_SIZE /* 188 */:
                return -8812393;
            case PsExtractor.PRIVATE_STREAM_1 /* 189 */:
                return -11232035;
            case 190:
                return -1;
            case 191:
                return -1;
            case PsExtractor.AUDIO_STREAM /* 192 */:
                return -1770871344;
            case 193:
                return -10915968;
            case 194:
                return -45994;
            case 195:
                return -11972268;
            case 196:
                return 1627389951;
            case 197:
                return -3874313;
            case 198:
                return -657931;
            case 199:
                return -3019777;
            case 200:
                return -10574624;
            case SearchViewPager.forwardItemId /* 201 */:
                return 2036100992;
            case SearchViewPager.deleteItemId /* 202 */:
                return 1196577362;
            case 203:
                return -10452291;
            case 204:
                return -4792577;
            case 205:
                return -620756993;
            case 206:
                return -10987432;
            case 207:
                return -14338750;
            case 208:
                return -1;
            case 209:
                return -4792321;
            case 210:
                return -1;
            case 211:
                return -10910270;
            case 212:
                return -8746857;
            case 213:
                return 301989887;
            case 214:
                return -1776412;
            case 215:
                return -11232035;
            case 216:
                return -5648402;
            case 217:
                return -11890739;
            case 218:
                return -11099173;
            case 219:
                return -11164709;
            case 220:
                return -13208924;
            case 221:
                return -11972268;
            case 222:
                return -3019777;
            case 223:
                return -10526881;
            case 224:
                return -15526377;
            case 225:
                return 217775871;
            case 226:
                return -7434610;
            case 227:
                return -1481631;
            case 228:
                return -7697782;
            case 229:
                return -6513508;
            case CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP /* 230 */:
                return -3019777;
            case 231:
                return -14605274;
            case 232:
                return -12214815;
            case 233:
                return -14605274;
            case 234:
                return -1;
            case 235:
                return -11972268;
            case 236:
                return -5648402;
            case 237:
                return -1;
            case 238:
                return -9934744;
            case 239:
                return -7697782;
            case PsExtractor.VIDEO_STREAM_MASK /* 240 */:
                return -3019777;
            case 241:
                return -11447983;
            case 242:
                return -10653824;
            case 243:
                return -6234891;
            case 244:
                return -10790053;
            case 245:
                return -657931;
            case 246:
                return -855310;
            case 247:
                return -12413479;
            case 248:
                return -1;
            case 249:
                return -14605274;
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION /* 250 */:
                return -11184811;
            case 251:
                return -6831126;
            case 252:
                return -1;
            case 253:
                return -1118482;
            case 254:
                return -3019777;
            case 255:
                return -11840163;
            case 256:
                return 352321535;
            case InputDeviceCompat.SOURCE_KEYBOARD /* 257 */:
                return -4792321;
            case 258:
                return -693579794;
            case 259:
                return -5582866;
            case 260:
                return -13948117;
            case 261:
                return -7697782;
            default:
                FileLog.w("returning color for key " + key + " from current theme");
                return Theme.getColor(key);
        }
    }

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(getColor(key), PorterDuff.Mode.MULTIPLY));
        return drawable;
    }
}
