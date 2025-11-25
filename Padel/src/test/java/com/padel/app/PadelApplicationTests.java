package com.padel.app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabled because it breaks Testcontainers context")
@SpringBootTest(properties = "spring.config.name=application-test")
class PadelApplicationTests {

	@Test
	void contextLoads() {
	}

}
