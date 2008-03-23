all: lib/kdoom.jar

lib/kdoom.jar: $(shell find src -name '*.java')
	javac -d classes -Xlint:deprecation $+
	jar cf lib/kdoom.jar -C classes .

clean:
	rm -rf classes/* lib/kdoom.jar
