package sparta.realm.utils.FormTools2;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;

import sparta.realm.Activities.SpartaAppCompactFingerPrintActivity;
import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartautils.biometrics.fp.BTV2;
import sparta.realm.spartautils.biometrics.fp.FP08_UAREU;
import sparta.realm.spartautils.biometrics.fp.FingerprintManger;
import sparta.realm.spartautils.biometrics.fp.MorphoFingerprintManager;
import sparta.realm.spartautils.biometrics.fp.SM9XFingerprintManager;
import sparta.realm.spartautils.biometrics.fp.T801;
import sparta.realm.spartautils.biometrics.fp.fp_handler_stf_usb_8_inch;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.FormTools.adapters.FingerprintSkippingReasonAdapter;
import sparta.realm.utils.FormTools.models.Fingerprint;
import sparta.realm.utils.FormTools.models.FingerprintSkippingReason;
import sparta.realm.utils.FormTools.models.FingerprintToCapture;
import sparta.realm.utils.FormTools.models.InputField;
import sparta.realm.utils.FormTools.models.MemberFingerprint;


public class FingerprintCapture extends ConstraintLayout {
    TextView title, instructions;
    Button skipFinger, capture4Left, capture4Right, captureThumbs;
    LinearLayout linearLayout, imagesLinearLayout;
    ImageView visualDisplay, finger1, finger2, finger3, finger4;
    RecyclerView fpGrid;
    ArrayList<FingerprintToCapture> fingerprintsToCapture = new ArrayList<>();
    ArrayList<FingerprintSkippingReason> reasons_to_skip = new ArrayList<>();
    public ArrayList<FingerprintSkippingReason> selectedReasonsToSkip = new ArrayList<>();
    ArrayList<MemberFingerprint> memberFingerprints = new ArrayList<>();
    public int activeFingerprintPosition = 0;

