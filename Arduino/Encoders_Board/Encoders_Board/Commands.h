// Commands.h

#ifndef _COMMANDS_h
#define _COMMANDS_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

class Commands
{
 protected:


 public:
	 //these are from client perspective
	 static const byte SEND_SENSORS_QUANTITY = 2;
	 static const byte START_MEASURING = 32;
	 static const byte STOP_MEASURING = 33;
	 static const byte GET_DATE = 3;
	 static const byte SET_DATE = 4;
	 static const byte NULL_COMMAND = 255;
	 static const byte GET_STATE = 5;

	 Commands::Commands();
};



#endif

