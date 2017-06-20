package utils;

public class ConvertersAndChecks {
    public static boolean isNaturalDigitString(String someNumberAsString) {
        // выход по исключению если пустая строка
        if (someNumberAsString.length() == 0)
            return false;

        // или если какой-то из символов -- не цифры
        for (int i = 0; i < someNumberAsString.length(); ++i){
            if (someNumberAsString.charAt(i) < '0' || someNumberAsString.charAt(i) > '9')
                return false;
        }
        return true;
    }

    public static boolean isDoubleDigit(String s){
        if (s.length() == 0)
            return false;

        int i = 0;
        char currCh = s.charAt(0);
        if (currCh == '-' || currCh == '+') {
            ++i;
        }

        boolean weCanMeetDotYet = true;
        for (; i < s.length(); ++i) {
            char ch = s.charAt(i);

            if (ch != '.' && (ch < '0' || ch > '9'))
                return false;

            if (ch == '.' && !weCanMeetDotYet)
                return false;

            weCanMeetDotYet = ch != '.';
        }

        return true;
    }
}
