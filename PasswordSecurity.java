import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PasswordSecurity {

    public static void main(String[] args) throws FileNotFoundException{

        //Ask how they want the user to enter username and password 
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your Username: ");
        String userName = scan.next();

        System.out.print("Enter your Password: ");
        String userDefinedPassword = scan.next();
        
        scan.close();
        boolean isValid = false;
        isValid = validatePassword(userDefinedPassword);
        boolean correctUserNameAndPassword = false;
        boolean userExists = false;
        if(isValid){
            //check if user is in the database and if the password is the same 
            File file = new File("Users.txt");
            if(file.length() == 0){
                signUp(userName, userDefinedPassword);
                System.out.println("You are now signed up as a new user!");
            } else {
                //can just check the word userName since passwords are hashed 
                userExists = userExists(userName, "Users.txt");

                if(userExists){
                    //can line by line and check to see if first word matches user name
                    //and second word matches password 
                    correctUserNameAndPassword = login(userName, userDefinedPassword);

                    if(!correctUserNameAndPassword){
                        System.out.println("Incorrect Username or Password ");
                    } else {
                        System.out.println("Successful login!");
                    }
                } else {
                    signUp(userName, userDefinedPassword);
                    System.out.println("You are now signed up as a new user!");
                }
            }
            

        }
        
    }

    public static boolean login(String userName, String password){
        String inputHashedPassword = new String(hashPassword(password));



        try {
            Scanner scan = new Scanner(new File("Users.txt"));
            while(scan.hasNextLine()){
                String[] userNameAndPassword = scan.nextLine().split(" ");
                if(userName.equals(userNameAndPassword[0])){
                    scan.close();
                    return inputHashedPassword.equals(userNameAndPassword[1]);
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        
        return false;
    }

    public static void signUp(String userName, String password){
        String hashedPassword = new String(hashPassword(password));
        try {
            File file = new File("Users.txt");
        
            /* This logic will make sure that the file 
                * gets created if it is not present at the
                * specified location*/
            if (!file.exists()) {
                file.createNewFile();
            }
  
        String myString = userName + " " + hashedPassword;
        PrintWriter pr = new PrintWriter(new FileWriter(file, true));
        pr.append(myString  + "\n");
        pr.close();
  
        } catch (IOException ioe) {
         ioe.printStackTrace();
      }

    }

    public static byte[] hashPassword(String password) {
        byte[] salt = new byte[16];
        byte[] hash = null;
        for (int i = 0; i < 16; i++) {
            salt[i] = (byte) i;
        }
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException nsale) {
            nsale.printStackTrace();

        } catch (InvalidKeySpecException ikse) {
            ikse.printStackTrace();
        }
        return hash;
    }

    


    public static boolean validatePassword(String password) throws FileNotFoundException{
        boolean hasLetter =false;
        boolean hasDigit = false;
        boolean isPopular = false;

        if(password.length() < 8) {
            System.out.println("Password must be atleast 8 characters");
            return false;
        }

        isPopular =  commonPasswords(password, "CommonPasswords.txt");
        if (isPopular) {
            System.out.println("Password cannot be common");
            return false;
        }
        

        for(int i = 0; i < password.length(); i++){
            char x = password.charAt(i);
                if (Character.isLetter(x)) {

                    hasLetter = true;
                }

                else if (Character.isDigit(x)) {

                    hasDigit = true;
                }
        }

        if(hasDigit == false || hasLetter == false) { 
            System.out.println("Password must be atleast include a digit and a letter");
            return false; 
        }


        int repeatedLetters = 0;
        int repeatedDigits = 0; 

        for(int i = 0; i < password.length(); i++){
            char x = password.charAt(i);
            if (Character.isLetter(x)) {
                repeatedLetters++;
                repeatedDigits = 0;
                if(repeatedLetters == 4){
                    System.out.println("Password cannot contain repetitive letters or numbers for"
                    +" four characters or more");
                    return false;
                } 
                
            }

            else if (Character.isDigit(x)) {
                repeatedDigits++;
                repeatedLetters = 0;
                if(repeatedDigits == 4){
                    System.out.println("Password cannot contain repetitive letters or numbers for"
                    +" four characters or more");
                    return false;
                } 
            } else {
                repeatedDigits = 0;
                repeatedLetters = 0;
            }
    
        }

        return true;
    }

    public static boolean commonPasswords(String theWord, String theFile) throws FileNotFoundException {
           return (new Scanner(new File(theFile)).useDelimiter("\\Z").next()).contains(theWord);
    }

    public static boolean userExists(String userName, String theFile){
  
        try{
            Scanner scan = new Scanner(new File(theFile));
            while(scan.hasNext()){

                String[] userNameAndPassword = scan.nextLine().toString().split(" ");
                if(userName.equals(userNameAndPassword[0])) {
                    scan.close();
                    return true;
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }
    
}