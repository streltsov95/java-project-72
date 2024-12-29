FROM eclipse-temurin:21-jdk

WORKDIR ./app

COPY ./app ./app

RUN gradle installDist

CMD ./app/build/install/app/bin/app
