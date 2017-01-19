package com.frable.radio.jul;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import utilities.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationDrawerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationDrawerFragment extends Fragment {
    RecyclerView rvPlayList;
    RecyclerView rvTopPlayed;
    Activity activity;
    TextView tvPlayListTitle, tvTopPlayed;
    private BroadcastReceiver bReciver;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationDrawerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationDrawerFragment newInstance(String param1, String param2) {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        activity = getActivity();
        rvPlayList = (RecyclerView) view.findViewById(R.id.rv_play_list);
        rvTopPlayed = (RecyclerView) view.findViewById(R.id.rv_top_play);
        ImageView imvTwitterFollow = (ImageView) view.findViewById(R.id.imv_twitter_follow);
        TextView tvComposeMail = (TextView) view.findViewById(R.id.tv_email_compose);
        tvPlayListTitle = (TextView) view.findViewById(R.id.tv_playlist_title);
        tvTopPlayed = (TextView) view.findViewById(R.id.tv_top_played);
        rvPlayList.setLayoutManager(new LinearLayoutManager(activity));
        rvTopPlayed.setLayoutManager(new LinearLayoutManager(activity));
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        // ObjectAnimator.ofInt(scrollView, "scrollY",  0).setDuration(100).start();
        scrollView.smoothScrollTo(0, 100);
        new SongsDetailsAsync(true).execute(Constants.RECENT_PLAYED_LIST);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("completedAsync");
        bReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new SongsDetailsAsync(false).execute(Constants.TOP_TRACKS);
//                if (intent.getBooleanExtra("isPlaylistNull", false)) {
//                    tvPlayListTitle.setVisibility(View.GONE);
//                } else {
//                    tvPlayListTitle.setVisibility(View.VISIBLE);
//                }
//
//                if (intent.getBooleanExtra("isTopPlayedNull", false)) {
//                    tvTopPlayed.setVisibility(View.GONE);
//                } else {
//                    tvTopPlayed.setVisibility(View.VISIBLE);
//                }
            }
        };
        activity.registerReceiver(bReciver, intentFilter);
        imvTwitterFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });

        tvComposeMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmailCompose();
            }
        });

        return view;
    }

    private void gmailCompose() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "frubbleltd@gmail.com", null));
        startActivity(Intent.createChooser(emailIntent, null));
//        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//        intent.setType("text/plain");
//         final PackageManager pm = activity.getPackageManager();
//        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
//        ResolveInfo best = null;
//        for (final ResolveInfo info : matches)
//            if (info.activityInfo.packageName.endsWith(".gm") ||
//                    info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
//        if (best != null)
//            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
//        startActivity(intent);
    }

    public void sendTweet() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=radio_jul")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/radio_jul")));
        }
    }

    public void onButtonPressed(int position) {
        if (mListener != null) {
            mListener.onFragmentInteraction(position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    class SongsDetailsAsync extends AsyncTask<String, Void, Void> {
        private NodeList nodelist;

        private boolean isPlaylist;

        public SongsDetailsAsync(boolean isPlaylist) {
            this.isPlaylist = isPlaylist;
        }

        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                nodelist = doc.getElementsByTagName("track");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> playListAndTopTrackTitle = new ArrayList<>();
            if (null != nodelist) {
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    Element eElement = (Element) node;
                    String title = null;
                    title = getNode("title", eElement);
                    Log.d("demo", title);
                    playListAndTopTrackTitle.add(title);
                }
            }
            Intent bReciver = new Intent();
            if (isPlaylist) {
                bReciver.setAction("completedAsync");
            } else {

            }
            if (playListAndTopTrackTitle.size() == 0 && isPlaylist) {
                bReciver.putExtra("isPlaylistNull", true);
                tvPlayListTitle.setVisibility(View.GONE);
            } else {
                rvPlayList.setAdapter(new NavigationDrawerAdapter(playListAndTopTrackTitle));
                bReciver.putExtra("isPlaylistNull", false);
                tvPlayListTitle.setVisibility(View.VISIBLE);
            }

            if (playListAndTopTrackTitle.size() == 0 && !isPlaylist) {
                bReciver.putExtra("isTopPlayedNull", true);
                tvTopPlayed.setVisibility(View.GONE);
            } else {
                bReciver.putExtra("isTopPlayedNull", false);
                rvTopPlayed.setAdapter(new NavigationDrawerAdapter(playListAndTopTrackTitle));
                tvTopPlayed.setVisibility(View.VISIBLE);
            }
            activity.sendBroadcast(bReciver);
        }
    }

    class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
        ArrayList<String> titleList;

        public NavigationDrawerAdapter(ArrayList<String> titleList) {
            this.titleList = titleList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d("demoData", titleList.get(position));
            holder.tvTitle.setText(titleList.get(position));
        }

        @Override
        public int getItemCount() {
            return titleList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_played_title);

            }
        }

    }

    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int position);
    }
}
