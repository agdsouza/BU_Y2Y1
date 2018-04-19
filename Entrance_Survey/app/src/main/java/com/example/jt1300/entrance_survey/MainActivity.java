package com.example.jt1300.entrance_survey;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private
    ListView lvQuestions;     //Reference to the listview GUI component
    ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvQuestions = (ListView)findViewById(R.id.survey);
        lvAdapter = new MyCustomAdapter(this.getBaseContext());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
        lvQuestions.setAdapter(lvAdapter);
    }
}

class MyCustomAdapter extends BaseAdapter {

    private String questions[];
    RadioButton rby;
    RadioButton rbn;
    RadioButton rbd;
    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.

    public MyCustomAdapter(Context aContext) {
        context = aContext;  //saving the context we'll need it again.
        questions = aContext.getResources().getStringArray(R.array.questions);  //retrieving list of questions predefined in strings-array "questions" in strings.xml
    }

    @Override
    public int getCount() {
        return questions.length;

    }

    @Override
    public Object getItem(int position) {
        return questions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  //convertView is Row (it may be null), parent is the layout that has the row Views.

//Inflate the listview row based on the xml.
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)

        if (convertView == null) {  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_row, parent, false);
        } else {
            row = convertView;
        }

// Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
        TextView question = (TextView)row.findViewById(R.id.q);
        rby = (RadioButton)row.findViewById(R.id.qy);
        rbn = (RadioButton)row.findViewById(R.id.qn);
        rbd = (RadioButton)row.findViewById(R.id.qd);

        question.setText(questions[position]);

        return row;
    }
}
