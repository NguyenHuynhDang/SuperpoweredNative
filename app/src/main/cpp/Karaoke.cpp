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

Karaoke::Karaoke(unsigned int samplerate, unsigned int bufferSize, int destinationfd)
{
    Superpowered::Initialize("ExampleLicenseKey-WillExpire-OnNextUpdate");

    player = new Superpowered::AdvancedAudioPlayer(samplerate, 0);
    recorder = new Superpowered::Recorder(NULL);
    recorder->preparefd(destinationfd, 0, samplerate, true, 10);

    audioIO = new SuperpoweredAndroidAudioIO(samplerate, bufferSize, true, true, audioProcessing,
                                             this);

}

Karaoke::~Karaoke()
{
    delete audioIO; // always delete first
    delete player;
    delete recorder;
}

void Karaoke::openFile(std::string path, int offset, int length)
{
    player->open(path.c_str(), offset, length);
    player->play();
}

bool Karaoke::process(short int *audio, unsigned int numFrames, unsigned int sampleRate)
{
    player->outputSamplerate = sampleRate;

    float outputBuffer[numFrames * 2];

    Superpowered::ShortIntToFloat(audio, outputBuffer, (unsigned int)numFrames);
    recorder->recordInterleaved(outputBuffer, (unsigned int)numFrames);

    if (player->processStereo(outputBuffer, false, (unsigned int)numFrames))
    {
        Superpowered::FloatToShortInt(outputBuffer, audio, (unsigned int)numFrames);
        return true;
    }

    return false;
}

void Karaoke::playBack()
{
    if (player->isPlaying())
        player->pause();
    else player->play();

    Superpowered::CPU::setSustainedPerformanceMode(player->isPlaying()); // prevent dropouts
}

void Karaoke::clean()
{
    delete audioIO;
    delete player;
    delete recorder;
}

void Karaoke::stopRecord()
{
    recorder->stop();
    while (!recorder->isFinished()) usleep(100000); // 0.1 second
}
