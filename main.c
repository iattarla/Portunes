
/************************************************************************
* Portunes v.0.0.2
*
* 
* this program use wiegand code written by Kyle Mallory
*
* compile with gcc -o rfid rfid.c -lpthread -lwiringPi -lrt $(mysql_config --cflags) $(mysql_config --libs)
*
*
* Copyright (c) 2015
*
*
*************************************************************************/
 
#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>
#include <time.h>
#include <unistd.h>
#include <memory.h>

#include <mysql.h>
#include <string.h>
 
#define D0_PIN 0
#define D1_PIN 1
 
#define WIEGANDMAXDATA 32
#define WIEGANDTIMEOUT 3000000
 
static unsigned char __wiegandData[WIEGANDMAXDATA]; // can capture upto 32 bytes of data -- FIXME: Make this dynamically allocated in init?
static unsigned long __wiegandBitCount; // number of bits currently captured
static struct timespec __wiegandBitTime; // timestamp of the last bit received (used for timeouts)
char* door_name = "main door";


char *server = "92.61.14.199";
char *user = "testuser";
char *password = "123456"; /* set me first */
char *database = "personnel_test";


void data0Pulse(void) {
  if (__wiegandBitCount / 8 < WIEGANDMAXDATA) {
    __wiegandData[__wiegandBitCount / 8] <<= 1;
    __wiegandBitCount++;
  }
  
  clock_gettime(CLOCK_MONOTONIC, &__wiegandBitTime);
}

void data1Pulse(void) {
  if (__wiegandBitCount / 8 < WIEGANDMAXDATA) {
    __wiegandData[__wiegandBitCount / 8] <<= 1;
    __wiegandData[__wiegandBitCount / 8] |= 1;
    __wiegandBitCount++;
  }
  clock_gettime(CLOCK_MONOTONIC, &__wiegandBitTime);
}
 
int wiegandInit(int d0pin, int d1pin) {
  // Setup wiringPi
  wiringPiSetup() ;
  pinMode(d0pin, INPUT);
  pinMode(d1pin, INPUT);
 
  wiringPiISR(d0pin, INT_EDGE_FALLING, data0Pulse);
  wiringPiISR(d1pin, INT_EDGE_FALLING, data1Pulse);
  printf("rfid init ok\n");
}
 
void wiegandReset() {
  memset((void *)__wiegandData, 0, WIEGANDMAXDATA);
  __wiegandBitCount = 0;
}
 
int wiegandGetPendingBitCount() {
  struct timespec now, delta;
  clock_gettime(CLOCK_MONOTONIC, &now);
  delta.tv_sec = now.tv_sec - __wiegandBitTime.tv_sec;
  delta.tv_nsec = now.tv_nsec - __wiegandBitTime.tv_nsec;
 
  if ((delta.tv_sec > 1) || (delta.tv_nsec > WIEGANDTIMEOUT))
    return __wiegandBitCount;
 
  return 0;
}
 
/*
* wiegandReadData is a simple, non-blocking method to retrieve the last code
* processed by the API.
* data : is a pointer to a block of memory where the decoded data will be stored.
* dataMaxLen : is the maximum number of -bytes- that can be read and stored in data.
* Result : returns the number of -bits- in the current message, 0 if there is no
* data available to be read, or -1 if there was an error.
* Notes : this function clears the read data when called. On subsequent calls,
* without subsequent data, this will return 0.
*/
int wiegandReadData(void* data, int dataMaxLen) {
  
  if (wiegandGetPendingBitCount() > 0) {
    
    int bitCount = __wiegandBitCount;
    
    int byteCount = (__wiegandBitCount / 8) + 1;
    
    memcpy(data, (void *)__wiegandData, ((byteCount > dataMaxLen) ? dataMaxLen : byteCount));

    wiegandReset();
    return bitCount;
  }
  return 0;
}

void printCharAsBinary(unsigned char ch) {
  int i;
  for (i = 0; i < 8; i++) {
    printf("%d", (ch & 0x80) ? 1 : 0);
    ch <<= 1;
  }
}

