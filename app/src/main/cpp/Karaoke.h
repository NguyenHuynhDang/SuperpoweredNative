//
// Created by HuynhDang on 25/04/2024.
//

#ifndef SUPERPOWEREDNATIVE_KARAOKE_H
#define SUPERPOWEREDNATIVE_KARAOKE_H

#include <string>
#include <Superpowered.h>
#include <SuperpoweredRecorder.h>
#include <SuperpoweredAdvancedAudioPlayer.h>
#include <OpenSource/SuperpoweredAndroidAudioIO.h>

class Karaoke
{
private:
    SuperpoweredAndroidAudioIO *audioIO;
    Superpowered::AdvancedAudioPlayer *player;
    Superpowered::Recorder *recorder;

public:
    Karaoke(unsigned int samplerate, unsigned int bufferSize, int destinationfd);
    ~Karaoke();
    void openFile(std::string path, int offset, int length);
    bool process(short int* output, unsigned int numFrames, unsigned int samplerate);
    void playBack();
    void stopRecord();
    void clean();
};

static Karaoke* karaoke = nullptr;

#endif //SUPERPOWEREDNATIVE_KARAOKE_H
