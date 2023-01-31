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

package com.navercorp.fixturemonkey.resolver;

import java.util.List;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.generator.ArbitraryContainerInfo;
import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.generator.ObjectProperty;

@API(since = "0.4.0", status = Status.EXPERIMENTAL)
public final class ContainerInfoManipulator {
	private final NodeResolver nodeResolver;
	private ArbitraryContainerInfo containerInfo;

	public ContainerInfoManipulator(NodeResolver nodeResolver, ArbitraryContainerInfo containerInfo) {
		this.nodeResolver = nodeResolver;
		this.containerInfo = containerInfo;
	}

	public NodeResolver getNodeResolver() {
		return nodeResolver;
	}

	public ArbitraryContainerInfo getContainerInfo() {
		return containerInfo;
	}

	public void fixed() {
		int fixedSize = this.containerInfo.getRandomSize();

		this.containerInfo = new ArbitraryContainerInfo(
			fixedSize,
			fixedSize,
			true
		);
	}

	public boolean isMatch(
		List<ArbitraryProperty> parentArbitraryProperties,
		ObjectProperty currentObjectProperty
	) {
		List<NextNodePredicate> nextNodePredicates = nodeResolver.toNextNodePredicate();
		int parentArbitraryPropertySize = parentArbitraryProperties.size();
		int nextNodePredicateSize = nextNodePredicates.size();

		if (nextNodePredicateSize != parentArbitraryPropertySize + 1) {
			return false;
		}

		for (int i = 0; i < nextNodePredicateSize; i++) {
			int reversedNextNodePredicateIndex = nextNodePredicateSize - 1 - i;
			int reversedCurrentArbitraryPropertyIndex = parentArbitraryPropertySize - i;

			NextNodePredicate nextNodePredicate = nextNodePredicates.get(reversedNextNodePredicateIndex);
			ArbitraryProperty parentArbitraryProperty = reversedCurrentArbitraryPropertyIndex == 0
				? null
				: parentArbitraryProperties.get(reversedCurrentArbitraryPropertyIndex - 1);

			if (reversedCurrentArbitraryPropertyIndex == parentArbitraryPropertySize) {
				if (!nextNodePredicate.test(
					parentArbitraryProperty,
					currentObjectProperty,
					null
				)) {
					return false;
				}
			} else {
				ArbitraryProperty currentArbitraryProperty =
					parentArbitraryProperties.get(reversedCurrentArbitraryPropertyIndex);

				if (!nextNodePredicate.test(
					parentArbitraryProperty,
					currentArbitraryProperty.getObjectProperty(),
					currentArbitraryProperty.getContainerProperty()
				)) {
					return false;
				}
			}
		}
		return true;
	}
}
