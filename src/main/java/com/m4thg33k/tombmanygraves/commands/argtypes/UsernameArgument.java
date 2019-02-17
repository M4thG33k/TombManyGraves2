package com.m4thg33k.tombmanygraves.commands.argtypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class UsernameArgument implements ArgumentType<String> {

	public static UsernameArgument get() {
		return new UsernameArgument();
	}
	
	@Override
	public <S> String parse(StringReader reader) {
		String word = "";
		while (reader.canRead()) {
			char letter = reader.read();
			if (letter == ' ') {
				reader.setCursor(reader.getCursor() - 1);
				break;
			}
			word += letter;
		}
		return word;
	}

	@Override
	public Collection<String> getExamples() {
		return Arrays.asList("notch", "tiffit", "m4thg33k");
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		for (String str : ServerLifecycleHooks.getCurrentServer().getOnlinePlayerNames())
			builder.suggest(str);
		return builder.buildFuture();

	}

}
