package pl.pollub.android.rmlab1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class AdapterOcen extends
        RecyclerView.Adapter<AdapterOcen.OcenyViewHolder>
{
    //lista przechowujące modele ocen
    private List<ModelOceny> mListaOcen;
    //odwołanie do layoutu
    private LayoutInflater mPompka;

    //konstruktor
    public AdapterOcen(Activity kontekst, List<ModelOceny> listaOcen)
    {
        mPompka = kontekst.getLayoutInflater();
        System.out.println(listaOcen);
        this.mListaOcen = listaOcen;
    }

    @NonNull
    @Override
    public OcenyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.oceny_row_item, parent, false);

        return new OcenyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OcenyViewHolder holder, int position) {
        holder.tekst.setText(mListaOcen.get(position).nazwa);
        holder.grupaOcen.setTag(mListaOcen.get(position));
        System.out.println((int)(mListaOcen.get(position).ocena*10));
        System.out.println(holder.grupaOcen.getChildAt(0).getId());
        switch ((int)(mListaOcen.get(position).ocena*10)) {
            case 20:
                holder.grupaOcen.check(R.id.radioButton4);
                break;
            case 30:
                holder.grupaOcen.check(R.id.radioButton5);
                break;
            case 35:
                ((RadioButton) holder.grupaOcen.getChildAt(2)).setChecked(true);
                break;
            case 40:
                holder.grupaOcen.check(holder.grupaOcen.getChildAt(3).getId());
                break;
            case 45:
                holder.grupaOcen.check(holder.grupaOcen.getChildAt(4).getId());
                break;
            case 50:
                holder.grupaOcen.check(holder.grupaOcen.getChildAt(5).getId());
                break;

        }
//        holder.mGrupaOceny.check(R.id.ocena2Button);
    }

    @Override
    public int getItemCount() {
        return mListaOcen.size();
    }

    public class OcenyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tekst;
        RadioGroup grupaOcen;



        public OcenyViewHolder(@NonNull View glownyElementWiersza) {
            super(glownyElementWiersza);
            tekst = (TextView) glownyElementWiersza.findViewById(R.id.nazwa);
            grupaOcen = (RadioGroup) glownyElementWiersza.findViewById(R.id.oceny_grupa);
            grupaOcen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    ModelOceny element = (ModelOceny) radioGroup.getTag();
                    element.ocena = Float.parseFloat(((RadioButton) radioGroup.findViewById(i)).getText().toString());
                }
            });
        }

    }

}