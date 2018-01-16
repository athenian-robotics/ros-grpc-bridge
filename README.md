[![](https://jitpack.io/v/athenian-robotics/ros-grpc-bridge.svg)](https://jitpack.io/#athenian-robotics/ros-grpc-bridge)
[![Build Status](https://travis-ci.org/athenian-robotics/ros-grpc-bridge.svg?branch=master)](https://travis-ci.org/athenian-robotics/ros-grpc-bridge)

# ROS gRPC Bridge  

## Python Client Stubs

Install the *grpcio-tools* package with:

```bash
pip install grpcio-tools
```

Generate the gRPC python stubs with: 

```bash
make py-stubs
```

The python stubs must be regenerated whenever changes are made to the *src/main/proto/rosbridge_service.proto* file.

## RosBridge Usage

The *ros-grpc-bridge* jar files are published to [Jitpack.io](https://jitpack.io/#athenian-robotics/ros-grpc-bridge/1.0.0).

### Gradle

Add the JitPack repository and dependecy to your root *build.gradle*:

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
	dependencies {
	        compile 'com.github.athenian-robotics:ros-grpc-bridge:1.0.0'
	}
```

### Maven

Add the JitPack repository and dependecy to your *pom.xml*:

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```xml
	<dependency>
	    <groupId>com.github.athenian-robotics</groupId>
	    <artifactId>ros-grpc-bridge</artifactId>
	    <version>1.0.0</version>
	</dependency>
```