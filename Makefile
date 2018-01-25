default: build

build:
	./mvnw -DskipTests=true clean package

tests:
	./mvnw clean package

clean:
	./mvnw clean

py-stubs:
	python -m grpc_tools.protoc -I./src/main/proto --python_out=./src/main/python/stubs --grpc_python_out=./src/main/python/stubs ./src/main/proto/rosbridge_service.proto

tree:
	./mvnw dependency:tree

jarcheck:
	./mvnw versions:display-dependency-updates

plugincheck:
	./mvnw versions:display-plugin-updates

versioncheck: jarcheck plugincheck
