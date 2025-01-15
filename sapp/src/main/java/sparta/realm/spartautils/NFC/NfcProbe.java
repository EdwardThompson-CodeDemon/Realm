package sparta.realm.spartautils.NFC;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;




import java.nio.charset.Charset;
import java.util.Locale;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.R;


public class NfcProbe extends SpartaAppCompactActivity {
	//------------------------nfc----------------------------
    public static final String TAG = "NfcScanning";
    private TextView mTextView;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    public static String nfc_uid = null;
    
    ComponentName prev;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


//        setSupportActionBar(toolbar);

		//--------------nfc------------------
        //mTextView = (TextView) findViewById(R.id.textView_explanation);
        
        resolveIntent(getIntent());
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mAdapter = manager.getDefaultAdapter();
//        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
        	Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
               "Message from NFC Reader :-)", Locale.ENGLISH, true) });


    }
	
	
	
    @SuppressLint("NewApi")
	private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }
    
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                dumpTagData(tag);

            }
            // Setup the views
            
            
            
        }
    }

    @SuppressLint("NewApi")
	@Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }
    
    @SuppressLint("NewApi")
	@Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
//            mAdapter.disableForegroundNdefPush(this);
        }
        
        //myKioskActivity.myState = NfcActivity.class;
    }
    
    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Nfc not available");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
              try{  Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);}catch (Exception ex){finish();}
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }
    
    @SuppressLint("NewApi")
	private void dumpTagData(Parcelable p) {

        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        
        
        Log.v("YYYYYYYYYYYY", getHex(id));
        Log.v("YYYYYYYYYYYYYYY", " "+getDec(id));
        Log.v("YYYYYYYYYYYYYYY", " "+getReversed(id));
    	Toast.makeText(this, "NFC ID. "+getHex(id), Toast.LENGTH_LONG).show();
    	
    	//go back to the activity that called
    	
    	nfc_uid = getHex(id);
    	
    	finish();
    	
		
    	
        
    }
    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append("");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }	


}
