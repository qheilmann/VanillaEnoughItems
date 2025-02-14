package me.qheilmann.vei.Core.Process;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;

public class SmeltingProcess extends Process<SmeltingProcessPanel> {
    public SmeltingProcess() {
        super();
    }

    @Override
    public String getProcessName() {
        return "Smelting";
    }

    @Override
    public @Nullable ProcessPanel<Recipe> getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }

}
