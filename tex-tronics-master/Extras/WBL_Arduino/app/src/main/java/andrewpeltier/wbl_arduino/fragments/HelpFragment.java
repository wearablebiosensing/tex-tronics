package andrewpeltier.wbl_arduino.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import andrewpeltier.wbl_arduino.R;

public class HelpFragment extends Fragment
{
    private static final String TAG = "HelpFragment";
    PDFView pdfView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        pdfView = view.findViewById(R.id.pdfView);

        // Loads PDF from asset folder
        pdfView.fromAsset("understanding-machine-learning-theory-algorithms.pdf")
                            .load(); //TODO: Change to new help pdf name

        // Optional: Loads PDF from web URL
//        new RetrievePDFStream().execute("http://ancestralauthor.com/download/sample.pdf");
        return view;
    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == 200)
                {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            catch (IOException e)
            {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream)
        {
            pdfView.fromStream(inputStream).load();
        }
    }
}
