#include "hardware.h"

#define I2C_ADDR 0x23
#define MAX_TIME 85
#define DHT11PIN 1
#define ATTEMPTS 5

#define led1 21
#define led2 22
#define led3 23

#define IN1 5
#define IN2 4

// 温湿度
static int dht11_val[5]={0,0,0,0,0};
// 电机速度
static int driSpeed = 0;

static int dht11_read_val(){
    uint8_t lststate=HIGH;         //last state
    uint8_t counter=0;
    uint8_t j=0,i;
    for(i=0;i<5;i++)
        dht11_val[i]=0;

    pinMode(DHT11PIN,OUTPUT);
    digitalWrite(DHT11PIN,LOW);
    delay(18);
    digitalWrite(DHT11PIN,HIGH);
    delayMicroseconds(40);

    pinMode(DHT11PIN,INPUT);
    for(i=0;i<MAX_TIME;i++)
    {
        counter=0;
        while(digitalRead(DHT11PIN)==lststate){
            counter++;
            delayMicroseconds(1);
            if(counter==255)
                break;
        }
        lststate=digitalRead(DHT11PIN);
        if(counter==255)
            break;
        if((i>=4)&&(i%2==0)){
            dht11_val[j/8]<<=1;
            if(counter>16)
                dht11_val[j/8]|=1;
            j++;
        }
    }

    if((j>=40)&&(dht11_val[4]==((dht11_val[0]+dht11_val[1]+dht11_val[2]+dht11_val[3])& 0xFF))){
        return 1;
    }
    else
        return 0;
}

// 电路驱动线程
static PI_THREAD(Driver) {
while(1) {
    if (driSpeed) {      // 电机
        softPwmWrite(IN1,driSpeed);
        digitalWrite(IN2,0);
    } else {
        softPwmWrite(IN1,1);
        digitalWrite(IN2,1);
    }
    delay(1);
}}

void init(){
    // 初始化wiringPi库
	wiringPiSetup();
    // 初始化PWM
	softPwmCreate(IN1,0,100);
    // 创建电路驱动线程
   //  piThreadCreate(Driver);
    // 初始化pin口
    pinMode(led1,OUTPUT);
    pinMode(led2,OUTPUT);
    pinMode(led3,OUTPUT);
}

const int* get_wsd() {
    while(!dht11_read_val());
    return dht11_val;
}

float get_brightness() {
    int fd;
    char buf[3];
    char val,value;
    float flight;
    fd=open("/dev/i2c-1",O_RDWR);

    if(fd<0) {
        return 1;
    }
    if(ioctl( fd,I2C_SLAVE,I2C_ADDR)<0 ) {
        return 1;
    }
    val=0x01;
    write(fd,&val,1);
    val=0x10;
    write(fd,&val,1);

    if(read(fd,&buf,3)) {
        flight=(buf[0]*256+buf[1])/1.2;
        return flight;
    } else {
        return 0;
    }
}

void set_light(char *mode) {
    int r = 1, b = 1, g = 1;
    if (mode) {
        char *p = mode;
        for (; *p; p++) {
            switch (*p) {
            case 'r':
                r = 0; break;
            case 'g':
                g = 0; break;
            case 'b':
                b = 0; break;
            }
        }
    }
	// 根据color设置灯光颜色
	digitalWrite(led1, r);
    digitalWrite(led2, g);
	digitalWrite(led3, b);
}

void set_speed(int s) {
    //driSpeed = s;
    softPwmWrite(IN1,s);
    digitalWrite(IN2,0);
}
