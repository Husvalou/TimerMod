# Timer Mod for Minecraft 1.8.9 /!\ Known bug crash if going in solo usage only for multiplayer. /!\

A standalone Minecraft Forge mod that adds a timer functionality to control game speed.

## Features

- **Timer Control**: Adjust game speed from 0.0x to 1.0x with 0.01 increments
- **GUI Interface**: Beautiful in-game GUI with a modern slider
- **Customizable Keybinds**: Change toggle and GUI keys to your preference
- **Auto-Save**: Speed and keybind settings are saved automatically
- **Emergency Thread**: Works even at 0.0x speed - never get stuck!

## Default Controls 
<img width="1379" height="955" alt="image" src="https://github.com/user-attachments/assets/ec1bb8c2-65b0-4c89-9fad-f73511d8490e" />

| Action | Default Key |
|--------|-------------|
| Toggle Timer | R |
| Open GUI | RSHIFT |
| Increase Speed | ALT + UP |
| Decrease Speed | ALT + DOWN |

## Installation

1. Install **Minecraft Forge 1.8.9** (version 11.15.1.2318 or later)
2. Download `timer-mod-1.0.0.jar`
3. Place the JAR file in your `.minecraft/mods` folder
4. Launch Minecraft with Forge

## Usage

### In-Game GUI
- Press **RSHIFT** (default) to open the GUI
- Use the slider to adjust speed (0.0x - 1.0x)
- Click "Timer: ON/OFF" to toggle
- Click "Toggle Key" or "GUI Key" to change keybinds

### Changing Keybinds
1. Open the GUI
2. Click on "Toggle Key" or "GUI Key" button
3. Press any key to set it
4. Press **ESCAPE** to:
   - Set Toggle Key to "none" (no key)
   - Reset GUI Key to **RSHIFT** (default)

### Emergency Features
- If you set speed to 0.0x and can't move, use **ALT + UP** to restore 1.0x speed
- The timer can be toggled on/off at any speed thanks to the independent input thread

## Configuration

Config file location: `.minecraft/config/timermod.cfg`

Settings saved automatically:
- Timer speed
- Toggle keybind
- GUI keybind

## Building from Source

### Requirements
- Java 8
- Gradle

### Build Commands
```bash
# Windows
gradlew.bat clean build

# Linux/Mac
./gradlew clean build
```

The compiled JAR will be in `build/libs/timer-mod-1.0.0.jar`

## Technical Details

- **Minecraft Version**: 1.8.9
- **Forge Version**: 11.15.1.2318-1.8.9
- **Mixin Framework**: Used for accessing private Minecraft timer
- **Client-side only**: No server installation required

## Credits

**Free 4 Everyone**  
Made by Hus

## License

This mod is free to use and distribute. No restrictions.
