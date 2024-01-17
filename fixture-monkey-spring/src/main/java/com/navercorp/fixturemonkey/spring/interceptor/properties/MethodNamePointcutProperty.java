package com.navercorp.fixturemonkey.spring.interceptor.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Value;

@ConfigurationProperties(prefix = "fixture.interceptor.method")
@Value
public class MethodNamePointcutProperty {
	List<String> names;
}
