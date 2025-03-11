package me.qheilmann.vei.Core.Process;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import net.kyori.adventure.text.Component;

public class SmeltingProcess extends Process<FurnaceRecipe> {
    
    public static final String PROCESS_NAME = "Smelting";
    protected static final ItemStack PROCESS_ICON = generateIcon();
    
    public SmeltingProcess() {
        super();
    }

    @Override
    public String getProcessName() {
        return "Smelting";
    }

    @Override
    public @NotNull ItemStack getProcessIcon() {
        return PROCESS_ICON.clone();
    }

    @Override
    public @NotNull SmeltingProcessPanel generateProcessPanel(@NotNull ProcessRecipeSet<FurnaceRecipe> processRecipeSet, int variant) {
        return new SmeltingProcessPanel(processRecipeSet, variant);
    }

    private static ItemStack generateIcon(){
        ItemStack icon = new ItemStack(Material.FURNACE);
        icon.editMeta(meta -> {
            meta.displayName(Component.text(PROCESS_NAME));
            meta.setMaxStackSize(1);
        });
        return icon;
    }
}
