# attendance
考勤
测试服务状态

8:1-8:20之间随机打卡
12:1-12:20之间随机打卡
20:1-20:59 之间随机打卡


每天夜晚22:1:1未写日志会发送提醒，并可点击补写;
每天夜晚23:13:1 还未写日志，自动调用写日志


启动示例：

nohup java -jar  lop-lecrep.jar --spring.profiles.active=prod --server.port=8093 >> /usr/local/attend/output.log 2>&1 &
