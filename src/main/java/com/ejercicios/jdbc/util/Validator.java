package com.ejercicios.jdbc.util;

public class Validator {
    public static boolean isNotEmpty(String s) { return s != null && !s.trim().isEmpty(); }
    public static boolean isValidPassword(String p) { return p != null && p.length() >= 8; }
    public static boolean isOnlyLettersAndSpaces(String s) { return s != null && s.matches("[A-Za-zÀ-ÖØ-öø-ÿ ]+"); }
    public static boolean isValidEdad(String s) {
        try { int v = Integer.parseInt(s); return v >=5 && v <=25; } catch(Exception e){return false;}
    }
    public static boolean isValidTelefono(String s) { return s != null && s.matches("\\d{8}"); }
    public static boolean isValidNota(String s) {
        try { double v = Double.parseDouble(s); return v >=0 && v <=10; } catch(Exception e){return false;}
    }
}