    FingerprintManger fingerprintManger;
    InputField inputField = new InputField();

    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, ArrayList<MemberFingerprint> input) {

        }
    };


    public interface InputListener {
        default void onInputAvailable(boolean valid, ArrayList<MemberFingerprint> input) {

        }


    }

    public FingerprintCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public FingerprintCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();
    }


    void setupUI() {
        title = new TextView(getContext(), null, R.style.TextAppearance_AppCompat_SearchResult_Subtitle);
        title.setText("");
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        visualDisplay = new ImageView(getContext());
        visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.fp_reg_thumb));
        int visualDisplayId = View.generateViewId();
        visualDisplay.setId(visualDisplayId);

        finger1 = new ImageView(getContext());
        int finger1Id = View.generateViewId();
        finger1.setId(finger1Id);

        finger2 = new ImageView(getContext());
        int finger2Id = View.generateViewId();

        finger2.setId(finger2Id);

        finger3 = new ImageView(getContext());
        int finger3Id = View.generateViewId();
        finger3.setId(finger3Id);

        finger4 = new ImageView(getContext());
        int finger4Id = View.generateViewId();
        finger4.setId(finger4Id);

        instructions = new TextView(getContext());
        instructions.setText("Click to capture any finger then place the active finger on the scanner");
        int instructionsId = View.generateViewId();
        instructions.setId(instructionsId);

        fpGrid = new RecyclerView(getContext());
        int fpGridId = View.generateViewId();
        fpGrid.setId(fpGridId);

        skipFinger = new Button(getContext());
        skipFinger.setText(R.string.skip_finger);
        int skipFingerId = View.generateViewId();
        skipFinger.setId(skipFingerId);

        capture4Left = new Button(getContext());
        capture4Left.setText("Capture 4 left");
        int capture4LeftId = View.generateViewId();
        capture4Left.setId(capture4LeftId);
        capture4Left.setBackground(getContext().getDrawable(R.drawable.button_positive));
        capture4Left.setTextColor(getContext().getColor(R.color.white));


        capture4Right = new Button(getContext());
        capture4Right.setText("Capture 4 right");
        int capture4RightId = View.generateViewId();
        capture4Right.setId(capture4RightId);
        capture4Right.setBackground(getContext().getDrawable(R.drawable.button_positive));
        capture4Right.setTextColor(getContext().getColor(R.color.white));


        captureThumbs = new Button(getContext());
        captureThumbs.setText("Capture thumbs");
        int captureThumbsId = View.generateViewId();
        captureThumbs.setId(captureThumbsId);
        captureThumbs.setBackground(getContext().getDrawable(R.drawable.button_positive));
        captureThumbs.setTextColor(getContext().getColor(R.color.white));


        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        int linearLayoutId = View.generateViewId();
        linearLayout.setId(linearLayoutId);

        imagesLinearLayout = new LinearLayout(getContext());
        imagesLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        imagesLinearLayout.setWeightSum(4);
        int imagesLinearLayoutId = View.generateViewId();
        imagesLinearLayout.setId(imagesLinearLayoutId);


        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.endToEnd = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);


        LayoutParams visualDisplayParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(captureMode==singlecaptureMode?300:150));
        visualDisplayParams.topToBottom = title_id;
        visualDisplayParams.startToStart = PARENT_ID;
        visualDisplayParams.endToEnd = PARENT_ID;
        visualDisplayParams.bottomMargin = 0;
        addView(visualDisplay, visualDisplayParams);


        LayoutParams instructionsParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instructionsParams.topToBottom = visualDisplayId;
        instructionsParams.startToStart = PARENT_ID;
        instructionsParams.endToEnd = PARENT_ID;
        instructionsParams.rightMargin = dpToPx(32);
        instructionsParams.leftMargin = dpToPx(32);
        addView(instructions, instructionsParams);


        LayoutParams skipFingerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        skipFingerParams.topToTop = PARENT_ID;
        skipFingerParams.endToEnd = PARENT_ID;
        skipFingerParams.rightMargin = dpToPx(16);
        addView(skipFinger, skipFingerParams);

        LayoutParams imageLinearLayoutParams = new LayoutParams(0, dpToPx(100));
        imageLinearLayoutParams.topToBottom = instructionsId;
        imageLinearLayoutParams.startToStart = PARENT_ID;
        imageLinearLayoutParams.endToEnd = PARENT_ID;
        addView(imagesLinearLayout, imageLinearLayoutParams);

        LinearLayout.LayoutParams finger1Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        finger1Params.weight = 1;
        finger1Params.leftMargin=dpToPx(10);
        finger1Params.rightMargin=dpToPx(10);
        imagesLinearLayout.addView(finger1, finger1Params);

        LinearLayout.LayoutParams finger2Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        finger2Params.weight = 1;
        finger2Params.leftMargin=dpToPx(10);
        finger2Params.rightMargin=dpToPx(10);
        imagesLinearLayout.addView(finger2, finger2Params);

        LinearLayout.LayoutParams finger3Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        finger3Params.weight = 1;
        finger3Params.leftMargin=dpToPx(10);
        finger3Params.rightMargin=dpToPx(10);
        imagesLinearLayout.addView(finger3, finger3Params);

        LinearLayout.LayoutParams finger4Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        finger4Params.weight = 1;
        finger4Params.leftMargin=dpToPx(10);
        finger4Params.rightMargin=dpToPx(10);
        imagesLinearLayout.addView(finger4, finger4Params);


        LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.topToBottom = imagesLinearLayoutId;
        linearLayoutParams.startToStart = PARENT_ID;
        linearLayoutParams.endToEnd = PARENT_ID;
        addView(linearLayout, linearLayoutParams);

        LayoutParams fpGridParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fpGridParams.topToBottom = linearLayoutId;
        fpGridParams.startToStart = PARENT_ID;
        fpGridParams.endToEnd = PARENT_ID;
        fpGridParams.bottomMargin = 0;
        addView(fpGrid, fpGridParams);

        LinearLayout.LayoutParams capture4LeftParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        capture4LeftParams.weight = 1;
        linearLayout.addView(capture4Left, capture4LeftParams);

        LinearLayout.LayoutParams capture4RightParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        capture4RightParams.weight = 1;
        linearLayout.addView(capture4Right, capture4RightParams);

        LinearLayout.LayoutParams captureThumbsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        captureThumbsParams.weight = 1;
        linearLayout.addView(captureThumbs, captureThumbsParams);

        capture4Left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.left_4));
                instructions.setText("Capturing 4 left ...");
                activeFingerprintPosition = 0;
                clearImages();
                fingerprintManger.capture4left();
            }
        });

        capture4Right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.right4));
                instructions.setText("Capturing 4 right ...");
                activeFingerprintPosition = 4;
                clearImages();
                fingerprintManger.capture4right();
            }
        });

        captureThumbs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.thumbs));
                instructions.setText("Capturing thumbs ...");
                activeFingerprintPosition = 8;
                clearImages();
                fingerprintManger.captureThumbs();

            }
        });


        setFingerprintsToCapture(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        skipFinger.setBackground(getContext().getDrawable(R.drawable.button_positive));
       // imagesLinearLayout.setBackground(getContext().getDrawable(R.drawable.button_positive));
       // finger1.setBackground(getContext().getDrawable(R.drawable.button_positive));
      //  finger2.setBackground(getContext().getDrawable(R.drawable.button_negative));
      //  finger3.setBackground(getContext().getDrawable(R.drawable.button_negative));
     //   finger4.setBackground(getContext().getDrawable(R.drawable.button_positive));
        skipFinger.setTextColor(getContext().getColor(R.color.white));
        int padding = dpToPx(10);
        skipFinger.setPadding(padding, dpToPx(5), padding, dpToPx(5));
        fpGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fpGrid.setAdapter(new FingerprintToCaptureAdapter(fingerprintsToCapture, (fingerprint, view, position) -> {
            if(captureMode==singlecaptureMode){
                for (FingerprintToCapture fp : fingerprintsToCapture) {
                    fp.capturing = false;
                }
                fingerprintsToCapture.get(position).wsq = null;
                fingerprintsToCapture.get(position).jpeg = null;
                fingerprintsToCapture.get(position).iso = null;
                setActiveFingerprint(position);
//                if (fingerprintManger instanceof MorphoFingerprintManagerX) {
//                    fingerprintManger.capture();
//                }

            }else{
                if(position==0||position==5){//thumbs

                    visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.thumbs));
                    instructions.setText("Capturing thumbs ...");
                    activeFingerprintPosition = 8;
                    clearImages();
                    fingerprintManger.captureThumbs();
                }else if(position==9||position==8||position==7||position==6){//4 left
                    visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.left_4));
                    instructions.setText("Capturing 4 left ...");
                    activeFingerprintPosition = 0;
                    clearImages();
                    fingerprintManger.capture4left();

                }else{
                    visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.right4));
                    instructions.setText("Capturing 4 right ...");
                    activeFingerprintPosition = 4;
                    clearImages();
                    fingerprintManger.capture4right();

                }

            }

