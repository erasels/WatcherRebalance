package WatcherRebalance.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

// Thank you Blank The Evil!

// Welcome to the utilities package. This package is for small utilities that make our life easier.
// You honestly don't need to bother with this unless you want to know how we're loading the textures.


public class TextureLoader {
    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
    public static final Logger logger = LogManager.getLogger(TextureLoader.class.getName());

    /**
     * @param textureString - String path to the texture you want to load relative to resources,
     *                      Example: "theDefaultResources/images/ui/missing_texture.png"
     * @return <b>com.badlogic.gdx.graphics.Texture</b> - The texture from the path provided
     */
    public static Texture getTexture(final String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString);
            } catch (GdxRuntimeException e) {
                logger.error("Could not find texture: " + textureString);
                return ImageMaster.CHECKBOX;
            }
        }
        return textures.get(textureString);
    }

    /**
     * @param - String path to the texture you want to load relative to resources,
     *          * Example: "img/ui/missingtexture.png"
     * @return <b>com.badlogic.gdx.graphics.TextureAtlas.AtlasRegion</b> - The texture is returned as an AtlasRegion
     */
    public static TextureAtlas.AtlasRegion getTextureAsAtlasRegion(String textureString) {
        Texture texture = getTexture(textureString);
        return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    /**
     * Creates an instance of the texture, applies a linear filter to it, and places it in the HashMap
     *
     * @param textureString - String path to the texture you want to load relative to resources,
     *                      Example: "img/ui/missingtexture.png"
     * @throws GdxRuntimeException
     */
    private static void loadTexture(final String textureString) throws GdxRuntimeException {
        logger.info("Watcher Rebalance | Loading Texture: " + textureString);
        Texture texture = new Texture(textureString);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures.put(textureString, texture);
    }

    @SuppressWarnings("unused")
    @SpirePatch(clz = Texture.class, method="dispose")
    public static class DisposeListener {
        @SpirePrefixPatch
        public static void DisposeListenerPatch(final Texture __instance) {
            textures.entrySet().removeIf(entry -> {
                if (entry.getValue().equals(__instance)) logger.info("TextureLoader | Removing Texture: " + entry.getKey());
                return entry.getValue().equals(__instance);
            });
        }
    }
}
