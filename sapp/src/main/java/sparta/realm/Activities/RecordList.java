package sparta.realm.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartaadapters.employees_adapter;
import sparta.realm.spartamodels.member;
import sparta.realm.Services.sdbw;



public class RecordList extends SpartaAppCompactActivity {

    ArrayList<member> members=new ArrayList<>();
    GridView records_grid;
    sdbw sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        member_category=getIntent().getStringExtra("member_category");
        setup_grid_controll();
    }



    ImageView prev,next;
  AutoCompleteTextView search_text;
    LinearLayout no_records_lay;
    TextView position_indicator;
    ProgressBar loading_bar;
    void setup_grid_controll()
    {
        prev=findViewById(R.id.prev);
        position_indicator=findViewById(R.id.position_indicator);
        next=findViewById(R.id.next);
        search_text=findViewById(R.id.search_text);
        no_records_lay=findViewById(R.id.no_records_lay);
        loading_bar=findViewById(R.id.loading_bar);
        records_grid=findViewById(R.id.records_grid);

        records_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intt=new Intent(act, Reg_pg1.class);
//intt.putExtra("reg_type",2);
//intt.putExtra("sid",employees.get(position).sid.value);
//                start_activity(intt);
            }
        });

next.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       offset=((limit+offset)<total)?offset+limit:offset;
       reset_list(search_text.getText().toString());
    }
});
prev.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       offset=(offset!=0)?offset-limit:offset;
        reset_list(search_text.getText().toString());
    }
});
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                offset=0;
                limit=100;
                reset_list(search_text.getText().toString());

            }
        });
        reset_list(search_text.getText().toString());

    }
    public static int search_counter=0;
    String prev_search_term="-";
    boolean required_to_reload=true;
    Thread search_thread;

    List<member> all_emps;
    String det;
    int offset=0,limit=100,total=0;



String member_category=null;

    void reset_list(String search_tearm)
    {
   /* ArrayList<employee>  temp_emp=sd.load_all_employees(search_text.getText().toString(),false,search_counter);
    employee_list.setAdapter(new employees_adapter(act,temp_emp));
    if(1==1){return;}
*/
        loading_bar.setVisibility(View.VISIBLE);

        search_thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(!required_to_reload) {
                    search_counter++;
                }
                int int_counter=search_counter;

                final String cur_search_term=search_tearm;


                    if(/*cur_search_term.startsWith(prev_search_term)&&*/!required_to_reload)
                    {
//                        employees =  Stream.of(all_emps).filter(new Predicate<employee>() {
//                            @Override
//                            public boolean test(employee item) {
//                                return item.fullname.value.toUpperCase().contains(cur_search_term.toUpperCase())||item.idno.value.toUpperCase().contains(cur_search_term.toUpperCase());
//                            }
//                        }).collect(Collectors.<employee>toList());
                        members = sd.load_all_employees(cur_search_term, int_counter,offset,limit,member_category);

                    }else {
                        required_to_reload=true;
                        search_counter++;
                        int_counter=search_counter;
                        members = sd.load_all_employees(cur_search_term, int_counter,offset,limit,member_category);
                        all_emps= members;
                        required_to_reload=false;
                    }
                    prev_search_term=cur_search_term;


                if(search_counter==int_counter)
                { /* */
                    if(search_tearm==null ||search_tearm.length()<1) {
                        total = Integer.parseInt(Realm.databaseManager.get_record_count("member_info_table", "category='"+member_category+"'"));
                    }else{
                        total = Integer.parseInt(Realm.databaseManager.get_record_count("member_info_table", "UPPER(fullname) LIKE '%" + search_tearm + "%' OR UPPER(idno) LIKE '%" + search_tearm + "%'", "category='"+member_category+"'"));

                    }
//                    if(employees.size()<limit)
//                    {
//                        offset=offset-(limit-employees.size());
//
//                    }

                    //    WHERE (UPPER(EIT.fullname) LIKE '%" + search_tearm + "%' OR UPPER(EIT.idno) LIKE '%" + search_tearm + "%')
//temp_emp=null;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            position_indicator.setText(offset+" - "+(offset+ members.size()) +" of "+total);
                            records_grid.setAdapter(new employees_adapter(act, members));
                            loading_bar.setVisibility(View.GONE);

                        }
                    });
                    Runtime.getRuntime().gc();

                }  /* */




            }
        });

        search_thread.start();
    }
}
