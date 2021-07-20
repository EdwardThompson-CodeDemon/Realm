// RealmClientInterface.aidl
package sparta.realm;

// Declare any non-default types here with import statements

interface RealmClientInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */


    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);


//         bool OnRequestToSync();
//         List<JSONObject> OnAboutToUploadObjects(sync_service_description ssd, ArrayList<JSONObject> objects);
//         JSONObject OnUploadingObject(sync_service_description ssd, JSONObject object) ;
//          JSONObject OnUploadedObject(sync_service_description ssd,JSONObject object, JSONObject response) ;
//          ANError OnUploadedObjectError(sync_service_description ssd,JSONObject object, ANError error);
//          Boolean OnAboutToDownload(sync_service_description ssd, JSONObject filter) ;
//          JSONObject OnDownloadedObject(sync_service_description ssd,JSONObject object, JSONObject response);
//          JSONObject OnAuthenticated(String token, JSONObject response);

              void on_status_code_changed(int status);
                    void on_status_changed(String status);
                    void on_info_updated(String status);
                    void on_main_percentage_changed(int progress);
                    void on_secondary_progress_changed(int progress);
                     void onSynchronizationBegun();
//                     void onSynchronizationCompleted(sync_service_description ssd);
                     void onSynchronizationCompleted();





}