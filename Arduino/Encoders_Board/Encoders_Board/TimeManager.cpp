// 
// 
// 

#include "TimeManager.h"
#include "RTClib.h"

void TimeManager::init()
{


}

void TimeManager::setDate() {
	//expected 6 values : 
	//Year, Month, Day, Hour, Minute, Second
	//Year is a byte number after 2000
	byte buffer[6];
	//wait for bytes
	unsigned long t = millis();
	while (Serial.available() != 6) {
		delay(1);
		unsigned long lasted = millis() - t;
		if (lasted > 1000) {
			break;
		}
	}
	Serial.readBytes(buffer, 6);


	DateTime setTime(2000 + buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5]);

	RTC_DS1307 rtc;
	rtc.begin();
	rtc.adjust(setTime);
	
}

void TimeManager::sendDate() {
	RTC_DS1307 rtc;
	rtc.begin();
	DateTime now = rtc.now();
	
	byte valuesToSend[6];
	valuesToSend[0] = (byte) (now.year() - 2000);
	valuesToSend[1] = now.month();
	valuesToSend[2] = now.day();
	valuesToSend[3] = now.hour();
	valuesToSend[4] = now.minute();
	valuesToSend[5] = now.second();


	Serial.write(valuesToSend, 6);
}




