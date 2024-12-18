package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.model.MpcPath;
import com.github.warren_bank.mpcfreemote.model.Server;

import com.eeeeeric.mpc.hc.api.FileInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DirListingView extends MpcFragment
                            implements View.OnClickListener,
                                       View.OnLongClickListener,
                                       PopupMenu.OnMenuItemClickListener,
                                       MpcPath.UICallback {

    public interface DirListingCallback {
        void onOpenFileRequest(final FileInfo fileInfo);
    }

    private DirListEntry_ViewAdapter dirViewAdapter;
    private DirListingCallback callback = null;
    private MpcPath mpcPath = null;

    /* Mostly Android boilerplate                               */
    /************************************************************/
    public DirListingView() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dir_listing_view, container, false);
        dirViewAdapter = new DirListEntry_ViewAdapter(this, this, getActivity());
        ((ListView) v.findViewById(R.id.wDirListing_List)).setAdapter(dirViewAdapter);
        v.findViewById(R.id.wDirListing_PopupMenu).setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            this.callback = (DirListingCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DirListingCallback");
        }

        this.mpcPath = new MpcPath(mpcProvider, context, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
        this.mpcPath = null;
    }

    /* Display & event handling                                 */
    /************************************************************/

    @Override
    public void onResume() {
        super.onResume();
        triggerCurrentPathListUpdate();
    }

    @Override
    public void onClick(View v) {
        MpcPath.ExtraFileInfo fileInfo = (MpcPath.ExtraFileInfo) v.getTag();

        switch (v.getId()) {
            case R.id.wDirListElement_Name:
                if (fileInfo == null) throw new RuntimeException(DirListingView.class.getName() + " received a menu item with no tag");
                if (fileInfo.isDirectory()) {
                    mpcPath.cd(fileInfo.filePath);
                    triggerCurrentPathListUpdate();
                } else {
                    onOpenFileRequest(fileInfo);
                }

                break;

            case R.id.wDirListElement_AlreadySeen:
                toggleItemSeen(fileInfo);
                break;

            case R.id.wDirListing_PopupMenu:
                showPopupMenu();
                break;

            default:
                throw new RuntimeException(DirListingView.class.getName() + " received a click event it can't handle.");
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // If a user long-pressed a file marked as viewed, unmark it
        MpcPath.ExtraFileInfo fileInfo = (MpcPath.ExtraFileInfo) v.getTag();
        return toggleItemSeen(fileInfo);
    }

    boolean toggleItemSeen(final MpcPath.ExtraFileInfo fileInfo) {
        if (fileInfo == null) throw new RuntimeException(DirListingView.class.getName() + " long-pressed a menu item with no tag");
        if (fileInfo.isDirectory()) {
            // Nothing to do with dirs
            return false;
        }

        fileInfo.wasPlayedBefore = ! fileInfo.wasPlayedBefore;
        this.dirViewAdapter.notifyDataSetChanged();

        // Save to DB
        mpcPath.toggleSeen(fileInfo.filePath, fileInfo.wasPlayedBefore);

        if (!fileInfo.wasPlayedBefore) {
            // If item is marked as not played before it means the user toggled it
            // Add a pop up notification to let the user know what this feature is
            CharSequence msg = getString(R.string.playlist_item_marked_as_unseen);
            Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
            toast.show();
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.wDirListing_Bookmark:
                saveCurrentPathAsBookmark();
                break;

            case R.id.wDirListing_JumpToBookmark:
                jumpToBookmark();
                break;

            case R.id.wDirListing_ManageBookmark:
                deleteBookmark();
                break;

            default:
                throw new RuntimeException(DirListingView.class.getName() + " received a menu event it can't handle.");
        }

        return true;
    }


    /* UI stuff                                                 */
    /************************************************************/

    private void showPopupMenu() {
        final View menu = requireView().findViewById(R.id.wDirListing_PopupMenu);
        final PopupMenu popup = new PopupMenu(getContext(), menu);
        popup.getMenuInflater().inflate(R.menu.fragment_dir_listing_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private void triggerCurrentPathListUpdate() {
        // If there's no activity we're not being displayed, so it's better not to update the UI
        final FragmentActivity activity = getActivity();
        if (!isAdded() || activity == null) return;

        mpcPath.updateDirContents();

        dirViewAdapter.clear();
        ((TextView) activity.findViewById(R.id.wDirListing_CurrentPath)).setText(mpcPath.getCWD());
        activity.findViewById(R.id.wDirListing_List).setEnabled(false);
        activity.findViewById(R.id.wDirListing_LoadingIndicator).setVisibility(View.VISIBLE);
    }

    @Override
    public void onNewDirListAvailable(List<MpcPath.ExtraFileInfo> results) {
        // If there's no activity we're not being displayed, so it's better not to update the UI
        final FragmentActivity activity = getActivity();
        if (!isAdded() || activity == null) return;

        activity.findViewById(R.id.wDirListing_List).setEnabled(true);
        activity.findViewById(R.id.wDirListing_LoadingIndicator).setVisibility(View.GONE);

        dirViewAdapter.clear();
        dirViewAdapter.addAll(results);
    }

    private void onOpenFileRequest(final MpcPath.ExtraFileInfo fileInfo) {
        if (fileInfo.isDirectory()) {
            // Nothing to do with dirs
            return;
        }

        // The model (MpcPath) will update the "played before" flag in the database,
        // but we also need to update the UI right now;
        // otherwise the user will need to refresh the directory to see the "seen" flag.
        fileInfo.wasPlayedBefore = true;
        this.dirViewAdapter.notifyDataSetChanged();

        callback.onOpenFileRequest(fileInfo);

        mpcPath.toggleSeen(fileInfo.filePath, true);
    }

    public void onServerChanged(final Server srv) {
        if (mpcPath != null) {
            mpcPath.onServerChanged(srv);
            if (dirViewAdapter != null) {
                dirViewAdapter.clear();
                triggerCurrentPathListUpdate();
            }
        }
    }

    private void saveCurrentPathAsBookmark() {
        mpcPath.bookmarkCurrentDirectory();

        final String msg = String.format(getResources().getString(R.string.dir_listing_saved_bookmark), mpcPath.getCWD());
        Toast toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    // TODO: Move to a Fragment?
    private interface BookmarkCallback {
        void onBookmarkSelected(final String bookmarkedPath);
    }

    private void jumpToBookmark() {
        displayBookmarkPicker(R.string.dir_listing_goto_bookmark_title, new BookmarkCallback() {
            @Override
            public void onBookmarkSelected(final String bookmarkedPath) {
                mpcPath.cd(bookmarkedPath);
                triggerCurrentPathListUpdate();
            }
        });
    }

    private void deleteBookmark() {
        displayBookmarkPicker(R.string.dir_listing_delete_bookmark_title, new BookmarkCallback() {
            @Override
            public void onBookmarkSelected(final String bookmarkedPath) {
                mpcPath.deleteBookmark(bookmarkedPath);
            }
        });
    }

    private void displayBookmarkPicker(int titleStringId, final BookmarkCallback cb) {
        List<String> bookmarks = mpcPath.getBookmarks();

        final List<String> bookmarkedPaths = new ArrayList<>();
        for (String bookmark : bookmarks) {
            bookmarkedPaths.add(bookmark);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(requireActivity().getString(titleStringId));
        builder.setItems(bookmarkedPaths.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String bookmarkedPath = bookmarkedPaths.get(which);
                cb.onBookmarkSelected(bookmarkedPath);
            }
        });

        builder.show();
    }


    /* List view stuff                                          */
    /************************************************************/
    private static class DirListEntry_ViewAdapter extends ArrayAdapter<MpcPath.ExtraFileInfo> {
        private static final int layoutResourceId = R.layout.fragment_dir_listing_list_element;

        final private LayoutInflater inflater;
        final private View.OnClickListener onClickCallback;
        final private View.OnLongClickListener onLongClickCallback;

        DirListEntry_ViewAdapter(View.OnClickListener onClickCallback,
                                 View.OnLongClickListener onLongClickCallback, Context context) {
            super(context, layoutResourceId, new ArrayList<MpcPath.ExtraFileInfo>());
            this.inflater = ((Activity) context).getLayoutInflater();
            this.onClickCallback = onClickCallback;
            this.onLongClickCallback = onLongClickCallback;
        }

        static class Row {
            MpcPath.ExtraFileInfo fileInfo;
            ImageView dirOrFile;
            TextView fName;
            ImageView alreadySeen;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final View row;
            if (convertView == null) {
                row = inflater.inflate(layoutResourceId, parent, false);
            } else {
                row = convertView;
            }

            Row holder = new Row();
            holder.fileInfo = this.getItem(position);

            holder.dirOrFile = row.findViewById(R.id.wDirListElement_DirOrFile);
            if (!holder.fileInfo.isDirectory()) {
                holder.dirOrFile.setVisibility(View.INVISIBLE);
            } else {
                holder.dirOrFile.setVisibility(View.VISIBLE);
            }

            holder.fName = row.findViewById(R.id.wDirListElement_Name);
            holder.fName.setText(holder.fileInfo.getFileName());
            holder.fName.setTag(holder.fileInfo);
            holder.fName.setOnClickListener(onClickCallback);
            holder.fName.setOnLongClickListener(onLongClickCallback);
            holder.fName.setLongClickable(true);

            holder.alreadySeen = row.findViewById(R.id.wDirListElement_AlreadySeen);
            holder.alreadySeen.setTag(holder.fileInfo);
            holder.alreadySeen.setOnClickListener(onClickCallback);
            holder.alreadySeen.setOnLongClickListener(onLongClickCallback);
            holder.alreadySeen.setLongClickable(true);
            if (holder.fileInfo.wasPlayedBefore) {
                holder.alreadySeen.setVisibility(View.VISIBLE);
            } else {
                holder.alreadySeen.setVisibility(View.INVISIBLE);
            }

            row.setTag(holder);

            return row;
        }
    }
}
