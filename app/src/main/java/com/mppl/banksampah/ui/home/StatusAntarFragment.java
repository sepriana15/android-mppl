package com.mppl.banksampah.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.mppl.banksampah.R;

public class StatusAntarFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    private Button btnIsiForm;
    private Button btnStatusAntar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_status_antar, container, false);

        btnIsiForm = root.findViewById(R.id.btn_isi_form);
        btnIsiForm.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_isi_form){
            AntarSampahFragment fragment = new AntarSampahFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment, AntarSampahFragment.class.getSimpleName())
                    .addToBackStack(null).commit();
        }

    }
}
