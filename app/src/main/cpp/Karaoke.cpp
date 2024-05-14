//
// Created by HuynhDang on 25/04/2024.
//

#include "Karaoke.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <SuperpoweredSimple.h>
#include <SuperpoweredCPU.h>
#include <unistd.h>

bool audioProcessing(void* clientData, short int* audioIO, int numFrames, int sampleRate)
{
    Karaoke* k = (Karaoke*) clientData;
    return k->process(audioIO, (unsigned int)numFrames, (unsigned int)sampleRate);
}

Karaoke::Karaoke(unsigned int samplerate, unsigned int bufferSize)
{
    Superpowered::Initialize("ExampleLicenseKey-WillExpire-OnNextUpdate");

    audioIO = new SuperpoweredAndroidAudioIO(samplerate, bufferSize, true, true, audioProcessing,
                                             this);
    audioIO->start();
}


bool Karaoke::process(short int *audio, unsigned int numFrames, unsigned int sampleRate)
{
//    float outputBuffer[numFrames * 2];
//
//    Superpowered::ShortIntToFloat(audio, outputBuffer, (unsigned int)numFrames);

    return true;
}

void Karaoke::clean()
{
    delete audioIO;
}

void Karaoke::stopRecord()
{
    audioIO->stop();
    clean();
}
