package com.example.qrcode;
// My Imports
// ---------------------
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    // My Elements
    // -----------
    private Button buttonScan;
    private EditText etName;
    private EditText etAddress;

    // My Properties
    // -------------
    private IntentIntegrator _qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjusting the Padding for the System Bars
        // -----------------------------------------
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Getting my Activity Elements
        // ----------------------------
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        buttonScan = findViewById(R.id.buttonScan);

        // Setting up the QR Code scanner
        // ------------------------------
        _qrScan = new IntentIntegrator(this);

        // For when the user wishes to begin Scanning, Activate the following
        // ------------------------------------------------------------------
        buttonScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                _qrScan.initiateScan();
            }
        });

        // Once the details from the QR Code are obtained, the User may Touch the Address Bar to go to the Link
        // ----------------------------------------------------------------------------------------------------
        etAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String websiteUrl = etAddress.getText().toString();
                RedirectUser(websiteUrl);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Once the User scans the QR code, grab the data
        // ----------------------------------------------
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            // Error Correction to see if the QR Code has Data
            // -----------------------------------------------
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    // I Convert the Contents of the QR Code from String to JSON to Access Specific Pieces of Data
                    // -------------------------------------------------------------------------------------------
                    JSONObject obj = new JSONObject(result.getContents());

                    // Read in the Title and Website URL then Populate the Corresponding Elements
                    // ---------------------
                    etName.setText(obj.getString("title"));
                    etAddress.setText(obj.getString("website"));
                }
                catch (JSONException e)
                {
                    // Display the error
                    // -----------------
                    e.printStackTrace();

                    // Alert the user something went wrong
                    // -----------------------------------
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void RedirectUser(String websiteUrl)
    {
        // Take the User to the URL Destination
        // ------------------------------------
        Uri websiteUri = Uri.parse(websiteUrl);
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, websiteUri);
        startActivity(websiteIntent);
    }
}
