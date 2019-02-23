/*
 Name:		ADC_Board.ino
 Created:	2/11/2018 05:52:24 PM
 Author:	PiotrD
*/

#include <SPI.h>
#include "AD7192.h"

AD7192 ADC_Arr[3] =
{
	{10,12},	//ADC1
	{7,12},		//ADC2
	{6,12}		//ADC3
};
const byte SYNC = 8;
const byte READY = 9;

byte ReadArr[3];
unsigned long var_arr[3];

unsigned long time;
void setup()
{

	Serial.begin(115200);
	//pins setting
	pinMode(READY, INPUT);
	pinMode(SYNC, OUTPUT);
	digitalWrite(SYNC, HIGH);
	//SPI setting
	SPI.begin();

	for (int i = 0; i < 3; i++)
	{
		ADC_Arr[i].Initialize();

		Serial.print("-----------------ADC numer:");
		Serial.println(i);
		SerialFunction(ADC_Arr[i]);

		ADC_Arr[i].InternalZeroScaleCalibration();
		ADC_Arr[i].InternalFullScaleCalibration();
		ADC_Arr[i].StartConversion();
	}

	delay(10);
	digitalWrite(SYNC, LOW);
	delay(10);
	digitalWrite(SYNC, HIGH);

	time = millis();
}

void loop()
{

	for (int i = 0; i < 3; i++)
	{
		ADC_Arr[i].ReadContinuous(ReadArr);
		var_arr[i] = (B00000000 * 1UL << 24) | (ReadArr[0] * 1UL << 16) | (ReadArr[1] * 1UL << 8) | ReadArr[2] * 1UL;
	}

	Serial.print(millis() - time);
	for (int i = 0; i < 3; i++)
	{
		Serial.print(',');
		Serial.print(var_arr[i]);
	}	
	Serial.println(' ');
	delay(300);
}

void SerialFunction(AD7192 konwerter)
{
	while (1)
	{
		Serial.println("0-kontynuuj; 1-zmien FWord; 2-zmien Gain; 3-zmien Polarity");
		while (Serial.available() == 0) {}
		char input = Serial.read();
		if (input == '0')
		{
			Serial.println("jest 0");
			break;
		}
		if (input == '1')
		{
			int liczba = 0;
			Serial.println("/ konczy wpisywanie");
			while (1)
			{
				while (Serial.available() == 0) {}

				char odczyt = Serial.read();
				if (odczyt == '/')
					break;
				byte cyfra = odczyt - '0';
				liczba = cyfra + liczba * 10;
				Serial.println(liczba);
			}
			konwerter.SetFSWord(liczba);
			Serial.println("Wpisano FSword");
		}
		if (input == '2')
		{
			byte liczba = 0;
			Serial.println("/ konczy wpisywanie");
			while (1)
			{
				while (Serial.available() == 0) {}

				char odczyt = Serial.read();
				if (odczyt == '/')
					break;
				byte cyfra = odczyt - '0';
				liczba = cyfra + liczba * 10;
				Serial.println(liczba);
			}
			konwerter.SetGain(liczba);
			Serial.println("Wpisano Gain");
		}
		if (input == '3')
		{
			Serial.println("0-bipolar, 1-unipolar");
			while (Serial.available() == 0) {}

			char odczyt = Serial.read();
			byte cyfra = odczyt - '0';
			Serial.println(cyfra);
			konwerter.SetPolarity(cyfra);
			Serial.println("Wpisano Polarity");
		}

	}
}