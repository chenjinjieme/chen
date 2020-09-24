.DEFAULT_GOAL := aigis

COMPILE_LIB := make/aigis/classes;\
	$(org.mybatis/mybatis);\
	$(org.springframework/spring-context);\
	$(org.springframework/spring-core);\
	$(org.springframework/spring-tx);\
	$(org.springframework/spring-web);\
	$(org.springframework.boot/spring-boot);\
	$(org.springframework.boot/spring-boot-autoconfigure);\
	$(org.yaml/snakeyaml)

RUNTIME_LIB := make/aigis/classes;\
	$(ch.qos.logback/logback-classic);\
	$(ch.qos.logback/logback-core);\
	$(com.fasterxml.jackson.core/jackson-annotations);\
	$(com.fasterxml.jackson.core/jackson-core);\
	$(com.fasterxml.jackson.core/jackson-databind);\
	$(com.zaxxer/HikariCP);\
	$(jakarta.annotation/jakarta.annotation-api);\
	$(org.apache.tomcat.embed/tomcat-embed-core);\
	$(org.attoparser/attoparser);\
	$(org.mybatis/mybatis);\
	$(org.mybatis/mybatis-spring);\
	$(org.mybatis.spring.boot/mybatis-spring-boot-autoconfigure);\
	$(org.slf4j/jul-to-slf4j);\
	$(org.slf4j/slf4j-api);\
	$(org.springframework/spring-aop);\
	$(org.springframework/spring-beans);\
	$(org.springframework/spring-context);\
	$(org.springframework/spring-core);\
	$(org.springframework/spring-expression);\
	$(org.springframework/spring-jcl);\
	$(org.springframework/spring-jdbc);\
	$(org.springframework/spring-tx);\
	$(org.springframework/spring-web);\
	$(org.springframework/spring-webmvc);\
	$(org.springframework.boot/spring-boot);\
	$(org.springframework.boot/spring-boot-autoconfigure);\
	$(org.thymeleaf/thymeleaf);\
	$(org.thymeleaf/thymeleaf-spring5);\
	$(org.unbescape/unbescape);\
	$(org.webjars/bootstrap);\
	$(org.webjars/jquery);\
	$(org.xerial/sqlite-jdbc);\
	$(org.yaml/snakeyaml)

COMPILE_LIB := $(subst ; ,:,$(COMPILE_LIB))
RUNTIME_LIB := $(subst ; ,:,$(RUNTIME_LIB))

.PHONY: aigis clean

aigis: make/aigis/classes
	java -cp $(RUNTIME_LIB) com.chen.spring.aigis.AigisApplication

make/aigis/classes: $(patsubst java/%.java,make/aigis/classes/%.class,$(shell cd build/project/spring/spring.aigis/src/main && find java -name *.java | xargs)) $(patsubst resources/%,make/aigis/classes/%,$(shell cd build/project/spring/spring.aigis/src/main && find resources -type f | xargs))
	$(eval COMPILE_JAVA := $(patsubst make/aigis/classes/%.class,build/project/spring/spring.aigis/src/main/java/%.java,$(filter %.class,$?)))
	$(if $(COMPILE_JAVA),javac -cp $(COMPILE_LIB) -d make/aigis/classes -encoding UTF-8 -parameters -verbose $(COMPILE_JAVA))
	@touch make/aigis/classes

make/aigis/classes/%.class: build/project/spring/spring.aigis/src/main/java/%.java
	@rm -fv $@

make/aigis/classes/%: build/project/spring/spring.aigis/src/main/resources/%
	@mkdir -p $(@D)
	@ln -fv $< $(@D)

clean:
	@rm -frv make/aigis/classes
	@echo clean
