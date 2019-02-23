
#include "AD7192.h"
#include <SPI.h>

AD7192::AD7192(byte CS, byte RDYpin)
	:ChipSelect(CS), RDY(RDYpin)
{
	SPISettings MySettings(4000000, MSBFIRST, SPI_MODE3);   //initialize SPIsettings object
	SPIsettingAD7192 = MySettings;

	pinMode(ChipSelect, OUTPUT);				//initialize ADC as turned off
	digitalWrite(ChipSelect, HIGH);
}

byte AD7192::ReadSingle(byte arr[])
{
	byte SendData = B01011000;

	SPI.beginTransaction(SPIsettingAD7192);
	digitalWrite(ChipSelect, LOW);

	SPI.transfer(SendData);
	arr[0] = SPI.transfer(0x00);
	arr[1] = SPI.transfer(0x00);
	arr[2] = SPI.transfer(0x00);
	byte RegisterByte = SPI.transfer(0x00);

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);

	return RegisterByte;
}

byte AD7192::ReadContinuous(byte arr[])
{
	byte RegisterByte;

	SPI.beginTransaction(SPIsettingAD7192);
	digitalWrite(ChipSelect, LOW);

	while (digitalRead(RDY) == 1)           //Wait for DOUT/RDY to go low
	{
		;
	}
	for (int i = 0; i < 3; i++)
		arr[i] = SPI.transfer(0x00);			//read 3 data bits
	RegisterByte = SPI.transfer(0x00);		//read register bit

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);

	return RegisterByte;
}

void AD7192::StartConversion()					//starts continuous read, after this, every other input will be discarded. Use "EndConversion" to return to normal operation
{
	const byte WriteGpconByte = B00101000;
	const byte SetGround = B01000000;
	const byte EnableContRead = B01011100;

	SPI.beginTransaction(SPIsettingAD7192);     //TURN ON ground, set continuous READ mode
	digitalWrite(ChipSelect, LOW);
	SPI.transfer(WriteGpconByte);           //signal that next operation will be write to gpcon
	SPI.transfer(SetGround);				//write 1 to BPDSW bit, activate GND switch

	SPI.transfer(EnableContRead);
	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);
}

void AD7192::EndConversion()
{
	const byte WriteGpconByte = B00101000;
	const byte ClearGround = B00000000;
	const byte DisableContRead = B01011000;

	digitalWrite(ChipSelect, LOW);
	SPI.beginTransaction(SPIsettingAD7192);      

	while (digitalRead(RDY) == 1)           //Wait for DOUT/RDY to go low
	{
		;
	}
	SPI.transfer(DisableContRead);         //Disable continuous conversions

	SPI.transfer(WriteGpconByte);			//disable ground sink
	SPI.transfer(ClearGround);

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);
}

void AD7192::Initialize()
{
	byte ModeArray[3] =
	{
		B00011000,
		B00100000,
		B00010000
	};
	byte ConfArray[3] =
	{
		B10000000,
		B00000010,
		B00011000
	};

	SPI.beginTransaction(SPIsettingAD7192);
	digitalWrite(ChipSelect, LOW);

	for (int i = 0; i < 6; i++)
		SPI.transfer(0xff);           //write 6 one's bytes to reset

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);

	delay(500);

	WriteModeRegister(ModeArray);
	WriteConfigurationRegister(ConfArray);

}

byte AD7192::GetCS()
{
	return ChipSelect;
}

void AD7192::SetGain(byte Gain)
{
	byte GainByte;
	byte array[3];

	switch (Gain)
	{
	case 1:
		GainByte = B00000000;
		break;
	case 8:
		GainByte = B00000011;
		break;
	case 16:
		GainByte = B00000100;
		break;
	case 32:
		GainByte = B00000101;
		break;
	case 64:
		GainByte = B00000110;
		break;
	case 128:
		GainByte = B00000111;
		break;
	default:
		GainByte = B00000000;
		break;
	}

	ReadConfigurationRegister(array);
	array[2] = array[2] & B11111000;  //clear last 3 bits
	array[2] = array[2] | GainByte;

	WriteConfigurationRegister(array);
}

