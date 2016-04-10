package net.tekpartner.hack4sac.voterregistration;


import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Created with IntelliJ IDEA.
 * User: cgaajula
 * Date: 4/8/16
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class BingTranslator {

    public static void main(String[] args) throws Exception {
        //Replace client_id and client_secret with your own.
        Translate.setClientId("<<<CLIENT_ID>>>");
        Translate.setClientSecret("<<<CLIENT_SECRET>>>");

        // Translate an english string to spanish
        String englishString = "Hello World!";
        String spanishTranslation = Translate.execute(englishString, Language.SPANISH);

        System.out.println("Original english phrase: " + englishString);
        System.out.println("Translated spanish phrase: " + spanishTranslation);
    /*
    OUTPUT:
    Original english phrase: Hello World!
    Translated spanish phrase: ¡Hola mundo!
    */
    }
}
