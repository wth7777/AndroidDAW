#pragma once
#include <oboe/Oboe.h>
#include <atomic>

extern "C" {
    void nativeInitOboe();
    void nativeReleaseOboe();
    void nativeSetTransport(bool playing);
    void nativeSetFrequency(float frequency);
}

class SineWaveEngine : public oboe::AudioStreamDataCallback {
public:
    SineWaveEngine() : phase_(0.0), frequency_(440.0), playing_(false) {}

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

private:
    std::atomic<bool> playing_;
    std::atomic<float> frequency_;
    const double kTwoPi = 6.283185307179586476925286766559;
    double phase_; // accumulated phase in radians
};

extern "C" oboe::AudioStream *getOboeStream(); // For debugging if needed