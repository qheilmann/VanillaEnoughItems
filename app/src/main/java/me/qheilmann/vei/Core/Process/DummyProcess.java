package me.qheilmann.vei.Core.Process;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;

public class DummyProcess extends Process<SmeltingProcessPanel> {
    public DummyProcess() {
        super();
    }

    @Override
    public String getProcessName() {
        return "Dummy";
    }

    @Override
    public @Nullable ProcessPanel<Recipe> getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }

}
