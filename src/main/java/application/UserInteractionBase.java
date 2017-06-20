package application;

import java.util.Collection;
import java.util.Scanner;

class UserInteractionBase {

    UserInteractionBase() {}

    static void say(String msg) {
        System.out.println(msg);
    }

    static void say(Collection<String> rows) {
        rows.forEach(UserInteractionBase::say);
    }

    static String ask(String prompt) {
        say(prompt);
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }
}
