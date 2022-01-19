package sparta.realm.utils.Mail;
/**
 * Created by Edward Thompson on 19/01/2022.
 */
public class GmailMailBuilder extends MailBuilder {

    public  GmailMailBuilder()
    {
        md.hostAddress="smtp.gmail.com";
        md.port="587";

    }
}
