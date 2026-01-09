package sparta.realm.utils.FormTools2;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.utils.FormTools.adapters.SelectionDataRecyclerViewAdapter;
import sparta.realm.utils.FormTools.models.InputField;
import sparta.realm.utils.FormTools.models.SelectionData;
import sparta.realm.utils.FormTools.models.ValidationRules;


public class SearchSpinner extends LinearLayout {
    TextView title, topSelectInstructions, selectInstructions, popupTitle;
    ImageView dropArrow;
    View underline;
    TextView selectedItemTitle, selectedItemInfo;
    AutoCompleteTextView searchText, searchText2;
    RecyclerView searchList;
    View selectedItemView;
    public SelectionData selectedItem;
    ArrayList<? extends SelectionData> selectionData = new ArrayList<>();
    private PopupWindow mPopupWindow;
    ProgressBar loadingBar;

    public SearchSpinner(Context context) {
        super(context);
        setupUI();
    }

    public SearchSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SearchSpinner,
                0, 0);

        setTitle(a.getString(R.styleable.SearchSpinner_title));
        setPlaceholder(a.getString(R.styleable.SearchSpinner_placeholder));
        setSearchTitle(a.getString(R.styleable.SearchSpinner_searchListTitle));
        setMandatory(a.getBoolean(R.styleable.SearchSpinner_mandatory, false));
        a.recycle();
    }

    public SearchSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //     setupUI();
    }

    public SearchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //    setupUI();
    }
    Pager<? extends SelectionData> pager;
    int pagerSearchId=new Random().nextInt();
    void setupPager(){

       String customQuery="";
        try {
            if(inputField.dataset==null){
                return;
            }
            pager = new Pager(Class.forName(inputField.dataset), pagerSearchId, 100, new Pager.PagerCallback<SelectionData>() {
                @Override
                public void onDataRefreshed(ArrayList<SelectionData> data, int from, int to, int total) {
    selectionData.clear();
    selectionData.addAll((ArrayList)data);
                    selectionDataRecyclerViewAdapter.setupLists();
    //                binding.noRecordsLay.getRoot().setVisibility((data.size()) > 0 ? View.GONE : View.VISIBLE);


                }
            }, searchText2, new ImageView(getContext()), new ImageView(getContext()),new TextView(getContext()),loadingBar, null,(inputField.dataset_table_filter!=null&&inputField.dataset_table_filter.length()>3)? new String[]{inputField.dataset_table_filter}:new String[]{}, new String[]{"strftime('%s', reg_time) as time","*"}, "name");
            LinkedHashMap<String, Boolean> orderFilters=new LinkedHashMap<>();
            orderFilters.put("data_usage_frequency",false);
            orderFilters.put("name",true);
            pager.setOrderFilters(orderFilters);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    void setupUI() {
        setOrientation(VERTICAL);
        selectedItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_selected_data, null);
        selectedItemTitle = selectedItemView.findViewById(R.id.title);
        selectedItemInfo = selectedItemView.findViewById(R.id.info1);

        title = new TextView(getContext(), null, R.style.TextAppearance_AppCompat_SearchResult_Subtitle);
        title.setText("Gender");
        title.setTypeface(null, Typeface.BOLD);

        selectInstructions = new TextView(getContext());
        selectInstructions.setText("  --Select gender--  ");

        dropArrow = new ImageView(getContext());
        dropArrow.setImageDrawable(getContext().getDrawable(android.R.drawable.arrow_down_float));

        underline = new View(getContext());
        underline.setBackgroundColor(Color.DKGRAY);


        topSelectInstructions = new TextView(getContext());
        topSelectInstructions.setText("Select gender  ");
        topSelectInstructions.setCompoundDrawables(null, null, getContext().getDrawable(android.R.drawable.arrow_down_float), null);
//        topSelectInstructions.setBackgroundTintList();

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
        topSelectInstructions.setCompoundDrawables(null, null, wrappedDrawable, null);

        searchText = new AutoCompleteTextView(getContext());
        searchText.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(android.R.drawable.ic_menu_search), null, null, null);

        searchList = new RecyclerView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 1;
        addView(title);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(selectedItemView, params);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(searchText, params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = 1;
        addView(topSelectInstructions, params);
//        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(getContext(),1.2f));
//        addView(underline, params);

        selectedItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled()) {
                    setState(state.searching);
                }
            }
        });
//        searchText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setState(state.searching);
//            }
//        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP && isEnabled()) {
                    setState(state.searching);
