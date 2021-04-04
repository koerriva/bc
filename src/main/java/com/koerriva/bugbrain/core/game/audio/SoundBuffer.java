package com.koerriva.bugbrain.core.game.audio;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.koerriva.bugbrain.utils.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundBuffer {
    private static final HashMap<String,SoundBuffer> resource = new HashMap<>();
    public final int id;
    private ByteBuffer vorbis;
    private ShortBuffer pcm;

    private SoundBuffer(int id){
        this.id = id;
    }

    public static SoundBuffer load(String filename) throws Exception {
        if(resource.containsKey(filename)){
            return resource.get(filename);
        }
        int bufferId = alGenBuffers();
        SoundBuffer soundBuffer = new SoundBuffer(bufferId);

        try (STBVorbisInfo info = STBVorbisInfo.malloc()){
            soundBuffer.readVorbis("data/sound/"+filename,32 * 1024,info);
            alBufferData(soundBuffer.id,
                    info.channels()==1?AL_FORMAT_MONO16:AL_FORMAT_STEREO16,
                    soundBuffer.pcm,
                    info.sample_rate());
        }
        resource.put(filename,soundBuffer);
        return soundBuffer;
    }

    private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vorbis = ioResourceToByteBuffer(resource, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            pcm = MemoryUtil.memAllocShort(lengthSamples);

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        }
    }

    public static void cleanup(){
        Iterator<Map.Entry<String,SoundBuffer>> iterator = resource.entrySet().iterator();
        while (iterator.hasNext()){
            SoundBuffer buffer = iterator.next().getValue();
            alDeleteBuffers(buffer.id);
            iterator.remove();
        }
    }
}
