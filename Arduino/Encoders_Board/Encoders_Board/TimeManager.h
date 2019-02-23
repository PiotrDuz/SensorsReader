// TimeManager.h

#ifndef _TIMEMANAGER_h
#define _TIMEMANAGER_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

class TimeManager
{
 protected:


 public:
	void TimeManager::init();
	void TimeManager::setDate();
	void TimeManager::sendDate();
};



#endif

