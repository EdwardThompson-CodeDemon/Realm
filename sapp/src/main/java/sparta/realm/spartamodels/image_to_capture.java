package sparta.realm.spartamodels;


import android.widget.ImageView;

import java.io.Serializable;

public class image_to_capture implements Serializable {



 public    int image_index;
  public   ImageView iv,delete;

    public image_to_capture(int image_index,ImageView iv,ImageView delete)
    {
this.image_index=image_index;
this.delete=delete;
this.iv=iv;
    }

}
