package com.example.myapplication_mapnavigationdrawer.ui.Help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication_mapnavigationdrawer.R;

public class HelpFragment extends Fragment {

    private ShareViewModel shareViewModel;
    private WebView webView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.webview_google_feedback, container, false);

        webView = root.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSdS-_rjUroT-wiCEjiRDHAMFpYAhcHaA--MkW5gkRrK4Ts1Hw/viewform?usp=sf_link");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        /*
        final TextView textView = root.findViewById(R.id.text_help);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    //@Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            onBackPressed();
        }

    }
}