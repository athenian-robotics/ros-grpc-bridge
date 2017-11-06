default: build

build:
	./mvnw -DskipTests=true clean package

clean:
	./mvnw -DskipTests=true clean

tree:
	./mvnw dependency:tree

jarcheck:
	./mvnw versions:display-dependency-updates

plugincheck:
	./mvnw versions:display-plugin-updates

versioncheck: jarcheck plugincheck


py-stubs:
	python -m grpc_tools.protoc -I./src/main/proto --python_out=./src/main/python/grpc --grpc_python_out=./src/main/python/grpc ./src/main/proto/service.proto