//                    return true;
                }
                return false;
            }
        });


        searchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    setState(b ? state.searching : state.idle);
                    inputField.inputValid = isInputValid();
                }

            }
        });
        topSelectInstructions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled()) {
                    setState(state.searching);
                }

            }
        });
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(getContext().getColor(R.color.ghostwhite));


        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 1;
        layout.addView(selectInstructions, params);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(searchList, params);

        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_spinner, null);
        loadingBar = popupView.findViewById(R.id.loading_bar);
        searchText2 = popupView.findViewById(R.id.search_field);
        searchList = popupView.findViewById(R.id.search_list);
        selectInstructions = popupView.findViewById(R.id.select_instruction);
        popupTitle = popupView.findViewById(R.id.title);

        searchList.setLayoutManager(new LinearLayoutManager(getContext()));
        searchList.setAdapter(selectionDataRecyclerViewAdapter);
        popupView.findViewById(R.id.close).setOnClickListener((view -> mPopupWindow.dismiss()));

        mPopupWindow = new PopupWindow(getContext());
        mPopupWindow.setContentView(popupView);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setOnDismissListener(() -> {
            setState(state.idle);
            inputField.inputValid = isInputValid();
//            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            hideKeyboard(searchText2);
//            }
//                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//            }
//            IBinder token = searchText2.getWindowToken();
//            ( ( InputMethodManager ) getContext().getSystemService( Context.INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow( token, 0 );
        });

        mPopupWindow.setFocusable(true);

        mPopupWindow.setElevation(16);
//        mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.spinner_drawable));
        mPopupWindow.setBackgroundDrawable(null);
        setState(state.idle);

        searchText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setState(state.searching);
//                selectionDataRecyclerViewAdapter.getFilter().filter(searchText2.getText().toString());

            }
        });
