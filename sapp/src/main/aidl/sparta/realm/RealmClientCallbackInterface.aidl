// RealmClientCallbackInterface.aidl
package sparta.realm;

// Declare any non-default types here with import statements

interface RealmClientCallbackInterface {


 void on_status_changed(String status);
    void on_info_updated(String status);
    void on_main_percentage_changed(int progress);
    void on_secondary_progress_changed(int progress);
    void onSynchronizationBegun();
    void onServiceSynchronizationCompleted(int service_id);
    void onSynchronizationCompleted();


//         bool OnRequestToSync();
          List<String> OnAboutToUploadObjects(String service_id,in List<String> objects);
//         JSONObject OnUploadingObject(sync_service_description ssd, JSONObject object) ;
//          JSONObject OnUploadedObject(sync_service_description ssd,JSONObject object, JSONObject response) ;
//          ANError OnUploadedObjectError(sync_service_description ssd,JSONObject object, ANError error);
//          Boolean OnAboutToDownload(sync_service_description ssd, JSONObject filter) ;
//          JSONObject OnDownloadedObject(sync_service_description ssd,JSONObject object, JSONObject response);
//          JSONObject OnAuthenticated(String token, JSONObject response);
}