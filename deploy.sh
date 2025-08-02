#!/bin/bash

BUILD_PATH=$(ls /home/ubuntu/build/*.jar)
JAR_NAME=$(basename $BUILD_PATH)

CURRENT_PID=$(pgrep -f $JAR_NAME)
if [ -z $CURRENT_PID ]; then
  echo "실행 중인 애플리케이션이 없습니다."
else
  echo "실행 중인 애플리케이션 종료: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_PATH=/home/ubuntu/
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

echo "새 애플리케이션 배포 시작..."
nohup java -jar $JAR_NAME > server.log 2>&1 &

echo "서버가 시작되었습니다."