//        setupPager();
        underline=selectedItemView.findViewById(R.id.separator);

    }
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    public enum state {
        idle, searching

    }

    public void setMandatory(boolean mandatory) {
//        this.mandatoryIndicator.setVisibility(mandatory ? VISIBLE : GONE);
        inputField.validationRules.mandatory = mandatory ? ValidationRules.MandatoryStatus.Mandatory.ordinal() + "" : ValidationRules.MandatoryStatus.NonMandatory.ordinal() + "";
    }

    public void setTitle(String title_) {
        this.title.setText(title_);
        this.popupTitle.setText(title_);
    }

    public String placeholder = null;

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        this.searchText.setHint(placeholder);
        if (selectedItem == null) {
            selectedItemTitle.setTextColor(Color.parseColor("#808080"));
            selectedItemTitle.setText(placeholder);

        } else {
            selectedItemTitle.setTextColor(Color.BLACK);
            selectedItemTitle.setText(selectedItem.name);
        }
    }

    public void setSearchTitle(String searchTitle) {
        this.selectInstructions.setText(searchTitle);
    }
    public void setDataset(String dataset, InputListener inputListener) {
        this.inputListener = inputListener;
        inputField.dataset=dataset;
//        Query dataLoadingQuery = new Query()
//                .addOrderFilters("data_usage_frequency", false)
//                .addOrderFilters("name", true)
//                .setLimit(10);
//
//        dataLoadingQuery = inputField.dataset_table_filter == null ? dataLoadingQuery : dataLoadingQuery.setTableFilters(inputField.dataset_table_filter);
//        selectionData.clear();
//        try {
//            selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), dataLoadingQuery));
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        setupPager();

        selectionDataRecyclerViewAdapter.setupLists();
    }
    public void setDataset_(String dataset, InputListener inputListener) {
        this.inputListener = inputListener;
        try {
            selectionData.clear();
            selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(dataset), new Query().addOrderFilters("data_usage_frequency", false).addOrderFilters("name", true)));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        selectionDataRecyclerViewAdapter.setupLists();
    }

    public void setTableFilters(String... tableFilters) {
        if(pager!=null){
            inputField.dataset_table_filter=tableFilters==null?"": DatabaseManager.concatString(" AND ",tableFilters);
            pager.setTableFilters(tableFilters);
        }

    }
    public void setDataset(ArrayList dataset) {
        selectionData.clear();
        Handler handler = new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {

                selectionData.addAll(dataset);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid = isInputValid();
                    }
                });
            }
        }).start();
        setState(state.idle);

    }
    public void setDataset_(ArrayList dataset) {
        selectionData.clear();
        Handler handler = new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {

                selectionData.addAll(dataset);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid = isInputValid();
                    }
                });
            }
        }).start();
        setState(state.idle);

    }

    public void setDataset(ArrayList dataset, InputListener inputListener) {
        this.inputListener = inputListener;
        selectionData.clear();
//        selectionDataRecyclerViewAdapter.setupLists();
//        selectionData.clear();
//        selectionData.addAll(dataset);
//        selectedItem = null;
//        selectionDataRecyclerViewAdapter.setupLists();
        Handler handler = new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {

                selectionData.addAll(dataset);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid = isInputValid();
                    }
                });
            }
        }).start();
        setState(state.idle);

    }

    String input = null;

    public String getInput() {
        return selectedItem == null ? null : selectedItem.sid;

    }

    public void setInput(String input) {
        selectedItem = null;
        if (input == null) {
            inputField.input = null;
            setState(state.idle);
            return;
        }
        if(inputField!=null){

            Query dataLoadingQuery = new Query()
                    .addOrderFilters("data_usage_frequency", false)
                    .addOrderFilters("name", true);

            dataLoadingQuery = ((inputField.dataset_table_filter == null )? dataLoadingQuery.setTableFilters("sid='"+input+"'") : dataLoadingQuery.setTableFilters(inputField.dataset_table_filter+" AND sid='"+input+"'"));
            try {
                selectedItem= (SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset),dataLoadingQuery);
            } catch (Exception e) {
                selectedItem=null;
            }
            if(selectedItem!=null){

                selectedItemTitle.setText(selectedItem.name);
                selectedItemInfo.setText(selectedItem.code);
            }else{
                selectedItemTitle.setText("");
                selectedItemInfo.setText("");
                inputField.input = null;
            }
        }

        setState(state.idle);


    }
 public void setInput_(String input) {
        int tot = selectionData.size();
        selectedItem = null;
        if (input == null) {
            inputField.input = null;
            setState(state.idle);
            return;
        }
        for (int i = 0; i < tot; i++) {
            if (selectionData.get(i).sid.equals(input)) {
                selectedItem = selectionData.get(i);
                if (inputField != null) inputField.input = input;
                selectedItemTitle.setText(selectedItem.name);
                selectedItemInfo.setText(selectedItem.code);
//              inputListener.onInputAvailable(isInputValid(),selectedItem.sid);
                break;
            }
        }
        if (selectedItem == null) {
            inputField.input = null;
        }
        setState(state.idle);


    }

    boolean invalidOnce = false;

    public boolean isInputValid() {
        if (inputField == null) {
            return true;
        }
        if (inputField.validationRules.mandatory != null && inputField.validationRules.mandatory.equals(ValidationRules.MandatoryStatus.Mandatory.ordinal() + "") && selectedItem == null) {
            setError(inputField.validationRules.value_not_selected_error != null ? inputField.validationRules.value_not_selected_error : "Invalid input");
            ObjectAnimator.ofFloat(this, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
            if (invalidOnce) {
                underline.setBackgroundColor(Color.RED);
                searchText.setBackgroundColor(Color.RED);
            }


            invalidOnce = true;
            return false;
        } else {
            searchText.setError(null);
            underline.setBackgroundColor(getResources().getColor(android.R.color.black));

            return true;
//          searchText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);

        }
//        return selectedItem != null;

    }

    public void setError(String error) {
        searchText.setError(error);
        searchText.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
        underline.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

        Toast.makeText(getContext(), searchText.getError(), Toast.LENGTH_LONG).show();

    }

    public interface InputListener {
        default void onInputAvailable(boolean valid, String input) {

        }

        default void onDataLoaded(boolean valid) {

        }


    }

    InputListener inputListener = new InputListener() {
        @Override
        public void onInputAvailable(boolean valid, String input) {

        }
    };


    void setState(state state) {
//        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_arrow_drop_down_24);
//        unwrappedDrawable.setBounds();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
//        searchText.setCompoundDrawables(null,null, wrappedDrawable,null);
//        searchText.setCompoundDrawablesWithIntrinsicBounds(null,null, wrappedDrawable,null);
        searchText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
        switch (state) {
            case idle:
                if (selectedItem == null) {
//                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), android.R.drawable.arrow_down_float);
//                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
//                    DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY);
//                    searchText.setCompoundDrawables(getContext().getDrawable(android.R.drawable.ic_menu_search),null, wrappedDrawable,null);

                    topSelectInstructions.setVisibility(GONE);
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(VISIBLE);
                    selectedItemTitle.setVisibility(VISIBLE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);
                    selectedItemTitle.setTextColor(Color.parseColor("#808080"));
                    selectedItemTitle.setText(placeholder);

                } else {
                    selectedItemTitle.setTextColor(Color.BLACK);
                    selectedItemTitle.setText(selectedItem.name);
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(VISIBLE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);
                }
                mPopupWindow.dismiss();

                break;
            case searching:
                topSelectInstructions.setVisibility(GONE);
//                selectInstructions.setVisibility(VISIBLE);
                selectedItemView.setVisibility(VISIBLE);
                searchText.setVisibility(GONE);
                searchList.setVisibility(VISIBLE);
                searchText2.requestFocus();
//                mPopupWindow.showAsDropDown(searchText);
                mPopupWindow.showAtLocation((View) getParent(), Gravity.CENTER, 0, 0);
                break;
        }


    }

    void setState_(state state) {
        switch (state) {
            case idle:
                if (selectedItem == null) {
                    topSelectInstructions.setVisibility(VISIBLE);
                    selectedItemView.setVisibility(GONE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);

                } else {
                    topSelectInstructions.setVisibility(GONE);
                    selectedItemView.setVisibility(VISIBLE);
                    searchText.setVisibility(GONE);
                    searchList.setVisibility(GONE);
                }
                mPopupWindow.dismiss();
                break;
            case searching:
                topSelectInstructions.setVisibility(GONE);
//                selectInstructions.setVisibility(VISIBLE);
                selectedItemView.setVisibility(GONE);
                searchText.setVisibility(VISIBLE);
                searchList.setVisibility(VISIBLE);
                searchText.requestFocus();
                mPopupWindow.showAsDropDown(searchText);
                break;
        }


    }

    private int mScreenHeightPixels;
    private int mScreenWidthPixels;

    private void getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenHeightPixels = metrics.heightPixels;
        mScreenWidthPixels = metrics.widthPixels;
    }

    private boolean mShowBorders = false;
    private @Px int mBordersSize = 4;
    private @Px int mExpandSize = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getScreenSize();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mShowBorders) {     // + 4 because of card layout_margin in the view_searchable_spinner.xml
            width -= dpToPx((mBordersSize + 4));
        } else {
            width -= dpToPx(8);
        }
        mPopupWindow.setWidth(width);
        if (mExpandSize <= 0) {
            mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            mPopupWindow.setHeight(heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int dpToPx(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public void reset() {


    }

    InputField inputField = new InputField();

    public void setInputField_OtherThreadLoad(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        reset();
        setTitle(inputField.title);
//        searchText.setHint(inputField.placeholder);
        searchText.setHint(getContext().getString(R.string.search) + " " + inputField.title.toLowerCase());
        this.placeholder = getContext().getString(R.string.search) + " " + inputField.title.toLowerCase();
        setSearchTitle((inputField.title.toLowerCase().startsWith("a")
                || inputField.title.toLowerCase().startsWith("e")
                || inputField.title.toLowerCase().startsWith("i")
                || inputField.title.toLowerCase().startsWith("o")
                || inputField.title.toLowerCase().startsWith("u") ? getContext().getString(R.string.select_an) : getContext().getString(R.string.select_a)) + "" + inputField.title.toLowerCase());
        selectionData.clear();
        Handler handler = new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                        selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), new Query().setTableFilters(inputField.dataset_table_filter)));
                    selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), inputField.dataset_table_filter == null ? new Query() : new Query().setTableFilters(inputField.dataset_table_filter)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid = isInputValid();
                    }
                });
            }
        }).start();


