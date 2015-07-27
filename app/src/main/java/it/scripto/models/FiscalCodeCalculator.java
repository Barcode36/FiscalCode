package it.scripto.models;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that allow to calculate fiscal code of person.
 * @author pincopallino93
 * @version 1.0
 */
public class FiscalCodeCalculator {

    /**
     * Surname.
     */
    private String surname;

    /**
     * Name.
     */
    private String name;

    /**
     * Birthday in the form dd/MM/yyyy.
     */
    private Date birthday;

    /**
     * Gender (using Enum below).
     */
    private Gender gender;

    /**
     * Belfiore code of the birth place.
     */
    private String birthPlace;

    /**
     * Table of month code.
     */
    @SuppressLint("UseSparseArrays")
    private static Map monthCode = new HashMap<Integer, Character>() {{
        put(1, 'A');
        put(2, 'B');
        put(3, 'C');
        put(4, 'D');
        put(5, 'E');
        put(6, 'H');
        put(7, 'L');
        put(8, 'M');
        put(9, 'P');
        put(10, 'R');
        put(11, 'S');
        put(12, 'T');
    }};

    /**
     * Table of odd character.
     */
    private static Map oddCode = new HashMap<Character, Integer>() {{
        put('0', 1);
        put('1', 0);
        put('2', 5);
        put('3', 7);
        put('4', 9);
        put('5', 13);
        put('6', 15);
        put('7', 17);
        put('8', 19);
        put('9', 21);
        put('A', 1);
        put('B', 0);
        put('C', 5);
        put('D', 7);
        put('E', 9);
        put('F', 13);
        put('G', 15);
        put('H', 17);
        put('I', 19);
        put('J', 21);
        put('K', 2);
        put('L', 4);
        put('M', 18);
        put('N', 20);
        put('O', 11);
        put('P', 3);
        put('Q', 6);
        put('R', 8);
        put('S', 12);
        put('T', 14);
        put('U', 16);
        put('V', 10);
        put('W', 22);
        put('X', 25);
        put('Y', 24);
        put('Z', 23);
    }};

    /**
     * Table of even character.
     */
    private static Map evenCode = new HashMap<Character, Integer>() {{
        put('0', 0);
        put('1', 1);
        put('2', 2);
        put('3', 3);
        put('4', 4);
        put('5', 5);
        put('6', 6);
        put('7', 7);
        put('8', 8);
        put('9', 9);
        put('A', 0);
        put('B', 1);
        put('C', 2);
        put('D', 3);
        put('E', 4);
        put('F', 5);
        put('G', 6);
        put('H', 7);
        put('I', 8);
        put('J', 9);
        put('K', 10);
        put('L', 11);
        put('M', 12);
        put('N', 13);
        put('O', 14);
        put('P', 15);
        put('Q', 16);
        put('R', 17);
        put('S', 18);
        put('T', 19);
        put('U', 20);
        put('V', 21);
        put('W', 22);
        put('X', 23);
        put('Y', 24);
        put('Z', 25);
    }};

    /**
     * Table of remainder.
     */
    @SuppressLint("UseSparseArrays")
    private static Map remainderCode = new HashMap<Integer, Character>() {{
        put(0, 'A');
        put(1, 'B');
        put(2, 'C');
        put(3, 'D');
        put(4, 'E');
        put(5, 'F');
        put(6, 'G');
        put(7, 'H');
        put(8, 'I');
        put(9, 'J');
        put(10, 'K');
        put(11, 'L');
        put(12, 'M');
        put(13, 'N');
        put(14, 'O');
        put(15, 'P');
        put(16, 'Q');
        put(17, 'R');
        put(18, 'S');
        put(19, 'T');
        put(20, 'U');
        put(21, 'V');
        put(22, 'W');
        put(23, 'X');
        put(24, 'Y');
        put(25, 'Z');
    }};

