package com.google.android.exoplayer2.text.cea;

import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.gms.location.LocationRequest;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.voip.GroupCallGridCell;
/* loaded from: classes3.dex */
public final class Cea608Decoder extends CeaDecoder {
    private static final int CC_FIELD_FLAG = 1;
    private static final byte CC_IMPLICIT_DATA_HEADER = -4;
    private static final int CC_MODE_PAINT_ON = 3;
    private static final int CC_MODE_POP_ON = 2;
    private static final int CC_MODE_ROLL_UP = 1;
    private static final int CC_MODE_UNKNOWN = 0;
    private static final int CC_TYPE_FLAG = 2;
    private static final int CC_VALID_FLAG = 4;
    private static final byte CTRL_BACKSPACE = 33;
    private static final byte CTRL_CARRIAGE_RETURN = 45;
    private static final byte CTRL_DELETE_TO_END_OF_ROW = 36;
    private static final byte CTRL_END_OF_CAPTION = 47;
    private static final byte CTRL_ERASE_DISPLAYED_MEMORY = 44;
    private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = 46;
    private static final byte CTRL_RESUME_CAPTION_LOADING = 32;
    private static final byte CTRL_RESUME_DIRECT_CAPTIONING = 41;
    private static final byte CTRL_RESUME_TEXT_DISPLAY = 43;
    private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = 37;
    private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = 38;
    private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = 39;
    private static final byte CTRL_TEXT_RESTART = 42;
    private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
    private static final int NTSC_CC_CHANNEL_1 = 0;
    private static final int NTSC_CC_CHANNEL_2 = 1;
    private static final int NTSC_CC_FIELD_1 = 0;
    private static final int NTSC_CC_FIELD_2 = 1;
    private static final int STYLE_ITALICS = 7;
    private static final int STYLE_UNCHANGED = 8;
    private static final String TAG = "Cea608Decoder";
    private int captionMode;
    private int captionRowCount;
    private List<Cue> cues;
    private boolean isCaptionValid;
    private boolean isInCaptionService;
    private List<Cue> lastCues;
    private final int packetLength;
    private byte repeatableControlCc1;
    private byte repeatableControlCc2;
    private boolean repeatableControlSet;
    private final int selectedChannel;
    private final int selectedField;
    private static final int[] ROW_INDICES = {11, 1, 3, 12, 14, 5, 7, 9};
    private static final int[] COLUMN_INDICES = {0, 4, 8, 12, 16, 20, 24, 28};
    private static final int[] STYLE_COLORS = {-1, -16711936, -16776961, -16711681, SupportMenu.CATEGORY_MASK, InputDeviceCompat.SOURCE_ANY, -65281};
    private static final int[] BASIC_CHARACTER_SET = {32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 97, 98, 99, 100, 101, 102, 103, LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632};
    private static final int[] SPECIAL_CHARACTER_SET = {174, 176, PsExtractor.PRIVATE_STREAM_1, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251};
    private static final int[] SPECIAL_ES_FR_CHARACTER_SET = {193, SearchViewPager.forwardItemId, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, PsExtractor.AUDIO_STREAM, 194, 199, 200, SearchViewPager.deleteItemId, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187};
    private static final int[] SPECIAL_PT_DE_CHARACTER_SET = {195, 227, 205, 204, 236, 210, 242, 213, 245, 123, ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, GroupCallGridCell.CELL_HEIGHT, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496};
    private static final boolean[] ODD_PARITY_BYTE_TABLE = {false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false};
    private final ParsableByteArray ccData = new ParsableByteArray();
    private final ArrayList<CueBuilder> cueBuilders = new ArrayList<>();
    private CueBuilder currentCueBuilder = new CueBuilder(0, 4);
    private int currentChannel = 0;

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public /* bridge */ /* synthetic */ SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        return super.dequeueInputBuffer();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public /* bridge */ /* synthetic */ SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        return super.dequeueOutputBuffer();
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    public /* bridge */ /* synthetic */ void queueInputBuffer(SubtitleInputBuffer subtitleInputBuffer) throws SubtitleDecoderException {
        super.queueInputBuffer(subtitleInputBuffer);
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.text.SubtitleDecoder
    public /* bridge */ /* synthetic */ void setPositionUs(long j) {
        super.setPositionUs(j);
    }

    public Cea608Decoder(String mimeType, int accessibilityChannel) {
        this.packetLength = MimeTypes.APPLICATION_MP4CEA608.equals(mimeType) ? 2 : 3;
        switch (accessibilityChannel) {
            case 1:
                this.selectedChannel = 0;
                this.selectedField = 0;
                break;
            case 2:
                this.selectedChannel = 1;
                this.selectedField = 0;
                break;
            case 3:
                this.selectedChannel = 0;
                this.selectedField = 1;
                break;
            case 4:
                this.selectedChannel = 1;
                this.selectedField = 1;
                break;
            default:
                Log.w(TAG, "Invalid channel. Defaulting to CC1.");
                this.selectedChannel = 0;
                this.selectedField = 0;
                break;
        }
        setCaptionMode(0);
        resetCueBuilders();
        this.isInCaptionService = true;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public String getName() {
        return TAG;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void flush() {
        super.flush();
        this.cues = null;
        this.lastCues = null;
        setCaptionMode(0);
        setCaptionRowCount(4);
        resetCueBuilders();
        this.isCaptionValid = false;
        this.repeatableControlSet = false;
        this.repeatableControlCc1 = (byte) 0;
        this.repeatableControlCc2 = (byte) 0;
        this.currentChannel = 0;
        this.isInCaptionService = true;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder, com.google.android.exoplayer2.decoder.Decoder
    public void release() {
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    protected boolean isNewSubtitleDataAvailable() {
        return this.cues != this.lastCues;
    }

    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    protected Subtitle createSubtitle() {
        this.lastCues = this.cues;
        return new CeaSubtitle(this.cues);
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x005c, code lost:
        if (r8[r4] != false) goto L23;
     */
    @Override // com.google.android.exoplayer2.text.cea.CeaDecoder
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void decode(com.google.android.exoplayer2.text.SubtitleInputBuffer r11) {
        /*
            Method dump skipped, instructions count: 259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.cea.Cea608Decoder.decode(com.google.android.exoplayer2.text.SubtitleInputBuffer):void");
    }

    private boolean updateAndVerifyCurrentChannel(byte cc1) {
        if (isCtrlCode(cc1)) {
            this.currentChannel = getChannel(cc1);
        }
        return this.currentChannel == this.selectedChannel;
    }

    private boolean isRepeatedCommand(boolean captionValid, byte cc1, byte cc2) {
        if (captionValid && isRepeatable(cc1)) {
            if (this.repeatableControlSet && this.repeatableControlCc1 == cc1 && this.repeatableControlCc2 == cc2) {
                this.repeatableControlSet = false;
                return true;
            }
            this.repeatableControlSet = true;
            this.repeatableControlCc1 = cc1;
            this.repeatableControlCc2 = cc2;
        } else {
            this.repeatableControlSet = false;
        }
        return false;
    }

    private void handleMidrowCtrl(byte cc2) {
        this.currentCueBuilder.append(' ');
        boolean z = true;
        if ((cc2 & 1) != 1) {
            z = false;
        }
        boolean underline = z;
        int style = (cc2 >> 1) & 7;
        this.currentCueBuilder.setStyle(style, underline);
    }

    private void handlePreambleAddressCode(byte cc1, byte cc2) {
        int row = ROW_INDICES[cc1 & 7];
        boolean underline = false;
        boolean nextRowDown = (cc2 & CTRL_RESUME_CAPTION_LOADING) != 0;
        if (nextRowDown) {
            row++;
        }
        if (row != this.currentCueBuilder.row) {
            if (this.captionMode != 1 && !this.currentCueBuilder.isEmpty()) {
                CueBuilder cueBuilder = new CueBuilder(this.captionMode, this.captionRowCount);
                this.currentCueBuilder = cueBuilder;
                this.cueBuilders.add(cueBuilder);
            }
            this.currentCueBuilder.row = row;
        }
        boolean isCursor = (cc2 & 16) == 16;
        if ((cc2 & 1) == 1) {
            underline = true;
        }
        int cursorOrStyle = (cc2 >> 1) & 7;
        this.currentCueBuilder.setStyle(isCursor ? 8 : cursorOrStyle, underline);
        if (!isCursor) {
            return;
        }
        this.currentCueBuilder.indent = COLUMN_INDICES[cursorOrStyle];
    }

    private void handleMiscCode(byte cc2) {
        switch (cc2) {
            case 32:
                setCaptionMode(2);
                return;
            case 37:
                setCaptionMode(1);
                setCaptionRowCount(2);
                return;
            case 38:
                setCaptionMode(1);
                setCaptionRowCount(3);
                return;
            case 39:
                setCaptionMode(1);
                setCaptionRowCount(4);
                return;
            case 41:
                setCaptionMode(3);
                return;
            default:
                int i = this.captionMode;
                if (i == 0) {
                    return;
                }
                switch (cc2) {
                    case 33:
                        this.currentCueBuilder.backspace();
                        return;
                    case 36:
                    default:
                        return;
                    case 44:
                        this.cues = Collections.emptyList();
                        int i2 = this.captionMode;
                        if (i2 == 1 || i2 == 3) {
                            resetCueBuilders();
                            return;
                        }
                        return;
                    case 45:
                        if (i == 1 && !this.currentCueBuilder.isEmpty()) {
                            this.currentCueBuilder.rollUp();
                            return;
                        }
                        return;
                    case 46:
                        resetCueBuilders();
                        return;
                    case 47:
                        this.cues = getDisplayCues();
                        resetCueBuilders();
                        return;
                }
        }
    }

    private List<Cue> getDisplayCues() {
        int positionAnchor = 2;
        int cueBuilderCount = this.cueBuilders.size();
        List<Cue> cueBuilderCues = new ArrayList<>(cueBuilderCount);
        for (int i = 0; i < cueBuilderCount; i++) {
            Cue cue = this.cueBuilders.get(i).build(Integer.MIN_VALUE);
            cueBuilderCues.add(cue);
            if (cue != null) {
                positionAnchor = Math.min(positionAnchor, cue.positionAnchor);
            }
        }
        List<Cue> displayCues = new ArrayList<>(cueBuilderCount);
        for (int i2 = 0; i2 < cueBuilderCount; i2++) {
            Cue cue2 = cueBuilderCues.get(i2);
            if (cue2 != null) {
                if (cue2.positionAnchor != positionAnchor) {
                    cue2 = this.cueBuilders.get(i2).build(positionAnchor);
                }
                displayCues.add(cue2);
            }
        }
        return displayCues;
    }

    private void setCaptionMode(int captionMode) {
        if (this.captionMode == captionMode) {
            return;
        }
        int oldCaptionMode = this.captionMode;
        this.captionMode = captionMode;
        if (captionMode == 3) {
            for (int i = 0; i < this.cueBuilders.size(); i++) {
                this.cueBuilders.get(i).setCaptionMode(captionMode);
            }
            return;
        }
        resetCueBuilders();
        if (oldCaptionMode == 3 || captionMode == 1 || captionMode == 0) {
            this.cues = Collections.emptyList();
        }
    }

    private void setCaptionRowCount(int captionRowCount) {
        this.captionRowCount = captionRowCount;
        this.currentCueBuilder.setCaptionRowCount(captionRowCount);
    }

    private void resetCueBuilders() {
        this.currentCueBuilder.reset(this.captionMode);
        this.cueBuilders.clear();
        this.cueBuilders.add(this.currentCueBuilder);
    }

    private void maybeUpdateIsInCaptionService(byte cc1, byte cc2) {
        if (isXdsControlCode(cc1)) {
            this.isInCaptionService = false;
        } else if (isServiceSwitchCommand(cc1)) {
            switch (cc2) {
                case 32:
                case 37:
                case 38:
                case 39:
                case 41:
                case 47:
                    this.isInCaptionService = true;
                    return;
                case 42:
                case 43:
                    this.isInCaptionService = false;
                    return;
                default:
                    return;
            }
        }
    }

    private static char getBasicChar(byte ccData) {
        int index = (ccData & Byte.MAX_VALUE) - 32;
        return (char) BASIC_CHARACTER_SET[index];
    }

    private static boolean isSpecialNorthAmericanChar(byte cc1, byte cc2) {
        return (cc1 & 247) == 17 && (cc2 & 240) == 48;
    }

    private static char getSpecialNorthAmericanChar(byte ccData) {
        int index = ccData & 15;
        return (char) SPECIAL_CHARACTER_SET[index];
    }

    private static boolean isExtendedWestEuropeanChar(byte cc1, byte cc2) {
        return (cc1 & 246) == 18 && (cc2 & 224) == 32;
    }

    private static char getExtendedWestEuropeanChar(byte cc1, byte cc2) {
        if ((cc1 & 1) == 0) {
            return getExtendedEsFrChar(cc2);
        }
        return getExtendedPtDeChar(cc2);
    }

    private static char getExtendedEsFrChar(byte ccData) {
        int index = ccData & 31;
        return (char) SPECIAL_ES_FR_CHARACTER_SET[index];
    }

    private static char getExtendedPtDeChar(byte ccData) {
        int index = ccData & 31;
        return (char) SPECIAL_PT_DE_CHARACTER_SET[index];
    }

    private static boolean isCtrlCode(byte cc1) {
        return (cc1 & 224) == 0;
    }

    private static int getChannel(byte cc1) {
        return (cc1 >> 3) & 1;
    }

    private static boolean isMidrowCtrlCode(byte cc1, byte cc2) {
        return (cc1 & 247) == 17 && (cc2 & 240) == 32;
    }

    private static boolean isPreambleAddressCode(byte cc1, byte cc2) {
        return (cc1 & 240) == 16 && (cc2 & 192) == 64;
    }

    private static boolean isTabCtrlCode(byte cc1, byte cc2) {
        return (cc1 & 247) == 23 && cc2 >= 33 && cc2 <= 35;
    }

    private static boolean isMiscCode(byte cc1, byte cc2) {
        return (cc1 & 246) == 20 && (cc2 & 240) == 32;
    }

    private static boolean isRepeatable(byte cc1) {
        return (cc1 & 240) == 16;
    }

    private static boolean isXdsControlCode(byte cc1) {
        return 1 <= cc1 && cc1 <= 15;
    }

    private static boolean isServiceSwitchCommand(byte cc1) {
        return (cc1 & 247) == 20;
    }

    /* loaded from: classes3.dex */
    public static class CueBuilder {
        private static final int BASE_ROW = 15;
        private static final int SCREEN_CHARWIDTH = 32;
        private int captionMode;
        private int captionRowCount;
        private int indent;
        private int row;
        private int tabOffset;
        private final List<CueStyle> cueStyles = new ArrayList();
        private final List<SpannableString> rolledUpCaptions = new ArrayList();
        private final StringBuilder captionStringBuilder = new StringBuilder();

        public CueBuilder(int captionMode, int captionRowCount) {
            reset(captionMode);
            setCaptionRowCount(captionRowCount);
        }

        public void reset(int captionMode) {
            this.captionMode = captionMode;
            this.cueStyles.clear();
            this.rolledUpCaptions.clear();
            this.captionStringBuilder.setLength(0);
            this.row = 15;
            this.indent = 0;
            this.tabOffset = 0;
        }

        public boolean isEmpty() {
            return this.cueStyles.isEmpty() && this.rolledUpCaptions.isEmpty() && this.captionStringBuilder.length() == 0;
        }

        public void setCaptionMode(int captionMode) {
            this.captionMode = captionMode;
        }

        public void setCaptionRowCount(int captionRowCount) {
            this.captionRowCount = captionRowCount;
        }

        public void setStyle(int style, boolean underline) {
            this.cueStyles.add(new CueStyle(style, underline, this.captionStringBuilder.length()));
        }

        public void backspace() {
            int length = this.captionStringBuilder.length();
            if (length > 0) {
                this.captionStringBuilder.delete(length - 1, length);
                for (int i = this.cueStyles.size() - 1; i >= 0; i--) {
                    CueStyle style = this.cueStyles.get(i);
                    if (style.start != length) {
                        return;
                    }
                    style.start--;
                }
            }
        }

        public void append(char text) {
            this.captionStringBuilder.append(text);
        }

        public void rollUp() {
            this.rolledUpCaptions.add(buildCurrentLine());
            this.captionStringBuilder.setLength(0);
            this.cueStyles.clear();
            int numRows = Math.min(this.captionRowCount, this.row);
            while (this.rolledUpCaptions.size() >= numRows) {
                this.rolledUpCaptions.remove(0);
            }
        }

        public Cue build(int forcedPositionAnchor) {
            int positionAnchor;
            float position;
            int lineAnchor;
            int line;
            SpannableStringBuilder cueString = new SpannableStringBuilder();
            for (int i = 0; i < this.rolledUpCaptions.size(); i++) {
                cueString.append((CharSequence) this.rolledUpCaptions.get(i));
                cueString.append('\n');
            }
            cueString.append((CharSequence) buildCurrentLine());
            if (cueString.length() != 0) {
                int startPadding = this.indent + this.tabOffset;
                int endPadding = (32 - startPadding) - cueString.length();
                int startEndPaddingDelta = startPadding - endPadding;
                if (forcedPositionAnchor != Integer.MIN_VALUE) {
                    positionAnchor = forcedPositionAnchor;
                } else if (this.captionMode == 2 && (Math.abs(startEndPaddingDelta) < 3 || endPadding < 0)) {
                    positionAnchor = 1;
                } else if (this.captionMode == 2 && startEndPaddingDelta > 0) {
                    positionAnchor = 2;
                } else {
                    positionAnchor = 0;
                }
                switch (positionAnchor) {
                    case 1:
                        position = 0.5f;
                        break;
                    case 2:
                        float position2 = (32 - endPadding) / 32.0f;
                        position = (0.8f * position2) + 0.1f;
                        break;
                    default:
                        float position3 = startPadding / 32.0f;
                        position = (0.8f * position3) + 0.1f;
                        break;
                }
                if (this.captionMode == 1 || this.row > 7) {
                    int line2 = this.row - 15;
                    lineAnchor = 2;
                    line = line2 - 2;
                } else {
                    lineAnchor = 0;
                    line = this.row;
                }
                return new Cue(cueString, Layout.Alignment.ALIGN_NORMAL, line, 1, lineAnchor, position, positionAnchor, -3.4028235E38f);
            }
            return null;
        }

        private SpannableString buildCurrentLine() {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.captionStringBuilder);
            int length = builder.length();
            int underlineStartPosition = -1;
            int italicStartPosition = -1;
            int colorStartPosition = 0;
            int color = -1;
            boolean nextItalic = false;
            int nextColor = -1;
            for (int i = 0; i < this.cueStyles.size(); i++) {
                CueStyle cueStyle = this.cueStyles.get(i);
                boolean underline = cueStyle.underline;
                int style = cueStyle.style;
                if (style != 8) {
                    nextItalic = style == 7;
                    nextColor = style == 7 ? nextColor : Cea608Decoder.STYLE_COLORS[style];
                }
                int position = cueStyle.start;
                int nextPosition = i + 1 < this.cueStyles.size() ? this.cueStyles.get(i + 1).start : length;
                if (position != nextPosition) {
                    if (underlineStartPosition != -1 && !underline) {
                        setUnderlineSpan(builder, underlineStartPosition, position);
                        underlineStartPosition = -1;
                    } else if (underlineStartPosition == -1 && underline) {
                        underlineStartPosition = position;
                    }
                    if (italicStartPosition != -1 && !nextItalic) {
                        setItalicSpan(builder, italicStartPosition, position);
                        italicStartPosition = -1;
                    } else if (italicStartPosition == -1 && nextItalic) {
                        italicStartPosition = position;
                    }
                    if (nextColor != color) {
                        setColorSpan(builder, colorStartPosition, position, color);
                        color = nextColor;
                        colorStartPosition = position;
                    }
                }
            }
            if (underlineStartPosition != -1 && underlineStartPosition != length) {
                setUnderlineSpan(builder, underlineStartPosition, length);
            }
            if (italicStartPosition != -1 && italicStartPosition != length) {
                setItalicSpan(builder, italicStartPosition, length);
            }
            if (colorStartPosition != length) {
                setColorSpan(builder, colorStartPosition, length, color);
            }
            return new SpannableString(builder);
        }

        private static void setUnderlineSpan(SpannableStringBuilder builder, int start, int end) {
            builder.setSpan(new UnderlineSpan(), start, end, 33);
        }

        private static void setItalicSpan(SpannableStringBuilder builder, int start, int end) {
            builder.setSpan(new StyleSpan(2), start, end, 33);
        }

        private static void setColorSpan(SpannableStringBuilder builder, int start, int end, int color) {
            if (color == -1) {
                return;
            }
            builder.setSpan(new ForegroundColorSpan(color), start, end, 33);
        }

        /* loaded from: classes3.dex */
        public static class CueStyle {
            public int start;
            public final int style;
            public final boolean underline;

            public CueStyle(int style, boolean underline, int start) {
                this.style = style;
                this.underline = underline;
                this.start = start;
            }
        }
    }
}
