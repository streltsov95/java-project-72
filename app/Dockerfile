FROM eclipse-temurin:21-jdk

WORKDIR ./app

COPY src src

RUN gradle installDist

CMD ./app/build/install/app/bin/app
