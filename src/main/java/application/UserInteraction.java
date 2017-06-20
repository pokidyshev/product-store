package application;

import utils.ConvertersAndChecks;

import java.util.Arrays;

final class UserInteraction extends UserInteractionBase {

    private UserInteraction() {}


    static UserAction askForUserAction(String prompt, UserAction[] actions) {
        String answer = ask(prompt + "\n--> ");
        return parseAnswer(answer, actions);
    }

    static boolean askYesOrNo(String prompt) {
        final String[] positiveAnswers = {"д","да","y","l","lf","yes"};
        String answer = ask(prompt + "\n--> (д/н)").toLowerCase();
        return Arrays.stream(positiveAnswers).anyMatch((a) -> a.equals(answer));
    }

    static int getInt(String prompt) {
        String s = UserInteraction.ask(prompt);

        while (!ConvertersAndChecks.isNaturalDigitString(s)) {
            UserInteraction.say("Введено некорректное значение!");
            s = UserInteraction.ask(prompt);
        }

        return Integer.parseInt(s);
    }

    static double getDouble(String prompt) {
        String s = UserInteraction.ask(prompt);

        while (!ConvertersAndChecks.isDoubleDigit(s)) {
            UserInteraction.say("Введено некорректное значение!");
            s = UserInteraction.ask(prompt);
        }

        return Double.parseDouble(s);
    }


    private static UserAction parseAnswer(String answer, UserAction[] actions) {
        if (ConvertersAndChecks.isNaturalDigitString(answer)) {
            int actionIndex = Integer.parseInt(answer);
            if (actionIndex < actions.length) {
                return actions[actionIndex];
            }
        }
        return UserAction.WRONG_INPUT;
    }
}