    /**
     * Default constructor with all parameter.
     *
     * @param surname the surname of the person.
     * @param name the name of the person.
     * @param birthday the birthday in the form dd/MM/yyyy of the person.
     * @param gender the gender of the person (use Enum below).
     * @param birthPlace the Belfiore code of the birth place of the person.
     */
    public FiscalCodeCalculator(String surname, String name, Date birthday, Gender gender, String birthPlace) {
        this.surname = surname;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.birthPlace = birthPlace;
    }

    /**
     * This method remove all spaces and consonants from a string.
     *
     * @param string the string to clean.
     * @return all the vocals of the string in uppercase.
     */
    private char[] getVocals(String string) {
        string = string.toUpperCase().replaceAll(" ", "").replaceAll("[^AEIOU]", "");
        return string.toCharArray();
    }

    /**
     * This method remove all spaces and vocals from a string.
     *
     * @param string the string to clean.
     * @return all the consonant of the string in uppercase.
     */
    private char[] getConsonants(String string) {
        string = string.toUpperCase().replaceAll(" ", "").replaceAll("[AEIOU]", "");
        return string.toCharArray();
    }

    /**
     * This method return the first three consonants of the surname. If there is more
     * than one surname, both are considered as if they were one. If the surname has
     * less than three consonants, then vowels will replace the blank spaces, in the
     * same order they appear in the surname (e.g. "Rossi" would be RSS, "Masi" would be MSA).
     * If the whole surname has less than three letters, the blank spaces are replaced
     * with an X (e.g. "Fo" would be FOX, "Hu" would be HUX). If women have a married
     * name, only the maiden one has to be considered.
     *
     * @return the encoded surname.
     */
    private String getEncodedSurname() {

        char[] consonants = this.getConsonants(this.surname);  // Get all consonants
        char[] vocals = this.getVocals(this.surname);  // Get all vocals

        String encodedSurname = "";

        int consonantsLength = consonants.length;  // Get consonants length
        if (consonantsLength >= 3) {  // If there is enough consonants
            for (int i = 0; i < 3; ++i) {
                encodedSurname = encodedSurname + consonants[i];  // Use only them
            }
        } else {  // Otherwise
            int i = 0;
            for (; i < consonantsLength; ++i) {
                encodedSurname = encodedSurname + consonants[i];  // Use all of them
            }
            for (int j = 0; j < vocals.length && j < 3 - i; ++j, ++i) {
                encodedSurname = encodedSurname + vocals[j];  // With all enough vocals
            }
            for (; i < 3; ++i) {
                encodedSurname = encodedSurname + "X";  // Or X
            }
        }

        return encodedSurname;
    }

    /**
     * This method return the first three consonants of the name. If there is more
     * than one name, both are considered as if they were one. If the name has less
     * than three consonants, then vowels will replace the blank spaces, in the same
     * order they appear in the name (e.g. "Marco" would be MRC, "Paola" would be PLA).
     * If the whole name has less than three letters, the blank spaces are filled with
     * an X (e.g. Chinese name "Na" would be NAX). Some Indian immigrants in Italy are
     * registered with a triple X instead of their first name, since their passport only
     * indicates a single word which is used as a surname by the Italian register office.
     * If the name has more than three consonants, the 2nd is skipped (e.g. "Riccardo"
     * would be RCR; "Martina" would be MTN). This second-consonant skipping rule for
     * names that have more than three consonants is only used for first names, not
     * for surnames.
     *
     * @return the encoded name.
     */
    private String getEncodedName() {

        char[] consonants = this.getConsonants(this.name);  // Get all consonants
        char[] vocals = this.getVocals(this.name);  // Get all vocals

        String encodedName = "";

        int consonantsLength = consonants.length;  // Get consonants length
        if (consonantsLength >= 3) {  // If there is enough consonants
            if (consonantsLength >= 4) { // If they are more than four
                for (int i = 0; i < 4; ++i) {
                    if (i != 1) {  // Hop the second one
                        encodedName = encodedName + consonants[i];  // But use only them
                    }
                }
            } else {  // Otherwise
                for (int i = 0; i < 3; ++i) {
                    encodedName = encodedName + consonants[i];  // Use only them
                }
            }
        } else {  // Otherwise
            int i = 0;
            for (; i < consonantsLength; ++i) {
                encodedName = encodedName + consonants[i];  // Use all of them
            }
            for (int j = 0; j < vocals.length && j < 3 - i; ++j, ++i) {
                encodedName = encodedName + vocals[j];  // With all enough vocals
            }
            for (; i < 3; ++i) {
                encodedName = encodedName + "X";  // Or X
            }
        }

        return encodedName;
    }

