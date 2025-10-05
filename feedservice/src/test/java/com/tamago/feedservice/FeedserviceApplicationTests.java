package com.tamago.feedservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled in CI: requires DB and full Spring context. Unit tests for services run instead.")
class FeedserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
