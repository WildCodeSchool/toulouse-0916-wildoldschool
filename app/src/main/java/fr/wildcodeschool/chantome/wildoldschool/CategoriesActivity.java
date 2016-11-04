package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chantome on 27/10/2016.
 */

public class CategoriesActivity extends AppCompatActivity {
    private final static String TAG = "WOS-Catégories";
    private DatabaseReference rootCats = FirebaseDatabase.getInstance().getReference().child("categories");
    private DatabaseReference rootUsers = FirebaseDatabase.getInstance().getReference().child("users");
    private ListView list;
    private LinearLayout linearCat;
    private LinearLayout.LayoutParams txtLp,chkLp;
    private Button addCats;
    private String currentId,mesCats="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //LIST
        list = (ListView) findViewById(R.id.list_cats);

        //BUTTON
        addCats = (Button) findViewById(R.id.send_cats);

        //FIREBASE USER ID
        currentId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        //Favories list
        FirebaseListAdapter<Categorie> catAdapter = new FirebaseListAdapter<Categorie>(
                CategoriesActivity.this,
                Categorie.class,
                R.layout.categorie,
                rootCats
        ) {
            @Override
            protected void populateView(View v, Categorie model, int position) {

                //GET LAYOUT
                linearCat = (LinearLayout) v.findViewById(R.id.categorie);
                //NEW TEXTVIEW
                TextView mtxtCat = new TextView(CategoriesActivity.this);
                //NEW CHECKBOXE
                CheckBox mchkCat = new CheckBox(CategoriesActivity.this);

                //Text init
                mtxtCat.setText(model.getName().toString());
                mtxtCat.setHeight(50);
                mtxtCat.setGravity(Gravity.CENTER);
                txtLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,0.8f);
                mtxtCat.setLayoutParams(txtLp);
                String txtId = "txt_"+model.getName().toString().toLowerCase();
                mtxtCat.setTag(txtId);

                //Checkboxe init
                mchkCat.setGravity(Gravity.CENTER);
                mchkCat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckboxClicked(v);
                    }
                });
                chkLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mchkCat.setLayoutParams(chkLp);
                String checkId = "check_"+model.getName().toString().toLowerCase();
                mchkCat.setTag(checkId);

                //Add Text and Checkbox on Linear layout
                linearCat.addView(mtxtCat);
                linearCat.addView(mchkCat);
            }
        };

        list.setAdapter(catAdapter);

        //Button create chat
        addCats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootUsers.child(currentId).child("favories").setValue(mesCats);
                Intent intent = new Intent(CategoriesActivity.this,ListChatsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //Get values of checkboxes clicked
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getTag().toString()) {
            case "check_php":
                if (checked){
                    mesCats += "0";
                }
                else
                {
                    mesCats = mesCats.replace("0", "");
                }
                break;
            case "check_ruby":
                if (checked){
                    mesCats+="1";
                }
                else
                {
                    mesCats = mesCats.replace("1", "");
                }
                break;
            case "check_javascript":
                if (checked){
                    mesCats+="2";
                }
                else
                {
                    mesCats = mesCats.replace("2", "");
                }
                break;
            case "check_java":
                if (checked){
                    mesCats+="3";
                }
                else
                {
                    mesCats = mesCats.replace("3", "");
                }
                break;
            case "check_ui/ux":
                if (checked){
                    mesCats+="4";
                }
                else
                {
                    mesCats = mesCats.replace("4", "");
                }
                break;
            case "check_veille tech.":
                if (checked){
                    mesCats+="5";
                }
                else
                {
                    mesCats = mesCats.replace("5", "");
                }
                break;
            case "check_jobs":
                if (checked){
                    mesCats+="6";
                }
                else
                {
                    mesCats = mesCats.replace("6", "");
                }
                break;
            case "check_android":
                if (checked){
                    mesCats+="7";
                }
                else
                {
                    mesCats = mesCats.replace("7", "");
                }
                break;
            default:
                mesCats="6";
        }
    }

}
