package com.m4thg33k.tombmanygraves.commands.argtypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class TimestampArgument implements ArgumentType<String> {

	public static TimestampArgument get() {
		return new TimestampArgument();
	}

	@Override
	public <S> String parse(StringReader reader) throws CommandSyntaxException {
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
		return Arrays.asList("latest");
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> names = DeathInventoryHandler.getFilenames(context.getArgument("player", String.class));
		for (String str : names)
			builder.suggest(str);
		builder.suggest("latest");
		return builder.buildFuture();

	}

}
