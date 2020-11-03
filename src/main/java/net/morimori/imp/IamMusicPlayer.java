package net.morimori.imp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.morimori.imp.compat.theoneprobe.TheOneProbePlugin;
import net.morimori.imp.config.CommonConfig;
import net.morimori.imp.proxy.ClientProxy;
import net.morimori.imp.proxy.CommonProxy;

@Mod(IamMusicPlayer.MODID)
public class IamMusicPlayer {
	public static final String MODID = "iammusicplayer";
	public static final Logger LOGGER = LogManager.getLogger();

	@SuppressWarnings("deprecation")
	public static final CommonProxy proxy = DistExecutor
			.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());

	public IamMusicPlayer() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::intermodEqueue);
		CommonConfig.init();
	}

	private void setup(final FMLCommonSetupEvent event) {
		proxy.preInit();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		ClientProxy.clientInit();

	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		proxy.init();
	}

	private void processIMC(final InterModProcessEvent event) {
		proxy.posInit();
	}

	private void intermodEqueue(final InterModEnqueueEvent event) {
		if (ModList.get().isLoaded("theoneprobe")) {
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbePlugin::new);
		}
	}

}
