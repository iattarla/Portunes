#include <stdio.h>
#include <wiringPi.h>
#include <string.h>
#include <time.h>

#define WIEGANDMAXDATA 32
#define WIEGANDTIMEOUT 3000000

#define BUZZER_PIN 4
#define LED_PIN 5

void data0Pulse(void);
void data1Pulse(void);
int wiegandInit(int d0pin, int d1pin);
void wiegandReset();
int wiegandGetPendingBitCount();
int wiegandReadData(void* data, int dataMaxLen);
void printCharAsBinary(unsigned char ch);

