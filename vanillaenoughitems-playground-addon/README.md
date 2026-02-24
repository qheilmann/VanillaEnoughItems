# VanillaEnoughItems Playground Addon

A demo plugin showcasing advanced VanillaEnoughItems API usage. This addon demonstrates how to:
- Override built-in recipe extractors
- Customize process panels with additional information
- Replace default process visualization

## What This Demo Shows

### Demo 1: Removing Built-in Features
**File**: [VeiRegistrationListener.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/VeiRegistrationListener.java#L48-L56)

Demonstrates how to unregister built-in recipe extractors to remove specific recipe types from VEI's index.

- **What it does**: Removes stonecutting recipes from the recipe index
- **How to test**: Use `/craft --all` and notice stonecutting recipes are missing
- **Key API**: `RecipeExtractorRegistry.unregisterExtractor()`

### Demo 2: Enhancing Built-in Panels
**Files**: 
- [VeiRegistrationListener.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/VeiRegistrationListener.java#L59-L74)
- [SmeltingXpPanelOverride.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/smeltingxp/SmeltingXpPanelOverride.java)

Shows how to override a built-in process panel to add extra information without changing recipe indexation.

- **What it does**: Adds XP and cook time display to smelting recipes
- **How to test**: Use `/craft glass` and see the enhanced smelting panel
- **Key API**: `ProcessPanelRegistry.registerProvider()`

### Demo 3: Complete Recipe Override
**Files**:
- [VeiRegistrationListener.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/VeiRegistrationListener.java#L77-L107)
- [CampfireSpongeOverrideExtractor.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/campfiresponge/CampfireSpongeOverrideExtractor.java)
- [CampfireSpongeOverrideProcess.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/campfiresponge/CampfireSpongeOverrideProcess.java)
- [CampfireSpongeOverridePanel.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/campfiresponge/CampfireSpongeOverridePanel.java)

Demonstrates the complete workflow of overriding both recipe indexation AND visualization.

- **What it does**: Makes all campfire recipes appear to use wet sponge as input and sponge as output
- **How to test**: Use `/craft sponge` and see all campfire recipes displayed with sponge
- **Key APIs**: 
  - `RecipeExtractorRegistry.registerExtractor()` - for indexation
  - `ProcessRegistry.registerProcess()` - for process definition
  - `ProcessPanelRegistry.registerProvider()` - for visualization

**Key Insight**: RecipeExtractor and ProcessPanelProvider are independent. You can use them together or separately depending on your needs.

## Architecture Notes

### Plugin Loading Order
This addon uses `load: AFTER` in [paper-plugin.yml](src/main/resources/paper-plugin.yml), which means it loads **before** VanillaEnoughItems. This allows the addon to register event listeners that will catch VEI's `VeiRegistrationEvent` when VEI fires it during its own `onEnable()`.

### Class Separation
The main plugin class ([PlaygroundAddonPlugin.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/PlaygroundAddonPlugin.java)) and the event listener ([VeiRegistrationListener.java](src/main/java/dev/qheilmann/vanillaenoughitems/playground/addon/VeiRegistrationListener.java)) are intentionally kept separate to avoid potential classloading issues with Paper's classloader.

## Dependencies
- Paper API
- VanillaEnoughItems API (compile-only, provided at runtime)