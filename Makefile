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

