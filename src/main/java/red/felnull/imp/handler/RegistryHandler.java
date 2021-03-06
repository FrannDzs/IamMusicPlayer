package red.felnull.imp.handler;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import red.felnull.imp.block.IMPBlocks;
import red.felnull.imp.container.IMPContainerTypes;
import red.felnull.imp.item.IMPItems;
import red.felnull.imp.tileentity.IMPTileEntityTypes;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {
    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
        IMPItems.MOD_ITEMS.forEach(n -> e.getRegistry().register(n));
        IMPBlocks.MOD_BLOCKITEMS.forEach(n -> e.getRegistry().register(n));
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
        IMPBlocks.MOD_BLOCKS.forEach(n -> e.getRegistry().register(n));
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> e) {
        IMPTileEntityTypes.MOD_TILEENTITYTYPES.forEach(n -> e.getRegistry().register(n));
    }

    @SubscribeEvent
    public static void onContainerTypeRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        IMPContainerTypes.MOD_CONTAINERTYPE.forEach(n -> e.getRegistry().register(n));
    }
}
