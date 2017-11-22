package com.ocrcomp.myapp.ocrcompiler;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowImage.OnShowImageListener} interface
 * to handle interaction events.
 * Use the {@link ShowImage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowImage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private int mParam1;


    //UI objects
    ImageView imv;
    Button brc;
    Bitmap bitmap=null,newbitmap=null,gbitmap=null;
    private ProgressDialog m_ProressDialog;


    //Objects
    TessBaseAPI mTess=MainActivity.mTess;

    //variables
    int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_GALLERY = 2;
    String mCurrentPhotoPath=null;
    String Ocrresult=null;
    boolean cam=false;

    private OnShowImageListener mListener;


    public ShowImage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment ShowImage.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowImage newInstance(int param1) {
        ShowImage fragment = new ShowImage();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_show_image, container, false);
        imv=(ImageView)v.findViewById(R.id.imv);
        brc=(Button)v.findViewById(R.id.brocr);


        if(REQUEST_TAKE_PHOTO==mParam1)
        {
            cam=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dispatchTakePictureIntent();
                }
            }).start();

        }
        else if(REQUEST_GALLERY==mParam1)
        {
            cam=false;
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent , REQUEST_GALLERY );

        }

        brc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Task().execute();

            }
        });




        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onShowImageInteraction(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShowImageListener) {
            mListener = (OnShowImageListener) context;
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
    public interface OnShowImageListener {
        // TODO: Update argument type and name
        void onShowImageInteraction(String data);
    }

    //Asynctask**
    public class Task extends AsyncTask<Void,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_ProressDialog=ProgressDialog.show(getActivity(),"Please wait...","Processing...",true);
        }

        @Override
        protected String doInBackground(Void... params) {

            if(bitmap!=null) {
                processImage();

                if(Ocrresult!=null) {
                    return Ocrresult;
                }
                else
                    Toast.makeText(getActivity(),"no code extracted!!!",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getActivity(),"Please choose an Image!!!",Toast.LENGTH_LONG).show();

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            m_ProressDialog.dismiss();
            onButtonPressed(s);


        }
    }


    //**



    //required methods...
    private void dispatchTakePictureIntent() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();

                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }

            }
        }

        ).start();
    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_TAKE_PHOTO && resultCode==getActivity().RESULT_OK){
            handleBigCameraPhoto();
        }
        else if(requestCode==REQUEST_GALLERY && resultCode==getActivity().RESULT_OK)
        {
            Uri imageUri = data.getData();

            if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                if (cursor == null || cursor.getCount() < 1) {
                    mCurrentPhotoPath = null;

                }
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                if(columnIndex < 0) { // no column index
                    mCurrentPhotoPath = null;

                }
                File file = new File(cursor.getString(columnIndex));
                mCurrentPhotoPath= file.getAbsolutePath();
                Toast.makeText(getActivity(), " file path not null ", Toast.LENGTH_SHORT).show();
                cursor.close();
            } else {
                mCurrentPhotoPath = null;
            }

            handleBigCameraPhoto();
        }
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
        }

    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imv.getWidth();
        int targetH = imv.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


		/* Associate the Bitmap to the ImageView */
        imv.setImageBitmap(bitmap);

        imv.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Scale factor = "+scaleFactor, Toast.LENGTH_SHORT).show();
    }

    public void processImage(){

        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,"!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?1234567890");
//@@@@@@@@@@@@@@@@@@2
        newbitmap=setGrayScale(bitmap);

        newbitmap=removeNoise(newbitmap);

        //@@@@@@@@@@@@@@@@@@

        mTess.setImage(newbitmap);
        Ocrresult = mTess.getUTF8Text();
    }



    //set gray scale
    private Bitmap setGrayScale(Bitmap img){
        Bitmap bmap=img.copy(img.getConfig(),true);

        int c;
        for(int i=0;i<bmap.getWidth();i++)
        {
            for(int j=0;j<bmap.getHeight();j++)
            {
                c=bmap.getPixel(i,j);
                byte gray=(byte)(0.299* Color.red(c) + .587 * Color.green(c) + .114 * Color.blue(c));
                bmap.setPixel(i,j,Color.argb(255,gray,gray,gray));
            }
        }
        return bmap;
    }

    //remove noise
    private Bitmap removeNoise(Bitmap img){

        Bitmap bmap=img.copy(img.getConfig(),true);
        int r,g,b;
        for(int x=0;x<bmap.getWidth();x++)
        {
            for(int y=0;y<bmap.getHeight();y++)
            {
                int pixel=bmap.getPixel(x,y);
                r=Color.red(pixel);
                g=Color.green(pixel);
                b=Color.blue(pixel);
                if(r<162 && g<162 && b<162)
                {
                    bmap.setPixel(x,y,Color.BLACK);
                }
                else if(r>162 && g>162 && b>162)
                {
                    bmap.setPixel(x,y,Color.WHITE);
                }
            }
        }
        return bmap;
    }


    //...
}
