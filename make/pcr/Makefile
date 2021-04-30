.DEFAULT_GOAL := pcr

COMPILE_LIB := make/pcr/classes;\
	$(org.mybatis/mybatis);\
	$(org.springframework/spring-context);\
	$(org.springframework/spring-web);\
	$(org.springframework.boot/spring-boot);\
	$(org.springframework.boot/spring-boot-autoconfigure)

RUNTIME_LIB := make/pcr/classes;\
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
	$(org.xerial/sqlite-jdbc);\
	$(org.yaml/snakeyaml)

COMPILE_LIB := $(subst ; ,:,$(COMPILE_LIB))
RUNTIME_LIB := $(subst ; ,:,$(RUNTIME_LIB))

.PHONY: pcr clean

pcr: make/pcr/classes
	java -cp $(RUNTIME_LIB) com.chen.spring.pcr.PCRApplication

make/pcr/classes: $(patsubst java/%.java,make/pcr/classes/%.class,$(shell cd build/project/spring/spring.pcr/src/main && find java -name *.java | xargs)) $(patsubst resources/%,make/pcr/classes/%,$(shell cd build/project/spring/spring.pcr/src/main && find resources -type f | xargs))
	$(eval COMPILE_JAVA := $(patsubst make/pcr/classes/%.class,build/project/spring/spring.pcr/src/main/java/%.java,$(filter %.class,$?)))
	$(if $(COMPILE_JAVA),javac -cp $(COMPILE_LIB) -d make/pcr/classes -encoding UTF-8 -parameters -verbose $(COMPILE_JAVA))
	@touch make/pcr/classes

make/pcr/classes/%.class: build/project/spring/spring.pcr/src/main/java/%.java
	@rm -fv $@

make/pcr/classes/%: build/project/spring/spring.pcr/src/main/resources/%
	@mkdir -p $(@D)
	@ln -fv $< $(@D)

clean:
	@rm -frv make/pcr/classes
	@echo clean
