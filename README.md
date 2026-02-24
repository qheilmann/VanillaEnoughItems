# Vanilla Enough Items

![Build](https://github.com/qheilmann/VanillaEnoughItems/actions/workflows/build.yml/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue)
![JitPack](https://jitpack.io/v/qheilmann/VanillaEnoughItems.svg)

**Vanilla Enough Items (VEI)** is a server-side Paper plugin inspired by [NEI](https://bitbucket.org/mistaqur/nei_plugins/wiki/Home), [JEI](https://www.curseforge.com/minecraft/mc-mods/jei), and [REI](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-items). It lets players browse recipes and item usages in-game without any client-side mod — everything runs through a vanilla-compatible GUI.

Players can type `/vei`, `/craft`, or other aliases and shortcuts to open the recipe browser.

---

## Server Installation

Download the latest JAR from the [Releases](https://github.com/qheilmann/VanillaEnoughItems/releases) page and drop it into your server's `plugins/` folder. Requires **Paper 1.21.11**.

---

## Developer API

VEI exposes a public API (`vanillaenoughitems-api`) that lets other plugins open GUIs, query the recipe index, and — for more advanced use cases — register custom crafting processes, recipe extractors, and panel UIs.

### Adding the dependency

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.qheilmann.VanillaEnoughItems:vanillaenoughitems-api:VERSION")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.qheilmann.VanillaEnoughItems</groupId>
    <artifactId>vanillaenoughitems-api</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

> Replace `VERSION` with a release tag. Available versions are listed on [JitPack](https://jitpack.io/#qheilmann/VanillaEnoughItems).

---

### Declaring the Paper plugin dependency

How you declare the dependency depends on what you need from the API.

#### Simple usage — open GUIs, query recipes (most plugins)

If you only need to open GUIs or query the recipe index, you don't need to listen to any VEI events. By the time your plugin's `onEnable()` runs, VEI has already finished enabling: both lifecycle events have already fired and the recipe index is ready. Just call `VanillaEnoughItemsAPI.get()` directly.

Declare VEI as a dependency that loads **before** your plugin:

```yaml
# paper-plugin.yml
dependencies:
  server:
    VanillaEnoughItems:
      required: true
      load: BEFORE  # VEI enables before your plugin
```

```yaml
# plugin.yml (legacy)
depend:
  - VanillaEnoughItems
```

See `vanillaenoughitems-playground-api` for a working example of this approach.

#### Advanced usage — register custom processes, extractors, or panels

If you need to hook into the VEI registration lifecycle (e.g. add a new recipe type, override a built-in panel, or remove a built-in extractor), your plugin must be enabled **before** VEI so that it can register event listeners that catch `VeiRegistrationEvent` and `VeiReadyEvent` as they fire during VEI's own startup.

```yaml
# paper-plugin.yml
dependencies:
  server:
    VanillaEnoughItems:
      required: true
      load: AFTER  # VEI enables after your plugin → your plugin catches its events
```

See `vanillaenoughitems-playground-addon` for a working example of this approach.

---

## Using the API

### Simple: accessing the API directly

For plugins that load after VEI, just call the singleton anywhere after `onEnable()`:

```java
VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();
```

> Throws `IllegalStateException` if VEI has not finished enabling yet.

### Opening GUIs

```java
VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();

// Open the recipe browser for an item
api.openRecipeGui(player, new ItemStack(Material.DIAMOND));

// Open the usage browser for an item (which recipes use this as an ingredient)
api.openUsageGui(player, new ItemStack(Material.STICK));

// Open a specific recipe by its recipe key
api.openRecipeGui(player, Key.key("minecraft:iron_sword"));

// Open from a custom reader (advanced use case)
api.openReaderGui(player, myCustomRecipeReader);

// Open the player's personal bookmarks
api.openPlayerBookmarkGui(player);

// Open the server-wide bookmarks
api.openServerBookmarkGui(player);
```

### Querying the recipe index

```java
RecipeIndexView index = VanillaEnoughItemsAPI.get().recipeIndex();

// All recipes that produce a diamond
MultiProcessRecipeReader reader = index.getByResult(new ItemStack(Material.DIAMOND));

// All recipes that use a stick as an ingredient
MultiProcessRecipeReader reader = index.getByIngredient(new ItemStack(Material.STICK));
```

### Lifecycle events

VEI fires two Bukkit events during its startup:

| Event | When | Purpose |
|---|---|---|
| `VeiRegistrationEvent` | Before recipe indexation | Register custom processes, extractors, and panel factories |
| `VeiReadyEvent` | After recipe indexation | Query the recipe index, open GUIs, create bookmarks |

Both events are only relevant if your plugin loads **before** VEI (advanced usage). If your plugin loads after VEI, these events have already fired — just access the API directly via `VanillaEnoughItemsAPI.get()`.

### Advanced: registering custom content (`VeiRegistrationEvent`)

```java
@EventHandler
public void onVeiRegistration(VeiRegistrationEvent event) {
    VanillaEnoughItemsAPI api = event.getApi();

    // Register a custom crafting process
    api.processRegistry().registerProcess(myCustomProcess);

    // Register a custom recipe extractor for this process
    api.recipeExtractorRegistry().registerExtractor(myExtractor);

    // Register the GUI panel factory for your process
    api.processPanelRegistry().registerProvider(myProcess, myPanelFactory);
}
```

### Advanced: reacting after indexation (`VeiReadyEvent`)

If your plugin loads before VEI, `VeiReadyEvent` is the first moment `recipeIndex()` is available:

```java
@EventHandler
public void onVeiReady(VeiReadyEvent event) {
    VanillaEnoughItemsAPI api = event.getApi();
    RecipeIndexView index = api.recipeIndex(); // safe to call from here on
}
```

---

## Project structure

| Module | Description |
|---|---|
| `vanillaenoughitems-api` | Public API — depend on this from other plugins |
| `vanillaenoughitems-paper` | Paper plugin implementation |
| `vanillaenoughitems-playground-api` | Example plugin: simple API usage (opening GUIs, querying recipes) — loads **after** VEI |
| `vanillaenoughitems-playground-addon` | Example plugin: advanced API usage (custom recipe types, panel overrides, extractor removal) — loads **before** VEI |

---

## Building from source

Requires Java 21 and Gradle (wrapper included).

```bash
./gradlew build
```

The plugin JAR is built by:

```bash
./gradlew :vanillaenoughitems-paper:shadowJar
```

---

## License

MIT (c) qheilmann
