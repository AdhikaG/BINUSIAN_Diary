package com.example.binusiandiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.binusiandiary.authentication.Register;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import model.Note;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;

    Adapter adapter;

    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note,NoteViewHolder>noteAdapter;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


                                                                      //Return USERID Firebase
        Query query =fStore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        //cara kerja masukin query Notes ke dalam tiap user Id yand didalamnya ada MyNotes->isinya banyak notes


        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>().
                setQuery(query,Note.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                //simpen color
                final  int code = getRandomColor();
                //biar tiap buka apps dia brubah warna notesnya
                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));

                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(v.getContext(), NoteDetails.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("code",code);
                        intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);
                    }
                });
                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(final View view) {
                        //Toast.makeText(MainActivity.this, "Menu Keluar", Toast.LENGTH_SHORT).show();
                        PopupMenu menu = new PopupMenu(view.getContext(),view);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
//                                Toast.makeText(MainActivity.this, "Edit Click", Toast.LENGTH_SHORT).show();

                                final  String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                                Intent intent = new Intent(view.getContext(),EditNote.class);
                                intent.putExtra("title",note.getTitle());
                                intent.putExtra("content",note.getContent());
                                intent.putExtra("noteId",docId);
                                startActivity(intent);
                                return false;
                            }
                        });
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                DocumentReference documentReference = fStore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    //berhasil delete
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //gagal
                                        Toast.makeText(MainActivity.this, "Delete not successfull", Toast.LENGTH_SHORT).show();
                                    }
                                });
                               Toast.makeText(MainActivity.this, "Notes deleted", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                        menu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                final  String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                                ApplicationInfo applicationInfo = getApplicationContext().getApplicationInfo();

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra("content",note.getContent());
                                intent.setType("text/plain");
                                String ShareText = note.getContent();
                                intent.putExtra(Intent.EXTRA_SUBJECT,ShareText);
                                startActivity(Intent.createChooser(intent,"Share Notes Using"));
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        noteLists= findViewById(R.id.noteList);

        drawerLayout = findViewById(R.id.drawer);
        nav_view=findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

// tembak data manual ke recycle view
//        List<String>titles = new ArrayList<>();
//        List<String>content = new ArrayList<>();
//
//        titles.add("1 Note Title");
//        content.add("1 note content");
//        titles.add("2 Note Title");
//        content.add("2 note content");
//        titles.add("3 Note Title");
//        content.add("3 note content");
//        adapter = new Adapter(titles,content);

        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userName_TextView);
        TextView userEmail = headerView.findViewById(R.id.userEmail_TextView);

        if(firebaseUser.isAnonymous()){
            userEmail.setText("Email not signed in");
            username.setText("Temporary User");
        }else {
            userEmail.setText(firebaseUser.getEmail());
            username.setText(firebaseUser.getDisplayName());
        }

        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),AddNotes.class));
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.addnotes:
                startActivity(new Intent(this,AddNotes.class));
               overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                break;
            case R.id.syncnotes:
                if (firebaseUser.isAnonymous()){
                    startActivity(new Intent(this,Register.class));
                    finish();
//                    overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                }else{
                    Toast.makeText(this, "You are Connected", Toast.LENGTH_SHORT).show();
                }
                break;
//                startActivity(new Intent(this,Register.class));
            case R.id.logoutapp:
                checkUser();
                break;
            default:
                Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        //validasi user tu punya akun ato ga
        if(firebaseUser.isAnonymous()){
            displayAlert();

        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Help.class));
//            overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
            finish();

        }
    }

    private void displayAlert() {

        AlertDialog.Builder warning = new AlertDialog.Builder(this, R.style.AlertDialog)

                .setTitle("Are you sure?")
                .setMessage("You are logged in with a temporary account. Logging out will delete your previous notes")
                .setPositiveButton("Sync your Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
//                        overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                        finish();

                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete Notes Anon User

                        //Delete Anon USer

                        firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),Help.class));
//                                overridePendingTransition(R.anim.animation_down,R.anim.animation_up);
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Toast.makeText(this,"Settings menu is clicked",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle;
        TextView noteContent;

        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemVIew){
            super(itemVIew);

            noteTitle=itemView.findViewById(R.id.titles);
            noteContent=itemView.findViewById(R.id.content);
            mCardView=itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }
    private int getRandomColor(){
        //buat color trus dia pick sesuai yang ada di file color list
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.blue);
        colorcode.add(R.color.yellow);
        colorcode.add(R.color.red);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightPurple);
        colorcode.add(R.color.lightGreen);

        Random randomcolor = new Random();
        int ColorIndexnumber = randomcolor.nextInt(colorcode.size());
        return  colorcode.get(ColorIndexnumber);
    }

    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }
}
