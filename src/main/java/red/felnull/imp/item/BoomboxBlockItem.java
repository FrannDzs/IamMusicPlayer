package red.felnull.imp.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BoomboxBlockItem extends BlockItem {
    public BoomboxBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }
/*
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
   //         NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) null);
        }
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
 */
}
