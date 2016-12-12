
![alt text][logo] Automation-util
===
[logo]: http://aspire.jo/images/logo.png "Logo Title Text 2"

[![Build Status](https://travis-ci.org/AspireInfotech/mobile-automation-util.svg?branch=master)](https://travis-ci.org/AspireInfotech/mobile-automation-util)

##### The Automation-util to help test automation for all projects
##### How can use `Automation-util` on your project:
You need to add maven dependency on `pom.xml` file before executing your tests  :

 ```sh
	<dependency>
	    <groupId>com.github.AspireInfotech</groupId>
	    <artifactId>mobile-automation-util</artifactId>
	    <version>v2.1.93</version>
	</dependency>
 ```	
 
##### Please update the version number for each update/new change
[![](https://jitpack.io/v/AspireInfotech/mobile-automation-util.svg)](https://jitpack.io/#AspireInfotech/mobile-automation-util)


### Features Automation-util

```sh
1. SoftAssert 
```

### 1) SoftAssert
Tests don’t stop running even if an assertion condition fails, but the test itself is marked as a failed test to indicate the right result


##### * Here is a sample test for Soft Assertion
```sh
 public void sampleSoftAssert(){
   SoftAssert.softAssertThat("not displayed ",true
   ,Matchers.equalTo(false));
 }
```

##### * How you initialize the assertions
add `SoftAssert.assertAll()` on class LifecycleSteps
This method collates all the failures and decides whether to fail the test or not at the end.

```sh
	@AfterScenario
	public void runAfterEachScenario() {
		System.out.println("afterScenario is called");
		SoftAssert.assertAll();
	}
```

add `SoftAssert.clear()` on class LifecycleSteps This method remove all the failures.
```sh
	@BeforeScenario(uponType = ScenarioType.ANY)
	public void beforeEachExampleScenario() {
		System.out.println("beforeScenario is called");
		SoftAssert.clear();
	}
```



