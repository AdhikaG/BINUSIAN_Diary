package model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binusiandiary.NoteDetails;
import com.example.binusiandiary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> titles;
    List<String> content;

    public Adapter(List<String>title, List<String>content){
        this.titles= title;
        this.content=content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));

        //simpen color
       final  int code = getRandomColor();
        //biar tiap buka apps dia brubah warna notesnya
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(v.getContext(), NoteDetails.class);
                intent.putExtra("title",titles.get(position));
                intent.putExtra("content",content.get(position));
                intent.putExtra("code",code);
                v.getContext().startActivity(intent);
            }
        });
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
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteContent;

        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.titles);
            noteContent=itemView.findViewById(R.id.content);
            mCardView=itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }
}
