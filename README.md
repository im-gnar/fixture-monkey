# Fixture Monkey
![Maven version](https://maven-badges.herokuapp.com/maven-central/com.navercorp.fixturemonkey/fixture-monkey/badge.svg)
[![Build](https://github.com/naver/fixture-monkey/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/naver/fixture-monkey/actions/workflows/build.yml)
[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/naver/fixture-monkey/blob/main/LICENSE)
[![Gitter](https://badges.gitter.im/fixture-monkey/community.svg)](https://gitter.im/fixture-monkey/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

<figure align="center">
    <img src= "https://user-images.githubusercontent.com/10272119/227154042-b43ab281-ac73-4648-ba8f-7f2146cde6d5.png" width="100%"/>
    <figcaption>Designed by <a href="https://www.linkedin.com/in/seongin-hong">SeongIn Hong</a> </figcaption>
</figure>


### "Write once, Test anywhere"

Fixture Monkey is designed to generate controllable arbitrary instances easily. It allows you to reuse same configurations of the instances in several tests.

You can write countless tests including edge cases by only one instance of the FixtureMonkey type. You can generate instances of complex types automatically and set fields with values from builders of the ArbitraryBuilder<T> type. The well-defined builders could be reused in any tests. Writing integration tests is easier with Fixture Monkey.
    
Each primitive type property is generated by [Jqwik](https://github.com/jlink/jqwik)


## Requirements

* JDK 1.8 or higher
* Jqwik 1.7.3
* Kotlin 1.8 or higher

## Install

### Gradle

#### Java

```groovy
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:0.6.10")
```

#### Kotlin

```groovy
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:0.6.10")
```

### Maven

#### Java

```xml
<dependency>
    <groupId>com.navercorp.fixturemonkey</groupId>
    <artifactId>fixture-monkey-starter</artifactId>
    <version>0.6.10</version>
    <scope>test</scope>
</dependency>
```

#### Kotlin

```xml
<dependency>
    <groupId>com.navercorp.fixturemonkey</groupId>
    <artifactId>fixture-monkey-starter-kotlin</artifactId>
    <version>0.6.10</version>
    <scope>test</scope>
</dependency>
```

## Example
> Add "lombok.anyConstructor.addConstructorProperties=true" in lombok.config

#### Java

```java
@Value
public class Order {
    Long id;

    String orderNo;

    String productName;

    int quantity;

    long price;

    List<String> items;

    Instant orderedAt;
}

@Test
void sampleOrder() {
    // given
    FixtureMonkey sut = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    // when
    Order actual = sut.giveMeBuilder(Order.class)
            .set("orderNo", "1")
            .set("productName", "Line Sally")
            .minSize("items", 1)
            .sample();

    // then
    then(actual.getOrderNo()).isEqualTo("1");
    then(actual.getProductName()).isEqualTo("Line Sally");
    then(actual.getItems()).hasSizeGreaterThanOrEqualTo(1);
}
```

#### Kotlin

```kotlin
data class Order (
    val id: Long,

    val orderNo: String,

    val productName: String,

    val quantity: Int,

    val price: Long,

    val items: List<String>,

    val orderedAt: Instant
)

@Test
fun sampleOrder() {
    // given
    val sut = FixtureMonkey.builder()
            .plugin(KotlinPlugin())
            .build()

    // when
    val actual = sut.giveMeBuilder<Order>()
            .setExp(Order::orderNo, "1")
            .setExp(Order::productName, "Line Sally")
            .minSizeExp(Order::items, 1)
            .sample()

    // then
    then(actual.orderNo).isEqualTo("1")
    then(actual.productName).isEqualTo("Line Sally")
    then(actual.items).hasSizeGreaterThanOrEqualTo(1)
}
```

## Documentation
* [English](https://naver.github.io/fixture-monkey)
* [한국어](https://naver.github.io/fixture-monkey/kr)

## [Third-party Modules](https://naver.github.io/fixture-monkey/docs/v0.4/third-party-modules/)

## Plugins
* [FixtureMonkey Helper](https://plugins.jetbrains.com/plugin/19589-fixturemonkey-helper)
  - IntelliJ plugin that makes it easier to use Fixture Monkey string expressions & Kotlin DSL

## Contributors
* 🐒 [ah.jo](https://github.com/seongside)
* 🐒 [mhyeon-lee](https://github.com/mhyeon-lee)
* 🐒 [acktsap](https://github.com/acktsap)
* 🐒 [benelog](https://github.com/benelog)
* 🐒 [jwChung](https://github.com/jwChung)
* 🐒 [SooKim1110](https://github.com/SooKim1110)
* @[KoEonYack](https://github.com/KoEonYack)
* @[G-ONL](https://github.com/G-ONL)
* @[imbyungjun](https://github.com/imbyungjun)
* @[sandrawangyx](https://github.com/sandrawangyx)
* @[dbgsprw](https://github.com/dbgsprw)
* @[kshired](https://github.com/kshired)
* @[ByungjunYou](https://github.com/ByungjunYou)
* @[esfomeado](https://github.com/esfomeado)
* @[yn-jn-j](https://github.com/yn-jn-j)
* @[jongwooo](https://github.com/jongwooo)
* @[doljae](https://github.com/doljae)
* @[jbl428](https://github.com/jbl428)
* @[sangy515](https://github.com/sangy515)
* @[yunseok-jeong0](https://github.com/yunseok-jeong0)
* @[wicksome](https://github.com/wicksome)

## More about Fixture Monkey
* [Deview 2021](https://tv.naver.com/v/23650158)

## Articles
* [fixure monkey로 예외 발생 테스트](https://yangbongsoo.tistory.com/68?category=982054)
* [테스트 객체를 더쉽게 만들어보자, Fixture-monkey](https://taes-k.github.io/2021/12/12/fixture-monkey/)
* [Junit Test with Fixture Monkey](https://kevin-park.medium.com/junit-test-with-fixture-monkey-ca50f6533385)
* [Fixture monkey](https://leeheefull.tistory.com/m/27)
* [테스트 데이터도구 - Fixture Monkey](https://jiwondev.tistory.com/272)
* [Fixture Monkey란?](https://velog.io/@pang_e/Fixture-Monkey%EB%9E%80)
* [테스트를 작성할 수 밖에 없는 사람들에게](https://brunch.co.kr/@seongside/3)

Welcome to write articles about Fixture Monkey!

## License

```
Copyright 2021-present NAVER Corp.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
