#include <mysql.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <wiringPi.h>
#include <libconfig.h>

char* shift_control(char* card_no);
void print_table();
void send_mysql_data(void *card_no);
void report_log(const char* door_name,char* card_no,char* error,char* date,char* time);
int initMysql();



