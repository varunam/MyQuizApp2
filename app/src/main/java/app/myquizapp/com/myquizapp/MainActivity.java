package app.myquizapp.com.myquizapp;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   /* private ViewPager viewPager;
    private CustomCardAdapter cardAdapter;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  viewPager = findViewById(R.id.viewPagerID);
        cardAdapter = new CustomCardAdapter();
        //viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager()));

        *//*CardItem cardItem1 = new CardItem("Title One", "Message One");
        CardItem cardItem2 = new CardItem("Title Two","Message two");
        CardItem cardItem3 = new CardItem("Title three", "Message three");
        CardItem cardItem4 = new CardItem("Title three", "Message three");

        CardItem cardItem5 = new CardItem("Title three", "Message three");

        CardItem cardItem6 = new CardItem("Title three", "Message three");

        CardItem cardItem7 = new CardItem("Title three", "Message three");

        CardItem cardItem8 = new CardItem("Title three", "Message three");

        CardItem cardItem9 = new CardItem("Title three", "Message three");


        cardAdapter.addCard(cardItem1);
        cardAdapter.addCard(cardItem2);
        cardAdapter.addCard(cardItem3);
        cardAdapter.addCard(cardItem4);
        cardAdapter.addCard(cardItem5);
        cardAdapter.addCard(cardItem6);
        cardAdapter.addCard(cardItem7);
        cardAdapter.addCard(cardItem8);
        cardAdapter.addCard(cardItem9);
*//*
        List<CardItem> cards = loadQuiz("easy.json");
        if (cards != null)
            for (CardItem card : cards) cardAdapter.addCard(card);
        viewPager.setAdapter(cardAdapter);
    }

    private List<CardItem> loadQuiz(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String buffer;
            StringBuilder builder = new StringBuilder();
            while ((buffer = bufferedReader.readLine()) != null)
                builder.append(buffer);
            JSONObject object = new JSONObject(builder.toString());
            JSONArray jsonArray = object.getJSONArray("Questions");
            List<CardItem> cardItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String question = jsonObject.getString("Question");
                String message = jsonObject.getString("solution");
                if(question!=null && message!=null)
                    cardItems.add(new CardItem(jsonObject.getString("Question"), jsonObject.getString("solution")));
            }
            bufferedReader.close();
            return cardItems;
        } catch (IOException | JSONException e) {
            throw new UnsupportedOperationException("Error in Json parsing", e);
        }
    }

    class CustomCardAdapter extends PagerAdapter {

        private List<CardView> cardViews;
        private List<CardItem> cardItems;

        public CustomCardAdapter(){
            cardViews = new ArrayList<>();
            cardItems = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return cardItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        public void addCard(CardItem cardItem)
        {
            cardItems.add(cardItem);
            cardViews.add(null);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.layout_cardview, null);
            container.addView(view);
            bind(cardItems.get(position),view);
            return view;
        }

        public void bind(CardItem item, final View view)
        {
            CardView cardView = view.findViewById(R.id.cardViewID);
            TextView titleText = view.findViewById(R.id.titleID);
            TextView messageText = view.findViewById(R.id.messageID);
            final RadioGroup radioGroup = view.findViewById(R.id.radioGroupID);
            final RadioButton[] radioButton = new RadioButton[1];

            Button submitButton = view.findViewById(R.id.submitButtonID);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int checkedID = radioGroup.getCheckedRadioButtonId();
                    radioButton[0] = view.findViewById(checkedID);
                }
            });

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), radioButton[0].getText().toString(),Toast.LENGTH_SHORT).show();
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
            });

            titleText.setText(item.getTitle());
            messageText.setText(item.getMessage());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(),"Clicked " + titleText.getText().toString() ,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            cardViews.set(position,null);
        }
    }*/
    }
}
