package me.qheilmann.vei.Core.Process;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import net.kyori.adventure.text.Component;

public class CraftingProcess extends Process<CraftingRecipe> {

    protected static final String PROCESS_NAME = "Crafting";
    protected static final ItemStack PROCESS_ICON = generateIcon();

    public CraftingProcess() {
        super();
    }

    @Override
    public @NotNull String getProcessName() {
        return PROCESS_NAME;
    }

    @Override
    public @NotNull ItemStack getProcessIcon() {
        return PROCESS_ICON.clone();
    }
    
    @Override
    public @Nullable CraftingProcessPanel generateProcessPanel(@NotNull ProcessRecipeSet<CraftingRecipe> processRecipeSet, int variant) {
        return new CraftingProcessPanel(processRecipeSet, variant);
    }

    private static ItemStack generateIcon(){
        ItemStack icon = new ItemStack(Material.CRAFTING_TABLE);
        icon.editMeta(meta -> {
            meta.displayName(Component.text(PROCESS_NAME));
            meta.setMaxStackSize(1);
        });
        return icon;
    }
}
