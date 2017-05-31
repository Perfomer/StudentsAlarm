package views;

import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.drivemode.android.typeface.TypefaceHelper;

import java.util.List;

import activities.AlarmSettingsActivity;
import activities.ApplicationWrapper;
import activities.MainActivity;
import managers.Algorithms;
import managers.TimeHandler;
import objects.Alarm;
import objects.AlarmModel;
import volkovmedia.perfo.studentsalarm.R;

import static activities.MainActivity.ALARM_SETTINGS;

public class AlarmAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<AlarmModel> aItems;
    private MainActivity aActivity;

    public AlarmAdapter(List<AlarmModel> listItems, MainActivity context) {
        this.aItems = listItems;
        this.aActivity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_alarm, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TypefaceHelper.getInstance().setTypeface(holder.vBasis, ApplicationWrapper.FONT_MAIN_LIGHT);
        final Alarm alarm = new Alarm(aItems.get(position));
        String condition = alarm.isAlarmEnabled() ? aActivity.getString(R.string.on) : aActivity.getString(R.string.off);

        holder.vTime.setText(Algorithms.getTimeText(alarm.getHours(), alarm.getMinutes()));
        holder.vCondition.setText(condition);

        String name = alarm.getName();
        if (name.isEmpty()) holder.vName.setVisibility(View.GONE);
        else holder.vName.setText(name);

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(aActivity, R.style.DayViewMini);

        boolean[] days = alarm.getSchedule();
        int count = Algorithms.getActiveDaysCount(days);

        switch (count) {
            case 0:
                addViewToGrid(holder.vDays, getReadyDateView(contextThemeWrapper, R.string.once));
                break;
            case 7:
                addViewToGrid(holder.vDays, getReadyDateView(contextThemeWrapper, R.string.everyday));
                break;
            default:
                for (int i = 0; i < days.length; i++) {
                    if (days[i]) {
                        String text = TimeHandler.getDayName(aActivity.getResources(), i + 1, false);
                        addViewToGrid(holder.vDays, getReadyDateView(contextThemeWrapper, text));
                    }
                }
        }

        if (count != 0) {
            contextThemeWrapper = new ContextThemeWrapper(aActivity, R.style.WeekViewMini);
            if (alarm.isAlarmWeekEven() || alarm.isWeekly())
                addViewToGrid(holder.vWeeks, getReadyDateView(contextThemeWrapper, R.string.ev));

            if (alarm.isAlarmWeekOdd() || alarm.isWeekly())
                addViewToGrid(holder.vWeeks, getReadyDateView(contextThemeWrapper, R.string.od));
        }

        holder.vBasis.setTag(String.valueOf(alarm.getIdentifier()));

        holder.vBasis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAlarm(alarm);
            }
        });
        enableCardView(holder, alarm.isAlarmEnabled(), false);

        holder.vSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableCardView(holder, holder.vSwitch.isIconEnabled(), true);
                alarm.switchAlarm();
                aActivity.getDatabaseManager().saveAlarm(alarm);
            }
        });

        holder.vBasis.setLongClickable(true);
        holder.vBasis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(aActivity, holder.vBasis);
                popupMenu.inflate(R.menu.menu_alarm);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                editAlarm(alarm);
                                break;
                            case R.id.action_delete:
                                aActivity.getDatabaseManager().deleteAlarm(alarm);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    private void editAlarm(Alarm alarm) {
        Intent intent = new Intent(aActivity, AlarmSettingsActivity.class);
        intent.putExtra(Alarm.ALARM_KEY, alarm);
        aActivity.startActivityForResult(intent, ALARM_SETTINGS);
    }

    private void enableCardView(ViewHolder holder, boolean enable, boolean animate) {
        float alpha = 1;
        if (enable) holder.vCondition.setText(aActivity.getString(R.string.on));
        else {
            holder.vCondition.setText(aActivity.getString(R.string.off));
            alpha = 0.3f;
        }

        holder.vDays.setAlpha(alpha);
        holder.vWeeks.setAlpha(alpha);
        holder.vName.setAlpha(alpha);
        holder.vTime.setAlpha(alpha);
        holder.vSwitch.setIconEnabled(enable, animate);
    }

    private TextView createDateView(ContextThemeWrapper ctw) { return new TextView(ctw); }

    private void addViewToGrid(GridLayout grid, View view) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.setMargins(0, 0, 6, 0);

        grid.addView(view, lp);
    }

    private TextView getReadyDateView(ContextThemeWrapper ctw, int textId) {
        return getReadyDateView(ctw, aActivity.getString(textId));
    }

    private TextView getReadyDateView(ContextThemeWrapper ctw, String text) {
        TextView week = createDateView(ctw);
        week.setText(text);
        return week;
    }

    @Override
    public int getItemCount() {
        return aItems.size();
    }
}