#include "audio_engine.h"
#include <cmath>
#include <oboe/Oboe.h>

using namespace oboe;

std::unique_ptr<AudioStream> gStream;
SineWaveEngine gEngine;

DataCallbackResult SineWaveEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    if (!playing_.load(std::memory_order_relaxed)) {
        // Output silence when not playing
        memset(audioData, 0, numFrames * 2 * sizeof(int16_t)); // stereo 16-bit
        return DataCallbackResult::Continue;
    }

    float frequency = frequency_.load(std::memory_order_relaxed);
    auto *out = static_cast<int16_t*>(audioData);

    for (int i = 0; i < numFrames; ++i) {
        // Generate sine wave sample
        float sample = sinf(static_cast<float>(phase_));

        // Advance phase: 2π * frequency * dt
        phase_ += (kTwoPi * frequency) / 48000.0f; // assuming 48kHz sample rate

        // Wrap phase to [0, 2π)
        if (phase_ > kTwoPi) phase_ -= kTwoPi;

        // Convert to 16-bit PCM [-32767, 32767]
        int16_t pcm = static_cast<int16_t>(sample * 32767.0f);

        // Stereo: duplicate to left and right channels
        *out++ = pcm; // left
        *out++ = pcm; // right
    }

    return DataCallbackResult::Continue;
}

extern "C" void nativeInitOboe() {
    gStream = createOboeStream();
    if (gStream) {
        gStream->requestStart();
    }
}

extern "C" void nativeReleaseOboe() {
    if (gStream) {
        gStream->requestStop();
        gStream->close();
    }
    gStream.reset();
}

extern "C" void nativeSetTransport(bool playing) {
    gEngine.playing_.store(playing, std::memory_order_relaxed);
}

extern "C" void nativeSetFrequency(float frequency) {
    gEngine.frequency_.store(frequency, std::memory_order_relaxed);
}

// For debugging if needed
extern "C" oboe::AudioStream *getOboeStream() {
    return gStream.get();
}