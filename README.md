# Create Powerful Panel
Create Powerful Panel is an expansion mod for the Create mod that adds advanced factory panel functionality, featuring a recipe multiplier to give players more flexible control over the output quantity of factory panels.

## Features
- **Recipe Multiplier**: Adds a multiplier feature to factory panels, allowing a multiplier coefficient from -1 (no multiplier) to 6400.
- **GUI Integration**: Adds a dedicated multiplier control widget in the factory panel interface, represented by a shulker box icon.
- **Network Synchronization**: Uses network packets to ensure multiplier settings are synchronized between client and server.

## How to Use
1. Place and configure a Factory Panel.
2. Right-click to open the Factory Panel interface.
3. Locate the shulker box icon in the interface and use the up/down arrows to adjust the multiplier coefficient.
4. A multiplier value of -1 means no multiplication; any other value represents the output quantity multiplier.
5. Click the confirm button to save your settings.

## Technical Details
- **Mod ID**: createpowerfulpanel
- **Version**: 1.0.0
- **Key Classes**:
  - `CreatePowerfulPanel`: Main mod class
  - `MixinFactoryPanelScreen`: Modifies the factory panel GUI
  - `RecipeMultiplier`: Interface for multiplier functionality
  - `CPPPackets`: Handles network packet communication

## Developer
- jooi

## Acknowledgements
- Thanks to the Create mod team for their excellent foundation.
- Thanks to NeoForged for providing mod development tools.