    /**
     * This method return five characters represent birthday and gender:
     * the last two year of birth digits are used (e.g. "1972" would be 72);
     * each single month is associated with one letter, as shown in the table:
     *
     *  Letter	Month
     *    A	  january
     *    B	  february
     *    C	  march
     *    D	  april
     *    E   may
     *    H   june
     *    L   july
     *    M   august
     *    P   september
     *    R   october
     *    S   november
     *    T   december
     *
     * the two birthday digits are used (from 01 to 31); if the person is a woman,
     * 40 is added (e.g. 01 would be 41, 31 would be 71).
     *
     * @return the encoded birthday.
     */
    private String getEncodedBirthday() {

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");  // Get day into two digit
        int day = Integer.parseInt(dayFormat.format(this.birthday));

        SimpleDateFormat monthFormat = new SimpleDateFormat("M");  // Get month into one digit
        int month = Integer.parseInt(monthFormat.format(this.birthday));

        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");  // Get last two digit of year
        int year = Integer.parseInt(yearFormat.format(this.birthday));

        // Return the string coded with female having +40 to day code
        return String.valueOf(year) + monthCode.get(month) + (this.gender.equals(Gender.MALE) ? String.format("%02d", day) : String.format("%02d", day + 40));
    }

    /**
     * This method starting from the partial fiscal code composed by 15 characters
     * and return a check digit that is determined as follow:
     * the eight odd characters (1st, 3rd, 5th, etc.) are set apart; same thing
     * for the seven even ones (2nd, 4th, 6th, etc.). After that, each single
     * character is converted into a numeric value with according table (see evenCode
     * and oddCode maps).
     *
     * After that, all of the values are to be added up, and the final result
     * has to be divided by 26; the remainder (Modulo) will give the last
     * character, according to the table (see remainderCode map).
     *
     * @param partialFiscalCode the partial code used in order to create check
     *                          digit.
     * @return the check digit.
     */
    private String getCheckCharacter(String partialFiscalCode) {

        char[] fiscalCodeCharArray = partialFiscalCode.toCharArray();  // Get array of character

        int sum = 0;

        for (int i = 0; i < fiscalCodeCharArray.length; i++) {  // Sum all character code
            Character character = fiscalCodeCharArray[i];
            if ((i+1) % 2 == 0) {  // I don't understand why i+1..
                sum = sum + (int) evenCode.get(character);  // If even get code from even
            } else {
                sum = sum + (int) oddCode.get(character);  // Otherwise from odd
            }
        }

        // Return the code after made module 26 to sum of codes
        return (remainderCode.get(sum % 26)).toString();
    }

    /**
     * This public method allow everybody to calculate the fiscal code
     * of a person.
     *
     * @return the fiscal code of the person.
     */
    public String calculate() {
        // Get partial fiscal code
        String partialFiscalCode = this.getEncodedSurname() + this.getEncodedName() + this.getEncodedBirthday() + this.birthPlace;
        // Return fiscal code with check digit calculated from first part
        return partialFiscalCode + this.getCheckCharacter(partialFiscalCode);
    }

    /**
     * Enum for gender of the person.
     */
    public enum Gender {
        MALE,
        FEMALE
    }

    public static void main(String[] args) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date birthDay = new Date();
        try {
            birthDay = simpleDateFormat.parse("18/06/1993");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FiscalCodeCalculator fiscalCodeCalculator = new FiscalCodeCalculator("Pastorini", "Claudio", birthDay, Gender.MALE, "H501");
        System.out.println(fiscalCodeCalculator.calculate());
    }
}
