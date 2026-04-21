@echo off
set "JAVA_HOME=D:\java\jdk\jdk17"
set "PATH=D:\java\jdk\jdk17\bin;%PATH%"
cd /d D:\Progects\Codex\bishe\eapple-admin
mvn spring-boot:run -DskipTests 1> ..\runtime\backend.out.log 2> ..\runtime\backend.err.log
