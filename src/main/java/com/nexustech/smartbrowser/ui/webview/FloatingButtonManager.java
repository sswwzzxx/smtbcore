package com.nexustech.smartbrowser.ui.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nexustech.smartbrowser.core.R;

/**
 * Manages floating action button and its sub-buttons
 */
public class FloatingButtonManager {
    
    private static final int TOUCH_SLOP_DP = 5;
    
    private final Context context;
    private final ViewGroup parentView;
    private final FloatingButtonCallbacks callbacks;
    
    private FrameLayout buttonContainer;
    private ImageButton mainButton;
    private LinearLayout subButtonsLayout;
    private FloatingActionButton closeButton;
    
    private boolean isExpanded = false;
    private int initialX = 0;
    private int initialY = 0;
    private float initialTouchX = 0f;
    private float initialTouchY = 0f;
    private boolean isDragging = false;
    private int touchSlopPx = 0;
    
    public interface FloatingButtonCallbacks {
        void onBackPressed();
        void onReloadPressed();
        void onHomePressed();
    }
    
    public FloatingButtonManager(Context context, ViewGroup parentView, FloatingButtonCallbacks callbacks) {
        this.context = context;
        this.parentView = parentView;
        this.callbacks = callbacks;
        this.touchSlopPx = dpToPx(TOUCH_SLOP_DP);
    }
    
    public void show() {
        LayoutInflater inflater = LayoutInflater.from(context);
        buttonContainer = (FrameLayout) inflater.inflate(
            R.layout.layout_floating_button,
            parentView,
            false
        );
        
        mainButton = buttonContainer.findViewById(R.id.mainFloatingButton);
        subButtonsLayout = buttonContainer.findViewById(R.id.subButtonsLayout);
        closeButton = buttonContainer.findViewById(R.id.closeButton);
        
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.setMargins(0, 0, dpToPx(16), dpToPx(16));
        parentView.addView(buttonContainer, layoutParams);
        
        setupListeners();
    }
    
    public void hide() {
        if (buttonContainer != null) {
            parentView.removeView(buttonContainer);
            buttonContainer = null;
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        if (mainButton != null) {
            mainButton.setOnClickListener(v -> {
                if (!isExpanded) {
                    toggleSubButtons();
                }
            });
            
            mainButton.setOnTouchListener((view, event) -> {
                if (isExpanded) return false;
                
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = (int) buttonContainer.getX();
                        initialY = (int) buttonContainer.getY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - initialTouchX;
                        float dy = event.getRawY() - initialTouchY;
                        if (Math.abs(dx) > touchSlopPx || Math.abs(dy) > touchSlopPx) {
                            isDragging = true;
                            buttonContainer.setX(initialX + dx);
                            buttonContainer.setY(initialY + dy);
                            return true;
                        }
                        return false;
                        
                    case MotionEvent.ACTION_UP:
                        if (isDragging) {
                            snapToEdge();
                            isDragging = false;
                            return true;
                        } else {
                            view.performClick();
                            return true;
                        }
                        
                    default:
                        return false;
                }
            });
        }
        
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> toggleSubButtons());
        }
        
        if (buttonContainer != null) {
            FloatingActionButton backButton = buttonContainer.findViewById(R.id.subButton1);
            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    toggleSubButtons();
                    callbacks.onBackPressed();
                });
            }
            
            FloatingActionButton reloadButton = buttonContainer.findViewById(R.id.subButton2);
            if (reloadButton != null) {
                reloadButton.setOnClickListener(v -> {
                    toggleSubButtons();
                    callbacks.onReloadPressed();
                });
            }
            
            FloatingActionButton homeButton = buttonContainer.findViewById(R.id.subButton3);
            if (homeButton != null) {
                homeButton.setOnClickListener(v -> {
                    toggleSubButtons();
                    callbacks.onHomePressed();
                });
            }
        }
    }
    
    private void toggleSubButtons() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            showSubButtons();
        } else {
            hideSubButtons();
        }
    }
    
    private void showSubButtons() {
        if (subButtonsLayout != null) {
            subButtonsLayout.setVisibility(View.INVISIBLE);
            if (mainButton != null) {
                mainButton.setVisibility(View.GONE);
            }
            if (buttonContainer != null) {
                buttonContainer.post(() -> {
                    adjustSubButtonsPosition();
                    subButtonsLayout.setVisibility(View.VISIBLE);
                });
            }
        }
    }
    
    private void hideSubButtons() {
        if (subButtonsLayout != null) {
            subButtonsLayout.setVisibility(View.GONE);
            if (mainButton != null) {
                mainButton.setVisibility(View.VISIBLE);
            }
            subButtonsLayout.setTranslationX(0f);
            subButtonsLayout.setTranslationY(0f);
            if (buttonContainer != null) {
                buttonContainer.post(this::snapToEdge);
            }
        }
    }
    
    private void snapToEdge() {
        if (buttonContainer == null) return;
        
        int parentWidth = parentView.getWidth();
        int parentHeight = parentView.getHeight();
        int viewWidth = buttonContainer.getWidth();
        int viewHeight = buttonContainer.getHeight();
        float centerX = buttonContainer.getX() + viewWidth / 2f;
        
        float newX = centerX < parentWidth / 2f ? 0f : (parentWidth - viewWidth);
        buttonContainer.animate().x(newX).setDuration(200).start();
        
        if (buttonContainer.getY() < 0) {
            buttonContainer.animate().y(0f).setDuration(200).start();
        } else if (buttonContainer.getY() + viewHeight > parentHeight) {
            buttonContainer.animate().y(parentHeight - viewHeight).setDuration(200).start();
        }
    }
    
    private void adjustSubButtonsPosition() {
        if (buttonContainer == null || mainButton == null || subButtonsLayout == null) {
            return;
        }
        
        int parentWidth = parentView.getWidth();
        int parentHeight = parentView.getHeight();
        float mainButtonCurrentX = buttonContainer.getX();
        float mainButtonCurrentY = buttonContainer.getY();
        int mainButtonWidth = mainButton.getWidth();
        int mainButtonHeight = mainButton.getHeight();
        int subButtonsTotalWidth = subButtonsLayout.getWidth();
        int subButtonsHeight = subButtonsLayout.getHeight();
        
        boolean isLeftHalf = (mainButtonCurrentX + mainButtonWidth / 2f) < parentWidth / 2f;
        float idealSubButtonsLeft = isLeftHalf 
            ? mainButtonCurrentX + mainButtonWidth + dpToPx(8)
            : mainButtonCurrentX - dpToPx(8) - subButtonsTotalWidth;
        
        float idealSubButtonsCenterY = mainButtonCurrentY + mainButtonHeight / 2f;
        float idealSubButtonsTop = idealSubButtonsCenterY - subButtonsHeight / 2f;
        
        float finalSubButtonsLeft = Math.max(0f, Math.min(idealSubButtonsLeft, parentWidth - subButtonsTotalWidth));
        float finalSubButtonsTop = Math.max(0f, Math.min(idealSubButtonsTop, parentHeight - subButtonsHeight));
        
        buttonContainer.setX(finalSubButtonsLeft);
        buttonContainer.setY(finalSubButtonsTop);
        subButtonsLayout.setTranslationX(0f);
        subButtonsLayout.setTranslationY(0f);
        buttonContainer.requestLayout();
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
