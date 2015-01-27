

/************************************************************************
* Portunes v.0.0.2
*
*
* compile with gcc -o rfid rfid.c -lpthread -lwiringPi -lrt $(mysql_config --cflags) $(mysql_config --libs)
*
*
* Copyright (c) 2015
*
*
*************************************************************************/
 
#include <stdio.h>

#include "database.h"
#include "wiegand.h"

#define D0_PIN 0
#define D1_PIN 1
 
char *bina;
void hextobin(char* hexa,int hexlen,char* bina){
  int i;
  int bitCount = 31;

  puts(hexa);
  printf("%d",hexlen);

  for(i=0; i < hexlen ; i++){

    switch(hexa[i]){
    case '0':
      bina[bitCount]   = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;	
      break;
    case '1':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      break;
    case '2':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      break;
    case '3':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      break;
    case '4':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      break;
    case '5':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      break;
    case '6':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      break;
    case '7':
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      break;
    case '8':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      break;
    case '9':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      break;
    case 'A':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      break;
    case 'B':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      break;
    case 'C':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 0;
      break;
    case 'D':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      bina[bitCount-1] = 1;
      break;
    case 'E':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 0;
      break;
    case 'F':
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      bina[bitCount-1] = 1;
      break;


    }


  }

}
///////////
void main(void) {
  int i;
  
  wiegandInit(D0_PIN, D1_PIN);
  puts("Portunes (c) 2015 v0.0.1");
  

  while(1) {
    int bitLen = wiegandGetPendingBitCount();
    
    if (bitLen == 0) {
      usleep(5000);
    } else {
      
      char data[100];
      bitLen = wiegandReadData((void *)data, 100);
      
      int bytes = bitLen / 8 + 1;
      
      //printf("Read %d bits (%d bytes): ", bitLen, bytes);
      
      char datahex[200];
      char datahex2[50];

      //memcpy(datahex,data,bytes);
      printf("card no:");
      for (i = 0; i < bytes; i++){
	
	printf("%02X",data[i]);
	sprintf(datahex +i*2,"%02X",data[i]);

      }
      
      printf("\n");

      /*print bit card no
      for (i = 0; i < bytes; i++)
	printCharAsBinary(data[i]);
      printf("\n");
      */
      

      //hextobin("00AB",4,bina);

      send_mysql_data(datahex);
      
    }
  }
} 




