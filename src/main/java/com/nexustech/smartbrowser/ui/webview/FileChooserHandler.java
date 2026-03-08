package com.nexustech.smartbrowser.ui.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.nexustech.smartbrowser.core.R;
import java.util.ArrayList;

/**
 * Handles file chooser and camera operations for WebView
 */
public class FileChooserHandler {
    
    private final Context context;
    private ValueCallback<Uri[]> fileUploadCallback;
    private PermissionRequest permissionRequest;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    
    public FileChooserHandler(Context context) {
        this.context = context;
    }
    
    public void setPickMediaLauncher(ActivityResultLauncher<PickVisualMediaRequest> launcher) {
        this.pickMediaLauncher = launcher;
    }
    
    public void handleFileChooser(ValueCallback<Uri[]> callback) {
        this.fileUploadCallback = callback;
        showSourceSelectionDialog();
    }
    
    public void handlePermissionRequest(PermissionRequest request) {
        String[] permissions = request.getResources();
        for (String resource : permissions) {
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) {
                this.permissionRequest = request;
                showSourceSelectionDialog();
            }
        }
    }
    
    public void handlePickMediaResult(Uri uri) {
        Uri[] results = uri != null ? new Uri[]{uri} : null;
        if (fileUploadCallback != null) {
            fileUploadCallback.onReceiveValue(results);
            fileUploadCallback = null;
        }
    }
    
    public void cleanup() {
        if (fileUploadCallback != null) {
            fileUploadCallback.onReceiveValue(null);
            fileUploadCallback = null;
        }
        permissionRequest = null;
    }
    
    private void showSourceSelectionDialog() {
        String[] options = {
            context.getString(R.string.camera),
            context.getString(R.string.album)
        };
        
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.select_image_source))
            .setCancelable(false)
            .setItems(options, (dialog, which) -> {
                if (which == 0) {
                    openCamera();
                } else {
                    openGallery();
                }
            })
            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                dialog.dismiss();
                cleanup();
            })
            .show();
    }
    
    private void openCamera() {
        if (!(context instanceof AppCompatActivity)) {
            return;
        }
        
        AppCompatActivity activity = (AppCompatActivity) context;
        
        // Check camera permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.CAMERA) 
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1001);
                return;
            }
        }
        
        PictureSelector.create(activity)
            .openCamera(SelectMimeType.ofImage())
            .forResult(new OnResultCallbackListener<LocalMedia>() {
                @Override
                public void onResult(ArrayList<LocalMedia> result) {
                    String path = result != null && !result.isEmpty() && result.get(0) != null
                        ? result.get(0).getAvailablePath() : null;
                    Uri[] results = path != null ? new Uri[]{Uri.parse(path)} : null;
                    if (fileUploadCallback != null) {
                        fileUploadCallback.onReceiveValue(results);
                        fileUploadCallback = null;
                    }
                }
                
                @Override
                public void onCancel() {
                    cleanup();
                }
            });
    }
    
    private void openGallery() {
        if (pickMediaLauncher != null) {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
        }
    }
}
