package team.diamond.kaizer.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import team.diamond.kaizer.R;
import team.diamond.kaizer.models.ModelStoryFree;



//extends  расширяемый RV  адаптером
public class AdapterStoryFree extends RecyclerView.Adapter<AdapterStoryFree.MyHolder> {

    Context context;
    List<ModelStoryFree> listModelStoryFree;

    String myUid;

    public AdapterStoryFree(Context context, List<ModelStoryFree> modelStoryFrees) {
        this.context = context;
        this.listModelStoryFree = modelStoryFrees;
        //  хз не ебу зачем но связано с удалением поста
       myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    //типо создаем держатель
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_true_story, viewGroup, false);

        return new MyHolder(view);
    }


    //типо указываем  всего и вся в держателе  / и подгружаем
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data  из модели ModelPost.class
        String Name = listModelStoryFree.get(i).getName();
        String StoryTitle = listModelStoryFree.get(i).getStoryTitle();
        String StoryText = listModelStoryFree.get(i).getStoryText();
        String StoryImage = listModelStoryFree.get(i).getStoryImage();
        String StoryTime = listModelStoryFree.get(i).getStoryTime();
        String Profile_pic = listModelStoryFree.get(i).getProfile_pic();        // тут можно прописать все что угодно... с запасом но при этом не использовать
        String keyFreeStory = listModelStoryFree.get(i).getKeyFreeStory();
        String CustomVer1 = listModelStoryFree.get(i).getCustomVer1();
        String StoryLike = listModelStoryFree.get(i).getStoryLike();
        String StoryView = listModelStoryFree.get(i).getStoryView();
        String inkognito_id = listModelStoryFree.get(i).getInkognito_id();
        String uid = listModelStoryFree.get(i).getUid();

        //хз не ебу
        //convert timestamp to dd/mm/yyyy  hh:mm   ap/mp
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
//        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        myHolder.storyAuthor.setText(Name);
        myHolder.storyTitle.setText(StoryTitle);
        myHolder.storyDescription.setText(StoryText);
        myHolder.storyTimePublish.setText(StoryTime);
        //set user image profile
        try {
            Picasso.get().load(Profile_pic).placeholder(R.drawable.who32px).into(myHolder.PictureAuthorStory);
        } catch (Exception e) {
            //empty
        }
        //set post image story  станавливаем изображение поста
        //if there is no image i.e. pImage.equals("noImage") then hide ImageView
        if (StoryImage.equals("noImage")) {
            // hide imageview
            // скрыть изображение
            myHolder.storyImage.setVisibility(View.GONE);
        } else {
            //show imageview
            myHolder.storyImage.setVisibility(View.VISIBLE); // make sure to correct this
            //загрузка изображения истории
            try {
                Picasso.get().load(StoryImage).into(myHolder.storyImage);
            } catch (Exception e) {
                //empty
            }
        }

        //handle button clicks
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMoreOptions(myHolder.moreBtn, uid, myUid, pId, pImage);
                showMoreOptions(myHolder.moreBtn,keyFreeStory, StoryImage, uid, myUid);
            }
        });

        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "и тебе поставят лайк", Toast.LENGTH_SHORT).show();
            }
        });

//        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, ThereProfileActivity.class);
//                intent.putExtra("uid", uid);
//                context.startActivity(intent);
//
//            }
//        });



    }

    //   добавить myUid !!!!
//    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, String pImage) {
    private void showMoreOptions(ImageButton moreBtn, String keyFreeStory, String StoryImage, String uid , String myUid) {
        //creating popup menu currently having option Delete, we will add more options later
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //show delete option in only post(s) of currently signed in user
        if (uid.equals(myUid)) {
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
        }


        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    //delete is clicked
                    beginDelete(keyFreeStory, StoryImage);
                }

                return false;
            }
        });
        //show menu
        popupMenu.show();

    }

    private void beginDelete(String keyFreeStory, String StoryImage) {
        // post can be with or without image

        if (StoryImage.equals("noImage")){
            //post is without image
            deleteWhithoutImage(keyFreeStory);
        }
        else {
            //post is with image
            deleteWithImage(keyFreeStory,StoryImage);
        }
    }

    private void deleteWithImage(String keyFreeStory, String StoryImage) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        //step
    // 1) Delete Image using url
    // 2) Delete from database using post id

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(StoryImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted , now delete database

                        Query fquery = FirebaseDatabase.getInstance().getReference("StoryFree").orderByChild("keyFreeStory").equalTo(keyFreeStory);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); // remove values from firabase where pid matches
                                }
                                //deleted
                                Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed, can't go further
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteWhithoutImage(String keyFreeStory) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("StoryFree").orderByChild("keyFreeStory").equalTo(keyFreeStory);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); // remove values from firabase where pid matches
                }
                //deleted
                Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return listModelStoryFree.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //view from row_post.xml
        ImageView PictureAuthorStory, storyImage;
        TextView storyAuthor, storyTimePublish, storyTitle, storyDescription, storyLikes;
        ImageButton moreBtn,moreBtnAboutAuthor;
        ImageButton likeBtn;


        // тут можно прописать все что угодно... с запасом но при этом не использовать
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init views
            PictureAuthorStory = itemView.findViewById(R.id.PictureAuthorStory);
            storyImage = itemView.findViewById(R.id.storyImage);
            storyAuthor = itemView.findViewById(R.id.storyAuthor);
            storyTimePublish = itemView.findViewById(R.id.storyTimePublish);
            storyTitle = itemView.findViewById(R.id.storyTitle);
            storyDescription = itemView.findViewById(R.id.storyDescription);
            storyLikes = itemView.findViewById(R.id.storyLikes);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            moreBtnAboutAuthor = itemView.findViewById(R.id.moreBtnAboutAuthor);
            likeBtn = itemView.findViewById(R.id.likeBtn);

        }
    }


}