//        searchText.setAdapter(new SelectionDataAdapter(getContext(),selectionData));

        selectionDataRecyclerViewAdapter.setupLists();
        setInput(inputField.input);
    }

    public void setInputField_(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        reset();
        setTitle(inputField.title);
//        searchText.setHint(inputField.placeholder);
        searchText.setHint(getContext().getString(R.string.search) + " " + inputField.title.toLowerCase());
        this.placeholder = getContext().getString(R.string.search) + " " + inputField.title.toLowerCase();

        setSearchTitle((inputField.title.toLowerCase().startsWith("a")
                || inputField.title.toLowerCase().startsWith("e")
                || inputField.title.toLowerCase().startsWith("i")
                || inputField.title.toLowerCase().startsWith("o")
                || inputField.title.toLowerCase().startsWith("u") ? getContext().getString(R.string.select_an) : getContext().getString(R.string.select_a)) + "" + inputField.title.toLowerCase());
        selectionData.clear();
        Handler handler = new Handler(getContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Query dataLoadingQuery = new Query()
                            .addOrderFilters("data_usage_frequency", false)
                            .addOrderFilters("name", true);
                    dataLoadingQuery = inputField.dataset_table_filter == null ? dataLoadingQuery : dataLoadingQuery.setTableFilters(inputField.dataset_table_filter);
                    selectionData.addAll((ArrayList) Realm.databaseManager.loadObjectArray(Class.forName(inputField.dataset), dataLoadingQuery));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        selectionDataRecyclerViewAdapter.setupLists();
                        setInput(inputField.input);
                        inputField.inputValid = isInputValid();
                        if (selectedItem != null) {
                            inputListener.onDataLoaded(isInputValid());
                        }
                    }
                });
            }
        }).start();


