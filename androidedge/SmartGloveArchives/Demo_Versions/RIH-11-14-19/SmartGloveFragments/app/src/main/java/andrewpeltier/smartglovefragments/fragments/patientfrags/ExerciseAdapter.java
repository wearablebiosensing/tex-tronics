package andrewpeltier.smartglovefragments.fragments.patientfrags;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.visualize.Exercise;

/** ======================================
 *
 *          ExerciseAdapter Class
 *
 *  ======================================
 *
 *      While this is not a fragment, it is necessary to load the view that the
 *  ExerciseSelectionFragment uses. More specifically, this class is used to provide
 *  functionality to the Discrete Scroll View, otherwise known as the exercise selection
 *  carousel.
 *
 * @author Andrew Peltier
 *
 * Heavily inspired by yarolegovich
 * https://github.com/yarolegovich/DiscreteScrollView/tree/master/sample/src/main/java/com/yarolegovich/discretescrollview/sample
 *
 * @version 1.0
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>
{
    private static final String TAG = "ExerciseAdapter";
    private RecyclerView parentRecycler;
    private Context mContext;
    private List<Exercise> data;        // List of exercises
    private ListView listView;

    /** ExerciseAdapter Constructor
     *
     * For the constructor, we simply set up the carousel with the
     * list of possible exercises.
     *
     * @param data          -List of exercises
     * @param context       -State of the application
     */
    public ExerciseAdapter(List<Exercise> data, Context context)
    {
        this.data = data;
        mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    // Loads the carousel into its respective view, which will be the lower left side
    // of the exercise selection fragment
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.exercise_icon_card, parent, false);
        return new ViewHolder(v);
    }

    /** onBindViewHolder()
     *
     * Binds the carousel to one current element. The Glide effect provides the animation
     * that allows the icons to slide when the user moves it.
     *
     * @param holder            -The carousel which holds all of its icons
     * @param position          -Position of the element (or exercise icon) to bind to
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int iconTint = ContextCompat.getColor(holder.itemView.getContext(), R.color.grayIconTint);
        // Get the exercise from the list based on its position
        Exercise exercise = data.get(position);
        // Animate the carousel to move to the exercise's icon
        Glide.with(holder.itemView.getContext())
                .load(exercise.getExerciseIcon())
                .listener(new TintOnLoad(holder.imageView, iconTint))
                .into(holder.imageView);
        holder.textView.setText(exercise.getExerciseName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.choice_image);
            textView = itemView.findViewById(R.id.choice_name);

            itemView.findViewById(R.id.container).setOnClickListener(this);
        }

        /** showText()
         *
         * Animates the text of the exercise icon to appear when it is in the center of the screen. This
         * method animates the size and visibility of the text from invisible to visible.
         *
         */
        public void showText()
        {
            // Scale the text according to the size of the icon
            int parentHeight = ((View) imageView.getParent()).getHeight();
            float scale = (parentHeight - textView.getHeight()) / (float) imageView.getHeight();
            imageView.setPivotX(imageView.getWidth() * 0.5f);
            imageView.setPivotY(0);
            // Animate the text to appear and increase gradually in size
            imageView.animate().scaleX(scale)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            textView.setVisibility(View.VISIBLE);
                            imageView.setColorFilter(Color.BLACK);
                        }
                    })
                    .scaleY(scale).setDuration(200)
                    .start();
        }

        /** hideText()
         *
         * Opposite of showText(). Hides the text of the exercise icons when it is not in the center of the
         * screen. It also controls the animation from visible to invisible
         *
         */
        public void hideText() {
            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.grayIconTint));
            textView.setVisibility(View.INVISIBLE);
            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Selected the " + textView.getText().toString() + " exercise.");
            ExerciseSelectionFragment.addItems(v, textView.getText().toString());
        }
    }

    /** TintOnLoad class
     *
     * Animates the images inside of the glide drawable (i.e the exercise icons)
     *
     */
    private static class TintOnLoad implements RequestListener<Integer, GlideDrawable>
    {

        private ImageView imageView;        // Exercise Icon
        private int tintColor;              // Color of the icon tint

        public TintOnLoad(ImageView view, int tintColor)
        {
            this.imageView = view;
            this.tintColor = tintColor;
        }

        @Override
        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            imageView.setColorFilter(tintColor);
            return false;
        }
    }
}
