package com.kirankunigiri.Sprint;

import com.kirankunigiri.Sprint.Interpolators.*;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Kiran Kunigiri
 * Enhanced with additional Sprint animations including rotation and scaling
 *
 * An example controller class to show how you can animate a scene
 * using Sprint. In this scene, elements first animate onto the screen
 * by sliding in. Clicking the animate button will cause them
 * to re-animate in using different interpolators with spinning effects.
 */

public class Controller {

    Sprint sprint;
    boolean titleIsAnimating = true;
    Sprint buttonSprint;
    boolean buttonIsAnimating = false;
    Sprint colorSprint;
    public static Controller instance;
    int interpolatorIndex = 1;

    @FXML
    Button button;
    @FXML
    Label title;
    @FXML
    Label subtitle;
    @FXML
    Circle colorCircle;

    public void initialize() {
        System.out.println("Initialized");
        instance = this;
    }

    public void setup() {
        System.out.println("Scene is displayed!");

        // Initial title animation - slide in with spin
        sprint = new Sprint(title);
        sprint.setInterpolator(new ElasticInterpolator());
        sprint.wait(0.5);
        sprint.slideFromLeft(2).rotateTo(2, 360);

        // Subtitle - slide from right with scaling
        sprint.setNode(subtitle)
                .slideFromRight(2)
                .scaleFrom(2, 0.5, 0.5)
                .wait(0.5)
                .sprint();

        // Button - bounce in from bottom with rotation
        buttonSprint = new Sprint(button);
        buttonSprint.setInterpolator(new BounceInterpolator());
        buttonSprint.wait(0.8)
                .slideFromBottom(1.0)
                .rotateTo(1.0, 360)
                .sprint();
        buttonSprint.setInterpolator(new BackInterpolator());

        // Circle animation - continuous looping background effect
        colorSprint = new Sprint(colorCircle);
        colorSprint.setInterpolator(new SineInterpolator());
        colorSprint.scaleTo(2, 1.3, 1.3)
                .rotateTo(2, 360)
                .loop(0);

        // Animation state listeners
        sprint.isAnimating.addListener((v, oldValue, newValue) -> {
            System.out.println("Title animation state: " + newValue);
            titleIsAnimating = newValue;
        });

        buttonSprint.isAnimating.addListener((v, oldValue, newValue) -> {
            System.out.println("Button animation state: " + newValue);
            buttonIsAnimating = newValue;
        });
    }

    public void buttonClicked() {
        if (!buttonIsAnimating && !titleIsAnimating) {

            // Particle effect stays as-is
            particleExplosion();

            // Next interpolator
            Interpolator newInterpolator = getInterpolator();

            // BUTTON: rotate + pop + quick fade pulse
            buttonSprint.setInterpolator(newInterpolator);
            buttonSprint
                    .rotateTo(1.2, button.getRotate() + 360)  // feel free to use +720 if you prefer
                    .scaleTo(0.18, 1.12, 1.12)
                    .fadeTo(0.18, 0.85)
                    .wait(0.05)
                    .fadeTo(0.18, 1.0)
                    .scaleTo(0.18, 1.0, 1.0)
                    .sprint();

            // TITLES: re-enter with slight parallax + settle
            sprint.setInterpolator(newInterpolator);

            // small randomization for organic feel
            double d = rand(1.1, 1.6);

            // reuse your existing direction pattern
            int direction = interpolatorIndex % 4;

            // title: slide from a side, then settle with two small nudges
            sprint.setNode(title);
            switch (direction) {
                case 1: sprint.slideFromTop(d); break;
                case 2: sprint.slideFromRight(d); break;
                case 3: sprint.slideFromBottom(d); break;
                default: sprint.slideFromLeft(d); break;
            }
            sprint
                    .moveFrom(0.20, -14, 0)
                    .moveFrom(0.14, 8, 0);

            // subtitle: tiny stagger + complementary settle
            sprint.setNode(subtitle)
                    .wait(0.08)
                    .slideFromRight(d * 0.95)
                    .moveFrom(0.20, 14, 0)
                    .moveFrom(0.14, -8, 0)
                    .sprint();

            // optional: restore predictable default for next click
            sprint.setInterpolator(new BackInterpolator(EasingMode.EASE_IN));
        }
    }

    /**
     * Gets the next interpolator from the 9 SprintInterpolators. Each time it is run,
     * the function will return the next interpolator from the list
     * @return The next SprintInterpolator
     */
    public Interpolator getInterpolator() {
        if (interpolatorIndex > 9) {
            interpolatorIndex = 1;
        }
        int num = interpolatorIndex;
        interpolatorIndex++;
        switch (num) {
            case 1: return new BackInterpolator();
            case 2: return new BounceInterpolator();
            case 3: return new CircularInterpolator();
            case 4: return new CubicInterpolator();
            case 5: return new ElasticInterpolator();
            case 6: return new ExponentialInterpolator();
            case 7: return new QuadraticInterpolator();
            case 8: return new QuinticInterpolator();
            case 9: return new SineInterpolator();
            default: return new BounceInterpolator();
        }
    }

    /**
     * Gets a random color for the circle animation
     * @return A random Color
     */
    private Color getRandomColor() {
        Color[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.PURPLE, Color.ORANGE, Color.PINK, Color.CYAN
        };
        int index = (int) (Math.random() * colors.length);
        return colors[index];
    }

    /**
     * Creates a particle explosion effect with 8 particles flying outward
     */
    public void particleExplosion() {
        // Get the scene center coordinates (unused for now, but kept for clarity)
        double centerX = button.getScene().getWidth() / 2;
        double centerY = button.getScene().getHeight() / 2;

        // Create 8 particles in a circle pattern
        for (int i = 0; i < 8; i++) {
            Circle particle = new Circle(10, Color.GOLD);
            particle.setStroke(Color.ORANGE);
            particle.setStrokeWidth(2);

            // Add particle to the scene
            ((VBox) button.getParent()).getChildren().add(particle);

            // Calculate outward direction
            double angle = i * 45; // 360/8 = 45 degrees apart
            double distance = 250;
            double dx = distance * Math.cos(Math.toRadians(angle));
            double dy = distance * Math.sin(Math.toRadians(angle));

            // Animate particle outward with fade
            Sprint particleSprint = new Sprint(particle);
            particleSprint.setInterpolator(new ExponentialInterpolator());
            particleSprint
                    .moveTo(1.5, (int) dx, (int) dy)
                    .fadeTo(1.5, 0.0)
                    .scaleTo(1.5, 2.0, 2.0)
                    .sprint();
        }
    }

    // Tiny helper to randomize durations a bit
    private double rand(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
