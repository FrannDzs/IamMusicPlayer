package net.morimori.imp.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.morimori.imp.IamMusicPlayer;
import net.morimori.imp.file.PlayList;

public class ReloadWorldPlaylistCommand {
	public static void register(CommandDispatcher<CommandSource> d) {
		d.register(Commands.literal("worldplaylistreload").requires((source) -> {
			return source.hasPermissionLevel(2);
		})
				.executes(source -> {
					source.getSource().sendFeedback(
							new TranslationTextComponent(IamMusicPlayer.MODID + ".command.plreload"),
							true);
					PlayList.checkWorldPlayLists(source.getSource().getServer(), true);
					return 1;
				}));
	}
}
