FROM openjdk:19

COPY ./out/kpip_demo_jar/ /kpip-demo-jar
WORKDIR /kpip-demo-jar
ENTRYPOINT ["java","-cp", "kpip.demo.jar", "ru.kpip.demo.KpipAdapterMain"]
