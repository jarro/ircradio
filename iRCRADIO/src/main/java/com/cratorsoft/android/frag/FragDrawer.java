package com.cratorsoft.android.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cratorsoft.android.aamain.AdapterDrawer;
import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.aamain.LTDrawerMenu;
import com.cratorsoft.android.aamain.RowDrawer;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.ircradio.ActAccountList;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.logmanager.LogManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment
public class FragDrawer extends BusyTaskFragment  {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;

    @ViewById(android.R.id.list)
    ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_drawer, container, false);

        //hide listdivider
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setDividerHeight(0);

		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
        getActivity().invalidateOptionsMenu();
        String mainTitle = this.getResources().getString(R.string.app_name);
        this.getDrawerDisplayer().setDrawerTitle(mainTitle, "");
        refreshTask();
	}


    @ItemClick(android.R.id.list)
    public void listViewItemClicked(RowDrawer item) {

        //Launch Activities
        /*
        if (item.id == LTDrawer.PREFERENCES) {
            ((DrawerDisplayer) getActivity()).drawerClose();
            Intent intent = new Intent(this.getActivity(),ActPreferences.class);
            this.getActivity().startActivity(intent);
            return;
        }
        */

        //Launch SubFragments
        /*
        if (item.id == LTDrawer.MANUAL) {
            getFragmentManager().popBackStack("listmanual", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragListManual_ fb = new FragListManual_();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("listmanual");
            ft.commit();
            ((DrawerDisplayer) getActivity()).drawerClose();
            return;
        }
        */

        //Launch Top Fragments


        FragmentTransaction ft = getFragmentManager().beginTransaction();


        if (item.id == LTDrawerMenu.ACCOUNTS) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragAccountList_ fb = new FragAccountList_();
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("accountlist");
            ft.commit();
        } else if (item.id == LTDrawerMenu.MESSAGING) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragListMessaging_ fb = new FragListMessaging_();
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("messaginglist");
            ft.commit();
        } else if (item.id == LTDrawerMenu.NOTIFICATIONS) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragListNotifications_ fb = new FragListNotifications_();
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("notificationlist");
            ft.commit();
        } else if (item.id == LTDrawerMenu.LISTCHANNELS) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragCommandListChannels_ fb = new FragCommandListChannels_();
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("commandlistchannels");
            ft.commit();
        } else if (item.id == LTDrawerMenu.CHAT) {
            clickChat(ft);
            ft.commit();
        }else if (item.id == LTDrawerMenu.PREFERENCES) {
            getFragmentManager().popBackStack("editprefs", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences);
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("editprefs");
            ft.commit();
        }



        ((DrawerDisplayer) getActivity()).drawerClose();

    }


    public void refreshTask() {

             new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

                 List<RowDrawer> listDrawer;

                 protected Boolean doInBackground() {
                     try {
                         listDrawer = RowDrawer.getListDrawer();
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
                         refreshList(listDrawer);
                     } else {
                         Toast.makeText(this.getActivity(), "DB Error Drawer", Toast.LENGTH_SHORT).show();
                         // this.getActivity().finish();
                     }
                 }
             }.execute();

         }

    public void refreshList(List<RowDrawer> listDrawer) {
             AdapterDrawer adapter = new AdapterDrawer(this.getActivity(), R.layout.row_drawer_detail, listDrawer);
             listView.setAdapter(adapter);
         }

	// MENU BUTTONS
	public void clickAccounts() {
		Intent accountIntent = new Intent(this.getActivity(), ActAccountList.class);
		this.startActivity(accountIntent);		
	}







	public void clickChat( FragmentTransaction ft) {


		NavItem next = LogManager.getActiveLog();
		if (next.acttype.equals("none")) {
			makeToast(this.getString(R.string.error_nochatlogs));

		} else {

            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

			Globo.pref_chan_showchannav = true;

            Bundle args = next.getBundle();

            FragChatLog_ fb = new FragChatLog_();
            fb.setArguments(args);
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("commandlistchannels");

		}


	}





	public void makeToast(String message) {

             try {
                 Globo.toast.cancel();
             } catch (Exception e) {
                 // TODO Auto-generated catch block
                 // e.printStackTrace();
             }

             Globo.toast = Toast.makeText(this.getActivity(), message,
                     Toast.LENGTH_LONG);
             Globo.toast.show();

             // Toast.makeText(this, message, Toast.LENGTH_LONG).show();
         }

}
