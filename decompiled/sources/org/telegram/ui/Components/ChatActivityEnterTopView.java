package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/* loaded from: classes5.dex */
public class ChatActivityEnterTopView extends FrameLayout {
    private boolean editMode;
    private EditView editView;
    private View replyView;

    public ChatActivityEnterTopView(Context context) {
        super(context);
    }

    public void addReplyView(View replyView, FrameLayout.LayoutParams layoutParams) {
        if (this.replyView == null) {
            this.replyView = replyView;
            addView(replyView, layoutParams);
        }
    }

    public void addEditView(EditView editView, FrameLayout.LayoutParams layoutParams) {
        if (this.editView == null) {
            this.editView = editView;
            editView.setVisibility(8);
            addView(editView, layoutParams);
        }
    }

    public void setEditMode(boolean editMode) {
        if (editMode != this.editMode) {
            this.editMode = editMode;
            int i = 8;
            this.replyView.setVisibility(editMode ? 8 : 0);
            EditView editView = this.editView;
            if (editMode) {
                i = 0;
            }
            editView.setVisibility(i);
        }
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public EditView getEditView() {
        return this.editView;
    }

    /* loaded from: classes5.dex */
    public static class EditView extends LinearLayout {
        private EditViewButton[] buttons = new EditViewButton[2];

        public EditView(Context context) {
            super(context);
        }

        public void addButton(EditViewButton button, LinearLayout.LayoutParams layoutParams) {
            int childCount = getChildCount();
            if (childCount < 2) {
                this.buttons[childCount] = button;
                addView(button, layoutParams);
            }
        }

        public EditViewButton[] getButtons() {
            return this.buttons;
        }

        public void updateColors() {
            EditViewButton[] editViewButtonArr;
            for (EditViewButton button : this.buttons) {
                button.updateColors();
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class EditViewButton extends LinearLayout {
        private boolean editButton;
        private ImageView imageView;
        private TextView textView;

        public abstract void updateColors();

        public EditViewButton(Context context) {
            super(context);
        }

        public void addImageView(ImageView imageView, LinearLayout.LayoutParams layoutParams) {
            if (this.imageView == null) {
                this.imageView = imageView;
                addView(imageView, layoutParams);
            }
        }

        public void addTextView(TextView textView, LinearLayout.LayoutParams layoutParams) {
            if (this.textView == null) {
                this.textView = textView;
                addView(textView, layoutParams);
            }
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public TextView getTextView() {
            return this.textView;
        }

        public void setEditButton(boolean editButton) {
            this.editButton = editButton;
        }

        public boolean isEditButton() {
            return this.editButton;
        }
    }
}
