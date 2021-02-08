# attendance
考勤
测试服务状态
http://121.41.13.216:8093/wanglonghai/testAlive

8:1-8:20之间随机打卡
12:1-12:20之间随机打卡
20:1-20:59 之间随机打卡

手动打卡
早上打卡
http://121.41.13.216:8093/wanglonghai/请删除中文/doAttendance?timeTip=morning

中午打卡
http://121.41.13.216:8093/wanglonghai/请删除中文/doAttendance?timeTip=noon
晚上打卡
http://121.41.13.216:8093/wanglonghai/请删除中文/doAttendance?timeTip=afternoon

每天夜晚22:1:1未写日志会发送提醒，并可点击补写;
每天夜晚23:13:1 还未写日志，自动调用写日志
补写日志：
http://121.41.13.216:8093/summary/toSaveSummary



启动示例：

nohup java -jar  lop-lecrep.jar --spring.profiles.active=prod --server.port=8093 >> /usr/local/attend/output.log 2>&1 &
