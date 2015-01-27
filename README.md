# Portunes
RF-ID door control software with Raspberry Pi


the name come from Roman myth.
In ancient Roman religion, Portunes (alternatively spelled Portumnes or Portunus) was a god of keys, doors and livestock.
(source wikipedia)


### Compile
`gcc -o portunes.out main.c -lpthread -lconfig -lwiringPi -lrt $(mysql_config --cflags) $(mysql_config --libs)`

you can also use compileandrun.sh script

### Need to compile
- wiringPi
- mysql-server
- mysql-client
- libconfig

### RF-ID data format
http://www.pagemac.com/azure/data_formats.php
