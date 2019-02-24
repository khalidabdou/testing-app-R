package com.example.clientimadradio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static db_manager dbase;
    static listadapter listadapter;
    static ListView LVStationsfav;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public Tab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter station1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab2 newInstance(String param1, String param2) {
        Tab2 fragment = new Tab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbase = new db_manager(getActivity());


        View viewlist = inflater.inflate(R.layout.fragment_tab2, container, false);
        LVStationsfav = viewlist.findViewById(R.id.lv_stationsfavorit);
        listadapter = new listadapter(Tab1.listF);
        LVStationsfav.setAdapter(listadapter);

        // Inflate the layout for this fragment
        return viewlist;
        // Inflate the layout for this fragment

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class listadapter extends BaseAdapter {
        ArrayList<class_itm> al_item = new ArrayList<>();

        listadapter(ArrayList<class_itm> al_item) {
            this.al_item = al_item;
        }

        @Override
        public int getCount() {
            return al_item.size();
        }

        @Override
        public Object getItem(int position) {
            return al_item.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view2 = layoutInflater.inflate(R.layout.item_radio, null);
            TextView txt_name = view2.findViewById(R.id.txt_itme);
            TextView txt_desc = view2.findViewById(R.id.txt_desc);
            final ImageView img_fav = view2.findViewById(R.id.img_favorite);
            LinearLayout linearLayout_click = view2.findViewById(R.id.ll_click);
            ImageView icon_st = view2.findViewById(R.id.iconst);

            icon_st.setImageDrawable(getResources().getDrawable(Tab1.geticon(al_item.get(position).ids)));
            txt_name.setText(al_item.get(position).name_station);
            txt_desc.setText(al_item.get(position).desc);
            img_fav.setImageDrawable(getResources().getDrawable(R.drawable.favor));


            try {
                img_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        img_fav.setImageDrawable(getResources().getDrawable(R.drawable.favorite0));
                        dbase.updatefav(al_item.get(position).ids, 1);
                        Toast.makeText(getActivity(), "remove", Toast.LENGTH_SHORT).show();
                        if (Tab1.listF.contains(al_item.get(position))) {
                            // Toast.makeText(getActivity(), String.valueOf(al_item.get(position).ids), Toast.LENGTH_SHORT).show();
                            int i = Tab1.liststations.indexOf(al_item.get(position));
                            Tab1.liststations.get(i).favorite = 0;
                            Tab1.listadapter.notifyDataSetChanged();
                            Tab1.listF.remove(al_item.get(position));
                            listadapter.notifyDataSetChanged();
                        }

                    }
                });
            } catch (Exception ex) {
                // Toast.makeText(getActivity(), String.valueOf(ex), Toast.LENGTH_SHORT).show();
            }

            linearLayout_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), al_item.get(position).name_station, Toast.LENGTH_SHORT).show();
                    MainActivity.play(al_item.get(position).url, 0, al_item.get(position).name_station);


                }
            });

            return view2;
        }
    }
}
