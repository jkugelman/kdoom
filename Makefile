all: lib/kdoom.jar

lib/kdoom.jar: $(shell find src -name '*.java')
	javac -d classes $+
	jar cf lib/kdoom.jar -C classes .
