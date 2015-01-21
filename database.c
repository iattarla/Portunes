/************************************************************************
* Portunes v.0.0.2
*
*
* Mysql database connection definitions
*
*
* Copyright (c) 2015
*
*
*************************************************************************/


#include "wiegand.h"
#include "database.h"


char* door_name = "main door";
char *server = "92.61.14.199";
char *user = "testuser";
char *password = "123456"; /* set me first */
char *database = "personnel_test";



void report_log(char* door_name,char* card_no,char* error,char* date,char* time){

  MYSQL *conn;
  MYSQL_RES *res;
  MYSQL_ROW row;  
  
  conn = mysql_init(NULL);
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }

  char st[250];   
    
  /* Build sql Query to insert data */
  sprintf(st,"INSERT INTO door_logs(door_name,card_no,error,log_date,log_time) VALUES('%s','%s','%s','%s','%s')",door_name,card_no,error,date,time);
  
  
  if (mysql_real_query(conn, st, strlen(st) ) ){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }

  mysql_free_result(res);
  mysql_close(conn);
   
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
  sprintf(query,"SELECT EXISTS(SELECT card_no FROM door_control WHERE card_no='%s' AND enter_date='%s' AND shift='end');",card_no,currentDate);
  

  if (mysql_query(conn, query)){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }
  
  res = mysql_use_result(conn);
 
  row = mysql_fetch_row(res);
  
  if(strcmp(row[0],"1") == 0){
    return "out";
  }
  
  
  mysql_free_result(res);
  mysql_close(conn);


  //CONTROL FOR personnel did enter today
  
  conn = mysql_init(NULL);
  
  if (!mysql_real_connect(conn, server,
			  user, password, database, 0, NULL, 0)) {
    fprintf(stderr, "%s\n", mysql_error(conn));
    //exit(1);
  }

  char query2[100];
  sprintf(query2,"SELECT EXISTS(SELECT card_no FROM door_control WHERE card_no='%s' AND enter_date='%s')",card_no,currentDate);
  
  
  if (mysql_query(conn, query2)){
    //finish_with_error(conn);
    fprintf(stderr, "%s\n", mysql_error(conn));
  }

  
  res = mysql_use_result(conn);
 
  row = mysql_fetch_row(res);

    
  if(strcmp(row[0],"1") == 0){ // if column exist mysql return 1
    return "end";
  }else{
    return "start";
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
    return 1;
  }else{
    return 0;
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
    
    if( personnel_exist(card_no) && (strcmp(shift,"out") != 0) ){
    
    char st[250];   
    
    /* Build sql Query to insert data */
    sprintf(st,"INSERT INTO door_control(card_no,door_name,shift,enter_date,enter_time) VALUES('%s','%s','%s','%s','%s')",card_no,door_name,shift,currentDate,currentTime);
  
  
    if (mysql_real_query(conn, st, strlen(st) ) ){
      //finish_with_error(conn);
      fprintf(stderr, "%s\n", mysql_error(conn));
    }
    report_log(door_name,card_no,"no error",currentDate,currentTime);
    print_table();
  }else{
      if((strcmp(shift,"out") != 0)){
	puts("you have not permission to enter");
	report_log(door_name,card_no,"permission denied",currentDate,currentTime);
        
	digitalWrite (BUZZER_PIN, LOW) ; 
	delay (500) ;
	digitalWrite (BUZZER_PIN,  HIGH);
      }
      else{
	puts("personnel already out today");
	report_log(door_name,card_no,"personnel out",currentDate,currentTime);
      }
  }
 
  /* close connection */
  //mysql_free_result(res);
  //mysql_close(conn);
} 
