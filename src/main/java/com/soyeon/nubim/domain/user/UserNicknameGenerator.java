package com.soyeon.nubim.domain.user;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UserNicknameGenerator {
	private static final Random RANDOM = new Random();

	private static final List<String> NICKNAME_DICTIONARY = Arrays.asList(
		"Explorer", "Traveler", "Voyager", "Adventurer", "Nomad", "Wanderer",
		"Pilgrim", "Globetrotter", "Journeyer", "Pathfinder"
	);

	private UserNicknameGenerator() {
	}

	public static String generate() {
		String word = NICKNAME_DICTIONARY.get(RANDOM.nextInt(NICKNAME_DICTIONARY.size()));

		return word + "-" + RandomCodeGenerator.generate();
	}

	private class RandomCodeGenerator {
		private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		private static final int CODE_LENGTH = 10;

		public static String generate() {
			StringBuilder code = new StringBuilder(CODE_LENGTH);
			for (int i = 0; i < CODE_LENGTH; i++) {
				int index = RANDOM.nextInt(CHARACTERS.length());
				code.append(CHARACTERS.charAt(index));
			}
			return code.toString();
		}
	}
}
