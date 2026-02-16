package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Maintains a mapping between Process types and their corresponding ProcessPanelFactory implementations.
 * Includes a factory method to create the correct panel for a given recipe.
 */
@NullMarked
public class ProcessPanelRegistryImpl implements ProcessPanelRegistry {
    
    private final Map<Process, ProcessPanelFactory> factories = new HashMap<>();
    private final RecipeExtractor extractor;

    public ProcessPanelRegistryImpl(RecipeExtractor extractor) {
        this.extractor = extractor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerProvider(Process process, ProcessPanelFactory provider) {
        factories.put(process, provider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessPanel createPanel(Process process, Recipe recipe, Style style) {       
        // Non registered factory
        if (!hasFactory(process)) {
            return new ProcessPanel.UndefinedProcessPanel(recipe, style, extractor, process);
        }

        ProcessPanelFactory factory = factories.get(process);
        return factory.create(recipe, style);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFactory(Process process) {
        return factories.containsKey(process);
    }
}
