package me.qheilmann.vei.Core.Process;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import net.kyori.adventure.text.Component;

public class DummyProcess extends Process<FurnaceRecipe> {
    
    protected static final String PROCESS_NAME = "Dummy";
    protected static final ItemStack PROCESS_ICON = generateIcon();
    
    public DummyProcess() {
        super();
    }

    @Override
    public String getProcessName() {
        return "Dummy";
    }
    
    @Override
    public @NotNull ItemStack getProcessIcon() {
        return PROCESS_ICON.clone();
    }

    @Override
    public @Nullable SmeltingProcessPanel generateProcessPanel(@NotNull ProcessRecipeSet<FurnaceRecipe> processRecipeSet, int variant) {
        return new SmeltingProcessPanel(processRecipeSet, variant); // TODO replace with custom process panel
    }

    private static ItemStack generateIcon(){
        ItemStack icon = new ItemStack(Material.STONE);
        icon.editMeta(meta -> {
            meta.displayName(Component.text(PROCESS_NAME));
            meta.setMaxStackSize(1);
        });
        return icon;
    }
}
