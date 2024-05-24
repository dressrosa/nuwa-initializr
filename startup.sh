#!/usr/bin/env bash
#  startup.sh默认置于项目根目录下
jvmOpts="-server  -Xms1024m -Xmx1024m -XX:+UseG1GC -XX:+DisableExplicitGC"
if [[ "$env" == "production"  ]]; then
    jvmOpts="-server -Xms1024m -Xmx1024m -XX:+UseG1GC -XX:+DisableExplicitGC"
fi
java $jvmOpts \
    -Dspring.config.location=classpath:config/$env/application.yaml \
#    -Dlogging.config=classpath:config/$env/log.xml \
    -jar initializr.jar 
