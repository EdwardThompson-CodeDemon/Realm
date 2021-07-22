// RealmClientInterface.aidl
package sparta.realm;
import sparta.realm.RealmClientCallbackInterface;

// Declare any non-default types here with import statements

interface RealmClientInterface {


void registerCallback(RealmClientCallbackInterface cb);
void unregisterCallback(RealmClientCallbackInterface cb);
int InitializeClient(String server_ip,int port,String device_code,String username,String password);
void upload(String service_id);
void download(String service_id);
void downloadAll();
void uploadAll();
void synchronize();







}