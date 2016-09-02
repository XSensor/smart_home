# 通信协议格式
app代表客户端(手机应用/网页)，
term代表运行在用户智能设备上的云终端，
ser代表云端服务器

## app 2 term
- openauto
  开启自动发送感知层数据
- getlight
  获取灯光颜色
- getspeed
  获取电机转速
- gethumiture
  获取温湿度
- getbrightness
  获取光照强度
- setlight RGB
  设置灯光颜色，RGB为由'rgb'组合成的字符串
- setspeed SPEED
  设置电机速度，SPEED是0-100之间的整数

## term 2 app
- light RGG
  灯光颜色
- speed SPEED
  电机速度
- humiture 温度 湿度
  温湿度
- brightness B
  光照强度

## term|app 2 ser
- LINK USERNAME PASSWORD
  与term|app连线，需要提供用户名和密码来标识身份

## ser 2 term|app
- UNLINK
  term未与app连线，消息无法转发
- LINKERR
  连接出错，用户或密码不对
