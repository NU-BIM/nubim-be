package com.soyeon.nubim.domain.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserNicknameGeneratorTest {

	@Test
	void generate_Validation_Test() {
		for (int i = 0; i < 10000; i++) {
			// given
			String generatedNickname = UserNicknameGenerator.generate();

			//when

			// then
			assertThat(generatedNickname)
				.hasSizeBetween(User.NicknamePolicy.MIN_LENGTH, User.NicknamePolicy.MAX_LENGTH)
				.matches(User.NicknamePolicy.REGEXP);
		}
	}
}