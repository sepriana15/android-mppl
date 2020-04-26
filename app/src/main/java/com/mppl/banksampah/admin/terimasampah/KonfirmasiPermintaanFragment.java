package com.mppl.banksampah.admin.terimasampah;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mppl.banksampah.R;
import com.mppl.banksampah.admin.RiwayatTukarSampahFragment;

import java.util.Objects;

public class KonfirmasiPermintaanFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA = "extra";
    public static final String tag = "tag";

    private DatabaseReference ref;

    private TextView pickedDate;
    private TextView tvTelpUser;
    private Spinner spnrJenisSampah;
    private Spinner spnrSatuan;
    private EditText edtJumlahSampah;
    private TextView tvPoinTransaksi;


    private String emailUser;
    private String tanggalPermintaan;
    private String jenisSampah;
    private String satuanSampah;
    private String jumlahSampah;
    private String poinSampah;
    private String telpUser;
    private String requestChildKey;

    private NavigationView navigation_view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_konfirmasi_permintaan_antar, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            emailUser = bundle.getString("UserEmail");
            tanggalPermintaan = bundle.getString("Tanggal");
            jumlahSampah = bundle.getString("JumlahSampah");
            satuanSampah = bundle.getString("Satuan");
            jenisSampah = bundle.getString("JenisSampah");
            poinSampah = String.valueOf(bundle.getInt("PoinTransaksi"));
            requestChildKey = bundle.getString("RequestChildKey");
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnOK = view.findViewById(R.id.btn_terima);
        btnOK.setOnClickListener(this);
        Button btnTolak = view.findViewById(R.id.btn_tolak);
        btnTolak.setOnClickListener(this);

        spnrJenisSampah = view.findViewById(R.id.spinner_jenis_sampah);
        spnrSatuan = view.findViewById(R.id.spinner_satuan);
        edtJumlahSampah = view.findViewById(R.id.edtJumlahSampah);
        tvPoinTransaksi = view.findViewById(R.id.tv_poin_transaksi_terima);

        TextView tvEmailUser = view.findViewById(R.id.userEmail);
        TextView tvTanggalPermintaan = view.findViewById(R.id.tglPermintaanAntar);
        tvTelpUser = view.findViewById(R.id.userTelephone);

        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(emailUser);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                telpUser = dataSnapshot.child("no_telp").getValue().toString();
                tvTelpUser.setText(telpUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        tvEmailUser.setText(emailUser.replace('_', '.'));
        tvTanggalPermintaan.setText(tanggalPermintaan);
        edtJumlahSampah.setText(jumlahSampah);
        tvPoinTransaksi.setText(poinSampah);
        spnrJenisSampah.setSelection((((ArrayAdapter) spnrJenisSampah.getAdapter()).getPosition(jenisSampah)));
        spnrSatuan.setSelection((((ArrayAdapter) spnrSatuan.getAdapter()).getPosition(satuanSampah)));


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

    }

    private void terimaRequest() {
        ref = FirebaseDatabase.getInstance().getReference().child("AntarSampah").child(emailUser).child(requestChildKey);
        ref.child("status").setValue("Berhasil");

        ref.child("poin").setValue(Integer.parseInt(tvPoinTransaksi.getText().toString()));
        ref.child("satuan").setValue(spnrSatuan.getSelectedItem().toString());
        ref.child("jenisSampah").setValue(spnrJenisSampah.getSelectedItem().toString());
        ref.child("berat").setValue(edtJumlahSampah.getText().toString());

        addUserPoint();
    }

    private void tolakRequest() {
        ref = FirebaseDatabase.getInstance().getReference().child("AntarSampah").child(emailUser).child(requestChildKey);
        ref.child("status").setValue("Tidak Berhasil");
    }

    private void addUserPoint() {

        final int poin = Integer.parseInt(tvPoinTransaksi.getText().toString());
        final DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(emailUser);
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userCurrentPoint = Objects.requireNonNull(dataSnapshot.child("point").getValue()).toString();

                int finalPoint = Integer.parseInt(userCurrentPoint) + poin;
                userDataRef.child("point").setValue(finalPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtJumlahSampah.getText().toString())) {
            edtJumlahSampah.setError("Harap masukkan jumlah sampah");
            result = false;
        } else if (spnrJenisSampah.getSelectedItem().toString().equals("Jenis Sampah")) {
            new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setMessage("Harap masukkan jenis sampah")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            result = false;

        } else if (spnrSatuan.getSelectedItem().toString().equals("Satuan")) {
            new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setMessage("Harap masukkan satuan sampah")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            result = false;
        }
        return result;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_terima) {
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setMessage("Terima permintaan ?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (validateForm()) {
                                terimaRequest();

                                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                                        .setMessage("Permintaan antar telah diterima, poin akan ditambah ke akun user")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                returnToList();
                                            }
                                        }).show();
                            }
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if (v.getId() == R.id.btn_tolak) {
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setMessage("Tolak permintaan ?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tolakRequest();

                            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                                    .setMessage("Permintaan antar user telah ditolak")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            returnToList();
                                        }
                                    }).show();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void returnToList() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RiwayatTukarSampahFragment()).commit();
    }

}
