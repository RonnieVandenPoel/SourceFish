package SourceFishModel;

import java.util.Date;



public class Checker {
//testklasse 
    public static void main(String[] args) throws Exception {

        String password = "123";
        String passwordEnc = AES.encrypt(password);
        String passwordDec = AES.decrypt(passwordEnc);

        System.out.println("Plain Text : " + password);
        System.out.println("Encrypted Text : " + passwordEnc);
        System.out.println("Decrypted Text : " + passwordDec);
        
        User u = new User("maarten","123");
        if(u.login()){
        	System.out.println("gelukt");
        
        }
        else
        	System.out.println("mislukt");
    Date date= new Date();    //geeft huidige tijd
    System.out.println(date);
    Entry e  = new Entry(date,"test",new Project(13));
    System.out.println(e.getStart());
    //e.create(u);
    }
}
