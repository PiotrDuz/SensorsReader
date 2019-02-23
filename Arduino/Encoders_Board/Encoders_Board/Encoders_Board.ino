/*
 Name:		Encoders_Board.ino
 Created:	2/20/2018 09:45:14 PM
 Author:	PiotrD
*/


// the setup function runs once when you press reset or power the board

#include "TimeManager.h"
#include <SPI.h>
#include "SensorsManager.h"
#include "Commands.h"
#include "Encoder.h"

SensorsManager manager;

void setup() {
	manager.init();

	Serial.begin(115200);

	SPI.begin();
}

// the loop function runs over and over again until power down or reset
void loop() {

	byte command = Commands::NULL_COMMAND;
	if (Serial.available() > 0) {
		command = Serial.read();
	}

	switch (command) {
	case Commands::SEND_SENSORS_QUANTITY:
		manager.setSensorsQuantity();
		command = Commands::NULL_COMMAND;
		break;
	case Commands::START_MEASURING:
		manager.continuousRead();
		command = Commands::NULL_COMMAND;
		break;
	case Commands::SET_DATE: {
		TimeManager timeManager;
		timeManager.setDate();
		command = Commands::NULL_COMMAND;
		break; }
	case Commands::GET_DATE: {
		TimeManager timeManager;
		timeManager.sendDate();
		command = Commands::NULL_COMMAND;
		break; }
	case Commands::GET_STATE: 
		manager.getState();
		command = Commands::NULL_COMMAND;
		break; 
	}



}

