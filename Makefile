.DEFAULT_GOAL := gateway

REPOSITORY := ../../.m2/repository

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
		2.11.0;
com.zaxxer
	HikariCP
		3.4.5;
io.netty
	netty-buffer
	netty-codec
	netty-codec-http
	netty-common
	netty-handler
	netty-handler-proxy
	netty-resolver
	netty-transport
		4.1.52.Final;
io.projectreactor
	reactor-core
		3.3.10.RELEASE;
io.projectreactor.addons
	reactor-extra
		3.3.4.RELEASE;
io.projectreactor.netty
	reactor-netty
		0.9.12.RELEASE;
jakarta.annotation
	jakarta.annotation-api
		1.3.5;
jakarta.validation
	jakarta.validation-api
		2.0.2;
org.apache.tomcat.embed
	tomcat-embed-core
		9.0.38;
org.attoparser
	attoparser
		2.0.5.RELEASE;
org.hibernate.validator
	hibernate-validator
		6.1.5.Final;
org.jboss.logging
	jboss-logging
		3.4.1.Final;
org.mybatis
	mybatis
		3.5.5;
org.mybatis
	mybatis-spring
		2.0.5;
org.mybatis.spring.boot
	mybatis-spring-boot-autoconfigure
		2.1.3;
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
		5.2.9.RELEASE;
org.springframework.boot
	spring-boot
	spring-boot-autoconfigure
		2.3.4.RELEASE;
org.springframework.cloud
	spring-cloud-commons
	spring-cloud-context
	spring-cloud-gateway-core
		2.2.5.RELEASE;
org.thymeleaf
	thymeleaf
	thymeleaf-spring5
		3.0.11.RELEASE;
org.unbescape
	unbescape
		1.1.6.RELEASE;
org.webjars
	bootstrap
		4.5.2;
org.webjars
	jquery
		3.5.1;
org.xerial
	sqlite-jdbc
		3.32.3.2;
org.yaml
	snakeyaml
		1.26;
endef

$(foreach 0,$(JAVA_LIB),$(if $1,$(if $(findstring ;,$0),$(eval 3 := $(subst ;,,$0)) $(eval $(foreach 4,$2,$(eval export $1/$4 := $(REPOSITORY)/$(subst .,/,$1)/$4/$3/$4-$3.jar))) $(eval 1 := ) $(eval 2 := ),$(eval 2 += $0)),$(eval 1 := $0)))

.PHONY: aigis aigis/clean gateway gateway/clean pcr pcr/clean clean

aigis:
	@gmake -f make/aigis/Makefile

aigis/clean:
	@gmake -f make/aigis/Makefile clean

gateway:
	@gmake -f make/gateway/Makefile

gateway/clean:
	@gmake -f make/gateway/Makefile clean

pcr:
	@gmake -f make/pcr/Makefile

pcr/clean:
	@gmake -f make/pcr/Makefile clean

clean: aigis/clean gateway/clean pcr/clean
