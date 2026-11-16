package view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Centraliza o carregamento e a reprodução dos arquivos WAV usados pela partida.
 * Efeitos curtos ficam prontos antes do primeiro disparo; cada efeito tem vozes
 * alternadas para permitir sons próximos sem reiniciar sempre a mesma reprodução.
 * A trilha, que é maior, é aberta em uma thread de fundo para manter a interface Swing
 * responsiva enquanto o arquivo é carregado.
 */
final class GameAudio {
    private static final int EFFECT_VOICES = 2;
    /** Redução em decibéis aplicada aos efeitos para evitar picos excessivos. */
    private static final float EFFECT_GAIN_DB = -16.0f;
    /** Música em nível moderado para permanecer abaixo dos sons de ação. */
    private static final float MUSIC_GAIN_DB = -10.0f;

    /** Tipos de efeito disponíveis e seus nomes exatos dentro de src/audio. */
    private enum Effect {
        PLAYER_SHOT("tiro personagem.wav"),
        BOSS_SHOT("tiro boss.wav"),
        BOSS_THREE_SHOT("tiro boss3.wav"),
        SPLIT("tiro separado.wav");

        private final String fileName;

        Effect(String fileName) {
            this.fileName = fileName;
        }
    }

    /** Vozes de cada efeito e índice da próxima voz a ser usada em cada grupo. */
    private final Clip[][] effects = new Clip[Effect.values().length][];
    private final int[] nextVoice = new int[Effect.values().length];

    /** Estado sincronizado da carga assíncrona da música e do pedido para mantê-la tocando. */
    private Clip music;
    private boolean musicLoading;
    private boolean musicRequested;

    /** Carrega os efeitos leves ao construir o serviço; a trilha é solicitada separadamente. */
    GameAudio() {
        for (Effect effect : Effect.values()) {
            effects[effect.ordinal()] = loadVoices(effect.fileName);
        }
    }

    /** Reproduz o som de disparo do jogador sem interromper a voz anterior do mesmo efeito. */
    void playPlayerShot() {
        play(Effect.PLAYER_SHOT);
    }

    /**
     * Reproduz o tiro do chefe atual. O terceiro chefe usa o arquivo específico dele;
     * os demais compartilham o efeito genérico de tiro de chefe.
     */
    void playBossShot(boolean thirdBoss) {
        play(thirdBoss ? Effect.BOSS_THREE_SHOT : Effect.BOSS_SHOT);
    }

    /** Reproduz o efeito reservado para o momento em que um projétil se divide. */
    void playSplit() {
        play(Effect.SPLIT);
    }

    /**
     * Solicita que a trilha toque em repetição contínua. A leitura do WAV acontece
     * uma única vez fora da thread de eventos; chamadas repetidas enquanto ela carrega
     * apenas mantêm o pedido ativo e não criam outras threads.
     */
    void startMusic() {
        synchronized (this) {
            musicRequested = true;
            if (music != null) {
                if (!music.isRunning()) {
                    music.setFramePosition(0);
                    music.loop(Clip.LOOP_CONTINUOUSLY);
                }
                return;
            }
            if (musicLoading) {
                return;
            }
            musicLoading = true;
        }

        Thread loader = new Thread(() -> {
            Clip loadedMusic = loadClip("soundtacker POOassault.wav", MUSIC_GAIN_DB);
            synchronized (GameAudio.this) {
                musicLoading = false;
                if (loadedMusic == null) {
                    musicRequested = false;
                    System.err.println("Não foi possível carregar a trilha sonora do jogo.");
                    return;
                }
                music = loadedMusic;
                if (musicRequested) {
                    music.setFramePosition(0);
                    music.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        }, "bullet-hell-music-loader");
        loader.setDaemon(true);
        loader.start();
    }

    /**
     * Para a música se a janela ou a partida quiser suspender o áudio. O estado do pedido
     * também é limpo para impedir que uma carga ainda em andamento inicie a trilha depois.
     */
    synchronized void stopMusic() {
        musicRequested = false;
        if (music != null && music.isRunning()) {
            music.stop();
            music.setFramePosition(0);
        }
    }

    /** Escolhe a próxima voz do efeito, reinicia somente essa voz e inicia sua reprodução. */
    private void play(Effect effect) {
        Clip[] voices = effects[effect.ordinal()];
        if (voices == null || voices.length == 0) {
            return;
        }

        int index = nextVoice[effect.ordinal()];
        Clip voice = voices[index];
        nextVoice[effect.ordinal()] = (index + 1) % voices.length;
        if (voice.isRunning()) {
            voice.stop();
        }
        voice.setFramePosition(0);
        voice.start();
    }

    /** Abre até duas cópias de um efeito para permitir sobreposição de disparos próximos. */
    private Clip[] loadVoices(String fileName) {
        Clip[] loaded = new Clip[EFFECT_VOICES];
        int count = 0;
        for (int voice = 0; voice < EFFECT_VOICES; voice++) {
            Clip clip = loadClip(fileName, EFFECT_GAIN_DB);
            if (clip == null) {
                break;
            }
            loaded[count++] = clip;
        }
        if (count == 0) {
            System.err.println("Efeito sonoro indisponível: " + fileName);
            return new Clip[0];
        }

        Clip[] result = new Clip[count];
        System.arraycopy(loaded, 0, result, 0, count);
        return result;
    }

    /** Carrega um WAV de uma pasta de recursos ou, em execução de desenvolvimento, de src/audio. */
    private Clip loadClip(String fileName, float gainDb) {
        try (InputStream raw = openAudio(fileName);
             BufferedInputStream buffered = new BufferedInputStream(raw);
             AudioInputStream audio = AudioSystem.getAudioInputStream(buffered)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            applyGain(clip, gainDb);
            return clip;
        } catch (Exception ignored) {
            // Áudio indisponível não impede a partida; o chamador simplesmente não toca este som.
            return null;
        }
    }

    /** Aplica a redução de volume respeitando a faixa aceita pelo dispositivo de áudio. */
    private void applyGain(Clip clip, float gainDb) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float adjustedGain = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), gainDb));
            gain.setValue(adjustedGain);
            return;
        }

        // Alguns mixers expõem volume linear em vez de ganho em decibéis.
        if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
            float linearGain = (float) Math.pow(10, gainDb / 20.0);
            float adjustedVolume = volume.getMinimum()
                    + linearGain * (volume.getMaximum() - volume.getMinimum());
            volume.setValue(Math.max(volume.getMinimum(),
                    Math.min(volume.getMaximum(), adjustedVolume)));
        }
    }

    /** Prefere o recurso empacotado; se não existir, abre o WAV no diretório de desenvolvimento. */
    private InputStream openAudio(String fileName) throws IOException {
        InputStream resource = GameAudio.class.getResourceAsStream("/audio/" + fileName);
        if (resource != null) {
            return resource;
        }
        return new FileInputStream(new File("src/audio", fileName));
    }
}
