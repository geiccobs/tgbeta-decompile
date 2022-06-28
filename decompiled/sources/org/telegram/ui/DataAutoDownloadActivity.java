package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.DataAutoDownloadActivity;
/* loaded from: classes4.dex */
public class DataAutoDownloadActivity extends BaseFragment {
    private boolean animateChecked;
    private int autoDownloadRow;
    private int autoDownloadSectionRow;
    private int currentPresetNum;
    private int currentType;
    private DownloadController.Preset defaultPreset;
    private int filesRow;
    private String key;
    private String key2;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int photosRow;
    private int rowCount;
    private int typeHeaderRow;
    private DownloadController.Preset typePreset;
    private int typeSectionRow;
    private int usageHeaderRow;
    private int usageProgressRow;
    private int usageSectionRow;
    private int videosRow;
    private boolean wereAnyChanges;
    private ArrayList<DownloadController.Preset> presets = new ArrayList<>();
    private int selectedPreset = 1;
    private DownloadController.Preset lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
    private DownloadController.Preset mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
    private DownloadController.Preset highPreset = DownloadController.getInstance(this.currentAccount).highPreset;

    public DataAutoDownloadActivity(int type) {
        this.currentType = type;
        int i = this.currentType;
        if (i == 0) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentMobilePreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            this.defaultPreset = this.mediumPreset;
            this.key = "mobilePreset";
            this.key2 = "currentMobilePreset";
        } else if (i == 1) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentWifiPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).wifiPreset;
            this.defaultPreset = this.highPreset;
            this.key = "wifiPreset";
            this.key2 = "currentWifiPreset";
        } else {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentRoamingPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).roamingPreset;
            this.defaultPreset = this.lowPreset;
            this.key = "roamingPreset";
            this.key2 = "currentRoamingPreset";
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        fillPresets();
        updateRows();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnMobileData", R.string.AutoDownloadOnMobileData));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnWiFiData", R.string.AutoDownloadOnWiFiData));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnRoamingData", R.string.AutoDownloadOnRoamingData));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.DataAutoDownloadActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    DataAutoDownloadActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i2) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i2, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i2, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i2, float f, float f2) {
                DataAutoDownloadActivity.this.m3302lambda$createView$4$orgtelegramuiDataAutoDownloadActivity(view, i2, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DataAutoDownloadActivity */
    public /* synthetic */ void m3302lambda$createView$4$orgtelegramuiDataAutoDownloadActivity(final View view, final int position, float x, float y) {
        int type;
        DownloadController.Preset currentPreset;
        String key;
        String key2;
        DownloadController.Preset currentPreset2;
        boolean z;
        BottomSheet.Builder builder;
        DownloadController.Preset preset;
        int i = position;
        if (i == this.autoDownloadRow) {
            int i2 = this.currentPresetNum;
            if (i2 != 3) {
                if (i2 == 0) {
                    this.typePreset.set(this.lowPreset);
                } else if (i2 == 1) {
                    this.typePreset.set(this.mediumPreset);
                } else if (i2 == 2) {
                    this.typePreset.set(this.highPreset);
                }
            }
            TextCheckCell cell = (TextCheckCell) view;
            boolean checked = cell.isChecked();
            if (!checked && this.typePreset.enabled) {
                System.arraycopy(this.defaultPreset.mask, 0, this.typePreset.mask, 0, 4);
            } else {
                this.typePreset.enabled = !preset.enabled;
            }
            boolean z2 = this.typePreset.enabled;
            String str = Theme.key_windowBackgroundChecked;
            view.setTag(z2 ? str : Theme.key_windowBackgroundUnchecked);
            boolean z3 = !checked;
            if (!this.typePreset.enabled) {
                str = Theme.key_windowBackgroundUnchecked;
            }
            cell.setBackgroundColorAnimated(z3, Theme.getColor(str));
            updateRows();
            if (this.typePreset.enabled) {
                this.listAdapter.notifyItemRangeInserted(this.autoDownloadSectionRow + 1, 8);
            } else {
                this.listAdapter.notifyItemRangeRemoved(this.autoDownloadSectionRow + 1, 8);
            }
            this.listAdapter.notifyItemChanged(this.autoDownloadSectionRow);
            SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString(this.key, this.typePreset.toString());
            String str2 = this.key2;
            this.currentPresetNum = 3;
            editor.putInt(str2, 3);
            int i3 = this.currentType;
            if (i3 == 0) {
                DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
            } else if (i3 == 1) {
                DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
            } else {
                DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
            }
            editor.commit();
            cell.setChecked(!checked);
            DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
            this.wereAnyChanges = true;
        } else if ((i == this.photosRow || i == this.videosRow || i == this.filesRow) && view.isEnabled()) {
            if (i == this.photosRow) {
                type = 1;
            } else if (i == this.videosRow) {
                type = 4;
            } else {
                type = 8;
            }
            final int index = DownloadController.typeToIndex(type);
            int i4 = this.currentType;
            if (i4 == 0) {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentMobilePreset();
                key = "mobilePreset";
                key2 = "currentMobilePreset";
            } else if (i4 == 1) {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentWiFiPreset();
                key = "wifiPreset";
                key2 = "currentWifiPreset";
            } else {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentRoamingPreset();
                key = "roamingPreset";
                key2 = "currentRoamingPreset";
            }
            NotificationsCheckCell cell2 = (NotificationsCheckCell) view;
            boolean checked2 = cell2.isChecked();
            if ((LocaleController.isRTL && x <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                int i5 = this.currentPresetNum;
                if (i5 != 3) {
                    if (i5 == 0) {
                        this.typePreset.set(this.lowPreset);
                    } else if (i5 == 1) {
                        this.typePreset.set(this.mediumPreset);
                    } else if (i5 == 2) {
                        this.typePreset.set(this.highPreset);
                    }
                }
                boolean hasAny = false;
                int a = 0;
                while (true) {
                    if (a < this.typePreset.mask.length) {
                        if ((currentPreset.mask[a] & type) == 0) {
                            a++;
                        } else {
                            hasAny = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                for (int a2 = 0; a2 < this.typePreset.mask.length; a2++) {
                    if (checked2) {
                        int[] iArr = this.typePreset.mask;
                        iArr[a2] = iArr[a2] & (type ^ (-1));
                    } else if (!hasAny) {
                        int[] iArr2 = this.typePreset.mask;
                        iArr2[a2] = iArr2[a2] | type;
                    }
                }
                int a3 = this.currentAccount;
                SharedPreferences.Editor editor2 = MessagesController.getMainSettings(a3).edit();
                editor2.putString(key, this.typePreset.toString());
                this.currentPresetNum = 3;
                editor2.putInt(key2, 3);
                int i6 = this.currentType;
                if (i6 == 0) {
                    DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
                } else if (i6 == 1) {
                    DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
                } else {
                    DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
                }
                editor2.commit();
                cell2.setChecked(!checked2);
                RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, i);
                }
                DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
                this.wereAnyChanges = true;
                fillPresets();
            } else if (getParentActivity() != null) {
                BottomSheet.Builder builder2 = new BottomSheet.Builder(getParentActivity());
                builder2.setApplyTopPadding(false);
                builder2.setApplyBottomPadding(false);
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                builder2.setCustomView(linearLayout);
                HeaderCell headerCell = new HeaderCell(getParentActivity(), Theme.key_dialogTextBlue2, 21, 15, false);
                if (i == this.photosRow) {
                    headerCell.setText(LocaleController.getString("AutoDownloadPhotosTitle", R.string.AutoDownloadPhotosTitle));
                } else if (i == this.videosRow) {
                    headerCell.setText(LocaleController.getString("AutoDownloadVideosTitle", R.string.AutoDownloadVideosTitle));
                } else {
                    headerCell.setText(LocaleController.getString("AutoDownloadFilesTitle", R.string.AutoDownloadFilesTitle));
                }
                linearLayout.addView(headerCell, LayoutHelper.createFrame(-1, -2.0f));
                MaxFileSizeCell[] sizeCell = new MaxFileSizeCell[1];
                final TextCheckCell[] checkCell = new TextCheckCell[1];
                final AnimatorSet[] animatorSet = new AnimatorSet[1];
                final TextCheckBoxCell[] cells = new TextCheckBoxCell[4];
                int a4 = 0;
                for (int i7 = 4; a4 < i7; i7 = 4) {
                    HeaderCell headerCell2 = headerCell;
                    LinearLayout linearLayout2 = linearLayout;
                    final MaxFileSizeCell[] sizeCell2 = sizeCell;
                    final TextCheckBoxCell checkBoxCell = new TextCheckBoxCell(getParentActivity(), true, false);
                    cells[a4] = checkBoxCell;
                    if (a4 == 0) {
                        builder = builder2;
                        cells[a4].setTextAndCheck(LocaleController.getString("AutodownloadContacts", R.string.AutodownloadContacts), (currentPreset.mask[0] & type) != 0, true);
                    } else {
                        builder = builder2;
                        if (a4 == 1) {
                            cells[a4].setTextAndCheck(LocaleController.getString("AutodownloadPrivateChats", R.string.AutodownloadPrivateChats), (currentPreset.mask[1] & type) != 0, true);
                        } else if (a4 != 2) {
                            cells[a4].setTextAndCheck(LocaleController.getString("AutodownloadChannels", R.string.AutodownloadChannels), (currentPreset.mask[3] & type) != 0, i != this.photosRow);
                        } else {
                            cells[a4].setTextAndCheck(LocaleController.getString("AutodownloadGroupChats", R.string.AutodownloadGroupChats), (currentPreset.mask[2] & type) != 0, true);
                        }
                    }
                    cells[a4].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cells[a4].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            DataAutoDownloadActivity.this.m3300lambda$createView$0$orgtelegramuiDataAutoDownloadActivity(checkBoxCell, cells, position, sizeCell2, checkCell, animatorSet, view2);
                        }
                    });
                    linearLayout2.addView(cells[a4], LayoutHelper.createFrame(-1, 50.0f));
                    a4++;
                    i = position;
                    linearLayout = linearLayout2;
                    headerCell = headerCell2;
                    cell2 = cell2;
                    sizeCell = sizeCell2;
                    key2 = key2;
                    builder2 = builder;
                    key = key;
                    currentPreset = currentPreset;
                }
                LinearLayout linearLayout3 = linearLayout;
                final MaxFileSizeCell[] sizeCell3 = sizeCell;
                final BottomSheet.Builder builder3 = builder2;
                final String key22 = key2;
                final String key3 = key;
                DownloadController.Preset currentPreset3 = currentPreset;
                if (position != this.photosRow) {
                    final TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(getParentActivity());
                    sizeCell3[0] = new MaxFileSizeCell(getParentActivity()) { // from class: org.telegram.ui.DataAutoDownloadActivity.3
                        @Override // org.telegram.ui.Cells.MaxFileSizeCell
                        protected void didChangedSizeValue(int value) {
                            if (position == DataAutoDownloadActivity.this.videosRow) {
                                boolean z4 = true;
                                infoCell.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize(value)));
                                if (value <= 2097152) {
                                    z4 = false;
                                }
                                boolean enabled = z4;
                                if (enabled != checkCell[0].isEnabled()) {
                                    ArrayList animators = new ArrayList();
                                    checkCell[0].setEnabled(enabled, animators);
                                    AnimatorSet[] animatorSetArr = animatorSet;
                                    if (animatorSetArr[0] != null) {
                                        animatorSetArr[0].cancel();
                                        animatorSet[0] = null;
                                    }
                                    animatorSet[0] = new AnimatorSet();
                                    animatorSet[0].playTogether(animators);
                                    animatorSet[0].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DataAutoDownloadActivity.3.1
                                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                        public void onAnimationEnd(Animator animator) {
                                            if (animator.equals(animatorSet[0])) {
                                                animatorSet[0] = null;
                                            }
                                        }
                                    });
                                    animatorSet[0].setDuration(150L);
                                    animatorSet[0].start();
                                }
                            }
                        }
                    };
                    currentPreset2 = currentPreset3;
                    sizeCell3[0].setSize(currentPreset2.sizes[index]);
                    linearLayout3.addView(sizeCell3[0], LayoutHelper.createLinear(-1, 50));
                    checkCell[0] = new TextCheckCell(getParentActivity(), 21, true);
                    linearLayout3.addView(checkCell[0], LayoutHelper.createLinear(-1, 48));
                    checkCell[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            TextCheckCell[] textCheckCellArr = checkCell;
                            textCheckCellArr[0].setChecked(!textCheckCellArr[0].isChecked());
                        }
                    });
                    Drawable drawable = Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    infoCell.setBackgroundDrawable(combinedDrawable);
                    linearLayout3.addView(infoCell, LayoutHelper.createLinear(-1, -2));
                    if (position == this.videosRow) {
                        sizeCell3[0].setText(LocaleController.getString("AutoDownloadMaxVideoSize", R.string.AutoDownloadMaxVideoSize));
                        checkCell[0].setTextAndCheck(LocaleController.getString("AutoDownloadPreloadVideo", R.string.AutoDownloadPreloadVideo), currentPreset2.preloadVideo, false);
                        infoCell.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize(currentPreset2.sizes[index])));
                    } else {
                        sizeCell3[0].setText(LocaleController.getString("AutoDownloadMaxFileSize", R.string.AutoDownloadMaxFileSize));
                        checkCell[0].setTextAndCheck(LocaleController.getString("AutoDownloadPreloadMusic", R.string.AutoDownloadPreloadMusic), currentPreset2.preloadMusic, false);
                        infoCell.setText(LocaleController.getString("AutoDownloadPreloadMusicInfo", R.string.AutoDownloadPreloadMusicInfo));
                    }
                } else {
                    currentPreset2 = currentPreset3;
                    sizeCell3[0] = null;
                    checkCell[0] = null;
                    View divider = new View(getParentActivity());
                    divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                    linearLayout3.addView(divider, new LinearLayout.LayoutParams(-1, 1));
                }
                if (position == this.videosRow) {
                    boolean hasAny2 = false;
                    int b = 0;
                    while (true) {
                        if (b < cells.length) {
                            if (!cells[b].isChecked()) {
                                b++;
                            } else {
                                hasAny2 = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (hasAny2) {
                        z = false;
                    } else {
                        z = false;
                        sizeCell3[0].setEnabled(false, null);
                        checkCell[0].setEnabled(false, null);
                    }
                    if (currentPreset2.sizes[index] <= 2097152) {
                        char c = z ? 1 : 0;
                        char c2 = z ? 1 : 0;
                        checkCell[c].setEnabled(z, null);
                    }
                }
                FrameLayout buttonsLayout = new FrameLayout(getParentActivity());
                buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                linearLayout3.addView(buttonsLayout, LayoutHelper.createLinear(-1, 52));
                TextView textView = new TextView(getParentActivity());
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                textView.setGravity(17);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 51));
                textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        BottomSheet.Builder.this.getDismissRunnable().run();
                    }
                });
                TextView textView2 = new TextView(getParentActivity());
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                textView2.setGravity(17);
                textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView2.setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
                textView2.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                buttonsLayout.addView(textView2, LayoutHelper.createFrame(-2, 36, 53));
                final int i8 = type;
                textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        DataAutoDownloadActivity.this.m3301lambda$createView$3$orgtelegramuiDataAutoDownloadActivity(cells, i8, sizeCell3, index, checkCell, position, key3, key22, builder3, view, view2);
                    }
                });
                showDialog(builder3.create());
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-DataAutoDownloadActivity */
    public /* synthetic */ void m3300lambda$createView$0$orgtelegramuiDataAutoDownloadActivity(TextCheckBoxCell checkBoxCell, TextCheckBoxCell[] cells, int position, MaxFileSizeCell[] sizeCell, TextCheckCell[] checkCell, final AnimatorSet[] animatorSet, View v) {
        if (!v.isEnabled()) {
            return;
        }
        checkBoxCell.setChecked(!checkBoxCell.isChecked());
        boolean hasAny = false;
        int b = 0;
        while (true) {
            if (b >= cells.length) {
                break;
            } else if (!cells[b].isChecked()) {
                b++;
            } else {
                hasAny = true;
                break;
            }
        }
        int b2 = this.videosRow;
        if (position == b2 && sizeCell[0].isEnabled() != hasAny) {
            ArrayList<Animator> animators = new ArrayList<>();
            sizeCell[0].setEnabled(hasAny, animators);
            if (sizeCell[0].getSize() > 2097152) {
                checkCell[0].setEnabled(hasAny, animators);
            }
            if (animatorSet[0] != null) {
                animatorSet[0].cancel();
                animatorSet[0] = null;
            }
            animatorSet[0] = new AnimatorSet();
            animatorSet[0].playTogether(animators);
            animatorSet[0].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DataAutoDownloadActivity.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(animatorSet[0])) {
                        animatorSet[0] = null;
                    }
                }
            });
            animatorSet[0].setDuration(150L);
            animatorSet[0].start();
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DataAutoDownloadActivity */
    public /* synthetic */ void m3301lambda$createView$3$orgtelegramuiDataAutoDownloadActivity(TextCheckBoxCell[] cells, int type, MaxFileSizeCell[] sizeCell, int index, TextCheckCell[] checkCell, int position, String key, String key2, BottomSheet.Builder builder, View view, View v1) {
        int i = this.currentPresetNum;
        if (i != 3) {
            if (i == 0) {
                this.typePreset.set(this.lowPreset);
            } else if (i == 1) {
                this.typePreset.set(this.mediumPreset);
            } else if (i == 2) {
                this.typePreset.set(this.highPreset);
            }
        }
        for (int a = 0; a < 4; a++) {
            if (cells[a].isChecked()) {
                int[] iArr = this.typePreset.mask;
                iArr[a] = iArr[a] | type;
            } else {
                int[] iArr2 = this.typePreset.mask;
                iArr2[a] = iArr2[a] & (type ^ (-1));
            }
        }
        if (sizeCell[0] != null) {
            int size = (int) sizeCell[0].getSize();
            this.typePreset.sizes[index] = (int) sizeCell[0].getSize();
        }
        if (checkCell[0] != null) {
            if (position == this.videosRow) {
                this.typePreset.preloadVideo = checkCell[0].isChecked();
            } else {
                this.typePreset.preloadMusic = checkCell[0].isChecked();
            }
        }
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putString(key, this.typePreset.toString());
        this.currentPresetNum = 3;
        editor.putInt(key2, 3);
        int i2 = this.currentType;
        if (i2 == 0) {
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
        } else if (i2 == 1) {
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
        } else {
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
        }
        editor.commit();
        builder.getDismissRunnable().run();
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
        if (holder != null) {
            this.animateChecked = true;
            this.listAdapter.onBindViewHolder(holder, position);
            this.animateChecked = false;
        }
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        this.wereAnyChanges = true;
        fillPresets();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.wereAnyChanges) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(this.currentType);
            this.wereAnyChanges = false;
        }
    }

    private void fillPresets() {
        this.presets.clear();
        this.presets.add(this.lowPreset);
        this.presets.add(this.mediumPreset);
        this.presets.add(this.highPreset);
        if (!this.typePreset.equals(this.lowPreset) && !this.typePreset.equals(this.mediumPreset) && !this.typePreset.equals(this.highPreset)) {
            this.presets.add(this.typePreset);
        }
        Collections.sort(this.presets, DataAutoDownloadActivity$$ExternalSyntheticLambda4.INSTANCE);
        int i = this.currentPresetNum;
        if (i == 0 || (i == 3 && this.typePreset.equals(this.lowPreset))) {
            this.selectedPreset = this.presets.indexOf(this.lowPreset);
        } else {
            int i2 = this.currentPresetNum;
            if (i2 == 1 || (i2 == 3 && this.typePreset.equals(this.mediumPreset))) {
                this.selectedPreset = this.presets.indexOf(this.mediumPreset);
            } else {
                int i3 = this.currentPresetNum;
                if (i3 == 2 || (i3 == 3 && this.typePreset.equals(this.highPreset))) {
                    this.selectedPreset = this.presets.indexOf(this.highPreset);
                } else {
                    this.selectedPreset = this.presets.indexOf(this.typePreset);
                }
            }
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            RecyclerView.ViewHolder holder = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (holder != null && (holder.itemView instanceof SlideChooseView)) {
                updatePresetChoseView((SlideChooseView) holder.itemView);
            } else {
                this.listAdapter.notifyItemChanged(this.usageProgressRow);
            }
        }
    }

    public static /* synthetic */ int lambda$fillPresets$5(DownloadController.Preset o1, DownloadController.Preset o2) {
        int index1 = DownloadController.typeToIndex(4);
        int index2 = DownloadController.typeToIndex(8);
        boolean video1 = false;
        boolean doc1 = false;
        for (int a = 0; a < o1.mask.length; a++) {
            if ((o1.mask[a] & 4) != 0) {
                video1 = true;
            }
            if ((o1.mask[a] & 8) != 0) {
                doc1 = true;
            }
            if (video1 && doc1) {
                break;
            }
        }
        boolean video2 = false;
        boolean doc2 = false;
        for (int a2 = 0; a2 < o2.mask.length; a2++) {
            if ((o2.mask[a2] & 4) != 0) {
                video2 = true;
            }
            if ((o2.mask[a2] & 8) != 0) {
                doc2 = true;
            }
            if (video2 && doc2) {
                break;
            }
        }
        long j = 0;
        long size1 = (video1 ? o1.sizes[index1] : 0L) + (doc1 ? o1.sizes[index2] : 0L);
        long j2 = video2 ? o2.sizes[index1] : 0L;
        if (doc2) {
            j = o2.sizes[index2];
        }
        long size2 = j2 + j;
        if (size1 > size2) {
            return 1;
        }
        if (size1 < size2) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.autoDownloadRow = 0;
        this.rowCount = i + 1;
        this.autoDownloadSectionRow = i;
        if (this.typePreset.enabled) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.usageHeaderRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.usageProgressRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.usageSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.typeHeaderRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.photosRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.videosRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.filesRow = i8;
            this.rowCount = i9 + 1;
            this.typeSectionRow = i9;
            return;
        }
        this.usageHeaderRow = -1;
        this.usageProgressRow = -1;
        this.usageSectionRow = -1;
        this.typeHeaderRow = -1;
        this.photosRow = -1;
        this.videosRow = -1;
        this.filesRow = -1;
        this.typeSectionRow = -1;
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            DataAutoDownloadActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        /* JADX WARN: Removed duplicated region for block: B:68:0x0229  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x0233  */
        /* JADX WARN: Removed duplicated region for block: B:74:0x0235  */
        /* JADX WARN: Removed duplicated region for block: B:77:0x0240  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r20, int r21) {
            /*
                Method dump skipped, instructions count: 750
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    TextCheckCell cell = new TextCheckCell(this.mContext);
                    cell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
                    cell.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    cell.setHeight(56);
                    view = cell;
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = headerCell;
                    break;
                case 3:
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    slideChooseView.setCallback(new SlideChooseView.Callback() { // from class: org.telegram.ui.DataAutoDownloadActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Components.SlideChooseView.Callback
                        public final void onOptionSelected(int i) {
                            DataAutoDownloadActivity.ListAdapter.this.m3303x3dca3f42(i);
                        }

                        @Override // org.telegram.ui.Components.SlideChooseView.Callback
                        public /* synthetic */ void onTouchEnd() {
                            SlideChooseView.Callback.CC.$default$onTouchEnd(this);
                        }
                    });
                    slideChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = slideChooseView;
                    break;
                case 4:
                    View notificationsCheckCell = new NotificationsCheckCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = notificationsCheckCell;
                    break;
                default:
                    View view2 = new TextInfoPrivacyCell(this.mContext);
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    view = view2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-DataAutoDownloadActivity$ListAdapter */
        public /* synthetic */ void m3303x3dca3f42(int index) {
            DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(index);
            if (preset == DataAutoDownloadActivity.this.lowPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 0;
            } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 1;
            } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 2;
            } else {
                DataAutoDownloadActivity.this.currentPresetNum = 3;
            }
            if (DataAutoDownloadActivity.this.currentType == 0) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentMobilePreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else if (DataAutoDownloadActivity.this.currentType == 1) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentWifiPreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentRoamingPreset = DataAutoDownloadActivity.this.currentPresetNum;
            }
            SharedPreferences.Editor editor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            editor.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            editor.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            for (int a = 0; a < 3; a++) {
                RecyclerView.ViewHolder holder = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + a);
                if (holder != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(holder, DataAutoDownloadActivity.this.photosRow + a);
                }
            }
            DataAutoDownloadActivity.this.wereAnyChanges = true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != DataAutoDownloadActivity.this.autoDownloadRow) {
                if (position != DataAutoDownloadActivity.this.usageSectionRow) {
                    if (position != DataAutoDownloadActivity.this.usageHeaderRow && position != DataAutoDownloadActivity.this.typeHeaderRow) {
                        if (position != DataAutoDownloadActivity.this.usageProgressRow) {
                            if (position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow) {
                                return 4;
                            }
                            return 5;
                        }
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }
    }

    public void updatePresetChoseView(SlideChooseView slideChooseView) {
        String[] presetsStr = new String[this.presets.size()];
        for (int i = 0; i < this.presets.size(); i++) {
            DownloadController.Preset preset = this.presets.get(i);
            if (preset == this.lowPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadLow", R.string.AutoDownloadLow);
            } else if (preset == this.mediumPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadMedium", R.string.AutoDownloadMedium);
            } else if (preset == this.highPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadHigh", R.string.AutoDownloadHigh);
            } else {
                presetsStr[i] = LocaleController.getString("AutoDownloadCustom", R.string.AutoDownloadCustom);
            }
        }
        int i2 = this.selectedPreset;
        slideChooseView.setOptions(i2, presetsStr);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, SlideChooseView.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundUnchecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundCheckText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlue));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlueChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlueThumb));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlueThumbChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlueSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackBlueSelectorChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        return themeDescriptions;
    }
}