//            fingerprintManger.capture();
        }));
        skipFinger.setOnClickListener(view -> skipCurrentActiveFingerprint());
        setActiveFingerprint(0);
        if (captureMode == captureMode442) {
            capture4Left.setVisibility(VISIBLE);
            capture4Right.setVisibility(VISIBLE);
            captureThumbs.setVisibility(VISIBLE);
            fpGrid.setVisibility(VISIBLE);
            skipFinger.setVisibility(GONE);
            instructions.setVisibility(VISIBLE);
            linearLayout.setVisibility(VISIBLE);
            imagesLinearLayout.setVisibility(VISIBLE);

        } else {
            fpGrid.setVisibility(VISIBLE);
            capture4Left.setVisibility(GONE);
            capture4Right.setVisibility(GONE);
            captureThumbs.setVisibility(GONE);
            linearLayout.setVisibility(GONE);
            imagesLinearLayout.setVisibility(GONE);

        }
    }
    public static int[] convertCsvToIntArray(String input) {
        if (input == null || input.isEmpty()) return new int[0];

        String[] parts = input.split(",");
        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }

        return result;
    }

    public void changeStatus(String statusCode) {
        if (statusCode.equals("1")) {
//             activeFingerprintPosition++;
            if (activeFingerprintPosition == 1) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.left_4));
                instructions.setText("Capturing 4 left ...");
                activeFingerprintPosition = 0;
                fingerprintManger.capture4left();
            } else if (activeFingerprintPosition == 0) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.right4));
                instructions.setText("Capturing 4 right ...");
                activeFingerprintPosition = 4;
                fingerprintManger.capture4right();
            } else if (activeFingerprintPosition == 4) {
                visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.thumbs));
                instructions.setText("Capturing thumbs ...");
                activeFingerprintPosition = 8;
                fingerprintManger.captureThumbs();
            } else {
                instructions.setText("Captured all.\nClick on the relevant buttons to capture relevant fingerprints ");


            }
        } else {
            instructions.setText("Failed.\nClick on the relevant buttons to re-capture relevant fingerprints ");
//            activeFingerprintPosition++;


        }

    }

    public void setTitle(String title_) {
        this.title.setText(title_);
    }

    public void setInstructions(String instructions) {
        this.instructions.setText(instructions);
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;

        setTitle(inputField.title);
        setInstructions(inputField.instructions);
        setFingerprintsToCapture(convertCsvToIntArray(inputField.dataset));
        populateFingerprintToCapture(inputField.fingerprintsInput.fingerprintsInput);
        inputField.inputValid = validated();
        reasons_to_skip.clear();
        reasons_to_skip.add(0, new FingerprintSkippingReason("1", getContext().getString(R.string.finger_is_missing)));
        reasons_to_skip.add(0, new FingerprintSkippingReason("2", getContext().getString(R.string.finger_is_unreadable)));
        reasons_to_skip.add(0, new FingerprintSkippingReason("0", getContext().getString(R.string.select_reason_to_skip)));
    }

    public boolean isInputValid() {
        return validated();
    }

    boolean validated() {
        boolean valid = true;
        // if(1==1){return valid;}
        for (FingerprintToCapture fingerprint : fingerprintsToCapture) {
            if ((fingerprint.wsq == null || fingerprint.jpeg == null || fingerprint.iso == null || fingerprint.wsq.length() < 5) && checkFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index) == false) {
                Toast.makeText(getContext(), getContext().getString(R.string.finerprint_missing) + fingerprint.name, Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return valid;
    }

    public void populateFingerprintToCapture(ArrayList<MemberFingerprint> fingerprintsInput) {
        for (Fingerprint fingerprint : fingerprintsInput) {
            for (FingerprintToCapture fingerprintToCapture : fingerprintsToCapture) {
                if (fingerprint.template != null && fingerprint.fingerprint_index.equals((fingerprintToCapture.index + 1) + "")) {
                    switch (fingerprint.template_format) {
                        case "1":
                            fingerprintToCapture.wsq = fingerprint.template;

                            break;
                        case "2":
                            fingerprintToCapture.jpeg = fingerprint.template;

                            break;
                        case "3":
                            fingerprintToCapture.iso = fingerprint.template;

                            break;


                    }

                }

            }
        }
        memberFingerprints.clear();
        for (FingerprintToCapture fingerprintToCapture : fingerprintsToCapture) {
            MemberFingerprint memberFingerprintWsq = new MemberFingerprint(FingerprintDataType.WSQ.ordinal() + "", fingerprintToCapture.wsq, (fingerprintToCapture.index + 1) + "", null);
            MemberFingerprint memberFingerprintImg = new MemberFingerprint(FingerprintDataType.JPEG.ordinal() + "", fingerprintToCapture.jpeg, (fingerprintToCapture.index + 1) + "", null);
            MemberFingerprint memberFingerprintIso = new MemberFingerprint(FingerprintDataType.ISO.ordinal() + "", fingerprintToCapture.iso, (fingerprintToCapture.index + 1) + "", null);


            for (MemberFingerprint fingerprint : fingerprintsInput) {
                if (fingerprint.template != null && fingerprint.fingerprint_index.equals(fingerprintToCapture.index + "")) {
                    switch (fingerprint.template_format) {
                        case "1":
                            memberFingerprintWsq.template = fingerprint.template;
                            memberFingerprintWsq.member_transaction_no = fingerprint.member_transaction_no;
                            break;
                        case "2":
                            memberFingerprintImg.template = fingerprint.template;
                            memberFingerprintWsq.member_transaction_no = fingerprint.member_transaction_no;

                            break;
                        case "3":
                            memberFingerprintIso.template = fingerprint.template;
                            memberFingerprintWsq.member_transaction_no = fingerprint.member_transaction_no;

                            break;


                    }
                }
            }
            if (memberFingerprintWsq.template != null) {
                memberFingerprints.add(memberFingerprintWsq);
            }
            if (fingerprintToCapture.jpeg != null) {
                memberFingerprints.add(memberFingerprintImg);
            }
            if (fingerprintToCapture.iso != null) {
                memberFingerprints.add(memberFingerprintIso);
            }
        }

    }


    public void initFingerprint(Activity activity) {
        fingerprintManger = getDeviceFingerprintManger(activity);
        if (!(fingerprintManger instanceof MorphoFingerprintManager) || (1 - 1) == 0) {
//        if (!(fingerprintManger instanceof SM442FingerprintManager)) {
            ((SpartaAppCompactFingerPrintActivity) activity).startFPModule(fingerprintManger);
        }
        if (captureMode == captureMode442) {
            visualDisplay.setImageDrawable(getContext().getDrawable(R.drawable.left_4));
            instructions.setText("Capturing 4 left ...");
            activeFingerprintPosition = 0;
            fingerprintManger.capture4left();
        }

    }


//    FingerprintManger getFingerprintManger(Activity activity) {
//        ArrayList<String> uareu_devices = new ArrayList<>();
//        ArrayList<String> t801_devices = new ArrayList<>();
//        ArrayList<String> famoco_devices = new ArrayList<>();
//        ArrayList<String> sm9x_device = new ArrayList<>();
//        uareu_devices.add("SF807N");
//        uareu_devices.add("F807");
//        uareu_devices.add("FP-08");
//        uareu_devices.add("SF-08");
//        uareu_devices.add("SF-807");
//        uareu_devices.add("S807");
//        uareu_devices.add("ax6737_65_n");
//        uareu_devices.add("SF-807N");
//        t801_devices.add("SEEA900");
//        t801_devices.add("SPC_08101");
//        famoco_devices.add("FX205");
//        famoco_devices.add("FP200,1");
//        famoco_devices.add("FX205");
//        famoco_devices.add("FP200");
//        sm9x_device.add("SF-819");
//        if (uareu_devices.contains(Build.MODEL)) return new FP08_UAREU(activity);
//        if (t801_devices.contains(Build.MODEL)) return new T801(activity);

    /// /        if(famoco_devices.contains(Build.MODEL))return new MorphoFPManager(activity);
//        if (famoco_devices.contains(Build.MODEL)) return new MorphoFingerprintManager(activity);
//        if (sm9x_device.contains(Build.MODEL)) return new SM9XFingerprintManager(activity);
//
//        return new BTV2(activity);
//    }
    public static FingerprintManger getDeviceFingerprintManger(Activity activity) {
        ArrayList<String> uareu_devices = new ArrayList<>();
        ArrayList<String> t801_devices = new ArrayList<>();
        ArrayList<String> famoco_devices = new ArrayList<>();
        ArrayList<String> sm9x_device = new ArrayList<>();
        ArrayList<String> ts450_biomini_device = new ArrayList<>();
        uareu_devices.add("SF807N");
        uareu_devices.add("F807");
        uareu_devices.add("FP-08");
        uareu_devices.add("SF-08");
        uareu_devices.add("SF-807");
        uareu_devices.add("S807");
        uareu_devices.add("ax6737_65_n");
        uareu_devices.add("SF-807N");
        t801_devices.add("SEEA900");
        t801_devices.add("SPC_08101");
        famoco_devices.add("FP200,1");
        famoco_devices.add("FX205");
        sm9x_device.add("SF-819");
        ts450_biomini_device.add("P8100");
        ts450_biomini_device.add("P8100 4G");
//        captureMode=singlecaptureMode;
        captureMode = singlecaptureMode;
        if (uareu_devices.contains(Build.MODEL)) return new FP08_UAREU(activity);
        if (t801_devices.contains(Build.MODEL)) return new T801(activity);
//        if (famoco_devices.contains(Build.MODEL)) return new MorphoFingerprintManagerX(activity);
        if (sm9x_device.contains(Build.MODEL)) return new SM9XFingerprintManager(activity);
//        if (ts450_biomini_device.contains(Build.MODEL))
//            return new Biominiv3_0_3_3FingerprintManager2(activity);

//        if (sm442FingerprintManager == null) {
//            sm442FingerprintManager = new SM442FingerprintManager(activity);
//            sm442FingerprintManager.start();
//        } else {
//            sm442FingerprintManager.interf = (sfp_i) activity;
//        }
//        captureMode = captureMode442;
//        return sm442FingerprintManager;

        return new BTV2(activity);
    }

//    public static SM442FingerprintManager sm442FingerprintManager;

    public enum FingerprintDataType {
        None,

        WSQ,
        JPEG,
        ISO


    }

    public void setData(ArrayList<MemberFingerprint> memberFingerprints) {

        this.memberFingerprints.clear();
        this.memberFingerprints.addAll(memberFingerprints);
//        this.memberFingerprints=memberFingerprints;
        fpGrid.getAdapter().notifyDataSetChanged();

    }

    public boolean fingerDoesNotExist(String iso, int activeFingerprintPosition) {
        for (FingerprintToCapture fingerprintexisting : fingerprintsToCapture) {
            if (fingerprintexisting.equals(fingerprintsToCapture.get(activeFingerprintPosition)))
                continue;
            try {
                if (fingerprintexisting.iso != null && fp_handler_stf_usb_8_inch.match(iso, fingerprintexisting.iso) < 500) {

                    Toast.makeText(getContext(), fingerprintexisting.name + " " + getContext().getString(R.string.has_already_been_captured), Toast.LENGTH_LONG).show();

                    Log.e("FPCAPTURE", "Finger exists:: " + fp_handler_stf_usb_8_inch.match(iso, fingerprintexisting.iso) + "::" + iso + " \n" + fingerprintexisting.iso);

                    return false;
                }
            } finally {

            }

        }
        return true;
    }

    boolean move = true;
    void clearImages(){

        finger1.setImageURI(null);
        finger2.setImageURI(null);
        finger3.setImageURI(null);
        finger4.setImageURI(null);

    }

    public void setData(FingerprintDataType fingerprintDataType, String data, int position) {

        FingerprintToCapture fingerprint = fingerprintsToCapture.get(position);
        switch (fingerprintDataType) {
            case WSQ:
                fingerprint.wsq = data;
                Log.e("FPCAPTURE", "setData:: " + fingerprint.name + " wsq");
                break;
            case JPEG:
                fingerprint.jpeg = data;
                Uri uri = Uri.fromFile(new File(svars.current_app_config(Realm.context).appDataFolder + data));

                if(position==9||position==5||position==1){
                    try{

                        finger1.setImageURI(uri);
                    }catch (Exception exception){


                    }
                }else if(position==8||position==2){

                    try{

                        finger2.setImageURI(uri);
                    }catch (Exception exception){


                    }
                }else if(position==7||position==3){

                    try{

                        finger3.setImageURI(uri);
                    }catch (Exception exception){


                    }
                }else{

                    try{

                        finger4.setImageURI(uri);
                    }catch (Exception exception){


                    }
                }

                break;
            case ISO:
                fingerprint.iso = data;
                Log.e("FPCAPTURE", "setData:: " + fingerprint.name + " iso");

                break;

        }

        removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index);
        fingerprint.skipped = false;
        if ((fingerprint.wsq == null || fingerprint.jpeg == null || fingerprint.iso == null || fingerprint.wsq.length() < 10) && checkFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index) == false) {

        } else {
            if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1) && ((System.currentTimeMillis() - lastTimeAutoMovedToNext) >= minTimeAutoMovedToNext)) {
                lastTimeAutoMovedToNext = System.currentTimeMillis();
                if (fingerDoesNotExist(fingerprint.iso, position)) {
//                    setActiveFingerprint(activeFingerprintPosition + 1);


                } else {
                    move = false;
//                    setActiveFingerprint(activeFingerprintPosition - 1);
                    fingerprint.jpeg = null;
                    fingerprint.wsq = null;
                    fingerprint.iso = null;
                    return;
                }

            } else if (activeFingerprintPosition == (fingerprintsToCapture.size() - 1)) {
//                procceed();

            }
            memberFingerprints.clear();
            for (FingerprintToCapture fingerprintToCapture : fingerprintsToCapture) {
                MemberFingerprint memberFingerprintWsq = new MemberFingerprint(FingerprintDataType.WSQ.ordinal() + "", fingerprintToCapture.wsq, (fingerprintToCapture.index + 1) + "", null);
                MemberFingerprint memberFingerprintImg = new MemberFingerprint(FingerprintDataType.JPEG.ordinal() + "", fingerprintToCapture.jpeg, (fingerprintToCapture.index + 1) + "", null);
                MemberFingerprint memberFingerprintIso = new MemberFingerprint(FingerprintDataType.ISO.ordinal() + "", fingerprintToCapture.iso, (fingerprintToCapture.index + 1) + "", null);


                if (memberFingerprintWsq.template != null) {
                    memberFingerprints.add(memberFingerprintWsq);
                }
                if (fingerprintToCapture.jpeg != null) {
                    memberFingerprints.add(memberFingerprintImg);
                }
                if (fingerprintToCapture.iso != null) {
                    memberFingerprints.add(memberFingerprintIso);
                }
            }
            inputField.inputValid = validated();
            inputField.fingerprintsInput.fingerprintsInput = memberFingerprints;
            inputListener.onInputAvailable(inputField.inputValid, memberFingerprints);
            fpGrid.getAdapter().notifyDataSetChanged();

        }


    }

    public void setData(FingerprintDataType fingerprintDataType, String data) {

        FingerprintToCapture fingerprint = fingerprintsToCapture.get(activeFingerprintPosition);
        switch (fingerprintDataType) {
            case WSQ:
                fingerprint.wsq = data;
                Log.e("FPCAPTURE", "setData:: " + fingerprint.name + " wsq");
                break;
            case JPEG:
                fingerprint.jpeg = data;

                break;
            case ISO:
                fingerprint.iso = data;
                Log.e("FPCAPTURE", "setData:: " + fingerprint.name + " iso");

                break;

        }

        removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index);
        fingerprint.skipped = false;
        if ((fingerprint.wsq == null || fingerprint.jpeg == null || fingerprint.iso == null || fingerprint.wsq.length() < 10) && checkFingerprintSkippingReason(selectedReasonsToSkip, fingerprint.index) == false) {

        } else {
            if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1) && ((System.currentTimeMillis() - lastTimeAutoMovedToNext) >= minTimeAutoMovedToNext)) {
                lastTimeAutoMovedToNext = System.currentTimeMillis();
                if (fingerDoesNotExist(fingerprint.iso, activeFingerprintPosition)) {
                    setActiveFingerprint(activeFingerprintPosition + 1);


                } else {
                    move = false;
//                    setActiveFingerprint(activeFingerprintPosition - 1);
                    fingerprint.jpeg = null;
                    fingerprint.wsq = null;
                    fingerprint.iso = null;
                    return;
                }

            } else if (activeFingerprintPosition == (fingerprintsToCapture.size() - 1)) {
//                procceed();

            }
            memberFingerprints.clear();
            for (FingerprintToCapture fingerprintToCapture : fingerprintsToCapture) {
                MemberFingerprint memberFingerprintWsq = new MemberFingerprint(FingerprintDataType.WSQ.ordinal() + "", fingerprintToCapture.wsq, (fingerprintToCapture.index + 1) + "", null);
                MemberFingerprint memberFingerprintImg = new MemberFingerprint(FingerprintDataType.JPEG.ordinal() + "", fingerprintToCapture.jpeg, (fingerprintToCapture.index + 1) + "", null);
                MemberFingerprint memberFingerprintIso = new MemberFingerprint(FingerprintDataType.ISO.ordinal() + "", fingerprintToCapture.iso, (fingerprintToCapture.index + 1) + "", null);


                if (memberFingerprintWsq.template != null) {
                    memberFingerprints.add(memberFingerprintWsq);
                }
                if (fingerprintToCapture.jpeg != null) {
                    memberFingerprints.add(memberFingerprintImg);
                }
                if (fingerprintToCapture.iso != null) {
                    memberFingerprints.add(memberFingerprintIso);
                }
            }
            inputField.inputValid = validated();
            inputField.fingerprintsInput.fingerprintsInput = memberFingerprints;
            inputListener.onInputAvailable(inputField.inputValid, memberFingerprints);
            fpGrid.getAdapter().notifyDataSetChanged();

        }


    }

    long lastTimeAutoMovedToNext = 0;//7004205 outsome
    long minTimeAutoMovedToNext = 500;

    void setActiveFingerprint(int position) {
        Log.e("FPCAPTURE", "setActiveFingerprint:: " + position);
        fingerprintsToCapture.get(position > 0 ? position - 1 : fingerprintsToCapture.size() - 1).capturing = false;
        activeFingerprintPosition = position;
        visualDisplay.setImageDrawable(getContext().getDrawable(fingerprintsToCapture.get(position).drawable_resource));
        fingerprintsToCapture.get(position).capturing = true;
        fpGrid.getAdapter().notifyDataSetChanged();
        if (fingerprintManger instanceof BTV2 && !validated()) {

            fingerprintManger.capture();
        }
    }

    static final int singlecaptureMode = 1;
    static final int captureMode442 = 2;
    static int captureMode = singlecaptureMode;

    void setFingerprintsToCapture(int[] fps_to_capture) {
//        if (captureMode == singlecaptureMode) {
//
//            for (int i = 0; i < fps_to_capture.length; i++) {
//                switch (fps_to_capture[i]) {
//                    case 0:
//                        FingerprintToCapture left4 = new FingerprintToCapture(0, R.drawable.fp_reg_thumb_right, "4 left");
//                        fingerprintsToCapture.add(left4);
//                        break;
//                    case 1:
//                        FingerprintToCapture right4 = new FingerprintToCapture(1, R.drawable.fp_reg_index_right, "4 right");
//                        fingerprintsToCapture.add(right4);
//                        break;
//                    case 2:
//                        FingerprintToCapture thumbs = new FingerprintToCapture(2, R.drawable.fp_reg_middle_right,"2 thumbs");
//                        fingerprintsToCapture.add(thumbs);
//                        break;
//                }
//            }
//        } else
        {
            fingerprintsToCapture.clear();
            for (int i = 0; i < fps_to_capture.length; i++) {
                switch (fps_to_capture[i]) {
                    case 0:
                        FingerprintToCapture right_thumb = new FingerprintToCapture(0, R.drawable.fp_reg_thumb_right, getContext().getString(R.string.right_thumb));
                        fingerprintsToCapture.add(right_thumb);
                        break;
                    case 1:
                        FingerprintToCapture right_index = new FingerprintToCapture(1, R.drawable.fp_reg_index_right, getContext().getString(R.string.right_index));
                        fingerprintsToCapture.add(right_index);
                        break;
                    case 2:
                        FingerprintToCapture right_middle = new FingerprintToCapture(2, R.drawable.fp_reg_middle_right, getContext().getString(R.string.right_middle));
                        fingerprintsToCapture.add(right_middle);
                        break;
                    case 3:
                        FingerprintToCapture right_ring = new FingerprintToCapture(3, R.drawable.fp_reg_ring_right, getContext().getString(R.string.right_ring));
                        fingerprintsToCapture.add(right_ring);
                        break;
                    case 4:
                        FingerprintToCapture right_pinky = new FingerprintToCapture(4, R.drawable.fp_reg_pinky_right, getContext().getString(R.string.right_pinky));
                        fingerprintsToCapture.add(right_pinky);
                        break;
                    case 5:
                        FingerprintToCapture left_thumb = new FingerprintToCapture(5, R.drawable.fp_reg_thumb, getContext().getString(R.string.left_thumb));
                        fingerprintsToCapture.add(left_thumb);
                        break;
                    case 6:
                        FingerprintToCapture left_index = new FingerprintToCapture(6, R.drawable.fp_reg_index, getContext().getString(R.string.left_index));
                        fingerprintsToCapture.add(left_index);
                        break;
                    case 7:
                        FingerprintToCapture left_middle = new FingerprintToCapture(7, R.drawable.fp_reg_middle, getContext().getString(R.string.left_middle));
                        fingerprintsToCapture.add(left_middle);
                        break;
                    case 8:
                        FingerprintToCapture left_ring = new FingerprintToCapture(8, R.drawable.fp_reg_ring, getContext().getString(R.string.left_ring));
                        fingerprintsToCapture.add(left_ring);
                        break;
                    case 9:
                        FingerprintToCapture left_pinky = new FingerprintToCapture(9, R.drawable.fp_reg_pinky, getContext().getString(R.string.left_pinky));
                        fingerprintsToCapture.add(left_pinky);
                        break;


                }
            }

        }
    }

    void skipCurrentActiveFingerprint() {
        View aldv = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exception_code, null);
        final AlertDialog ald = new AlertDialog.Builder(getContext())
                .setView(aldv)
                .setCancelable(false)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button dismiss = (Button) aldv.findViewById(R.id.dismiss);
        Button procceed_btn = (Button) aldv.findViewById(R.id.proceed);
        Spinner reason_spn = (Spinner) aldv.findViewById(R.id.reason_selection);
        reason_spn.setAdapter(new FingerprintSkippingReasonAdapter(getContext(), reasons_to_skip));

        final EditText exception_code = (EditText) aldv.findViewById(R.id.exception_code_edt);
        dismiss.setOnClickListener((v) -> ald.dismiss());


        procceed_btn.setOnClickListener((v) -> {

            if (reason_spn.getSelectedItemPosition() == 0) {
                ald.findViewById(R.id.reason_spn_lay).setBackground(getContext().getDrawable(R.drawable.textback_error));

            } else {
                ald.dismiss();
                for (FingerprintToCapture fp : fingerprintsToCapture) {
                    fp.capturing = false;
                }
                FingerprintSkippingReason fingerprintSkippingReason = new FingerprintSkippingReason(fingerprintsToCapture.get(activeFingerprintPosition).index + "", reasons_to_skip.get(reason_spn.getSelectedItemPosition()).sid, "member_no");
                removeFingerprintSkippingReason(selectedReasonsToSkip, fingerprintsToCapture.get(activeFingerprintPosition).index);
                removeFingerprint(memberFingerprints, fingerprintsToCapture.get(activeFingerprintPosition).index);

                selectedReasonsToSkip.add(fingerprintSkippingReason);
                fingerprintsToCapture.get(activeFingerprintPosition).skipped = true;
                fingerprintsToCapture.get(activeFingerprintPosition).wsq = null;
                fingerprintsToCapture.get(activeFingerprintPosition).jpeg = null;
                fingerprintsToCapture.get(activeFingerprintPosition).iso = null;
                if (activeFingerprintPosition < (fingerprintsToCapture.size() - 1)) {
                    setActiveFingerprint(activeFingerprintPosition + 1);

                } else {
                    setActiveFingerprint(activeFingerprintPosition);

//                    procceed();
                }
                inputField.inputValid = validated();
            }
        });

    }

    boolean checkFingerprintSkippingReason(ArrayList<FingerprintSkippingReason> fingerprintSkippingReasons, int fp_index) {
        int len = fingerprintSkippingReasons.size();
        for (int i = 0; i < len; i++) {
            if (fingerprintSkippingReasons.get(i).finger_index.equalsIgnoreCase(fp_index + "")) {
                return true;
            }
        }
        return false;
    }

    void removeFingerprintSkippingReason(ArrayList<FingerprintSkippingReason> fingerprintSkippingReasons, int fp_index) {
        int len = fingerprintSkippingReasons.size();
        for (int i = 0; i < len; i++) {
            if (fingerprintSkippingReasons.get(i).finger_index.equalsIgnoreCase(fp_index + "")) {
                fingerprintSkippingReasons.remove(i);
                return;
            }
        }

    }


    void removeFingerprint(ArrayList<MemberFingerprint> memberMemberFingerprints, int fp_index) {
        int len = memberMemberFingerprints.size();
        for (int i = 0; i < len; i++) {
            if (memberMemberFingerprints.get(i).fingerprint_index.equalsIgnoreCase(fp_index + "")) {
                memberMemberFingerprints.remove(i);
                return;
            }
        }

    }

    int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}


