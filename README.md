# SkinEditor

A desktop skin editor for Minecraft, written in Java (Swing + JOGL).

Paint directly on the skin texture in a pixel-editing canvas while a live,
animated 3D preview of the player model updates in real time.

More details: https://www.badfalcon.net/tools/

## Features

- Modern flat UI ([FlatLaf](https://www.formdev.com/flatlaf/)) with light and
  dark themes, switchable from the View menu
- Pixel editing tools: brush, eraser, color dropper, paint bucket, line,
  rectangle / filled rectangle, ellipse / filled ellipse, area eraser, and
  rectangular selection with move / copy / cut / paste
- Live animated 3D preview (rotate by dragging, zoom with the mouse wheel,
  right-click to show/hide body parts, dockable or in its own window)
- Supports modern 64x64 skins, legacy 64x32 skins (with automatic conversion
  on open), and slim ("Alex") arm models
- Overlay template that labels each body part and face on the texture
- HSB / RGB color pickers with a color history palette
- Undo / redo of every edit
- Drag & drop a PNG onto the window to open it
- English and Japanese UI (follows the system language)

## Keyboard shortcuts

| Key | Tool |
| --- | ---- |
| `B` | Brush |
| `E` / `Shift+E` | Eraser / area eraser |
| `I` | Color dropper (right-click on the canvas also works) |
| `F` | Paint bucket |
| `L` | Line |
| `R` / `Shift+R` | Rectangle / filled rectangle |
| `C` / `Shift+C` | Ellipse / filled ellipse |

`Ctrl+N` new, `Ctrl+O` open, `Ctrl+S` save, `Ctrl+Z`/`Ctrl+Y` undo/redo,
`Ctrl+C`/`Ctrl+X`/`Ctrl+V` copy/cut/paste of the current selection.
The mouse wheel over the canvas cycles through tools.

## Building

Requirements: JDK 17 or newer. Everything else (including the
[JOGL](https://jogamp.org/) OpenGL bindings with natives for Windows, Linux,
and macOS — Intel and Apple Silicon) is fetched by Gradle.

```sh
./gradlew build
```

This produces a self-contained runnable jar in `build/libs/`.

To run straight from the source tree:

```sh
./gradlew run
```

## License

BSD-style license — see [SkinEditor_License.txt](SkinEditor_License.txt).

Paint icons from [Icons8](https://icons8.com/).
Great respect to *Minecraft Skin Edit* by Patrik Swedman, and to Mojang's
*Minecraft*.
