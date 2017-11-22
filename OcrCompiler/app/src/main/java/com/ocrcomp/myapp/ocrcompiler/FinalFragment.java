package com.ocrcomp.myapp.ocrcompiler;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    //UI objects
    ProgressDialog m_ProressDialog;
    TextView tvl,tvoe;


    //Objects
    String code="";



    // TODO: Rename and change types of parameters
    private String mParam1;



    public FinalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment FinalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinalFragment newInstance(String param1) {
        FinalFragment fragment = new FinalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_final, container, false);
        code=mParam1;
        tvl=(TextView)v.findViewById(R.id.final_loading);
        tvoe=(TextView)v.findViewById(R.id.final_tvoe);
        tvl.setText(code);

        new SendPostRequest().execute("http://192.168.43.88/cp.php",code);

        return v;
    }

    private class SendPostRequest extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_ProressDialog=ProgressDialog.show(getActivity(),"Please wait...","Processing...",true);
            Toast.makeText(getActivity(),"sending data",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url=new URL(params[0]);

                JSONObject postDataParams=new JSONObject();
                postDataParams.put("code",params[1]);

                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                int responsecode=conn.getResponseCode();

                if(responsecode==HttpURLConnection.HTTP_OK)
                {
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuffer sb=new StringBuffer("");
                    String line="";

                    while ((line=in.readLine())!=null)
                    {
                        sb.append(line);
                    }
                    in.close();

                    return sb.toString();
                }
                else
                {
                    return  new String("false :"+responsecode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new String("Exception : "+e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jobj=null;
            String output=null;
            String error=null;
            try {
                jobj=new JSONObject(s);
                output=jobj.getString("output");
                error=jobj.getString("error");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if("".equals(error))
            {
                tvoe.setText("Output:-");
                tvl.setText(output);
            }
            else
            {
                String err=error.replace("^",System.getProperty("line.separator") + System.getProperty("line.separator"));
                tvoe.setText("Error:-");
                tvl.setText(err);
            }

            Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
            m_ProressDialog.dismiss();

        }
    }

    public String getPostDataString(JSONObject params)throws  Exception{
        StringBuilder result=new StringBuilder();
        boolean first=true;

        Iterator<String> itr=params.keys();

        while (itr.hasNext())
        {
            String key=itr.next();
            Object value=params.get(key);

            if(first)
                first=false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key,"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(),"UTF-8"));

        }

        return result.toString();

    }


}
