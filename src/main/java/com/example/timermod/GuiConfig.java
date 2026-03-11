package com.example.timermod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiConfig extends GuiScreen {
    private final TimerModule timer;
    private GuiButton toggleButton;
    private GuiSlider speedSlider;
    private GuiButton closeButton;
    private GuiButton toggleKeyButton;
    private GuiButton guiKeyButton;
    private GuiSlider draggedSlider = null;

    public GuiConfig(TimerModule timer) {
        this.timer = timer;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        toggleButton = new GuiButton(0, centerX - 100, centerY - 60, 200, 20, "Timer: OFF");
        
        speedSlider = new GuiSlider(1, centerX - 100, centerY - 30, 200, 20, "Speed: ", 
            timer.getMinSpeed(), timer.getMaxSpeed(), timer.getSpeed(), timer.getIncrement(),
            new GuiSlider.SliderCallback() {
                @Override
                public void onValueChanged(double value) {
                    timer.setSpeed(value);
                }
            });
        
        // Key bind buttons
        toggleKeyButton = new GuiButton(4, centerX - 100, centerY + 5, 200, 20, "Toggle Key: " + Config.getKeyName(Config.getToggleKey()));
        guiKeyButton = new GuiButton(5, centerX - 100, centerY + 30, 200, 20, "GUI Key: " + Config.getKeyName(Config.getGuiKey()));
        
        closeButton = new GuiButton(3, centerX - 50, centerY + 60, 100, 20, "Close");

        this.buttonList.add(toggleButton);
        this.buttonList.add(speedSlider);
        this.buttonList.add(toggleKeyButton);
        this.buttonList.add(guiKeyButton);
        this.buttonList.add(closeButton);

        updateButtonText();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            timer.toggle();
        } else if (button.id == 3) {
            mc.displayGuiScreen(null);
        } else if (button.id == 4) {
            // Start capturing toggle key
            timer.setWaitingForToggleKey(true);
            button.displayString = "Press any key...";
        } else if (button.id == 5) {
            // Start capturing GUI key
            timer.setWaitingForGuiKey(true);
            button.displayString = "Press any key...";
        }
        updateButtonText();
    }

    private void updateButtonText() {
        toggleButton.displayString = timer.isEnabled() ? "Timer: ON" : "Timer: OFF";
        toggleButton.packedFGColour = timer.isEnabled() ? 0x00FF00 : 0xFF0000;
        
        if (!timer.isWaitingForToggleKey()) {
            toggleKeyButton.displayString = "Toggle Key: " + Config.getKeyName(Config.getToggleKey());
        }
        if (!timer.isWaitingForGuiKey()) {
            guiKeyButton.displayString = "GUI Key: " + Config.getKeyName(Config.getGuiKey());
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        // Update keybind buttons when not waiting
        if (!timer.isWaitingForToggleKey() && !timer.isWaitingForGuiKey()) {
            updateButtonText();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Title
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRendererObj, "Timer Mod", this.width / 4, 10, 0xFFFFFF);
        GlStateManager.popMatrix();

        // Controls info
        String toggleKeyName = Config.getKeyName(Config.getToggleKey());
        String guiKeyName = Config.getKeyName(Config.getGuiKey());
        this.drawCenteredString(this.fontRendererObj, 
            toggleKeyName + ": Toggle | " + guiKeyName + ": GUI | ALT+Up/Down: Adjust", 
            this.width / 2, this.height / 2 + 90, 0xAAAAAA);
            
        // Credits - bottom right with gradient border
        int creditX = this.width - 115;
        int creditY = this.height - 50;
        int creditWidth = 110;
        int creditHeight = 40;
        int borderThickness = 2;
        
        // Draw transparent background (same as rest of UI)
        drawRect(creditX, creditY, creditX + creditWidth, creditY + creditHeight, 0xDD000000);
        
        // Draw gradient border (pink to white)
        int startColor = 0xFFFF69B4; // Hot pink
        int endColor = 0xFFFFFFFF; // White
        
        // Top border - gradient left to right
        for (int i = 0; i < creditWidth; i++) {
            float ratio = (float) i / (float) creditWidth;
            int color = interpolateColor(startColor, endColor, ratio);
            drawRect(creditX + i, creditY, creditX + i + 1, creditY + borderThickness, color);
        }
        
        // Bottom border - gradient left to right
        for (int i = 0; i < creditWidth; i++) {
            float ratio = (float) i / (float) creditWidth;
            int color = interpolateColor(startColor, endColor, ratio);
            drawRect(creditX + i, creditY + creditHeight - borderThickness, creditX + i + 1, creditY + creditHeight, color);
        }
        
        // Left border - gradient top to bottom
        for (int i = 0; i < creditHeight; i++) {
            float ratio = (float) i / (float) creditHeight;
            int color = interpolateColor(startColor, endColor, ratio);
            drawRect(creditX, creditY + i, creditX + borderThickness, creditY + i + 1, color);
        }
        
        // Right border - gradient top to bottom
        for (int i = 0; i < creditHeight; i++) {
            float ratio = (float) i / (float) creditHeight;
            int color = interpolateColor(startColor, endColor, ratio);
            drawRect(creditX + creditWidth - borderThickness, creditY + i, creditX + creditWidth, creditY + i + 1, color);
        }
        
        // Draw credits text
        this.drawString(this.fontRendererObj, "§dFree 4 Everyone", creditX + 8, creditY + 10, 0xFF1493);
        this.drawString(this.fontRendererObj, "§7Made by Hus", creditX + 8, creditY + 23, 0xAAAAAA);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    private int interpolateColor(int startColor, int endColor, float ratio) {
        int r = (int) ((1 - ratio) * ((startColor >> 16) & 0xFF) + ratio * ((endColor >> 16) & 0xFF));
        int g = (int) ((1 - ratio) * ((startColor >> 8) & 0xFF) + ratio * ((endColor >> 8) & 0xFF));
        int b = (int) ((1 - ratio) * (startColor & 0xFF) + ratio * (endColor & 0xFF));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        
        // Handle slider dragging
        if (Mouse.getEventButtonState()) {
            for (Object button : this.buttonList) {
                if (button instanceof GuiSlider) {
                    GuiSlider slider = (GuiSlider) button;
                    if (slider.mousePressed(this.mc, mouseX, mouseY)) {
                        this.draggedSlider = slider;
                        break;
                    }
                }
            }
        } else if (Mouse.getEventButton() != -1) {
            // Mouse released
            if (this.draggedSlider != null) {
                this.draggedSlider.mouseReleased(mouseX, mouseY);
                this.draggedSlider = null;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.draggedSlider != null) {
            this.draggedSlider.mouseDragged(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public void onGuiClosed() {
        timer.setGuiConfig(null);
        // Cancel key capture if closing
        if (timer.isWaitingForToggleKey()) {
            timer.setWaitingForToggleKey(false);
        }
        if (timer.isWaitingForGuiKey()) {
            timer.setWaitingForGuiKey(false);
        }
    }
}
