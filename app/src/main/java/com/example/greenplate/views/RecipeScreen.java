import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class RecipeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Inflate the header layout
        TextView headerTextView = findViewById(R.id.header);
        headerTextView.setText("Recipe Screen");
    }

}