//        searchText.setAdapter(new SelectionDataAdapter(getContext(),selectionData));

        selectionDataRecyclerViewAdapter.setupLists();
        if (inputField.input != null) {
            try {
                SelectionData selectionData1 = ((SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset), inputField.dataset_table_filter == null ? new Query().setTableFilters("sid=?").setQueryParams(inputField.input) : new Query().setTableFilters(inputField.dataset_table_filter, "sid=?").setQueryParams(inputField.input)));
                if (selectionData1 == null) {
                    inputField.input = null;
                    inputListener.onInputAvailable(isInputValid(), null);
                } else {
                    selectedItemTitle.setText(selectionData1.name);
                    selectedItemInfo.setText(selectionData1.code);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

//        setInput(inputField.input);
    }
 public void setInputField(InputField inputField, InputListener inputListener) {
        this.inputField = inputField;
        this.inputListener = inputListener;
        reset();
        setTitle(inputField.title);
//        searchText.setHint(inputField.placeholder);
        searchText.setHint(getContext().getString(R.string.search) + " " + inputField.title.toLowerCase());
        this.placeholder = getContext().getString(R.string.search) + " " + inputField.title.toLowerCase();

        setSearchTitle((inputField.title.toLowerCase().startsWith("a")
                || inputField.title.toLowerCase().startsWith("e")
                || inputField.title.toLowerCase().startsWith("i")
                || inputField.title.toLowerCase().startsWith("o")
                || inputField.title.toLowerCase().startsWith("u") ? getContext().getString(R.string.select_an) : getContext().getString(R.string.select_a)) + "" + inputField.title.toLowerCase());
        selectionData.clear();
     setInput(inputField.input);
     inputField.inputValid = isInputValid();
     if (selectedItem != null) {
         inputListener.onDataLoaded(isInputValid());
     }
     setupPager();
//     selectionDataRecyclerViewAdapter.setupLists();

        if (inputField.input != null) {
            try {
                SelectionData selectionData1 = ((SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset), inputField.dataset_table_filter == null ? new Query().setTableFilters("sid=?").setQueryParams(inputField.input) : new Query().setTableFilters(inputField.dataset_table_filter, "sid=?").setQueryParams(inputField.input)));
                if (selectionData1 == null) {
                    inputField.input = null;
                    inputListener.onInputAvailable(isInputValid(), null);
                } else {
                    selectedItemTitle.setText(selectionData1.name);
                    selectedItemInfo.setText(selectionData1.code);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

//        setInput(inputField.input);
    }

    public static String[] addElement(String[] original, String newElement) {
        String[] newArray = Arrays.copyOf(original, original.length + 1);
        newArray[original.length] = newElement;
        return newArray;
    }
        SelectionDataRecyclerViewAdapter selectionDataRecyclerViewAdapter = new SelectionDataRecyclerViewAdapter(selectionData, new SelectionDataRecyclerViewAdapter.onItemClickListener() {
        @Override
        public void onItemClick(SelectionData selectionData, View view) {
            selectedItem = selectionData;
            selectedItemTitle.setText(selectionData.name);
            selectedItemInfo.setText(selectionData.code);

            setState(state.idle);
            inputListener.onInputAvailable(isInputValid(), selectionData.sid);
            try {
                if (selectionData.data_usage_frequency != null && selectionData.data_usage_frequency.length() > 0) {
                    int data_usage_freq = Integer.parseInt(selectionData.data_usage_frequency);
                    data_usage_freq++;
                    selectionData.data_usage_frequency = data_usage_freq + "";
                    Realm.databaseManager.executeQuery("UPDATE " + Realm.realm.getPackageTable(inputField.dataset) + " SET data_usage_frequency=? WHERE sid=?", "" + data_usage_freq, selectionData.sid);
                } else {
                    Realm.databaseManager.executeQuery("UPDATE " + Realm.realm.getPackageTable(inputField.dataset) + " SET data_usage_frequency='1' WHERE sid=?", selectionData.sid);

                }
            } catch (Exception ex) {
                Log.e("Search spinner exception", ex.getMessage());
            }
        }
    });


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setAlpha(1f);

        } else {

            setAlpha(0.5f);

        }
    }
}
