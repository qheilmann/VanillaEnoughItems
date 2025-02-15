package me.qheilmann.vei.Core.Process;

import org.jetbrains.annotations.Nullable;

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
    public @Nullable SmeltingProcessPanel getRecipePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipePanel'");
    }

}
