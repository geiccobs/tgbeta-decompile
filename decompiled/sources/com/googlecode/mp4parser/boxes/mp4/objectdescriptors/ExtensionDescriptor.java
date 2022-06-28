package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import androidx.recyclerview.widget.ItemTouchHelper;
import com.coremedia.iso.Hex;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.BasePermissionsActivity;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.voip.GroupCallGridCell;
@Descriptor(tags = {19, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH, 126, 127, 128, TsExtractor.TS_STREAM_TYPE_AC3, TsExtractor.TS_STREAM_TYPE_HDMV_DTS, 131, 132, 133, TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, TsExtractor.TS_STREAM_TYPE_E_AC3, 136, 137, TsExtractor.TS_STREAM_TYPE_DTS, 139, 140, 141, 142, TLRPC.LAYER, 144, 145, 146, 147, 148, 149, 150, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, GroupCallGridCell.CELL_HEIGHT, 166, 167, 168, 169, 170, 171, TsExtractor.TS_STREAM_TYPE_AC4, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, TsExtractor.TS_PACKET_SIZE, PsExtractor.PRIVATE_STREAM_1, 190, 191, PsExtractor.AUDIO_STREAM, 193, 194, 195, 196, 197, 198, 199, 200, SearchViewPager.forwardItemId, SearchViewPager.deleteItemId, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, CustomPhoneKeyboardView.KEYBOARD_HEIGHT_DP, 231, 232, 233, 234, 235, 236, 237, 238, 239, PsExtractor.VIDEO_STREAM_MASK, 241, 242, 243, 244, 245, 246, 247, 248, 249, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 251, 252, 253})
/* loaded from: classes3.dex */
public class ExtensionDescriptor extends BaseDescriptor {
    private static Logger log = Logger.getLogger(ExtensionDescriptor.class.getName());
    byte[] bytes;

    static int[] allTags() {
        int[] ints = new int[148];
        for (int i = 106; i < 254; i++) {
            int pos = i - 106;
            log.finest("pos:" + pos);
            ints[pos] = i;
        }
        return ints;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public void parseDetail(ByteBuffer bb) throws IOException {
        if (getSize() > 0) {
            byte[] bArr = new byte[this.sizeOfInstance];
            this.bytes = bArr;
            bb.get(bArr);
        }
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtensionDescriptor");
        sb.append("{bytes=");
        byte[] bArr = this.bytes;
        sb.append(bArr == null ? "null" : Hex.encodeHex(bArr));
        sb.append('}');
        return sb.toString();
    }
}
