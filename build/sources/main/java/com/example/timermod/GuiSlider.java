package com.example.timermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlider extends GuiButton {
    private double minValue;
    private double maxValue;
    private double currentValue;
    private double step;
    private SliderCallback callback;
    private boolean dragging = false;
    private String prefix;

    // Modern clean colors
    private static final int BG_COLOR = 0xFF1E1E1E;
    private static final int TRACK_BG = 0xFF404040;
    private static final int TRACK_FILL = 0xFF00C8AA;
    private static final int THUMB_COLOR = 0xFFFFFFFF;
    private static final int BORDER = 0xFF555555;

    public GuiSlider(int buttonId, int x, int y, int width, int height, String prefix, double minValue, double maxValue, double currentValue, double step, SliderCallback callback) {
        super(buttonId, x, y, width, height, prefix + String.format("%.2f", currentValue));
        this.prefix = prefix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.step = step;
        this.callback = callback;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && 
                          mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            
            int x = this.xPosition;
            int y = this.yPosition;
            int w = this.width;
            int h = this.height;
            int centerY = y + h / 2;
            
            // Background
            drawRect(x + 1, y, x + w - 1, y + h, BG_COLOR);
            drawRect(x, y + 1, x + w, y + h - 1, BG_COLOR);
            
            // Calculate percentage
            double percent = (currentValue - minValue) / (maxValue - minValue);
            int fillWidth = (int) (percent * (w - 16)) + 8;
            
            // Track background (thin line)
            int trackY = centerY - 1;
            drawRect(x + 8, trackY, x + w - 8, trackY + 2, TRACK_BG);
            
            // Track fill
            drawRect(x + 8, trackY, x + 8 + fillWidth - 8, trackY + 2, TRACK_FILL);
            
            // Thumb position
            int thumbX = x + 4 + fillWidth - 8;
            int thumbY = centerY - 6;
            int thumbSize = 12;
            
            // Thumb shadow
            drawRect(thumbX + 1, thumbY + 6, thumbX + thumbSize - 1, thumbY + thumbSize, 0x40000000);
            
            // Thumb body (circle-like as square with rounded corners feel)
            drawRect(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, TRACK_FILL);
            
            // Thumb highlight (inner)
            drawRect(thumbX + 2, thumbY + 2, thumbX + thumbSize - 2, thumbY + 4, 0xFF88FFDD);
            
            // Border
            drawRect(x, y, x + 1, y + h, BORDER);
            drawRect(x + w - 1, y, x + w, y + h, BORDER);
            drawRect(x, y, x + w, y + 1, BORDER);
            drawRect(x, y + h - 1, x + w, y + h, BORDER);
            
            // Text
            this.displayString = prefix + String.format("%.2fx", currentValue);
            int textColor = this.enabled ? 0xFFFFFF : 0x808080;
            this.drawCenteredString(mc.fontRendererObj, this.displayString, x + w / 2, y + 4, textColor);
        }
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && dragging) {
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            dragging = true;
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    private void updateValueFromMouse(int mouseX) {
        double relativeX = Math.max(0, Math.min(mouseX - this.xPosition, this.width));
        double percent = relativeX / this.width;
        double newValue = minValue + percent * (maxValue - minValue);
        
        // Round to step
        newValue = Math.round(newValue / step) * step;
        newValue = Math.max(minValue, Math.min(maxValue, newValue));
        newValue = Math.round(newValue * 100.0) / 100.0;
        
        if (newValue != currentValue) {
            currentValue = newValue;
            if (callback != null) {
                callback.onValueChanged(currentValue);
            }
        }
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
    }

    @FunctionalInterface
    public interface SliderCallback {
        void onValueChanged(double value);
    }
}