void AD7192::SetPolarity(byte flag)
{
	byte array[3];
	byte WriteByte;
	if (flag == 0)
		WriteByte = B00000000;   //bipolar
	else
		WriteByte = B00001000;  //unipolar

	ReadConfigurationRegister(array);
	array[2] = array[2] & B11110111;   //clear polarity bit
	array[2] = array[2] | WriteByte;
	WriteConfigurationRegister(array);
}

void AD7192::SetFSWord(int FSWord)
{
	unsigned int Word = (unsigned int)FSWord;
	byte low = Word & 0xff;
	byte high = (Word >> 8) & 0xff;
	high = high & B00000011;            //set all bits to 0, except last 2, which holds 2 MSB's of FSWord

	byte array[3];
	ReadModeRegister(array);
	array[1] = array[1] & B11111100;   //clear 2 BITs holding MSB'd of FSWord
	array[1] = array[1] | high;        //merge both, 6 first bits not touched, last 2 changed
	array[2] = low;
	WriteModeRegister(array);
}

void AD7192::ReadModeRegister(byte arr[])
{
	const byte ModeRegisterRead = B01001000;

	ReadRegister(arr, ModeRegisterRead);
}

void AD7192::WriteModeRegister(byte arr[])
{
	const byte ModeRegisterWrite = B00001000;

	WriteRegister(arr, ModeRegisterWrite);
}

void AD7192::ReadConfigurationRegister(byte arr[])
{
	const byte ConfigurationRegisterRead = B01010000;

	ReadRegister(arr, ConfigurationRegisterRead);
}

void AD7192::WriteConfigurationRegister(byte arr[])
{
	const byte ConfigurationRegisterWrite = B00010000;  //initiialize writing data to ConfigurationRegister

	WriteRegister(arr, ConfigurationRegisterWrite);
}

void AD7192::ReadOffset(byte arr[])
{
	const byte OffsetRegisterRead = B01110000;

	ReadRegister(arr, OffsetRegisterRead);
}

void AD7192::ReadScale(byte arr[])
{
	const byte ScaleRegisterRead = B01111000;

	ReadRegister(arr, ScaleRegisterRead);
}

void AD7192::ReadRegister(byte arr[], byte RegisterByte)
{
	SPI.beginTransaction(SPIsettingAD7192);
	digitalWrite(ChipSelect, LOW);

	SPI.transfer(RegisterByte);
	arr[0] = SPI.transfer(0x00);
	arr[1] = SPI.transfer(0x00);
	arr[2] = SPI.transfer(0x00);

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);
}

void AD7192::WriteRegister(byte arr[], byte RegisterByte)
{
	SPI.beginTransaction(SPIsettingAD7192);
	digitalWrite(ChipSelect, LOW);

	SPI.transfer(RegisterByte);
	SPI.transfer(arr[0]);
	SPI.transfer(arr[1]);
	SPI.transfer(arr[2]);

	SPI.endTransaction();
	digitalWrite(ChipSelect, HIGH);
}


void AD7192::InternalZeroScaleCalibration()
{
	byte ScaleByte = B10000000;

	Calibration(ScaleByte);
}

void AD7192::InternalFullScaleCalibration()
{
	byte FullScaleByte = B10100000;
	
	Calibration(FullScaleByte);
}

void AD7192::Calibration(byte CalibrationByte)
{
	byte array[3];
	byte ContinuousConversionByte = B00000000;

	ReadModeRegister(array);
	array[0] = array[0] & B00011111;
	array[0] = array[0] | CalibrationByte;
	WriteModeRegister(array);

	digitalWrite(ChipSelect, LOW);
	while (digitalRead(RDY) == 1)           //Wait for DOUT/RDY to go low
	{
		;
	}

	ReadModeRegister(array);
	array[0] = array[0] & B00011111;        //clear first 3 bytes, leave the rest
	array[0] = array[0] | ContinuousConversionByte;			//change mode to continuous conversion
	WriteModeRegister(array);
}