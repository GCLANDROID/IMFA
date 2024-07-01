package com.genius.imfa.wfh;


import static com.genius.imfa.Leave.fragment.OtherApplicationFragment.getRealPath;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.Activity.AndroidXCameraActivity;
import com.genius.imfa.Leave.LeaveApplicationActivity;
import com.genius.imfa.Model.CompOffDetailsModel;
import com.genius.imfa.Model.DayBreakUpModel;
import com.genius.imfa.Model.LeaveBalanceDetailsModel;
import com.genius.imfa.Model.PrevieModel;
import com.genius.imfa.Model.SpinnerModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.FileToBase64Converter;
import com.genius.imfa.Utility.FindDocumentInformation;
import com.genius.imfa.Utility.ImageUtils;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.TimeDateConverter;
import com.genius.imfa.adapter.CompOffAdapter;
import com.genius.imfa.adapter.DayBreakUpAdapter;
import com.genius.imfa.adapter.LeaveBalanceDetailsAdapter;
import com.genius.imfa.adapter.PreviewAdapter;
import com.genius.imfa.databinding.FragmentOtherApplicationBinding;
import com.genius.imfa.databinding.FragmentWfhBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WFHApplication extends Fragment {
    private static final String TAG = "ApplicationFragment";
    FragmentWfhBinding binding;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWfhBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


}
