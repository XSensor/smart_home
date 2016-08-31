#include <stdio.h>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <errno.h>
#include <wiringPi.h>
#include <softPwm.h>
#include <stdlib.h>
#include <stdint.h>

// 获取I的第N个比特位，从0开始计数
#define GETBIT(I, N) ((I) >> (N) & 1)
// 设置I的第N个比特位
#define SETBIT(I, N) ((I) |= (N) << 1)
// 转换为bool值，0或1
#define TOBOOL(I) (I ? 1 : 0)

/* 功能：进程初始化函数
 * 说明：初始化wiringPi库 */
void init();

/* 功能：设置灯光颜色
 * mode：指定相应灯光的颜色
 * 说明：如果mode为NULL，则关闭灯 */
void set_light(char *mode);

/* 功能：获取温湿度数值
 * 说明：返回一个整形数组，数组内容为：湿度整数，湿度小数，
         温度整数，温度小数，校验位 */
const int* get_wsd();

/* 功能：获取光照强度
 * 返回：光照强度数值 */
float get_brighness();

/* 功能：设置电机转速
 * s：转速
 * 说明：转速范围：0-100，如果s为0，则关闭电机 */
void set_speed(int s);
