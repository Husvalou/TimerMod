package com.example.timermod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class Config {
    private static Configuration config;
    
    private static int toggleKey = Keyboard.KEY_R;
    private static int guiKey = Keyboard.KEY_RSHIFT;
    private static double speed = 1.0;
    
    public static final int KEY_NONE = 0;
    
    public static void load(File configFile) {
        config = new Configuration(configFile);
        
        Property toggleProp = config.get("keys", "toggleKey", Keyboard.KEY_R, "Key to toggle timer (0 = none, R by default)");
        Property guiProp = config.get("keys", "guiKey", Keyboard.KEY_RSHIFT, "Key to open GUI (RSHIFT by default)");
        Property speedProp = config.get("settings", "speed", 1.0, "Timer speed (0.0 to 1.0)");
        
        toggleKey = toggleProp.getInt();
        guiKey = guiProp.getInt();
        speed = speedProp.getDouble();
        
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    public static void save() {
        if (config != null) {
            config.get("keys", "toggleKey", Keyboard.KEY_R).set(toggleKey);
            config.get("keys", "guiKey", Keyboard.KEY_RSHIFT).set(guiKey);
            config.get("settings", "speed", 1.0).set(speed);
            config.save();
        }
    }
    
    public static int getToggleKey() {
        return toggleKey;
    }
    
    public static void setToggleKey(int key) {
        toggleKey = key;
        save();
    }
    
    public static int getGuiKey() {
        return guiKey;
    }
    
    public static void setGuiKey(int key) {
        guiKey = key;
        save();
    }
    
    public static double getSpeed() {
        return speed;
    }
    
    public static void setSpeed(double newSpeed) {
        speed = Math.max(0.0, Math.min(1.0, newSpeed));
        save();
    }
    
    public static String getKeyName(int key) {
        if (key == KEY_NONE || key == -1) {
            return "none";
        }
        return Keyboard.getKeyName(key);
    }
}
