// 
// 
// 

#include "SensorsManager.h"

//only encoders here, initialize array how many sensors of each type
byte SensorsManager::SENSORS[] = { 0 };
//only 1 type of sensors ( Encoder )
byte SensorsManager::SENSORS_TYPES_NUM = 1;

void SensorsManager::init() {
	//initialize encoder pins
	EncoderClass::init();
}

void SensorsManager::setSensorsQuantity() {
	int i = 0;
	while (i < SENSORS_TYPES_NUM) {
		if (Serial.available() > 0) {
			SENSORS[i] = Serial.read();
			i++;
		}
	}

	//create encoders
	for (int i = 0; i < SENSORS[0]; i++) {
		EncoderClass enc;
		encoders[i] = enc;
		encoders[i].setCs(EncoderClass::CS_ARRAY[i]);
	}
}

void SensorsManager::continuousRead() {

	byte command = Commands::NULL_COMMAND;
	unsigned long initTime = millis();
	unsigned long lastedTime = 0L;

	int delay = 99;

	while (command != Commands::STOP_MEASURING)
	{
		//check if stop command send
		if (Serial.available() > 0) {
			command = Serial.read();
		}

		//read encoders without sending, so no rotation will be skipped
		for (int i = 0; i < SENSORS[0]; i++) {
			encoders[i].read(lastedTime == 0);
		}
		//read time lasted
		lastedTime = millis() - initTime;

		//if delay number of seconds passed, send readings
		if (lastedTime % delay == 0) {
			for (int i = 0; i < SENSORS[0]; i++) {
				send(encoders[i].getReading());
			}
			send(lastedTime);
		}
	}
}

void SensorsManager::getState() {
	for (int i = 0; i < SENSORS[0]; i++) {
		send(encoders[i].getRevolutionReading());
	}
}

void SensorsManager::send(long reading) {
	byte bytesToSend[4];
	//MSB
	bytesToSend[0] = (reading >> 24) & 0xff;
	bytesToSend[1] = (reading >> 16) & 0xff;
	bytesToSend[2] = (reading >> 8) & 0xff;
	//LSB
	bytesToSend[3] = (reading) & 0xff;

	Serial.write(bytesToSend, 4);
}




