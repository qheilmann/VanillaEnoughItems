package me.qheilmann.vei.Core.Process;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.Panels.DummyProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import net.kyori.adventure.text.Component;

public class DummyProcess extends Process<Recipe> {
    
    public static final String PROCESS_NAME = "Undefined";
    private static final ItemStack PROCESS_ICON = generateIcon();
    
    public DummyProcess() {
        super();
    }

    @Override
    public String getProcessName() {
        return PROCESS_NAME;
    }
    
    @Override
    public @NotNull ItemStack getProcessIcon() {
        return PROCESS_ICON.clone();
    }

    @Override
    public @Nullable DummyProcessPanel generateProcessPanel(@NotNull ProcessRecipeSet<Recipe> processRecipeSet, int variant) {
        return new DummyProcessPanel(processRecipeSet, variant);
    }

    private static ItemStack generateIcon(){
        ItemStack icon = new ItemStack(Material.BARRIER);
        icon.editMeta(meta -> {
            meta.displayName(Component.text(PROCESS_NAME));
            meta.setMaxStackSize(1);
        });
        return icon;
    }
}
