void data0Pulse(void);
void data1Pulse(void);
int wiegandInit(int d0pin, int d1pin);
void wiegandReset();
int wiegandGetPendingBitCount();
int wiegandReadData(void* data, int dataMaxLen);
void printCharAsBinary(unsigned char ch);
