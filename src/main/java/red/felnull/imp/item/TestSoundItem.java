package red.felnull.imp.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import red.felnull.imp.IamMusicPlayer;
import red.felnull.otyacraftengine.util.IKSGReflectionUtil;
import red.felnull.otyacraftengine.util.IKSGStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestSoundItem extends Item {
    public TestSoundItem(Properties properties) {
        super(properties);
    }

    private static final ResourceLocation fontLocation = new ResourceLocation(IamMusicPlayer.MODID, "msd");
    private static final Style fontStyle = IKSGStyles.withFont(fontLocation);

    public static List<ResourceLocation> locs = new ArrayList<>();

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        Object iku = new Object();
        if (worldIn.isRemote) {
            /*
            try {
                MusicUploader.instance().startUpload("Ikisugi of Music", URLs.newURL("https://files.minecraftforge.net/"), UUID.randomUUID().toString(), new PlayImage(PlayImage.ImageType.STRING, "ikisugi"), null);
            } catch (MalformedURLException e) {
            }*/
            Map<ResourceLocation, Texture> textures = IKSGReflectionUtil.getPrivateField(TextureManager.class, Minecraft.getInstance().textureManager, "mapTextureObjects");
            textures.forEach((n, m) -> playerIn.sendStatusMessage(new StringTextComponent(n.toString()), false));
        }
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}