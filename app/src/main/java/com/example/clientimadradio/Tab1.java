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
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static listadapter listadapter;
    static ArrayList<class_items> listF = new ArrayList<>();
    static ArrayList<class_items> liststations;
    db_manager dbase;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter station1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab1 newInstance(String param1, String param2) {
        Tab1 fragment = new Tab1();
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
        listF.clear();
        dbase = new db_manager(getActivity());
        liststations = new ArrayList<>();
        liststations = dbase.getsations();

        View viewlist = inflater.inflate(R.layout.fragment_tab1, container, false);
        ListView LVStations = viewlist.findViewById(R.id.lv_stations);
        listadapter = new listadapter(liststations);
        LVStations.setAdapter(listadapter);

        // Inflate the layout for this fragment
        return viewlist;
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

    public static class class_items {
        int ids;
        String img;
        String name_station;
        String desc;
        String url;
        int favorite;

        class_items(int ids, String img, String name_station, String desc, String url, int favorite) {
            this.ids = ids;
            this.img = img;
            this.name_station = name_station;
            this.desc = desc;
            this.url = url;
            this.favorite = favorite;
        }

    }

    public class listadapter extends BaseAdapter {
        ArrayList<class_items> al_item = new ArrayList<>();

        listadapter(ArrayList<class_items> al_item) {
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

            icon_st.setImageDrawable(getResources().getDrawable(geticon(al_item.get(position).ids)));
            txt_name.setText(al_item.get(position).name_station);
            txt_desc.setText(al_item.get(position).desc);
            if (al_item.get(position).favorite == 1) {
                img_fav.setImageDrawable(getResources().getDrawable(R.drawable.favor));
            }

            try {
                img_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (al_item.get(position).favorite == 1) {
                            img_fav.setImageDrawable(getResources().getDrawable(R.drawable.favorite0));
                            dbase.updatefav(al_item.get(position).ids, 1);
                            al_item.get(position).favorite = 0;
                            if (listF.contains(al_item.get(position)))
                                listF.remove(al_item.get(position));
                        } else {
                            img_fav.setImageDrawable(getResources().getDrawable(R.drawable.favor));
                            dbase.updatefav(al_item.get(position).ids, 0);
                            al_item.get(position).favorite = 1;
                            if (!listF.contains(al_item.get(position))) {
                                listF.add(al_item.get(position));
                            }
                        }
                        Tab2.listadapter.notifyDataSetChanged();


                    }
                });
            } catch (Exception ex) {
                Toast.makeText(getActivity(), String.valueOf(ex), Toast.LENGTH_SHORT).show();
            }

            linearLayout_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.icon_play.setImageDrawable(getResources().getDrawable(geticon(al_item.get(position).ids)));
                    MainActivity.play(al_item.get(position).url, 0, al_item.get(position).name_station);

                }
            });


            //filtration
            if (al_item.get(position).favorite == 1 && !listF.contains(al_item.get(position))) {

                listF.add(al_item.get(position));
            }
            return view2;
        }
    }
    public static int geticon(int id) {
        HashMap<Integer, Integer> array_image = new HashMap<Integer, Integer>();

        array_image.put(1, R.drawable.st1);
        array_image.put(2, R.drawable.st2);
        array_image.put(3, R.drawable.st3);
        array_image.put(4, R.drawable.st4);
        array_image.put(5, R.drawable.st5);
        array_image.put(6, R.drawable.st6);
        array_image.put(7, R.drawable.st7);
        array_image.put(8, R.drawable.st8);
        array_image.put(9, R.drawable.st9);
        array_image.put(10, R.drawable.st10);
        array_image.put(11, R.drawable.st11);
        array_image.put(13, R.drawable.st13);
        array_image.put(14, R.drawable.st14);
        array_image.put(15, R.drawable.st15);
        array_image.put(16, R.drawable.st16);
        array_image.put(17, R.drawable.st17);
        array_image.put(20, R.drawable.st20);
        array_image.put(23, R.drawable.st23);
        array_image.put(26, R.drawable.st26);
        array_image.put(44, R.drawable.st44);
        array_image.put(45, R.drawable.st45);
        array_image.put(46, R.drawable.st46);
        array_image.put(47, R.drawable.st47);
        array_image.put(50, R.drawable.st50);
        array_image.put(52, R.drawable.st52);
        array_image.put(53, R.drawable.st53);
        array_image.put(55, R.drawable.st55);
        array_image.put(57, R.drawable.st57);
        array_image.put(69, R.drawable.st69);
        array_image.put(81, R.drawable.st81);
        array_image.put(82, R.drawable.st82);
        array_image.put(83, R.drawable.st83);
        array_image.put(84, R.drawable.st84);
        array_image.put(85, R.drawable.st85);
        array_image.put(109, R.drawable.st109);

        return array_image.get(id);
    }

}
