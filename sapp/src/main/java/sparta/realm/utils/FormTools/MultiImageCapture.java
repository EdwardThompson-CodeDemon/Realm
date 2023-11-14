package sparta.realm.utils.FormTools;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.io.File;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.R;

import sparta.realm.spartautils.svars;
import sparta.realm.utils.FormTools.adapters.MultiPhotoInputAdapter;
import sparta.realm.utils.FormTools.models.AppData;
import sparta.realm.utils.FormTools.models.InputField;
import sparta.realm.utils.FormTools.models.ValidationRules;
import sparta.realm.utils.InputValidation;

public class MultiImageCapture extends ConstraintLayout {

    TextView title, subTitle;
    ImageView clearAllButon;

    InputField inputField = new InputField();
    RecyclerView recyclerView;
    //    AppData memberImage;
    SpartaAppCompactActivity activity;
    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, AppData input) {

        }

        @Override
        public void onInputRequested(InputField inputField) {

        }
    };


    public interface InputListener {
        void onInputRequested(InputField inputField);

        default void onInputAvailable(boolean valid, AppData input) {

        }


    }

    public MultiImageCapture(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public MultiImageCapture(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI();

    }

    MultiPhotoInputAdapter multiPhotoInputAdapter;

    void setupUI() {
        title = new TextView(getContext(), null, R.style.TextAppearance_AppCompat_SearchResult_Subtitle);
        title.setText("Multi photo capture");
        title.setTypeface(null, Typeface.BOLD);
        int title_id = View.generateViewId();
        title.setId(title_id);

        recyclerView = new RecyclerView(getContext());
        int recyclerViewId = View.generateViewId();
        recyclerView.setId(recyclerViewId);


        subTitle = new TextView(getContext());
        subTitle.setText("Click on the image to add a photo");
        int subTitleId = View.generateViewId();
        subTitle.setId(subTitleId);

        clearAllButon = new ImageView(getContext());
        clearAllButon.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_menu_delete));
        int clearAllButonId = View.generateViewId();
        clearAllButon.setId(clearAllButonId);


        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topToTop = PARENT_ID;
        titleParams.startToStart = PARENT_ID;
        titleParams.bottomMargin = 0;
        addView(title, titleParams);

        LayoutParams subTitleParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        subTitleParams.topToBottom = title_id;
        subTitleParams.startToStart = PARENT_ID;
        subTitleParams.endToEnd = clearAllButonId;
        subTitleParams.setMargins(dpToPx(4), dpToPx(8), dpToPx(0), dpToPx(0));
        subTitleParams.bottomMargin = 0;
        addView(subTitle, subTitleParams);

        LayoutParams clearAllButtonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clearAllButtonParams.topToBottom = title_id;
        clearAllButtonParams.endToEnd = PARENT_ID;
        addView(clearAllButon, clearAllButtonParams);


        LayoutParams recyclerViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerViewParams.topToBottom = clearAllButonId;
        recyclerViewParams.setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(0));
        addView(recyclerView, recyclerViewParams);
//        recyclerView.setBackgroundColor(getContext().getColor(R.color.antiquewhite));


        clearAllButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                multiPhotoInputAdapter.clearItems();
            }
        });

        multiPhotoInputAdapter = new MultiPhotoInputAdapter(new MultiPhotoInputAdapter.InputListener() {
            @Override
            public void onPhotoTaken() {

            }

            @Override
            public void onPhotoRequested() {
                inputListener.onInputRequested(inputField);
                activity.takePhoto(inputField.sid);
            }

            @Override
            public void onMaxItemsReached() {
                MultiPhotoInputAdapter.InputListener.super.onMaxItemsReached();

            }
        });
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(flowLayoutManager);
        recyclerView.setAdapter(multiPhotoInputAdapter);


    }

    int requiredImages = 1;

    public void setRequiredImages(int requiredImages) {
        this.requiredImages = requiredImages;
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        setTitle(inputField.title);
        setSubTitle(inputField.sub_title);
        addImage(inputField.imageInput);
        svars.setImageCameraType(getContext(), inputField.sid, Integer.parseInt(inputField.default_image_source));

        setValidationRules(inputField.validationRules);

    }

    public void setValidationRules(ValidationRules validationRules) {
        multiPhotoInputAdapter.setMaxImages(InputValidation.isNumeric(validationRules.max_input_value) ? Integer.parseInt(validationRules.max_input_value) : 0);

    }

    public boolean isInputValid() {
        if (inputField == null || inputField.validationRules.mandatory == null || !inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "")) {
            return true;
        }
        int imageCount = multiPhotoInputAdapter.getImageCount();
        int minImages = InputValidation.isNumeric(inputField.validationRules.min_input_value) ? Integer.parseInt(inputField.validationRules.min_input_value) : 0;
        int maxImages = InputValidation.isNumeric(inputField.validationRules.max_input_value) ? Integer.parseInt(inputField.validationRules.max_input_value) : -1;
        if (imageCount < minImages) {
            return false;
        }
        if (maxImages != -1 && imageCount > maxImages) {
            return false;
        }

        return true;
    }

    public void setActivity(SpartaAppCompactActivity activity) {
        this.activity = activity;
        multiPhotoInputAdapter.setActivity(activity);
    }

    public void setTitle(String title_) {
        this.title.setText(title_);

    }

    public void setSubTitle(String subTitle) {
        this.subTitle.setText(subTitle);

    }

    public void clearImages() {
        inputField.inputValid = false;
        multiPhotoInputAdapter.clearItems();
    }

    public void addImage(AppData appData) {

        if (appData == null || appData.data == null) {
            inputField.inputValid = false;
            return;
        } else {
            File file = new File(svars.current_app_config(getContext()).appDataFolder, appData.data);
            if (!file.exists() || file.length() < 500) {
                inputField.inputValid = false;
                return;
            }

        }
        multiPhotoInputAdapter.addImage(appData);


    }

    int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
