package com.example.timermod;

import com.example.timermod.mixin.IAccessorMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class TimerModule {
    private final Minecraft mc = Minecraft.getMinecraft();
    
    private boolean enabled = false;
    private double speed;
    private final double minSpeed = 0.0;
    private final double maxSpeed = 1.0;
    private final double increment = 0.01;
    
    private GuiConfig guiConfig;
    private Thread inputThread;
    private volatile boolean running = true;
    private volatile boolean waitingForToggleKey = false;
    private volatile boolean waitingForGuiKey = false;

    public TimerModule() {
        // Load speed from config
        this.speed = Config.getSpeed();
        startInputThread();
    }

    private void startInputThread() {
        inputThread = new Thread(() -> {
            boolean wasToggleDown = false;
            boolean wasGuiDown = false;
            boolean wasAltUp = false;
            boolean wasAltDown = false;
            
            while (running) {
                try {
                    int toggleKey = Config.getToggleKey();
                    int guiKey = Config.getGuiKey();
                    
                    // Handle key binding capture
                    if (waitingForToggleKey) {
                        // Check for any key press
                        boolean anyKeyPressed = false;
                        int pressedKey = -1;
                        for (int i = 0; i < 256; i++) {
                            if (Keyboard.isKeyDown(i)) {
                                anyKeyPressed = true;
                                pressedKey = i;
                                break;
                            }
                        }
                        if (anyKeyPressed) {
                            if (pressedKey == Keyboard.KEY_ESCAPE) {
                                // Escape = set to none (no bind)
                                Config.setToggleKey(Config.KEY_NONE);
                            } else {
                                Config.setToggleKey(pressedKey);
                            }
                            waitingForToggleKey = false;
                        }
                    } else if (waitingForGuiKey) {
                        // Check for any key press
                        boolean anyKeyPressed = false;
                        int pressedKey = -1;
                        for (int i = 0; i < 256; i++) {
                            if (Keyboard.isKeyDown(i)) {
                                anyKeyPressed = true;
                                pressedKey = i;
                                break;
                            }
                        }
                        if (anyKeyPressed) {
                            if (pressedKey == Keyboard.KEY_ESCAPE) {
                                // Escape = reset to default (RSHIFT)
                                Config.setGuiKey(Keyboard.KEY_RSHIFT);
                            } else {
                                Config.setGuiKey(pressedKey);
                            }
                            waitingForGuiKey = false;
                        }
                    } else {
                        // Toggle key (only if not KEY_NONE and only when in-game)
                        int currentToggleKey = Config.getToggleKey();
                        if (currentToggleKey != Config.KEY_NONE && mc.currentScreen == null) {
                            boolean isToggleDown = Keyboard.isKeyDown(currentToggleKey);
                            if (isToggleDown && !wasToggleDown) {
                                toggle();
                            }
                            wasToggleDown = isToggleDown;
                        }
                        
                        // GUI key (only when in-game, no screens open)
                        boolean isGuiDown = Keyboard.isKeyDown(guiKey);
                        if (isGuiDown && !wasGuiDown) {
                            // Only open GUI if no screen is currently open (in-game only)
                            if (mc.currentScreen == null) {
                                mc.addScheduledTask(() -> {
                                    mc.displayGuiScreen(new GuiConfig(TimerModule.this));
                                    setGuiConfig(new GuiConfig(TimerModule.this));
                                });
                            }
                        }
                        wasGuiDown = isGuiDown;
                        
                        // ALT + UP - Increase speed
                        boolean isAltUp = (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) 
                                         && Keyboard.isKeyDown(Keyboard.KEY_UP);
                        if (isAltUp && !wasAltUp) {
                            increaseSpeed();
                        }
                        wasAltUp = isAltUp;
                        
                        // ALT + DOWN - Decrease speed
                        boolean isAltDown = (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) 
                                           && Keyboard.isKeyDown(Keyboard.KEY_DOWN);
                        if (isAltDown && !wasAltDown) {
                            decreaseSpeed();
                        }
                        wasAltDown = isAltDown;
                    }
                    
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.setName("TimerInputThread");
        inputThread.start();
    }

    public void stop() {
        running = false;
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }

    public void toggle() {
        enabled = !enabled;
        if (!enabled) {
            resetTimer();
        }
    }

    public void onUpdate() {
        if (!enabled) return;
        if (mc.currentScreen instanceof GuiConfig) {
            resetTimer();
            return;
        }
        ((IAccessorMinecraft) mc).getTimer().timerSpeed = (float) speed;
    }

    public void resetTimer() {
        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
    }

    public void increaseSpeed() {
        speed = Math.min(speed + increment, maxSpeed);
        speed = Math.round(speed * 100.0) / 100.0;
    }

    public void decreaseSpeed() {
        speed = Math.max(speed - increment, minSpeed);
        speed = Math.round(speed * 100.0) / 100.0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = Math.max(minSpeed, Math.min(maxSpeed, speed));
        Config.setSpeed(this.speed); // Save to config
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getIncrement() {
        return increment;
    }

    public void setGuiConfig(GuiConfig gui) {
        this.guiConfig = gui;
    }
    
    public void setWaitingForToggleKey(boolean waiting) {
        this.waitingForToggleKey = waiting;
    }
    
    public void setWaitingForGuiKey(boolean waiting) {
        this.waitingForGuiKey = waiting;
    }
    
    public boolean isWaitingForToggleKey() {
        return waitingForToggleKey;
    }
    
    public boolean isWaitingForGuiKey() {
        return waitingForGuiKey;
    }
}
