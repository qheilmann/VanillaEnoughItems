package dev.qheilmann.vanillaenoughitems.utils.playerhead;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import com.destroystokyo.paper.profile.ProfileProperty;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;

public class PlayerHead {

    /**
     * Creates a dynamic player head using a player's UUID.
     * <p> Note: A dynamic player head be resolved via a network call to Mojang's servers. </p>
     * @param uuid The player's UUID
     * @return The player head ItemStack.
     */
    public static ItemStack fromPlayerUuid(UUID uuid) {
        ResolvableProfile profile = ResolvableProfile.resolvableProfile().uuid(uuid).build();
        return fromProfile(profile);
    }

    /**
     * Creates a static player head using a texture value.
     * @param value The base64-encoded texture value
     * @return The player head ItemStack.
     */
    public static ItemStack fromTextureValue(String value) {
        ResolvableProfile profile = ResolvableProfile.resolvableProfile().addProperty(
            new ProfileProperty("textures", value)
        ).build();
        return fromProfile(profile);
    }

    /**
     * Creates a player head using a ResolvableProfile.
     * @param profile The ResolvableProfile
     * @return The player head ItemStack.
     */
    public static ItemStack fromProfile(ResolvableProfile profile) {
        ItemStack skull = ItemType.PLAYER_HEAD.createItemStack();
        skull.setData(DataComponentTypes.PROFILE, profile);
        return skull;
    }
}


