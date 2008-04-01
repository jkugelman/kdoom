all: lib/kdoom.jar

rebuild: clean all

lib/kdoom.jar: $(shell find src -name '*.java')
	javac -d classes -Xlint -Xlint:-serial $+
	jar cf lib/kdoom.jar -C classes .

clean:
	rm -rf classes/* lib/kdoom.jar
