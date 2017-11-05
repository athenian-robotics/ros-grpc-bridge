default: build

build:
	./mvnw -DskipTests=true clean package

config:
	java -jar ./etc/jars/tscfg-0.8.3.jar --spec etc/config/config.conf --pn org.athenaian.common --cn ConfigVals --dd src/main/java/org/athenian/common

clean:
	./mvnw -DskipTests=true clean

tree:
	./mvnw dependency:tree

jarcheck:
	./mvnw versions:display-dependency-updates

plugincheck:
	./mvnw versions:display-plugin-updates

versioncheck: jarcheck plugincheck

