.DEFAULT_GOAL := build-run

setup:
	./gradlew wrapper --gradle-version 8.5

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean installDist

run-dist:
	./build/install/app/bin/app

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

checkstyleMain:
	./gradlew checkstyleMain

check-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build