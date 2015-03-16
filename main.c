

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

#include "wiegand.h"

#define D0_PIN 0
#define D1_PIN 1
 
///////////
void main(void) {
  int i;
  
  wiegandInit(D0_PIN, D1_PIN);

//  puts("Portunes (c) 2015 v0.0.1");
  
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

//      printf("card no:");

      for (i = 0; i < bytes; i++){
	
//	printf("%02X",data[i]);
	sprintf(datahex +i*2,"%02X",data[i]);

      }
      

//      printf("\n");

      //print bit card no
      for (i = 0; i < bytes; i++)
	printCharAsBinary(data[i]);
      printf("\n");
      
      
//      send_mysql_data(datahex);
}    
  }
} 




