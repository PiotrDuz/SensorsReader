// SensorsManager.h

#ifndef _SENSORSMANAGER_h
#define _SENSORSMANAGER_h

#include "Encoder.h"
#include "Commands.h"

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

class SensorsManager
{
 protected:
	 //array holding number of sensors per type
	 //each type has different index
	 static byte SENSORS[];
	 //how many types
	 static byte SENSORS_TYPES_NUM;
	 //allocate encoders
	 EncoderClass encoders[EncoderClass::ENCODERS_NUM];

 public:
	   void SensorsManager::init();
	   void SensorsManager::continuousRead();
	   void SensorsManager::setSensorsQuantity();
	   void SensorsManager::send(long reading);
	   void SensorsManager::getState();
};

#endif

