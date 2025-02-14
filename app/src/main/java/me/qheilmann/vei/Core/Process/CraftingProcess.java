package me.qheilmann.vei.Core.Process;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;

public class CraftingProcess extends Process<CraftingProcessPanel> {
    public CraftingProcess() {
        super();
    }

    @Override
    public @NotNull String getProcessName() {
        return "Crafting";
    }

    @Override
    public @Nullable ProcessPanel<Recipe> getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }
}
