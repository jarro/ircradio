package com.cratorsoft.android.frag;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.cratorsoft.android.aamain.Action;
import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.adapter.TTSSpinnerManager;
import com.cratorsoft.android.confirmback.FragConfirmBackPressed;
import com.cratorsoft.android.db.DBInsert;
import com.cratorsoft.android.db.DBQAccount;
import com.cratorsoft.android.db.DBUpdate;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.cratorsoft.android.ttslanguage.TTSLanguage;
import com.cratorsoft.android.viewpager.PagerAdapterTab;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

public class FragEditAccount extends FragConfirmBackPressed {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;
    int mAction;
    Long mAccountId;
    private boolean mInitialized = false;

    // Misc
    private Map<String,Integer> charsetMap;

    // Data
    AccountData mAccountData;

    // Views
    EditText etaccountname;
    EditText etserver;
    EditText etserverpass;
    EditText etnick;
    EditText etport;
    Spinner  spinnerLanguage;

    CheckBox cbssl;
    //CheckBox cbguesscharset;

    CheckBox cbautoidentify;
    EditText etnickserv;
    EditText etnickpass;

    EditText etperform;

    CheckBox cbencodingoverride;
    Spinner spinnerSend;
    Spinner spinnerReceive;
    Spinner spinnerServer;

