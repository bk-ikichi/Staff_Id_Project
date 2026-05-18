package org.example.ikichi_staffcard_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class IkichiStaffCardProjectApplicationTests {

	@Test
	void generatedPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// "password123" をハッシュ化して表示
		System.out.println("HASHED: " + encoder.encode("password123"));
	}
}
