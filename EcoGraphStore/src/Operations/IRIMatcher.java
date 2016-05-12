/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Operations;

/**
 *
 * @author luisdanielibanesgonzalez
 */
public class IRIMatcher {

    /*
     * Adapted from
     * https://gist.github.com/dperini/729294 
     */
    
    final static String iriregex=""
            + "^" +
// protocol identifier\n
"(?:(?:https?|ftp)://)" +
// user:pass authentication\n" +
"(?:\\S+(?::\\S*)?@)?" +
"(?:" +
// IP address exclusion\n" +
// private & local networks\n" +
"(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
"(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
"(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
// IP address dotted notation octets\n" +
// excludes loopback network 0.0.0.0\n" +
// excludes reserved space >= 224.0.0.0\n" +
// excludes network & broacast addresses\n" +
// (first & last IP address of each class)\n" +
"(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
"(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
"(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
"|" +
// host name\n" +
"(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)" +
// domain name\n" +
"(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*" +
// TLD identifier\n" +
"(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" +
")" +
// port number\n" +
"(?::\\d{2,5})?" +
// resource path\n" +
"(?:/[^\\s]*)?";

public static boolean isIRI(String str){
    return str.matches(iriregex);
}

public static boolean isResource(String str){

    return isIRI(str) && !str.contains("#");
}

public static boolean isProperty(String str){

    return isIRI(str) && str.contains("#");
}

public static void main(String [] args){
    System.out.println(isIRI("LITERAL"));
    System.out.println(isIRI("http://www.example.org/"));
    System.out.println(isIRI("http://www.example.org/test2"));
    System.out.println(isIRI("http://www.example.org/test2.html"));

    System.out.println(isProperty("http://www.w3.org/2002/07/owl#sameAs"));
    System.out.println(isResource("http://www.example.org/sameAs"));
}
    
}
