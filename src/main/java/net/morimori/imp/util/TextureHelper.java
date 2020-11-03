package net.morimori.imp.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.morimori.imp.IamMusicPlayer;
import net.morimori.imp.packet.ClientResponseMessage;
import net.morimori.imp.packet.PacketHandler;

public class TextureHelper {
	private static Minecraft mc = Minecraft.getInstance();
	private static ResourceLocation lodinglocation = new ResourceLocation(IamMusicPlayer.MODID,
			"textures/gui/loading_image.png");

	public static Map<String, ResourceLocation> pictuers = new HashMap<String, ResourceLocation>();

	public static ResourceLocation getPlayerSkinTexture(String name) {

		if (name.equals(mc.player.getName().getString())) {
			return mc.player.getLocationSkin();
		}

		ResourceLocation faselocation;
		GameProfile GP = PlayerHelper.getPlayerTextuerProfile(name);
		Map<Type, MinecraftProfileTexture> map = mc.getSkinManager().loadSkinFromCache(GP);
		faselocation = map.containsKey(Type.SKIN)
				? mc.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN)
				: DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(GP));
		return faselocation;
	}

	public static ResourceLocation getPictueLocation(String uuid) {

		if (pictuers.containsKey(uuid)) {
			return pictuers.get(uuid);
		}

		ResourceLocation imagelocation = new ResourceLocation(IamMusicPlayer.MODID,
				"pictuer/" + uuid);

		File picfile = FileHelper.getClientPictuerCashPath().resolve(uuid + ".png").toFile();

		if (picfile.exists() && picfile != null) {
			try {
				byte[] pibyte = FileLoader.fileBytesReader(picfile.toPath());
				ByteArrayInputStream bis = new ByteArrayInputStream(pibyte);
				NativeImage NI = NativeImage.read(bis);
				mc.textureManager.func_229263_a_(imagelocation, new DynamicTexture(NI));
				pictuers.put(uuid, imagelocation);
				return imagelocation;

			} catch (IOException e) {

			}

		} else {
			PacketHandler.INSTANCE.sendToServer(new ClientResponseMessage(3, 0, uuid));
			pictuers.put(uuid, lodinglocation);
		}
		return lodinglocation;
	}

	public static boolean isImageNotExists(String uuid) {

		return uuid.equals("null") || (!pictuers.containsKey(uuid) || pictuers.get(uuid).equals(lodinglocation));
	}

}