    CheckBox cbreconnect;
    EditText etreconnectinterval;
    EditText etreconnecttries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.parseIntent();
        loadRefreshDataTask();
        loadCreateDataTask();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_edit_account, container, false);
        return v;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configFrag();
    }

    @Override
    public void onStart() {
        super.onStart();
        // views bound
        //tab0
        etaccountname = (EditText)getView().findViewById(R.id.account);
        etserver = (EditText)getView().findViewById(R.id.server);
        etserverpass = (EditText)getView().findViewById(R.id.serverpass);
        etnick = (EditText)getView().findViewById(R.id.nick);
        etport = (EditText)getView().findViewById(R.id.port);
        cbssl = (CheckBox)getView().findViewById(R.id.ssl);
        //cbguesscharset = (CheckBox)getView().findViewById(R.id.guesscharset);
        spinnerLanguage = (Spinner)getView().findViewById(R.id.language);

        //tab1
        cbautoidentify = (CheckBox)getView().findViewById(R.id.autoidentify);
        etnickserv = (EditText)getView().findViewById(R.id.nickserv);
        etnickpass = (EditText)getView().findViewById(R.id.nickpass);

        //tab2
        etperform = (EditText)getView().findViewById(R.id.perform);

        //tab3
        cbencodingoverride = (CheckBox)getView().findViewById(R.id.encodingoverride);
        spinnerSend = (Spinner)getView().findViewById(R.id.encodingsend);
        spinnerReceive = (Spinner)getView().findViewById(R.id.encodingreceive);
        spinnerServer = (Spinner)getView().findViewById(R.id.encodingserver);

        //tab4
        cbreconnect = (CheckBox)getView().findViewById(R.id.reconnect);
        etreconnectinterval = (EditText)getView().findViewById(R.id.reconnectinterval);
        etreconnecttries = (EditText)getView().findViewById(R.id.reconnectretries);



    }

    @Override
    public void onResume() {
        setTitle();
        if (mInitialized) {
            loadRefreshDataTask();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInitialized) {
            stashData();
        }
    }

    // ======================== Fragment Data Lifecycle ====
    
    public void parseIntent(){
        Fargs fargs = Fargs.create(this);
        mAction = fargs.getAction();
        mAccountId = fargs.getAccount();
    }
    
    private void configViews() {

    }

    private void configFrag() {
        initPager();
        setCurrentTab();
    }

    private void stashData() {
        stashValues();
        stashSpinners();
    }

    private void unstashData() {
        attachAdapters();
        populateAccount();
    }

    private void initAllData() {
        attachAdapters();
        populateAccount();
    }

    // ======================== Fragment Functions =====
    
    private void setTitle(){
        getDrawerDisplayer().setFragTitle( this.getString(R.string.label_accounts), "");
    }

    private void initPager() {
        String[] tabTitles = new String[] { getString(R.string.account_serversettings),
                                            getString(R.string.account_identifysettings),
                                            getString(R.string.account_performsettings),
                                            getString(R.string.account_encodesettings),
                                            getString(R.string.account_reconnectsettings)};
        int[] contentviews = new int[] { R.layout.tab_edit_account_0, R.layout.tab_edit_account_1, R.layout.tab_edit_account_2,
                R.layout.tab_edit_account_3, R.layout.tab_edit_account_4 };

        ViewPager vp = (ViewPager) this.getView().findViewById(R.id.viewpager);
        vp.setOffscreenPageLimit(5);

        PagerAdapterTab adapter = new PagerAdapterTab(this.getChildFragmentManager(), tabTitles, contentviews);
        adapter.preload(this.getChildFragmentManager());
        vp.setAdapter(adapter);

        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) this.getView().findViewById(R.id.indicator);
        indicator.setViewPager(vp);

        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if(FragEditAccount.this.isResumed()){
                    Globo.tabaccount = arg0;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void setCurrentTab(){
        ViewPager vp = (ViewPager) this.getView().findViewById(R.id.viewpager);
        vp.setCurrentItem(Globo.tabaccount);
    }

    private void stashValues(){

        mAccountData.accountname = etaccountname.getText().toString();
        mAccountData.server = etserver.getText().toString();
        mAccountData.serverpass = etserverpass.getText().toString();
        mAccountData.nick = etnick.getText().toString();
        mAccountData.port = etport.getText().toString();
        mAccountData.ssl = String.valueOf(cbssl.isChecked());
        mAccountData.guesscharset = String.valueOf(false);

        try {
            mAccountData.language = ((TTSLanguage) spinnerLanguage.getSelectedItem()).getLanguageForDB();
        }catch(Exception e){
            mAccountData.language = "en";
        }


        mAccountData.autoidentify = String.valueOf(cbautoidentify.isChecked());
        mAccountData.nickserv = etnickserv.getText().toString();
        mAccountData.nickpass = etnickpass.getText().toString();

        mAccountData.perform = etperform.getText().toString();

        mAccountData.encodingoverride = String.valueOf(cbencodingoverride.isChecked());

        mAccountData.reconnect = String.valueOf(cbreconnect.isChecked());
        mAccountData.reconnectinterval = etreconnectinterval.getText().toString();
        mAccountData.reconnectretries = etreconnecttries.getText().toString();

    }



    // ======================== Fragment Actions =====

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (((DrawerDisplayer) this.getActivity()).drawerIsClosed()  ) {
            if (menu.findItem(R.id.ACTION_SAVE) == null) {
                menu.add(0, R.id.ACTION_SAVE, 0, "Save").setIcon(R.drawable.ic_save_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (isBusy()) {
            return true;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.ACTION_SAVE) {
            clickActionSave();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void clickActionSave() {
        stashData();
        saveTask();
    }

    // ======================== Fragment Background Tasks =====

    public void loadCreateDataTask() {

        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            AccountData accountData;
            int action = mAction;
            Long accountid = mAccountId;

            protected Boolean doInBackground() {

                try {

                    if (action == Action.EDIT){
                        accountData = DBQAccount.account(accountid);
                    }else {
                        accountData = AccountData.createForInsert();
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    // set data vars
                    mAccountData = accountData;

                    initAllData();
                    configViews();
                    mInitialized = true;
                } else {
                    abort("DB Error");
                }
            }
        }.execute();
    }

    public void loadRefreshDataTask() {

        cleanupRefreshDBConnections();
        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    // set data vars

                    if (mInitialized) {
                        unstashData();
                        configViews();
                    }

                } else {
                    abort("DB Error");
                }
            }
        }.execute();
    }

    public void saveTask() {

        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            AccountData accountdata = mAccountData;
            int action = mAction;


            protected Boolean doInBackground() {

                try {

                    boolean success = false;
                    if (action == Action.INSERT) {
                        success = DBInsert.account(accountdata);
                    } else {// Edit
                        success = DBUpdate.saveAccount(accountdata);
                    }

                    return success;

                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (mAction == Action.INSERT) {
                        getFragmentManager().popBackStack();
                    } else {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    Toast.makeText(this.getActivity(), "Error Saving, account.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    // ======================== Fragment Attach/Populate =====


    public void attachAdapters() {

        ArrayAdapter<String> adapterSpinnerReceive = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item);
        adapterSpinnerReceive.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReceive.setAdapter(adapterSpinnerReceive);

        ArrayAdapter<String> adapterSpinnerSend = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item);
        adapterSpinnerSend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSend.setAdapter(adapterSpinnerSend);

        ArrayAdapter<String> adapterSpinnerServer = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item);
        adapterSpinnerServer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServer.setAdapter(adapterSpinnerServer);

        //language spinner
        ArrayAdapter<TTSLanguage> languageAdapter = TTSSpinnerManager.INSTANCE.getTTSAdapter(this.getActivity());
        spinnerLanguage.setAdapter(languageAdapter);
        spinnerLanguage.setSelection((int) TTSSpinnerManager.INSTANCE.searchPosition(languageAdapter,mAccountData.language));


        //set spinner values
        Set<String> charsets = Charset.availableCharsets().keySet();

        Integer counter = 0;
        charsetMap = new HashMap<String,Integer>();
        for (String key : charsets ){

            charsetMap.put(key, counter);
            counter ++;
            adapterSpinnerSend.add(key);
            adapterSpinnerReceive.add(key);
            adapterSpinnerServer.add(key);
        }

        if(charsetMap.containsKey(mAccountData.encodingsend)) {
            spinnerSend.setSelection(charsetMap.get(mAccountData.encodingsend));
        }else{
            spinnerSend.setSelection(charsetMap.get("UTF-8"));
        }

        if(charsetMap.containsKey(mAccountData.encodingreceive)) {
            spinnerReceive.setSelection(charsetMap.get(mAccountData.encodingreceive));
        }else{
            spinnerReceive.setSelection(charsetMap.get("UTF-8"));
        }

        if(charsetMap.containsKey(mAccountData.encodingserver)) {
            spinnerServer.setSelection(charsetMap.get(mAccountData.encodingserver));
        }else{
            spinnerServer.setSelection(charsetMap.get("UTF-8"));
        }



    }

    private void populateAccount() {

        //tab0
        etaccountname.setText(mAccountData.accountname);
        etserver.setText(mAccountData.server);
        etserverpass.setText(mAccountData.serverpass);
        etnick.setText(mAccountData.nick);
        etport.setText(mAccountData.port);
        cbssl.setChecked(Boolean.valueOf(mAccountData.ssl));
        //cbguesscharset.setChecked(Boolean.valueOf(mAccountData.guesscharset));
        //tab1
        cbautoidentify.setChecked(Boolean.valueOf(mAccountData.autoidentify));
        etnickserv.setText(mAccountData.nickserv);
        etnickpass.setText(mAccountData.nickpass);
        //tab2
        etperform.setText(mAccountData.perform);
        //tab3
        cbencodingoverride.setChecked(Boolean.valueOf(mAccountData.encodingoverride));
        //tab4
        cbreconnect.setChecked(Boolean.valueOf(mAccountData.reconnect));
        etreconnectinterval.setText(mAccountData.reconnectinterval);
        etreconnecttries.setText(mAccountData.reconnectretries);

    }

    //========================= Stash Data =====================

    private void stashSpinners() {
        mAccountData.encodingsend = spinnerSend.getSelectedItem().toString();
        mAccountData.encodingreceive = spinnerReceive.getSelectedItem().toString();
        mAccountData.encodingserver = spinnerServer.getSelectedItem().toString();

        try {
            mAccountData.language = ((TTSLanguage) spinnerLanguage.getSelectedItem()).getLanguageForDB();
        }catch(Exception e){
            mAccountData.language = "en";
        }


    }


    //========================= Fragment Listeners ====================
    
    
    
}
