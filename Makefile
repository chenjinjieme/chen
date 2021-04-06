.DEFAULT_GOAL := gateway

REPOSITORY := $(HOME)/.m2/repository

define JAVA_LIB
ch.qos.logback
	logback-classic
	logback-core
		1.2.3;
com.fasterxml
	classmate
		1.5.1;
com.fasterxml.jackson.core
	jackson-annotations
	jackson-core
	jackson-databind
		2.12.3;
com.zaxxer
	HikariCP
		4.0.3;
io.github.classgraph
	classgraph
		4.8.104;
io.netty
	netty-buffer
	netty-codec
	netty-codec-dns
	netty-codec-http
	netty-codec-http2
	netty-common
	netty-handler
	netty-handler-proxy
	netty-resolver
	netty-resolver-dns
	netty-transport
	netty-transport-native-unix-common
		4.1.63.Final;
io.projectreactor
	reactor-core
		3.4.5;
io.projectreactor.addons
	reactor-extra
		3.4.3;
io.projectreactor.netty
	reactor-netty-core
	reactor-netty-http
		1.0.6;
jakarta.annotation
	jakarta.annotation-api
		1.3.5;
jakarta.validation
	jakarta.validation-api
		2.0.2;
org.apache.tomcat.embed
	tomcat-embed-core
		9.0.45;
org.attoparser
	attoparser
		2.0.5.RELEASE;
org.hibernate.validator
	hibernate-validator
		6.2.0.Final;
org.jboss.logging
	jboss-logging
		3.4.1.Final;
org.mybatis
	mybatis
		3.5.7;
org.mybatis
	mybatis-spring
		2.0.6;
org.mybatis.spring.boot
	mybatis-spring-boot-autoconfigure
		2.1.4;
org.reactivestreams
	reactive-streams
		1.0.3;
org.slf4j
	jul-to-slf4j
	slf4j-api
		1.7.30;
org.springframework
	spring-aop
	spring-beans
	spring-context
	spring-core
	spring-expression
	spring-jcl
	spring-jdbc
	spring-tx
	spring-web
	spring-webflux
	spring-webmvc
		5.3.6;
org.springframework.boot
	spring-boot
	spring-boot-autoconfigure
		2.4.5;
org.springframework.cloud
	spring-cloud-commons
	spring-cloud-context
	spring-cloud-gateway-server
		3.0.2;
org.springframework.security
	spring-security-crypto
		5.4.6;
org.thymeleaf
	thymeleaf
	thymeleaf-spring5
		3.0.12.RELEASE;
org.unbescape
	unbescape
		1.1.6.RELEASE;
org.webjars
	bootstrap
		4.6.0;
org.webjars
	jquery
		3.6.0;
org.webjars
	webjars-locator-core
		0.46;
org.xerial
	sqlite-jdbc
		3.32.3.2;
org.yaml
	snakeyaml
		1.28;
endef

$(foreach 0,$(JAVA_LIB),$(if $1,$(if $(findstring ;,$0),$(eval 3 := $(subst ;,,$0)) $(eval $(foreach 4,$2,$(eval export $1/$4 := $(REPOSITORY)/$(subst .,/,$1)/$4/$3/$4-$3.jar))) $(eval 1 := ) $(eval 2 := ),$(eval 2 += $0)),$(eval 1 := $0)))

.PHONY: clean phony

%: make/%/Makefile phony
	$(MAKE) -f $<

%/clean: make/%/Makefile phony
	$(MAKE) -f $< clean

clean: $(patsubst make/%/Makefile,%/clean,$(shell find make -name Makefile))
