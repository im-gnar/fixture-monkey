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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.navercorp.fixturemonkey.api.container.ConcurrentLruCache;
import com.navercorp.fixturemonkey.spring.interceptor.MethodInterceptorContext.RequestTarget.FixtureMonkeyManipulation;
import com.navercorp.fixturemonkey.spring.interceptor.MethodInterceptorContext.RequestTarget.RequestMethod;

public final class MethodInterceptorContext {
	private static final Map<Class<?>, RequestTarget> targetsByType =
		new ConcurrentLruCache<>(2048);

	public static FixtureMonkeyManipulation type(Class<?> methodCallerType, Class<?> returnType) {
		return method(methodCallerType, new RequestMethod<>(returnType, null));
	}

	public static FixtureMonkeyManipulation name(Class<?> methodCallerType, String methodName) {
		return method(methodCallerType, new RequestMethod<>(null, methodName));
	}

	private static FixtureMonkeyManipulation method(Class<?> type, RequestMethod<?> requestMethod) {
		RequestTarget requestTarget = targetsByType.computeIfAbsent(type, key ->
			new RequestTarget(key, new HashMap<>())
		);

		return requestTarget.manipulationsByRequestMethod.computeIfAbsent(
			requestMethod,
			key -> new FixtureMonkeyManipulation(new HashMap<>())
		);
	}

	static Map<String, Object> get(Class<?> apiClientType, Class<?> returnType) {
		return get(apiClientType, new RequestMethod<>(returnType, null));
	}

	static Map<String, Object> get(Class<?> apiClientType, String methodName) {
		return get(apiClientType, new RequestMethod<>(null, methodName));
	}

	private static Map<String, Object> get(Class<?> apiClientType, RequestMethod<?> requestMethod) {
		RequestTarget requestTarget = targetsByType.computeIfAbsent(apiClientType, type ->
			new RequestTarget(apiClientType, new HashMap<>())
		);

		FixtureMonkeyManipulation fixtureMonkeyManipulation =
			requestTarget.manipulationsByRequestMethod.get(requestMethod);
		if (fixtureMonkeyManipulation == null) {
			return Map.of();
		}

		return fixtureMonkeyManipulation.valuesByExpression;
	}

	public static void clear() {
		targetsByType.clear();
	}

	public record RequestTarget(
		Class<?> type,

		Map<RequestMethod<?>, FixtureMonkeyManipulation> manipulationsByRequestMethod
	) {
		public record RequestMethod<T>(
			@Nullable
			Class<T> returnType,

			@Nullable
			String methodName
		) {
			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null || getClass() != obj.getClass()) {
					return false;
				}
				RequestMethod<?> that = (RequestMethod<?>)obj;
				return Objects.equals(returnType, that.returnType) && Objects.equals(methodName, that.methodName);
			}

			@Override
			public int hashCode() {
				return Objects.hash(returnType, methodName);
			}
		}

		public record FixtureMonkeyManipulation(
			Map<String, Object> valuesByExpression
		) {
			public void set(String expression, Object value) {
				valuesByExpression.put(expression, value);
			}

			@Override
			public Map<String, Object> valuesByExpression() {
				return Collections.unmodifiableMap(valuesByExpression);
			}
		}
	}
}
