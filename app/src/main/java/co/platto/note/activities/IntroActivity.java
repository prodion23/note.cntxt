package co.platto.note.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import co.platto.note.R;
import co.platto.note.utils.ActionBarUtils;

/**
 * Created by Donnie Propst on 3/31/2016.
 */
public class IntroActivity extends AppIntro {

    public static boolean visited = false;
    @Override
    public void init(Bundle savedInstanceState) {

        ActionBarUtils.remove(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.LTGRAY);
        }


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("The Modern Note", "Let's face it. The sticky notes that we know today need a make over. With note.cntxt, ditch the pens and paper and use the modern medium.", R.drawable.stickynote, Color.parseColor("#A9D9E7")));
        addSlide(AppIntroFragment.newInstance("Location, Location", "Using Estimote's beacons and the Estimote SDK, note.cntxt allows you to send notes to co-workers based on their location.  They won't get your notes until they get to the office!", R.drawable.beacon, Color.parseColor("#E4D7EA")));
        addSlide(AppIntroFragment.newInstance("Vestibulum dapibus", "Fusce neque. Nulla consequat massa quis enim. Nulla neque dolor, sagittis eget, iaculis quis, molestie non, velit.", R.drawable.office, Color.parseColor("#7EB098")));
        setSeparatorColor(getResources().getColor(R.color.divider_color));

    }

    @Override
    public void onSkipPressed() {
        visited = true;
        createOrg();
    }

    @Override
    public void onDonePressed() {
        visited = true;
        createOrg();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    private void createOrg(){
        Intent intent = new Intent(this, CreateOrganization.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}