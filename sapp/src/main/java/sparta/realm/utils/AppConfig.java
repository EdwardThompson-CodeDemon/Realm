package sparta.realm.utils;

import android.content.Context;
import android.os.Environment;

import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.spartautils.app_control.models.module;

public class AppConfig {
        public String APP_MAINLINK = "",
                APP_CONTROLL_MAIN_LINK = "",
                ACCOUNT = "",
                ACCOUNT_BRANCH = "",
                AUTHENTICATION_URL = "";
        public boolean SYNC_USE_CAPS = true;
        public boolean print_receipt_on_registration = false;
        public boolean allow_employee_details_edition = false;

        public String app_folder_path = Environment.getExternalStorageDirectory().toString() + "/Realm/";
        public String file_path_db_folder = app_folder_path + ".DB/";
        public String file_path_db_traces = app_folder_path + ".Traces/";
        public String file_path_logs = app_folder_path + ".Logs/";
        public String file_path_app_downloads = app_folder_path + ".RAW_D_APKS/";
        public String file_path_app_uploads = app_folder_path + ".RAW_U_APKS/";

        public String file_path_employee_data = app_folder_path + ".RAW_APP_DATA/";
        public String file_path_db_backup = app_folder_path + ".DB_BACKUPS_RAW/";
        public String file_path_log_backup = app_folder_path + ".LOG_BACKUPS_RAW/";
        public String file_path_general_backup = app_folder_path + ".GN_BACKUPS/";
        //        public String DB_NAME = "live.rdb";
        public String DB_NAME = "android_toolbox.spartadb_v2";
        public String DB_PASS = "XXXXXX";
        public static String verbose_app_log = "log.s_crypt_0";

        //        public String DB_PASS = "000000";
        public String file_path_db(Context cntxt) {
            return cntxt.getExternalFilesDir(null).getAbsolutePath() + "/" + DB_NAME;
        }

        public String file_path_db() {
            return Realm.context.getExternalFilesDir(null).getAbsolutePath() + "/" + DB_NAME;
//            return file_path_db_folder+svars.DB_NAME;
        }

        public enum PROFILE_MODE {
            GENERAL,
            SELF_SERVICE

        }

        public PROFILE_MODE WORKING_PROFILE_MODE = PROFILE_MODE.GENERAL;

        public AppConfig(String APP_MAINLINK, String APP_CONTROLL_MAIN_LINK, String ACCOUNT, String ACCOUNT_BRANCH) {
            this.APP_MAINLINK = APP_MAINLINK;
            this.APP_CONTROLL_MAIN_LINK = APP_CONTROLL_MAIN_LINK;
            this.ACCOUNT = ACCOUNT;
            this.ACCOUNT_BRANCH = ACCOUNT_BRANCH;

        }

        public AppConfig(String APP_MAINLINK, String APP_CONTROLL_MAIN_LINK, String ACCOUNT, String ACCOUNT_BRANCH, String AUTHENTICATION_URL, boolean result_caps) {
            this.APP_MAINLINK = APP_MAINLINK;
            this.APP_CONTROLL_MAIN_LINK = APP_CONTROLL_MAIN_LINK;
            this.ACCOUNT = ACCOUNT;
            this.ACCOUNT_BRANCH = ACCOUNT_BRANCH;
            this.AUTHENTICATION_URL = AUTHENTICATION_URL;
            SYNC_USE_CAPS = result_caps;

        }

        public void set_app_folder() {


        }

        public MODULES WORKING_MODULES = new MODULES();


        public class MODULES {
            public module Registration = new module("00", "Registration", true);

            public ArrayList<module> load_modules() {
                ArrayList<module> modules = new ArrayList<>();

                modules.add(Registration);
                return modules;

            }


        }

        public static class FEATURES {
            public static boolean Biometrics = true;
            public static boolean Employee_data_update = true;
            public static boolean Geo_fencing = true;
            public static boolean Backup = true;


        }
    }
