package com.navercorp.fixturemonkey.spring.interceptor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Value;

@ConfigurationProperties(prefix = "fixture.interceptor.aspectj")
@Value
public class AspectJPointcutProperty {
	String expression;
}
