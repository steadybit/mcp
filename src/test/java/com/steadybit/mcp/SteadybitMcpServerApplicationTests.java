package com.steadybit.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "api.url=https://platform.steadybit.com/api",
                "api.token=my-secret-token"
        })
class SteadybitMcpServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
