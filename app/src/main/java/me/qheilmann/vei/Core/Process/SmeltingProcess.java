package me.qheilmann.vei.Core.Process;

import org.jetbrains.annotations.Nullable;

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
    public @Nullable SmeltingProcessPanel getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }

}
