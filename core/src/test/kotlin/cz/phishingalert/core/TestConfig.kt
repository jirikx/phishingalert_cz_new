package cz.phishingalert.core

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages =  ["cz.phishingalert.common", "cz.phishingalert.core.services"]
)
class TestConfig
