package com.nexustech.smartbrowser.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.nexustech.smartbrowser.core.R;

/**
 * Builder for custom alert dialogs
 * Refactored from TipsDialog with builder pattern
 */
public class AlertDialogBuilder {
    
    private final Context context;
    private String title;
    private String content;
    private String confirmText;
    private String cancelText;
    private Runnable onConfirmListener;
    private Runnable onCancelListener;
    
    private AlertDialogBuilder(Context context) {
        this.context = context;
    }
    
    public static AlertDialogBuilder create(Context context) {
        return new AlertDialogBuilder(context);
    }
    
    public AlertDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public AlertDialogBuilder setContent(String content) {
        this.content = content;
        return this;
    }
    
    public AlertDialogBuilder setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }
    
    public AlertDialogBuilder setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }
    
    public AlertDialogBuilder setOnConfirmListener(Runnable listener) {
        this.onConfirmListener = listener;
        return this;
    }
    
    public AlertDialogBuilder setOnCancelListener(Runnable listener) {
        this.onCancelListener = listener;
        return this;
    }
    
    public Dialog show() {
        Dialog dialog = build();
        dialog.show();
        return dialog;
    }
    
    public Dialog build() {
        Dialog dialog = new Dialog(context);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tips);
        dialog.setCancelable(false);
        
        // Set title
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        if (tvTitle != null && title != null) {
            tvTitle.setText(title);
        }
        
        // Set content
        TextView tvContent = dialog.findViewById(R.id.tvContent);
        if (tvContent != null && content != null) {
            tvContent.setText(content);
        }
        
        // Set cancel button
        TextView btnCancel = dialog.findViewById(R.id.btnNo);
        if (btnCancel != null) {
            if (cancelText != null) {
                btnCancel.setText(cancelText);
            }
            btnCancel.setOnClickListener(v -> {
                dialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.run();
                }
            });
        }
        
        // Set confirm button
        TextView btnConfirm = dialog.findViewById(R.id.btnOk);
        if (btnConfirm != null) {
            if (confirmText != null) {
                btnConfirm.setText(confirmText);
            }
            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                if (onConfirmListener != null) {
                    onConfirmListener.run();
                }
            });
        }
        
        // Configure window
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.6f;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        
        return dialog;
    }
}