char* shift_control(char* card_no){
  MYSQL *conn;
  MYSQL_RES *res;
  MYSQL_ROW row;  
  
  //date and time variables
  struct tm *timeinfo ;
  time_t rawtime ;
  char currentDate[11];


  rawtime = time (NULL) ;
  timeinfo = localtime(&rawtime);
  
  strftime(currentDate,11,"%Y-%m-%d",timeinfo);

  conn = mysql_init(NULL);
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }

  char query[250];

  //control for personnel did out today if true return already out
  sprintf(query,"SELECT EXISTS(SELECT card_no FROM door_control WHERE card_no='%s' AND enter_date='%s' AND shift='end')",card_no,currentDate);
  
  if (mysql_query(conn, query)){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }

  res = mysql_use_result(conn);
 
  row = mysql_fetch_row(res);

  if(strcmp(row[0],"1") == 0){
    puts("personnel already out today");
    return "out";
  } 

  //CONTROL FOR personnel did enter today 
  sprintf(query,"SELECT EXISTS(SELECT card_no FROM door_control WHERE card_no='%s' AND enter_date='%s')",card_no,currentDate);
  
  if (mysql_query(conn, query)){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }

  res = mysql_use_result(conn);
 
  row = mysql_fetch_row(res);

    
  if(strcmp(row[0],"1") == 0){ // if column exist mysql return 1
    return "end";
    puts("end");
  }else{
    return "start";
    puts("start");
  } 
 
  mysql_free_result(res);
  mysql_close(conn);
 
}
////////////

int personnel_exist(char* card_no){

  MYSQL *conn;
  MYSQL_RES *res;
  MYSQL_ROW row;  
  
  conn = mysql_init(NULL);
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }

  char query[250];

  sprintf(query,"SELECT EXISTS(SELECT * FROM personnel WHERE card_no='%s')",card_no);
  
  if (mysql_query(conn, query)){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }

  res = mysql_use_result(conn);
 
  row = mysql_fetch_row(res);
    
  if(strcmp(row[0],"1") == 0){ // if column exist mysql return 1
    puts("exist");
    return 1;
  }else{
    return 0;
    puts("not exist");
  } 
 
  mysql_free_result(res);
  mysql_close(conn);
   
}
/////////////
void print_table(){
  MYSQL *conn;
  MYSQL_RES *res;
  MYSQL_ROW row;


  conn = mysql_init(NULL);
  
  /* Connect to database */
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }

  //////////////////////////////////////for debug
  /* send SQL query */
  if (mysql_query(conn, "SELECT * from door_control")) { //show tables
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }
  res = mysql_use_result(conn);
  
  /* output table name */
  printf("door control table data:\n");
  while ((row = mysql_fetch_row(res)) != NULL){
    
    printf("%s %s %s %s %s %s \n", row[0],row[1],row[2],row[3],row[4],row[5]);
    
  }

  /////////////////////////////////////end of debug

  /* close connection */
  mysql_free_result(res);
  mysql_close(conn);
}
////
void send_mysql_data(void *card_no) {
  MYSQL *conn;
  MYSQL_RES *res;
  MYSQL_ROW row;

  //date and time variables
  struct tm *timeinfo ;
  time_t rawtime ;
  char strResponse [128] ;
  char currentTime[15];
  char currentDate[11];

  rawtime = time (NULL) ;
  timeinfo = localtime(&rawtime) ; 

    
  conn = mysql_init(NULL);
  
  /* Connect to database */
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }
 
  // Build date and time from format
  strftime(currentTime,15,"%H:%M:%S",timeinfo);
  strftime(currentDate,11,"%Y-%m-%d",timeinfo); 

    //shift control
    char *shift;

    shift = shift_control(card_no);
    puts(shift);


    if( personnel_exist(card_no) && (strcmp(shift,"out") != 0) ){
    
    char st[250];   
    
    /* Build sql Query to insert data */
    sprintf(st,"INSERT INTO door_control(card_no,door_name,shift,enter_date,enter_time) VALUES('%s','%s','%s','%s','%s')",card_no,door_name,shift,currentDate,currentTime);
  
  
    if (mysql_real_query(conn, st, strlen(st) ) ){
      //finish_with_error(conn);
      fprintf(stderr, "%s\n", mysql_error(conn));
    }

    print_table();
  }else{
    puts("you have not permission to enter");
  }
 
  /* close connection */
  //mysql_free_result(res);
  //mysql_close(conn);
} 
///////////
void main(void) {
  int i;
  
  wiegandInit(D0_PIN, D1_PIN);
  
  while(1) {
    int bitLen = wiegandGetPendingBitCount();
    
    if (bitLen == 0) {
      usleep(5000);
    } else {
      
      char data[100];
      bitLen = wiegandReadData((void *)data, 100);
      
      int bytes = bitLen / 8 + 1;
      
      printf("Read %d bits (%d bytes): ", bitLen, bytes);
      
      char datahex[200];
      char datahex2[50];

      //memcpy(datahex,data,bytes);

      for (i = 0; i < bytes; i++){
	
	printf("%02X",data[i]);
	sprintf(datahex +i*2,"%02X",data[i]);

      }
      
      printf(" : ");

     
      for (i = 0; i < bytes; i++)
	printCharAsBinary(data[i]);
      printf("\n");

      
      send_mysql_data(datahex);
      
    }
  }
} 




