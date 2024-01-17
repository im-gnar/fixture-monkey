/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.fixturemonkey.spring.interceptor;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import reactor.core.publisher.Mono;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.type.Types;

public final class FixtureMonkeyMethodInterceptor implements MethodInterceptor {
	private final FixtureMonkey fixtureMonkey;

	public FixtureMonkeyMethodInterceptor(FixtureMonkey fixtureMonkey) {
		this.fixtureMonkey = fixtureMonkey;
	}

	@SuppressWarnings({"rawtypes", "unchecked", "ReactiveStreamsUnusedPublisher"})
	@Nullable
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object methodReturnObject = invocation.proceed();

		if (Modifier.isStatic(invocation.getMethod().getModifiers())) {
			return methodReturnObject;
		}

		Class<?> callerType = requireNonNull(invocation.getThis()).getClass();
		Class<?> returnType = getReturnType(invocation.getMethod());

		if (AopUtils.isAopProxy(invocation.getThis()) && invocation.getThis() instanceof Advised advised) {
			callerType = advised.getProxiedInterfaces()[0];
		}

		Map<String, Object> manipulators = retrieveManipulators(
			callerType,
			invocation.getMethod().getName(),
			returnType
		);

		if (methodReturnObject instanceof Mono mono) {
			ArbitraryBuilder<?> fallbackBuilder = fixtureMonkey.giveMeBuilder(returnType);
			manipulators.forEach(fallbackBuilder::set);
			return mono.defaultIfEmpty(fallbackBuilder.sample())
				.onErrorResume(throwable -> Mono.just(fallbackBuilder.sample()))
				.map(value -> {
					ArbitraryBuilder<?> builder = fixtureMonkey.giveMeBuilder(value);
					manipulators.forEach(builder::set);
					return builder.sample();
				})
				.onErrorResume(throwable -> Mono.just(fixtureMonkey.giveMeOne(returnType)));
		} else if (methodReturnObject instanceof Optional optional) {
			return optional.map(value -> {
				ArbitraryBuilder<?> builder = fixtureMonkey.giveMeBuilder(value);
				manipulators.forEach(builder::set);
				return builder.sample();
			});
		}

		if (manipulators.isEmpty()) {
			return methodReturnObject;
		}

		ArbitraryBuilder<?> builder;
		if (methodReturnObject == null) {
			builder = fixtureMonkey.giveMeBuilder(returnType);
		} else {
			builder = fixtureMonkey.giveMeBuilder(methodReturnObject);
		}
		manipulators.forEach(builder::set);
		return builder.sample();
	}

	private static Class<?> getReturnType(Method method) {
		Class<?> returnType;
		AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();
		List<AnnotatedType> genericsTypes = Types.getGenericsTypes(annotatedReturnType);
		if (genericsTypes.isEmpty()) {
			returnType = method.getReturnType();
		} else {
			returnType = Types.getActualType(genericsTypes.get(0));
		}
		return returnType;
	}

	private static Map<String, Object> retrieveManipulators(
		Class<?> callerType,
		String methodName,
		Class<?> returnType
	) {
		Map<String, Object> manipulators = new HashMap<>();
		manipulators.putAll(MethodInterceptorContext.get(callerType, returnType));
		manipulators.putAll(MethodInterceptorContext.get(callerType, methodName));
		return manipulators;
	}
}
