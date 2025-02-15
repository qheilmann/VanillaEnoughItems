package me.qheilmann.vei.Core.Process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable CraftingProcessPanel getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }
}
