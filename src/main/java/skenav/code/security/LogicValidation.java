package skenav.code.security;

public class LogicValidation {
    public static boolean intInRange(int input, int lowerbound, int upperbound){
        return (input >= lowerbound) && (input <= upperbound);
    }
    public static boolean searchLengthLimit (String search){
        return (search.length() <= 100);
    }
}
