[![](https://jitpack.io/v/athenian-robotics/ros-grpc-bridge.svg)](https://jitpack.io/#athenian-robotics/ros-grpc-bridge)
[![Build Status](https://travis-ci.org/athenian-robotics/ros-grpc-bridge.svg?branch=master)](https://travis-ci.org/athenian-robotics/ros-grpc-bridge)
[![Code Health](https://landscape.io/github/athenian-robotics/ros-grpc-bridge/dev/landscape.svg?style=flat)](https://landscape.io/github/athenian-robotics/ros-grpc-bridge/dev)
[![Dependency Status](https://www.versioneye.com/user/projects/5a5e3a6d0fb24f005807744b/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5a5e3a6d0fb24f005807744b)

# ROS-gRPC Bridge  

This ROS-gRPC bridge is intended to connect a [roboRIO](http://www.ni.com/en-us/support/model.roborio.html) 
running Java and a ROS subsystem running Python on an [FRC](https://www.firstinspires.org/robotics/frc) robot.

The supported message types are:
* [Twist](http://docs.ros.org/api/geometry_msgs/html/msg/Twist.html)
 

## Python Client Stubs

Install the *grpcio-tools* package with:

```bash
pip install grpcio-tools
```

Generate the gRPC python stubs with: 

```bash
make py-stubs
```

Regenerate the python stubs whenever changes are made to the *src/main/proto/rosbridge_service.proto* file.

## RosBridge Usage

The *ros-grpc-bridge* jar files are published to [Jitpack.io](https://jitpack.io/#athenian-robotics/ros-grpc-bridge/1.0.9).

### Gradle

Add the JitPack repository and dependecy to your root *build.gradle*:

```groovy
	allprojects {
		repositories {
			...
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
	dependencies {
	        compile 'com.github.athenian-robotics:ros-grpc-bridge:1.0.9'
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
	    <version>1.0.9</version>
	</dependency>
```

## Create a Release

1) Create a *dev* branch.
2) Make the release changes in the *dev* branch. 
3) Update the version string in *pom.xml* and *README.md*.
4) Create a PR and squash and merge the changes in *dev* into the *master* branch.
5) Create a release on github.com and assign it a tag, which should match the version string above.
6) Goto [JitPack.io](http://jitpack.io) and lookup the *athenian-robotics/ros-grpc-bridge* repo and
click on **Get it** for the new release tag to generate a build.
