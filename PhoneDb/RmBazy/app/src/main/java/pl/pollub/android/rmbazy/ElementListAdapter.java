package pl.pollub.android.rmbazy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ElementViewHolder> {
    private List<Element> mElementList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public class ElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView marka;
        TextView model;
        Element mElement = null;

        public ElementViewHolder(@NonNull View glownyElementWiersza) {
            super(glownyElementWiersza);
            marka = glownyElementWiersza.findViewById(R.id.marka);
            model = glownyElementWiersza.findViewById(R.id.model);
            itemView.setOnClickListener(this); // wywoluje onClick
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClickListener(mElement);
            //kliknięto wiersz listy, wywołując metodę z mOnItemClickListener
            //powiadamiamy aktywności który element został wybrany

        }

    }

    public ElementListAdapter(Context context, OnItemClickListener onItemClickListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mElementList = null;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater
                .inflate(R.layout.list_row, parent, false);

        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        holder.marka.setText(mElementList.get(position).getMarka());
        holder.model.setText(mElementList.get(position).getModel());
        holder.mElement = mElementList.get(position);
    }

    @Override
    public int getItemCount() {
        //w momencie tworzenia obiektu adaptera lista może nie być dostępna
        if (mElementList != null)
            return mElementList.size();
        return 0;
    }
    //ponieważ dane wyświetlane na liście będą się zmieniały ta metoda umożliwia aktualizację
    //danych w adapterze (i w konsekwencji) wyświetlanych w RecyclerView
    public void setElementList(List<Element> elementList) {
        this.mElementList = elementList;
        notifyDataSetChanged();
    }

    //interfejs listenera, który będzie informowany o tym, jaki element wybrano
    interface OnItemClickListener {
        void onItemClickListener(Element element);
        // przekaż mi coś co implementuje taki listener, a ja to ustawię na onclick mojego wiersza
    }



}