package me.qheilmann.vei.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import com.destroystokyo.paper.profile.PlayerProfile;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CustomHeadFactory {

    public static final ItemStack DEFAULT                       = new ItemStack(Material.PLAYER_HEAD);
    public static final ItemStack CRAFTING_TABLE_2x2            = CustomHeadFactory.createFromURI(CustomHeadURI.CRAFTING_TABLE.getURI());

    public static final ItemStack WORKBENCH_TYPE_SCROLL_LEFT    = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_ARROW_LEFT.getURI());
    public static final ItemStack WORKBENCH_TYPE_SCROLL_RIGHT   = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_ARROW_RIGHT.getURI());
    public static final ItemStack WORKBENCH_VARIANT_SCROLL_UP   = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_ARROW_UP.getURI());
    public static final ItemStack WORKBENCH_VARIANT_SCROLL_DOWN = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_ARROW_DOWN.getURI());
    public static final ItemStack NEXT_RECIPE                   = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_FORWARD.getURI());
    public static final ItemStack PREVIOUS_RECIPE               = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_BACKWORD.getURI());
    public static final ItemStack BACK_RECIPE                   = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_BACKWARD_II.getURI());
    public static final ItemStack FORWARD_RECIPE                = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_FORWARD_II.getURI());
    public static final ItemStack MOVE_INGREDIENTS              = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_PLUS.getURI());

    public static final ItemStack QUICK_LINK                    = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_SLASH.getURI());
    public static final ItemStack INFO                          = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_REVERSE_EXCLAMATION_MARK.getURI());
    public static final ItemStack BOOKMARK_LIST                 = CustomHeadFactory.createFromURI(CustomHeadURI.FIREWORK_STAR_CYAN.getURI());
    public static final ItemStack BOOKMARK_SERVER_LIST          = CustomHeadFactory.createFromURI(CustomHeadURI.FIREWORK_STAR_GREEN.getURI());
    public static final ItemStack EXIT                          = CustomHeadFactory.createFromURI(CustomHeadURI.QUARTZ_X.getURI());

    /**
     * Create a custom head from a URI string
     * Retrive the head from the textures.minecraft.net
     * 
     * @param URIString The URI string to create the head from (example: http://textures.minecraft.net/texture/28f92353fd52d0eed657d997ea8149c825d366b028c1514bd9ecaaf243fb7bc6)
     * @return The custom head
     */
    public static ItemStack createFromURI(String URIString) {
        URL url = null;

        try {
            url = new URI(URIString).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
            ItemStack warningItem = new ItemStack(Material.BARRIER);
            warningItem.editMeta(meta -> meta.displayName(
                    Component.text("Warning: Conversion of the URI string to head failed (" + URIString + ")",
                            TextColor.color(255, 0, 0))));
            return warningItem;
        }

        // Here we generate an UUID from the URI string, this way we can an unique UUID for each head
        UUID uuid = UUID.nameUUIDFromBytes(URIString.getBytes());
        PlayerProfile profile = Bukkit.createProfile(uuid);
        PlayerTextures playerTextures = profile.getTextures();
        playerTextures.setSkin(url);
        profile.setTextures(playerTextures);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);

        return head;
    }

    public enum CustomHeadURI {

        QUARTZ_FORWARD                  ("http://textures.minecraft.net/texture/17b03b71d3f86220ef122f9831a726eb2b283319c7b62e7dcd2d64d9682"     , 11210),
        QUARTZ_FORWARD_II               ("http://textures.minecraft.net/texture/b54fabb1664b8b4d8db2889476c6feddbb4505eba42878c653a5d793f719b16" , 11211),
        QUARTZ_BACKWORD                 ("http://textures.minecraft.net/texture/48348aa77f9fb2b91eef662b5c81b5ca335ddee1b905f3a8b92095d0a1f141"  , 11212),
        QUARTZ_BACKWARD_II              ("http://textures.minecraft.net/texture/4c301a17c955807d89f9c72a19207d1393b8c58c4e6e420f714f696a87fdd"   , 11213),
        QUARTZ_ARROW_UP                 ("http://textures.minecraft.net/texture/a99aaf2456a6122de8f6b62683f2bc2eed9abb81fd5bea1b4c23a58156b669"  , 11214),
        QUARTZ_ARROW_RIGHT              ("http://textures.minecraft.net/texture/e3fc52264d8ad9e654f415bef01a23947edbccccf649373289bea4d149541f70", 11215),
        QUARTZ_ARROW_LEFT               ("http://textures.minecraft.net/texture/5f133e91919db0acefdc272d67fd87b4be88dc44a958958824474e21e06d53e6", 11218),
        QUARTZ_ARROW_DOWN               ("http://textures.minecraft.net/texture/3912d45b1c78cc22452723ee66ba2d15777cc288568d6c1b62a545b29c7187"  , 11221),
        QUARTZ_SLASH                    ("http://textures.minecraft.net/texture/cdf07aafe1a1fbe7804834e8d617cbdfc66493a941c11d87fb7141e57897f83" , 11227),
        QUARTZ_PLUS                     ("http://textures.minecraft.net/texture/47a0fc6dcf739c11fece43cdd184dea791cf757bf7bd91536fdbc96fa47acfb" , 11230),
        QUARTZ_X                        ("http://textures.minecraft.net/texture/391d6eda83ed2c24dcdccb1e33df3694eee397a57012255bfc56a3c244bcc474", 11268),
        QUARTZ_REVERSE_EXCLAMATION_MARK ("http://textures.minecraft.net/texture/e6d554ad5e0dc601efbb925d13424ccea532c831a90b9ca73d5e93ab6dbc5daf", 20816),
        CRAFTING_TABLE                  ("http://textures.minecraft.net/texture/2cdc0feb7001e2c10fd5066e501b87e3d64793092b85a50c856d962f8be92c78", 24180),
        FIREWORK_STAR_GREEN             ("http://textures.minecraft.net/texture/964ad8da319e6eb37721e02c78864990b45fc0fea06ee52ed4c24ac197278cb7", 29793),
        FIREWORK_STAR_CYAN              ("http://textures.minecraft.net/texture/28f92353fd52d0eed657d997ea8149c825d366b028c1514bd9ecaaf243fb7bc6", 29795);

        /**
         * URI of the custom head (from textures.minecraft.net)
         * example: http://textures.minecraft.net/texture/28f92353fd52d0eed657d997ea8149c825d366b028c1514bd9ecaaf243fb7bc6
         */
        private final String uri;

        /**
         * Id from the minecraft-heads.com website
         */
        private final int minecraftHeadId;

        CustomHeadURI(String uri, int minecraftHeadId) {
            this.uri = uri;
            this.minecraftHeadId = minecraftHeadId;
        }

        public String getURI() {
            return uri;
        }

        public int getId() {
            return minecraftHeadId;
        }
    }
}
