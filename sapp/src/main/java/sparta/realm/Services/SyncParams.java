package sparta.realm.Services;

public class SyncParams {
    String authenticationurl;
    public SyncParams setAuthenticationUrl(String url)
    {
        authenticationurl=url;
        return this;
    }
}
