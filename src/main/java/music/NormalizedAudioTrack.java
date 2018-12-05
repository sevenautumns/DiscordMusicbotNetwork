package music;

import com.google.api.services.youtube.YouTube;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.OpusAudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.transcoder.AudioChunkDecoder;
import com.sedmelluq.discord.lavaplayer.format.transcoder.AudioChunkEncoder;
import com.sedmelluq.discord.lavaplayer.format.transcoder.OpusChunkDecoder;
import com.sedmelluq.discord.lavaplayer.natives.opus.OpusDecoder;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NormalizedAudioTrack implements AudioTrack {
    private final AudioTrack audioTrack;
    private OpusChunkDecoder decoder;
    private int normalizedVolume;
    private final int targetVolume;
    private static final String infoUri = "http://www.youtube.com/get_video_info?video_id=";

    public NormalizedAudioTrack(AudioTrack audioTrack, int targetVolume){
        this.audioTrack = audioTrack;
        this.targetVolume = targetVolume;
        this.normalizedVolume = targetVolume;

        new Thread(){
            @Override
            public void run() {
                calculateNormalized();
            }
        }.start();
    }

    private synchronized void calculateNormalized(){
        if(!audioTrack.getInfo().uri.contains("youtu")){
            normalizedVolume = targetVolume;
            return;
        }

        String id = audioTrack.getInfo().uri.split("=")[1];
        String in = "";
        URL url = null;
        try {
            url = new URL(infoUri + id);
            Scanner scanner = new Scanner(url.openStream());

            in = scanner.nextLine();
            Pattern pat = Pattern.compile("relative_loudness=(.*?)&");
            Matcher mat = pat.matcher(in);

            if(!mat.find()) throw new Exception("No Loudness");
            float loudness = Float.valueOf(mat.group(1));
            double percent = ((95 + -7.22 * loudness) / 100);
            normalizedVolume = (int) Math.round(percent * targetVolume);
        } catch (Exception e) {
            normalizedVolume = targetVolume;
        }


    }

    public AudioTrack getAudioTrack(){
        return audioTrack;
    }

    public synchronized int getNormalizedVolume(){
        return normalizedVolume;
    }

    @Override
    public AudioTrackInfo getInfo() {
        return audioTrack.getInfo();
    }

    @Override
    public String getIdentifier() {
        return audioTrack.getIdentifier();
    }

    @Override
    public AudioTrackState getState() {
        return audioTrack.getState();
    }

    @Override
    public void stop() {
        audioTrack.stop();
    }

    @Override
    public boolean isSeekable() {
        return audioTrack.isSeekable();
    }

    @Override
    public long getPosition() {
        return audioTrack.getPosition();
    }

    @Override
    public void setPosition(long l) {
        audioTrack.setPosition(l);
    }

    @Override
    public void setMarker(TrackMarker trackMarker) {
        audioTrack.setMarker(trackMarker);
    }

    @Override
    public long getDuration() {
        return audioTrack.getDuration();
    }

    @Override
    public NormalizedAudioTrack makeClone() {
        return new NormalizedAudioTrack(audioTrack.makeClone(), targetVolume);
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return audioTrack.getSourceManager();
    }

    @Override
    public void setUserData(Object o) {
        audioTrack.setUserData(o);
    }

    @Override
    public Object getUserData() {
        return audioTrack.getUserData();
    }

    @Override
    public <T> T getUserData(Class<T> aClass) {
        return audioTrack.getUserData(aClass);
    }
}
