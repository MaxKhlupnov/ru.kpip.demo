FROM openjdk:19

COPY ./target/artifacts/ru_kpip_demo_bridge_jar/ru.kpip.demo.bridge.jar /kpip-demo-bridge
WORKDIR //kpip-demo-bridge
ENTRYPOINT ["java","-cp", "ru.kpip.demo.bridge.jar", "ru.kpip.demo.bridge.KpipBridgeExample"]