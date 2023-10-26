package invaders.engine;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private boolean easyMode = true;
    private boolean mediumMode = false;
    private boolean hardMode = false;
    private boolean modeJustChanged = false;
    private boolean saving = false;
    private boolean restoring = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();

    private Map<String, MediaPlayer> sounds = new HashMap<>();

    KeyboardInputHandler(GameEngine model) {
        this.model = model;

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if(keyEvent.getCode().equals(KeyCode.R)){
            this.setRestoring(true);

        }

        if(keyEvent.getCode().equals(KeyCode.DIGIT1)){
            //TODO Easy mode
            easyMode = true;
            mediumMode = false;
            hardMode = false;
            modeJustChanged = true;
            System.out.println("just pressed 1");
        }
        if(keyEvent.getCode().equals(KeyCode.DIGIT2)){
            //TODO Medium Mode
            easyMode = false;
            mediumMode = true;
            hardMode = false;
            modeJustChanged = true;
            System.out.println("just pressed 2");
        }
        if(keyEvent.getCode().equals(KeyCode.DIGIT3)){
            //TODO Hard mode
            easyMode = false;
            mediumMode = false;
            hardMode = true;
            modeJustChanged = true;
            System.out.println("just pressed 3");
        }

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            this.setSaving(true);
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }

        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }
    }
    public void setModeJustChanged(boolean bool){
        this.modeJustChanged = bool;
    }
    public boolean getModeJustChanged(){
        return this.modeJustChanged;
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());


        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
    }
    public boolean isSaving(){return this.saving;}
    public boolean isRestoring(){return this.restoring;}
    public void setSaving(boolean saving){
        this.saving = saving;
    }
    public void setRestoring(boolean restoring){
        this.restoring = restoring;
    }
    public boolean isEasyMode(){return this.easyMode;}
    public boolean isMediumMode(){return this.mediumMode;}
    public boolean isHardMode(){return this.hardMode;}

}
