.DEFAULT_GOAL := gateway

COMPILE_LIB := make/gateway/classes;\
	$(org.springframework/spring-context);\
	$(org.springframework.boot/spring-boot);\
	$(org.springframework.boot/spring-boot-autoconfigure)

RUNTIME_LIB := make/gateway/classes;\
	$(ch.qos.logback/logback-classic);\
	$(ch.qos.logback/logback-core);\
	$(com.fasterxml/classmate);\
	$(com.fasterxml.jackson.core/jackson-annotations);\
	$(com.fasterxml.jackson.core/jackson-core);\
	$(com.fasterxml.jackson.core/jackson-databind);\
	$(io.github.classgraph/classgraph);\
	$(io.netty/netty-buffer);\
	$(io.netty/netty-codec);\
	$(io.netty/netty-codec-dns);\
	$(io.netty/netty-codec-http);\
	$(io.netty/netty-codec-http2);\
	$(io.netty/netty-common);\
	$(io.netty/netty-handler);\
	$(io.netty/netty-handler-proxy);\
	$(io.netty/netty-resolver);\
	$(io.netty/netty-resolver-dns);\
	$(io.netty/netty-transport);\
	$(io.netty/netty-transport-native-unix-common);\
	$(io.projectreactor/reactor-core);\
	$(io.projectreactor.addons/reactor-extra);\
	$(io.projectreactor.netty/reactor-netty-core);\
	$(io.projectreactor.netty/reactor-netty-http);\
	$(jakarta.validation/jakarta.validation-api);\
	$(org.hibernate.validator/hibernate-validator);\
	$(org.jboss.logging/jboss-logging);\
	$(org.reactivestreams/reactive-streams);\
	$(org.slf4j/jul-to-slf4j);\
	$(org.slf4j/slf4j-api);\
	$(org.springframework/spring-aop);\
	$(org.springframework/spring-beans);\
	$(org.springframework/spring-context);\
	$(org.springframework/spring-core);\
	$(org.springframework/spring-expression);\
	$(org.springframework/spring-jcl);\
	$(org.springframework/spring-web);\
	$(org.springframework/spring-webflux);\
	$(org.springframework.boot/spring-boot);\
	$(org.springframework.boot/spring-boot-autoconfigure);\
	$(org.springframework.cloud/spring-cloud-commons);\
	$(org.springframework.cloud/spring-cloud-context);\
	$(org.springframework.cloud/spring-cloud-gateway-server);\
	$(org.springframework.security/spring-security-crypto);\
	$(org.webjars/bootstrap);\
	$(org.webjars/jquery);\
	$(org.webjars/webjars-locator-core);\
	$(org.yaml/snakeyaml)

COMPILE_LIB := $(subst ; ,:,$(COMPILE_LIB))
RUNTIME_LIB := $(subst ; ,:,$(RUNTIME_LIB))

.PHONY: gateway clean

gateway: make/gateway/classes
	java -cp $(RUNTIME_LIB) com.chen.spring.gateway.GatewayApplication

make/gateway/classes: $(patsubst java/%.java,make/gateway/classes/%.class,$(shell cd build/project/spring/spring.gateway/src/main && find java -name *.java | xargs)) $(patsubst resources/%,make/gateway/classes/%,$(shell cd build/project/spring/spring.gateway/src/main && find resources -type f | xargs))
	$(eval COMPILE_JAVA := $(patsubst make/gateway/classes/%.class,build/project/spring/spring.gateway/src/main/java/%.java,$(filter %.class,$?)))
	$(if $(COMPILE_JAVA),javac -cp $(COMPILE_LIB) -d make/gateway/classes -encoding UTF-8 -parameters -verbose $(COMPILE_JAVA))
	@touch make/gateway/classes

make/gateway/classes/%.class: build/project/spring/spring.gateway/src/main/java/%.java
	@rm -fv $@

make/gateway/classes/%: build/project/spring/spring.gateway/src/main/resources/%
	@mkdir -p $(@D)
	@ln -fv $< $(@D)

clean:
	@rm -frv make/gateway/classes
	@echo clean
