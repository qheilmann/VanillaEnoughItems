package dev.qheilmann.vanillaenoughitems.utils.playerhead;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import com.destroystokyo.paper.profile.ProfileProperty;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;

@NullMarked
public class PlayerHead {

    private PlayerHead() {} // Static helper

    /**
     * Creates a dynamic player head using a player's name.
     * <p> Note: A dynamic player head will be resolved via a network call to Mojang's servers. </p>
     * @param playerName The player's name
     * @return The player head ItemStack.
     */
    public static ItemStack fromPlayerName(String playerName) {
        ResolvableProfile profile = ResolvableProfile.resolvableProfile().name(playerName).build();
        return fromProfile(profile);
    }

    /**
     * Creates a dynamic player head using a player's UUID.
     * <p> Note: A dynamic player head will be resolved via a network call to Mojang's servers. </p>
     * @param uuid The player's UUID
     * @return The player head ItemStack.
     */
    public static ItemStack fromPlayerUuid(UUID uuid) {
        ResolvableProfile profile = ResolvableProfile.resolvableProfile().uuid(uuid).build();
        return fromProfile(profile);
    }

    /**
     * Creates a static player head using a custom texture value.
     * <p>
     * The value must be the base64-encoded result of this JSON structure:
     * {@code {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/<texture-hash>"}}}}.
     * Encoded value typically starts with <em>"ey"</em>.
     * </p>
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
