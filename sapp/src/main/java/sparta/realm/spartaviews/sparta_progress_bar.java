package sparta.realm.spartaviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;


public class sparta_progress_bar extends ProgressBar {

  public  interface on_progess_changed_listener{
        void on_progress_changed(int progess);


    }
    on_progess_changed_listener main_listener;

    public sparta_progress_bar(Context context) {
        super(context);
    }

    public sparta_progress_bar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public sparta_progress_bar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public sparta_progress_bar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void setOnProgressChangeListener(on_progess_changed_listener opcl)
    {
        main_listener=opcl;
    }
    @Override
    public void setProgress(int progress)
    {
        super.setProgress(progress);
        if(main_listener!=null&&getProgress()!=progress)
        {

            main_listener.on_progress_changed(progress);

        }


    }
}
